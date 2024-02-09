package com.example.qp

import com.google.gson.annotations.SerializedName

data class DetailedQResponse (
    @SerializedName(value="isSuccess")val isSuccess:Boolean,
    @SerializedName(value="code")val code:String,
    @SerializedName(value="message")val message:String,
    @SerializedName(value="result")val result:DetailedQResult
)
data class DetailedQResult(
    @SerializedName(value="result")val questionId:String,
    @SerializedName(value="result")val title:String,
    @SerializedName(value="result")val content:String,
    @SerializedName(value="result")val hashtags:String, //Hashtag 클래스 만들기
    @SerializedName(value="result")val createdAt:String,
    @SerializedName(value="result")val updatedAt:String
)

