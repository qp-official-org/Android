package com.example.qp

import com.google.gson.annotations.SerializedName

data class WriteQResponse(
    @SerializedName(value="isSuccess")val isSuccess:Boolean,
    @SerializedName(value="code")val code:String,
    @SerializedName(value="message")val message:String,
    @SerializedName(value="result")val result:WriteQResult
)
data class WriteQResult(
    @SerializedName(value="question_id")val questionId:String,
    @SerializedName(value="createdAt")val createdAt:String
)
