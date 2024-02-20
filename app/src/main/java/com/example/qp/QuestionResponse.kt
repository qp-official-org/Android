package com.example.qp

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serial
import java.io.Serializable
import java.sql.Timestamp

data class QuestionResponse(
    @SerializedName(value="isSuccess")val isSuccess: Boolean,
    @SerializedName(value="code")val code: String,
    @SerializedName(value="message")val message: String,
    @SerializedName(value="result")val result: QuestionChart
):Serializable

data class QuestionChart(
    @SerializedName(value="questions")val questions: ArrayList<QuestionInfo>,
    @SerializedName(value="listSize")val listSize: Int?,
    @SerializedName(value="totalPage")val totalPage: Int?,
    @SerializedName(value="totalElements")val totalElements: Long?,
    @SerializedName(value="first")val first: Boolean?,
    @SerializedName(value="last")val last: Boolean?
):Serializable

data class QuestionInfo(
    @SerializedName(value="user")var user: UserInfo?=null,
    @SerializedName(value="questionId")val questionId: Long=0,
    @SerializedName(value="title")var title: String,
    @SerializedName(value="content")var content: String?,
    @SerializedName(value="hit")val hit: Int?=0,
    @SerializedName(value="answerCount")val answerCount: Int?=0,
    @SerializedName(value="expertCount")val expertCount: Int?=0,
    @SerializedName(value="childStatus")val childStatus: String?="INACTIVE",
    @SerializedName(value="createdAt")val createdAt: String?="",
    @SerializedName(value="updatedAt")var updatedAt: String?="",
    @SerializedName(value="hashtags")val hashtags: ArrayList<TagInfo>?=null,
):Serializable

data class QuestionPost(
    @SerializedName(value="userId")val userId:Int,
    @SerializedName(value="title")val title:String,
    @SerializedName(value="content")val content:String,
    @SerializedName(value="childStatus")var childStatus:String,
    @SerializedName(value="hashtag")val hashtag:ArrayList<Int>?
):Serializable

data class UserInfo(
    @SerializedName(value="userId")val userId: Int,
    @SerializedName(value="profileImage")val profileImage: String="",
    @SerializedName(value="role")val role: String
):Serializable

data class TagInfo(
    @SerializedName(value="hashtagId")val hashtagId: Int,
    @SerializedName(value="hashtag")val hashtag: String
):Serializable

open class WriteQResponse(
    @SerializedName(value="isSuccess")val isSuccess: Boolean,
    @SerializedName(value="code")val code: String,
    @SerializedName(value="message")val message: String,
    @SerializedName(value="result")var result:WriteQResult
):Serializable
data class WriteQResult(
    @SerializedName("questionId")var questionId:Long,
    @SerializedName("createdAt")var createdAt:String
):Serializable

data class DetailedQResponse(
    @SerializedName(value="isSuccess")val isSuccess: Boolean?,
    @SerializedName(value="code")val code: String?,
    @SerializedName(value="message")val message: String?,
    @SerializedName(value="result")var result:QuestionInfo?,
    @SerializedName(value="createdAt")var createdAt: String?,
    @SerializedName(value="updatedAt")var updateAt: String?
):Serializable

data class HashtagResponse(
    @SerializedName(value="isSuccess")val isSuccess: Boolean?,
    @SerializedName(value="code")val code: String?,
    @SerializedName(value="message")val message: String?,
    @SerializedName(value="result")val result:TagInfo?=null
):Serializable

data class ModifyQResponse(
    @SerializedName(value="isSuccess")val isSuccess: Boolean?,
    @SerializedName(value="code")val code: String,
    @SerializedName(value="message")val message: String,
    @SerializedName(value="result")val result:ModifyQResult
):Serializable

data class ModifyQResult(
    @SerializedName(value="questionId")val questionId:Long,
    @SerializedName(value="title")val title:String,
    @SerializedName(value="content")val content:String,
    @SerializedName(value="updatedAt")val updatedAt:String
):Serializable

data class ModifyQInfo(
    @SerializedName(value="userId")var userId:Long,
    @SerializedName(value="title")var title:String,
    @SerializedName(value="content")var content:String
)

data class ModifyAnswerResponse(
    @SerializedName(value="isSuccess")val isSuccess: Boolean?,
    @SerializedName(value="code")val code: String,
    @SerializedName(value="message")val message: String,
    @SerializedName(value="result")val result:ModifyAnswerResult?
)
data class ModifyAnswerResult(
    @SerializedName(value="answerId")var answerId:Long,
    @SerializedName(value="title")var title:String,
    @SerializedName(value="content")var content:String,
    @SerializedName(value="likeCount")var likeCount:Int,
    @SerializedName(value="updatedAt")var updatedAt:String
)

data class LikeAnswerResponse(
    @SerializedName(value="isSuccess")val isSuccess: Boolean?,
    @SerializedName(value="code")val code: String,
    @SerializedName(value="message")val message: String,
    @SerializedName(value="result")val result:LikeAnswerResult,
    ):Serializable

data class LikeAnswerResult(
    @SerializedName(value="answerLikeStatus")val answerLikeStatus:String?
):Serializable

data class ReportQ(
    @SerializedName(value="userId")var userId:Long,
    @SerializedName(value="content")var content:String
):Serializable

data class ReportQResponse(
    @SerializedName(value="isSuccess")val isSuccess: Boolean?,
    @SerializedName(value="code")val code: String,
    @SerializedName(value="message")val message: String,
    @SerializedName(value="result")val result:ReportQResult
):Serializable

data class ReportQResult(
    @SerializedName(value="questionId")var questionId:Long,
    @SerializedName(value="userId")var userId:Long,
    @SerializedName(value="questionReportId")var questionReportId:Int,
    @SerializedName(value="content")var content:String,
    @SerializedName(value="createdAt")var createdAt:String
):Serializable

data class NotifyQResponse(
    @SerializedName(value="isSuccess")val isSuccess: Boolean?,
    @SerializedName(value="code")val code: String,
    @SerializedName(value="message")val message: String,
    @SerializedName(value="result")val result:NotifyQResult
):Serializable
data class NotifyQResult(
    @SerializedName(value="userId")var userId:Long,
    @SerializedName(value="createdAt")var createdAt:String
):Serializable

data class GetNotifyResponse(
    @SerializedName(value="isSuccess")val isSuccess: Boolean?,
    @SerializedName(value="code")val code: String,
    @SerializedName(value="message")val message: String,
    @SerializedName(value="result")val result:GetNotifyResult
):Serializable
data class GetNotifyResult(
    @SerializedName(value="questionId")var questionId:Long,
    @SerializedName(value="questionAlarms")var questionAlarms:ArrayList<NotifyQResult>,
    @SerializedName(value="totalElements")var total:Int
):Serializable

data class GetOtherQResponse(
    @SerializedName(value="isSuccess")val isSuccess: Boolean?,
    @SerializedName(value="code")val code: String,
    @SerializedName(value="message")val message: String,
    @SerializedName(value="result")val result:GetOtherQResult
):Serializable

data class GetOtherQResult(
    @SerializedName(value="hasLater")val hasPrev:Boolean,
    @SerializedName(value ="hasOlder")val hasNext:Boolean,
    @SerializedName(value = "laterQuestion")var prevQuestion:OtherQ,
    @SerializedName(value = "olderQuestion")var nextQuestion:OtherQ
):Serializable

data class OtherQ(
    @SerializedName(value = "questionId")val questionId:Long,
    @SerializedName(value = "title")val title:String
):Serializable