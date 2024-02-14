package com.example.qp

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuestionService {

    private lateinit var writeQView: WriteQView
    private lateinit var detailedQView:DetailedQView

    fun setWriteQView(view:WriteQView){
        this.writeQView=view
    }

    fun setDetailedQView(view:DetailedQView){
        this.detailedQView=view
    }

    fun writeQ(questionInfo :QuestionPost){
        val questionService= getRetrofit().create(QuestionInterface::class.java)
        Log.d("writeQArgument",questionInfo.toString())

        questionService.writeQ("",questionInfo).enqueue(object: Callback<WriteQResponse>{
            override fun onResponse(
                call: Call<WriteQResponse>,
                response: Response<WriteQResponse>
            ) {
                val resp=response.body()
                Log.d("writeQLog",resp.toString())
                Log.d("writeQ response","response:".plus(response.errorBody()?.string().toString()))

                if(resp!=null){
                    when(resp.code){
                        "2000"-> {
                            writeQView.onWriteSuccess()
                            Log.d("writeQ success","success!")
                        }
                        else-> {
                            writeQView.onWriteFailure()
                        }
                    }
                }

            }

            override fun onFailure(call: Call<WriteQResponse>, t: Throwable) {
                Log.d("writeQ Fail",t.message.toString())
            }

        })


    }
    fun getQuestion(questionId:Long?){
        val questionService= getRetrofit().create(QuestionInterface::class.java)

        questionService.getQuestion(questionId).enqueue(object :Callback<DetailedQResponse>{
            override fun onResponse(
                call: Call<DetailedQResponse>,
                response: Response<DetailedQResponse>
            ) {
                val resp=response.body()
                when(resp?.code){
                    "2000"-> {
                        detailedQView.onGetQSuccess(resp.result)
                    }
                    else-> {
                        detailedQView.onGetQFailure(response.errorBody()?.string().toString())
                    }
                }
            }

            override fun onFailure(call: Call<DetailedQResponse>, t: Throwable) {
                Log.d("detailedQFail",t.message.toString())
            }

        })

    }

    fun getParentAnswer(questionId:Long){
        val questionService= getRetrofit().create(QuestionInterface::class.java)

        questionService.getParentAnswer(questionId,0,0).enqueue(object :Callback<DetailedAnswerResponse>{
            override fun onResponse(
                call: Call<DetailedAnswerResponse>,
                response: Response<DetailedAnswerResponse>
            ) {
                val resp=response.body()
                when(resp?.code){
                    "3000"->detailedQView.onGetParentSuccess(resp.result.answerList)
                    else-> {
                        detailedQView.onGetaParentFailure(response.errorBody()?.string().toString())
                    }
                }
            }

            override fun onFailure(call: Call<DetailedAnswerResponse>, t: Throwable) {
                Log.d("getParent",t.message.toString())
            }

        })
    }
    //해시태그 생성
    fun getHashtag(hashtag:String){
        val questionService= getRetrofit().create(QuestionInterface::class.java)


    }
}