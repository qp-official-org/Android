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
    @SerializedName(value="user")val user: UserInfo?,
    @SerializedName(value="questionId")val questionId: Long,
    @SerializedName(value="title")var title: String,
    @SerializedName(value="result")var content: String,
    @SerializedName(value="hit")val hit: Int,
    @SerializedName(value="answerCount")val answerCount: Int,
    @SerializedName(value="expertCount")val expertCount: Int,
    @SerializedName(value="createAt")val createAt: String,
    @SerializedName(value="updateAt")var updateAt: String?,
    @SerializedName(value="hashtags")val hashtags: ArrayList<TagInfo>?
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