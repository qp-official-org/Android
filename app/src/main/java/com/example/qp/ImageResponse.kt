package com.example.qp

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ImageResponse(
    @SerializedName(value="isSuccess")val isSuccess: Boolean,
    @SerializedName(value="code")val code: String,
    @SerializedName(value="message")val message: String,
    @SerializedName(value="result")val result: Url
): Serializable

data class Url(
    @SerializedName(value="url") var url: String
)

data class DeleteResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String
)