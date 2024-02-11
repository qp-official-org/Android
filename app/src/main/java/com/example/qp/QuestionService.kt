package com.example.qp

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuestionService {

    private lateinit var writeQView: WriteQView

    fun setWriteQView(view:WriteQView){
        this.writeQView=view
    }

    fun writeQ(questionInfo :QuestionInfo){
        val questionService= getRetrofit().create(QuestionInterface::class.java)

        questionService.writeQ("",questionInfo).enqueue(object: Callback<QuestionResponse>{
            override fun onResponse(
                call: Call<QuestionResponse>,
                response: Response<QuestionResponse>
            ) {
                Log.d("writeQ response","success!")
                val resp=response.body()
                if(resp!=null){
                    when(resp.code){
                        "2000"-> {
                            writeQView.onWriteSuccess()
                            Log.d("writeQ success","success!")
                        }
                        else->writeQView.onWriteFailure()
                    }
                }

            }

            override fun onFailure(call: Call<QuestionResponse>, t: Throwable) {
                Log.d("writeQ Fail",t.message.toString())
            }

        })
    }

    //해시태그 생성
    fun getHashtag(hashtag:String){
        val questionService= getRetrofit().create(QuestionInterface::class.java)


    }
}