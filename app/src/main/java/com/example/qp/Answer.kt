package com.example.qp

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AnswerInfo (
    @SerializedName(value = "user")var user:AnswerUser,
    @SerializedName(value="answerId")var answerId:Long?,
    @SerializedName(value="content")var content:String="",
    @SerializedName(value="category")var category:String,
    @SerializedName(value="answerGroup")var answerGroup:Long,
    @SerializedName(value="likeCount")var likes:Long?=0,
    @SerializedName(value = "childAnswerCount")var childCount:Int=0
):Serializable

data class AnswerUser(
    @SerializedName(value="userId")var userId: Long?=0,
    @SerializedName(value = "nickname")var nickname:String?="",
    @SerializedName(value = "profileImage")var profileImage:String?="",
    @SerializedName(value="role")var role:String?="USER"
    )

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
data class AnswerPost(
    @SerializedName("userId")var userId: Long,
    @SerializedName("content")var content: String,
    @SerializedName("category")var category: String,
    @SerializedName("answerGroup")var answerGroup: Long,
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

data class ReportAnswerResponse(
    @SerializedName(value="isSuccess")val isSuccess: Boolean?,
    @SerializedName(value="code")val code: String,
    @SerializedName(value="message")val message: String,
    @SerializedName(value="result")val result:ReportAnswerResult
):Serializable

data class ReportAnswerResult(
    @SerializedName(value="answerId")var answerId:Long,
    @SerializedName(value="userId")var userId:Long,
    @SerializedName(value="answerReportId")var answerReportId:Int,
    @SerializedName(value="content")var content:String,
    @SerializedName(value="createdAt")var createdAt:String
):Serializable