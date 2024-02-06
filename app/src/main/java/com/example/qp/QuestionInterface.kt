package com.example.qp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface QuestionInterface {
    @GET("/questions")
    fun getQuestions(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("search") search: String?
    ): Call<QuestionResponse>
}
