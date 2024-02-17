package com.example.qp

import com.example.qp.databinding.ItemAnswerBinding


interface DetailedQView{
    fun onGetQSuccess(questionResp:QuestionInfo?)
    fun onGetQFailure(msg:String)

}


