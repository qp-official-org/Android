package com.example.qp

import com.example.qp.databinding.ItemAnswerBinding

interface WriteQView {
    fun onWriteSuccess()
    fun onWriteFailure()

    fun onPostHashtagSuccess(response: HashtagResponse)
    fun onPostHashtagFailure(tag:String,msg: String)

    fun onGetHashtagSuccess(response: HashtagResponse)
    fun onGetHashtagFailure(tag:String,msg:String)
}

interface DetailedQView{
    fun onGetQSuccess(questionResp:QuestionInfo?)
    fun onGetQFailure(msg:String)
    fun onGetParentSuccess(answerList:ArrayList<AnswerInfo>?)
    fun onGetaParentFailure(msg:String)

    fun onGetChildSuccess(answerList:ArrayList<AnswerInfo>?,id:Long,position:Int)
    fun onGetChildFailure(msg:String)

    //fun getChildAnswer(binding:ItemAnswerBinding,id:Long,position:Int)
}


