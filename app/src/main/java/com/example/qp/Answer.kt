package com.example.qp

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AnswerInfo (
    @SerializedName(value="answerId")var answerId:Long?,
    @SerializedName(value="userId")var userId:Long,
    @SerializedName(value="title")var title:String,
    @SerializedName(value="content")var content:String="",
    @SerializedName(value="category")var category:String,
    @SerializedName(value="answerGroup")var answerGroup:Long,
    @SerializedName(value="likes")var likes:Long?=0
):Serializable

data class ParentAnswerResponse(
    @SerializedName("isSuccess")var isSuccess:Boolean,
    @SerializedName("code")var code:String,
    @SerializedName("message")var message:String,
    @SerializedName("result")var result:ParentAnswerResult
):Serializable
data class ChildAnswerResponse(
    @SerializedName("isSuccess")var isSuccess:Boolean,
    @SerializedName("code")var code:String,
    @SerializedName("message")var message:String,
    @SerializedName("result")var result:ChildAnswerResult
):Serializable

data class ParentAnswerResult(
    @SerializedName("parentAnswerList")var answerList:ArrayList<AnswerInfo>?,
    @SerializedName("listSize")var listSize:Int,
    @SerializedName("totalPage")var totalPage:Int,
    @SerializedName("totalElements")var totalElements:Long,
    @SerializedName("isFirst")var isFirst:Boolean,
    @SerializedName("isLast")var isLast:Boolean
):Serializable
data class ChildAnswerResult(
    @SerializedName("childAnswerList")var answerList:ArrayList<AnswerInfo>?,
    @SerializedName("listSize")var listSize:Int,
    @SerializedName("totalPage")var totalPage:Int,
    @SerializedName("totalElements")var totalElements:Long,
    @SerializedName("isFirst")var isFirst:Boolean,
    @SerializedName("isLast")var isLast:Boolean
):Serializable
data class WriteAnswerResponse(
    @SerializedName("isSuccess")var isSuccess:Boolean,
    @SerializedName("code")var code:String,
    @SerializedName("message")var message:String,
    @SerializedName("result")var result:WriteAnswerResult
):Serializable
data class WriteAnswerResult(
    @SerializedName("answerId")var answerId:Long,
    @SerializedName("createdAt")var createdAt:String
):Serializable