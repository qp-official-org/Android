package com.example.qp

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getQuestions()
        Log.d("qdatas",qDatas.toString())

        //수정 임시 데이터(삭제해도 무관)
        var tagList= arrayListOf<TagInfo>(TagInfo(0,"tag1"),TagInfo(1,"tag2"))
        qDatas.apply {
            add(QuestionInfo(UserInfo(1,"1","USER"),0,"질문제목1","질문내용1",1,2,3,"2024-01-01","2024-01-01",tagList))
            add(QuestionInfo(UserInfo(1,"1","USER"),0,"질문제목2","질문내용2",1,2,3,"2024-01-01","2024-01-01",tagList))
            add(QuestionInfo(UserInfo(1,"1","USER"),0,"질문제목3","질문내용3",1,2,3,"2024-01-01","2024-01-01",tagList))

        }


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

        // 사용자명 불러오기
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

        binding.mainSearchBt.setOnClickListener {
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            intent.putExtra("qDatas", qDatas)
            startActivity(intent)
        }

        binding.mainLoginBt.setOnClickListener{
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }


        //Login 여부 확인
        val isLogin = intent.getIntExtra("isLogin", 0)
        if(isLogin == 1) {
            binding.mainLoginBt.visibility = View.GONE
            binding.mainLoginSuccessBt.visibility = View.VISIBLE
        }
        else {
            binding.mainLoginBt.visibility = View.VISIBLE
            binding.mainLoginSuccessBt.visibility = View.GONE
        }


        binding.mainLoginSuccessBt.setOnClickListener{
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun getQuestions() {
        val questionService = getRetrofit().create(QuestionInterface::class.java)

        questionService.getQuestions(0, 10, null)
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
                                qDatas = questionResponse.result.questions
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

}