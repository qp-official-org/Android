package com.example.qp

interface WriteQView {
    fun onWriteSuccess()
    fun onWriteFailure()
}

interface DetailedQView{
    fun onGetQSuccess(questionResp:QuestionInfo?)
    fun onGetQFailure(msg:String)
    fun onGetParentSuccess(answerList:ArrayList<AnswerInfo>)
    fun onGetaParentFailure(msg:String)
}

