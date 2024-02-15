package com.example.qp

import retrofit2.Call
import retrofit2.http.*

interface QuestionInterface {
    @GET("/questions") //질문 페이징 조회
    fun getQuestions(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("search") search: String?
    ): Call<QuestionResponse>

    @POST("/questions") //질문 등록
    fun writeQ(
        @Header("accessToken") token:String,
        @Body question: QuestionPost
    ): Call<WriteQResponse>

    @GET("/questions/{questionId}")
    fun getQuestion(
        @Path("questionId") questionId:Long?
    ):Call<DetailedQResponse>

    @GET("/answers/questions/{questionId}")
    fun getParentAnswer(
        @Path("questionId")questionId:Long,
        @Query("page") page:Int,
        @Query("size") size:Int
    ):Call<DetailedAnswerResponse>
}
