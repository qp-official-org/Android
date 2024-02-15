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
        
        //백엔드로부터 질문 정보를 가져와 리사이클러뷰를 구성하는 함수
        getQuestions()
//         Log.d("qdatas",qDatas.toString())

//         //수정 임시 데이터(삭제해도 무관)
//         var tagList= arrayListOf<TagInfo>(TagInfo(0,"tag1"),TagInfo(1,"tag2"))
//         qDatas.apply {
//             add(QuestionInfo(UserInfo(1,"1","USER"),34,"질문제목1","질문내용1",1,2,3,"2024-01-01","2024-01-01",tagList))
//             add(QuestionInfo(UserInfo(1,"1","USER"),0,"질문제목2","질문내용2",1,2,3,"2024-01-01","2024-01-01",tagList))
//             add(QuestionInfo(UserInfo(1,"1","USER"),0,"질문제목3","질문내용3",1,2,3,"2024-01-01","2024-01-01",tagList))


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

        //상단 검색바 클릭 시 검색화면으로 화면 전환
        binding.mainSearchBt.setOnClickListener {
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
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

    private fun getQuestions() {
        val questionService = getRetrofit().create(QuestionInterface::class.java)

        questionService.getQuestions(0, 10, null) //스크롤에 따라 추가 페이징 할 것!
            .enqueue(object: Callback<QuestionResponse>{
                override fun onResponse(
                    call: Call<QuestionResponse>,
                    response: Response<QuestionResponse>
                ) {
                    if(response.isSuccessful && response.code() == 200){
                        val questionResponse: QuestionResponse = response.body()!!

                        Log.d("Q-RESPONSE/SUCCESS", questionResponse.toString())

                        when(questionResponse.code){
                            "QUESTION_2000" -> {
                                Log.d("SUCCESS/DATA_LOAD", "리사이클러뷰의 데이터로 구성됩니다")
                                qDatas.addAll(questionResponse.result.questions)
                                setQuestionRVAdapter()
                                Log.d("getQsResp",qDatas.toString())
                            }
                            else -> {
                                Log.d("SUCCESS/DATA_FAILURE", "응답 코드 오류입니다")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<QuestionResponse>, t: Throwable) {
                    Log.d("Q-RESPONSE/FAILURE", t.message.toString())
                }

            })
    }

    private fun setQuestionRVAdapter(){
        val questionRVAdapter = QuestionRVAdapter(qDatas)
        binding.mainQuestionRv.adapter = questionRVAdapter
        binding.mainQuestionRv.layoutManager = GridLayoutManager(applicationContext, 2)

        //특정 질문 클릭 시 질문상세화면으로 전환
        questionRVAdapter.setMyItemClickListner(object : QuestionRVAdapter.MyItemClickListner{
            override fun onItemClick(questionInfo: QuestionInfo) {
                val intent = Intent(this@MainActivity, DetailedActivity::class.java)
                val gson = Gson()
                val qJson = gson.toJson(questionInfo)
                intent.putExtra("question", qJson)
                intent.putExtra("qDatas", qDatas)
                startActivity(intent)
            }
        })
    }

}