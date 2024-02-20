package com.example.qp

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuestionService {

    private lateinit var detailedQView:DetailedQView

    fun setDetailedQView(view:DetailedQView){
        this.detailedQView=view
    }


    fun getQuestion(questionId:Long?){
            val questionService= getRetrofit().create(QuestionInterface::class.java)

            questionService.getQuestion(questionId).enqueue(object :Callback<DetailedQResponse>{
                override fun onResponse(
                    call: Call<DetailedQResponse>,
                    response: Response<DetailedQResponse>
                ) {

                    val resp=response.body()
                    Log.d("getDetailedResp",resp.toString())
                    when(resp?.code){
                        "QUESTION_2000"-> {
                            Log.d("detailedQ/SUCCESS",resp.toString())
                            detailedQView.onGetQSuccess(resp.result)
                        }
                        else-> {
                            detailedQView.onGetQFailure(response.errorBody()?.string().toString())
                            Log.d("detailedQ/FAIL",response.errorBody()?.string().toString().plus(questionId))
                        }
                    }
                }

                override fun onFailure(call: Call<DetailedQResponse>, t: Throwable) {
                    Log.d("detailedQFail",t.message.toString())
                }

            })

    }


    /*fun getHashtag(hashtag: String){
        val questionService= getRetrofit().create(QuestionInterface::class.java)

        questionService.getHashtag(hashtag).enqueue(object:Callback<HashtagResponse>{
            override fun onResponse(
                call: Call<HashtagResponse>,
                response: Response<HashtagResponse>
            ) {
                val resp=response.body()
                Log.d("getHashtagResp",resp.toString())
                when(resp?.code){
                    "2000"->writeQView.onGetHashtagSuccess(resp)
                    else->writeQView.onGetHashtagFailure(hashtag,response.errorBody()?.string().toString())
                }
            }

            override fun onFailure(call: Call<HashtagResponse>, t: Throwable) {
                Log.d("getHashtagResp/FAIL",t.message.toString())
            }

        })
    }*/
}