package com.example.qp

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class QuestionResponse(
    @SerializedName(value="isSuccess")val isSuccess: Boolean,
    @SerializedName(value="code")val code: String,
    @SerializedName(value="message")val message: String,
    @SerializedName(value="result")val result: QuestionChart
)

data class QuestionChart(
    @SerializedName(value="questions")val questions: ArrayList<QuestionInfo>,
    @SerializedName(value="listSize")val listSize: Int?,
    @SerializedName(value="totalPage")val totalPage: Int?,
    @SerializedName(value="totalElements")val totalElements: Long?,
    @SerializedName(value="isFirst")val isFirst: Boolean?,
    @SerializedName(value="isLast")val isLast: Boolean?
)

data class QuestionInfo(
    @SerializedName(value="user")val user: UserInfo?=null,
    @SerializedName(value="questionId")val questionId: Long,
    @SerializedName(value="title")var title: String,
    @SerializedName(value="content")var content: String?,
    @SerializedName(value="hit")val hit: Int?=0,
    @SerializedName(value="answerCount")val answerCount: Int?=0,
    @SerializedName(value="expertCount")val expertCount: Int?=0,
    @SerializedName(value="createAt")val createAt: String?="",
    @SerializedName(value="updateAt")var updateAt: String?="",
    @SerializedName(value="hashtags")val hashtags: ArrayList<TagInfo>?=null,
    @SerializedName(value="hashtag")val hashtag:ArrayList<Int>?=null    //질문작성 위한 필드..
)

data class QuestionPost(
    @SerializedName(value="userId")val userId:String,
    @SerializedName(value="title")val title:String,
    @SerializedName(value="content")val content:String,
    @SerializedName(value="hashtag")val hashtag:ArrayList<Int>?
)

data class UserInfo(
    @SerializedName(value="userId")val userId: Long,
    @SerializedName(value="profileImage")val profileImage: String,
    @SerializedName(value="role")val role: String
)

data class TagInfo(
    @SerializedName(value="hashtagId")val hashtagId: Int,
    @SerializedName(value="hashtag")val hashtag: String
)

open class WriteQResponse(
    @SerializedName(value="isSuccess")val isSuccess: Boolean,
    @SerializedName(value="code")val code: String,
    @SerializedName(value="message")val message: String,
    @SerializedName(value="result")var result:WriteQResult
)
data class WriteQResult(
    @SerializedName("questionId")var questionId:String,
    @SerializedName("createdAt")var createdAt:String
)

data class DetailedQResponse(
    @SerializedName(value="isSuccess")val isSuccess: Boolean?,
    @SerializedName(value="code")val code: String?,
    @SerializedName(value="message")val message: String?,
    @SerializedName(value="result")var result:QuestionInfo?,
    @SerializedName(value="createdAt")var createdAt: String?,
    @SerializedName(value="updatedAt")var updateAt: String?
)
//data class DetailedQResult(
//    @SerializedName(value="questionId")var questionId:String?,
//    @SerializedName(value="title")var title:String?,
//    @SerializedName(value="content")var content:String?,
//    @SerializedName(value="hashtags")var hashtags:ArrayList<TagInfo>?
//)