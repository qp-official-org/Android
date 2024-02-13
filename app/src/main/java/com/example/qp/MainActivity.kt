package com.example.qp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.qp.databinding.ActivityMainBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        // 임시 데이터 (삭제 요망)
//        qDatas.apply {
//            add(Question("2023.11.16","아르테미스 계획","현재 아르테미스 계획은 어떻게 되어 가고 있나요?","#우주","달",""))
//            add(Question("2023.12.20","퓨전 한복이란?","퓨전한복이랑 개량한복의 차이점이 뭔가요?","#한복","#옷","#전통"))
//            add(Question("2024.12.26","콘크리트는 왜 분해되지 않나요?","콘크리트는 골재, 시멘트, 물 등을 가공해서 만든 것인데, 왜 다시 골재와 물로 분리할 수 없나요?","#콘크리트","#토목",""))
//            add(Question("2024.01.04","가장 빠른 동물","가장 빠른 동물이 무엇인가요?","#동물","#환경생물학",""))
//            add(Question("2024.01.07","필름카메라 필름 보관","필름카메라는 처음인데, 보관을 어떻게 해야 안전한가요?","#카메라","#필름","필름카메라"))
//        }

        val questionRVAdapter = QuestionRVAdapter(qDatas)
        binding.mainQuestionRv.adapter = questionRVAdapter
        binding.mainQuestionRv.layoutManager = GridLayoutManager(applicationContext, 2)

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

        binding.mainSearchBt.setOnClickListener {
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            // 검색화면에서 백엔드로 데이터 직접 요청 방식으로 수정 예정
            //intent.putExtra("qDatas", qDatas)
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