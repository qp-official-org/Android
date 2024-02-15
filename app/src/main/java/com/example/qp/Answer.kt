package com.example.qp

import com.google.gson.annotations.SerializedName

data class AnswerInfo (
    @SerializedName(value="answerId")var answerId:Long?,
    @SerializedName(value="userId")var userId:Long,
    @SerializedName(value="title")var title:String,
    @SerializedName(value="content")var content:String="",
    @SerializedName(value="category")var category:String,
    @SerializedName(value="answer_group")var answerGroup:Long,
    @SerializedName(value="likes")var likes:Long?
)

data class DetailedAnswerResponse(
    @SerializedName("isSuccess")var isSuccess:Boolean,
    @SerializedName("code")var code:String,
    @SerializedName("message")var message:String,
    @SerializedName("result")var result:DetailedAnswerResult
)
data class DetailedAnswerResult(
    @SerializedName("answerList")var answerList:ArrayList<AnswerInfo>,
    @SerializedName("listSize")var listSize:Int,
    @SerializedName("totalPage")var totalPage:Int,
    @SerializedName("totalElements")var totalElements:Long,
    @SerializedName("isFirst")var isFirst:Boolean,
    @SerializedName("isLast")var isLast:Boolean
)