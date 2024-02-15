package com.example.qp

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.example.qp.databinding.ActivityMainBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var qDatas = ArrayList<QuestionInfo>()

    // 뒤로가기 버튼 눌렀을 때 발동되는 함수 (두번 눌러야 종료)
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        var isExit : Boolean = false
        override fun handleOnBackPressed() {
            if(isExit) {
                finish()
            }
            else {
                Toast.makeText(this@MainActivity, "종료하려면 뒤로가기를 한 번 더 누르세요.", Toast.LENGTH_SHORT).show()
            }

            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                isExit = true
            }, 1000)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)    // 종료함수

        // 키 해시 확인용
        val keyHash = Utility.getKeyHash(this)
        Log.d("Hash", keyHash)

        // 로그인 여부 확인
        UserApiClient.instance.accessTokenInfo { token, error ->
            if (error != null) {
                Log.e("TAG", "로그인 실패", error)
                binding.mainLoginBt.visibility = View.VISIBLE
                binding.mainLoginSuccessBt.visibility = View.GONE
            } else if (token != null) {
                Log.i("TAG", "로그인 성공 $token")
                binding.mainLoginBt.visibility = View.GONE
                binding.mainLoginSuccessBt.visibility = View.VISIBLE
            }
        }

        // 유저 데이터가 담긴 객체를 받기 위함
        val intent = intent
        val qpUserData = intent.getSerializableExtra("data", QpUserData::class.java)

        // 사용자명 불러오기 (유저 닉네임으로 수정 필요)
        UserApiClient.instance.me { user, error ->
            binding.mainBarNicknameTv.text = "${user?.kakaoAccount?.profile?.nickname}"
        }

        // 임시 로그아웃 (로고 클릭시)
        binding.mainLogoIv.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Toast.makeText(this, "로그아웃 실패 $error", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                    binding.mainLoginBt.visibility = View.VISIBLE
                    binding.mainLoginSuccessBt.visibility = View.GONE
                }
            }
        }

//        getQuestions()
//        qDatas.apply {
//            add(Question("2023.11.16","질문 제목1","질문내용1","#태그1","#태그2","#태그3"))
//            add(Question("2023.12.20","질문 제목2","질문내용2","#태그1","#태그2","#태그3"))
//            add(Question("2024.01.01","질문 제목3","질문내용3","#태그1","#태그2","#태그3"))
//            add(Question("2024.01.04","질문 제목4","질문내용4","#태그1","#태그2","#태그3"))
//            add(Question("2024.01.07","질문 제목5","질문내용5","#태그1","#태그2",""))
//        }

//        // 임시 데이터 (삭제 요망)
//        qDatas.apply {
//            add(Question("2023.11.16","아르테미스 계획","현재 아르테미스 계획은 어떻게 되어 가고 있나요?"))
//            add(Question("2023.12.20","퓨전 한복이란?","퓨전한복이랑 개량한복의 차이점이 뭔가요?"))
//            add(Question("2024.12.26","콘크리트는 왜 분해되지 않나요?","콘크리트는 골재, 시멘트, 물 등을 가공해서 만든 것인데, 왜 다시 골재와 물로 분리할 수 없나요?"))
//            add(Question("2024.01.04","가장 빠른 동물","가장 빠른 동물이 무엇인가요?"))
//            add(Question("2024.01.07","필름카메라 필름 보관","필름카메라는 처음인데, 보관을 어떻게 해야 안전한가요?"))
//        }

//        val questionRVAdapter = QuestionRVAdapter(qDatas)
//        binding.mainQuestionRv.adapter = questionRVAdapter
//        binding.mainQuestionRv.layoutManager = GridLayoutManager(applicationContext, 2)
//
//        questionRVAdapter.setMyItemClickListner(object : QuestionRVAdapter.MyItemClickListner{
//            override fun onItemClick(questionInfo: QuestionInfo) {
//                val intent = Intent(this@MainActivity, DetailedActivity::class.java)
//                val gson = Gson()
//                val qJson = gson.toJson(question)
//                intent.putExtra("question", qJson)
//                intent.putExtra("qDatas", qDatas)
//                startActivity(intent)
//            }
//        })

        // 검색 화면으로 이동
        binding.mainSearchBt.setOnClickListener {
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            intent.putExtra("qDatas", qDatas)
            startActivity(intent)
        }

        // 로그인 화면으로 이동
        binding.mainLoginBt.setOnClickListener{
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }

        // 프로필 화면으로 이동
        binding.mainLoginSuccessBt.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("data", qpUserData)
            startActivity(intent)
        }
    }
}