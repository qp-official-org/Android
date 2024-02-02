package com.example.qp

import com.google.gson.annotations.SerializedName

data class ModifyQResponse(
    @SerializedName(value="isSuccess")val isSuccess:Boolean,
    @SerializedName(value="code")val code:String,
    @SerializedName(value="message")val message:String,
    @SerializedName(value="result")val result:ModifyQResult
)

data class ModifyQResult(
    @SerializedName(value="question_id")val questionId:String,
    @SerializedName(value="title")val title:String,
    @SerializedName(value="content")val content:String,
    @SerializedName(value="updateAt")val updateAt:String
)

