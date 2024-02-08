package com.example.qp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface QuestionRetrofitInterface {
    @POST("/questions")
    fun writeQ(@Header("accessToken") token:String, @Body question: Question): Call<WriteQResponse>
}