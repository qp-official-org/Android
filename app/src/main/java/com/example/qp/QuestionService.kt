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
                    Log.d("getDetailedResp",resp.toString())
                    when(resp?.code){
                        "QUESTION_2000"-> {
                            detailedQView.onGetQSuccess(resp.result)
                        }
                        else-> {
                            detailedQView.onGetQFailure(response.errorBody()?.string().toString())
                            //Log.d("getDetailedQFail",response.errorBody()?.string().toString().plus(questionId))
                        }
                    }
                }

                override fun onFailure(call: Call<DetailedQResponse>, t: Throwable) {
                    Log.d("detailedQFail",t.message.toString())
                }

            })

    }

    fun getParentAnswer(id:Long,isParent:Boolean,position:Int=0){
        val questionService= getRetrofit().create(QuestionInterface::class.java)

        questionService.getParentAnswer(id,1,1).enqueue(object :Callback<DetailedAnswerResponse>{
            override fun onResponse(
                call: Call<DetailedAnswerResponse>,
                response: Response<DetailedAnswerResponse>
            ) {
                val resp=response.body()
                Log.d("getParentResp",resp.toString())
                when(resp?.code){
                    "ANSWER_3000"-> {
                        if(isParent) {
                            detailedQView.onGetParentSuccess(resp.result.answerList)
                        }
                        else{
                            detailedQView.onGetChildSuccess(resp.result.answerList,id,position)
                        }
                    }
                    else-> {
                        if(isParent) {
                            detailedQView.onGetaParentFailure(
                                response.errorBody()?.string().toString()
                            )
                        }
                        else{
                            detailedQView.onGetChildFailure(response.errorBody()?.string().toString())
                        }
                    }
                }
            }

            override fun onFailure(call: Call<DetailedAnswerResponse>, t: Throwable) {
                Log.d("getParentResp/FAIL",t.message.toString())
            }

        })
    }
    //해시태그 생성
    fun postHashtag(hashtag:String){
        val questionService= getRetrofit().create(QuestionInterface::class.java)

        questionService.postHashtag(hashtag).enqueue(object:Callback<HashtagResponse>{
            override fun onResponse(
                call: Call<HashtagResponse>,
                response: Response<HashtagResponse>
            ) {
                val resp=response.body()
                Log.d("postHashtagResp",resp.toString())
                when(resp?.code){
                    "HASHTAG_6000"->writeQView.onPostHashtagSuccess(resp)
                    else->writeQView.onPostHashtagFailure(hashtag,response.errorBody()?.string().toString())
                }
            }

            override fun onFailure(call: Call<HashtagResponse>, t: Throwable) {
                Log.d("postHashtagResp/FAIL",t.message.toString())
            }

        })

    }
    fun getHashtag(hashtag: String){
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
    }
}