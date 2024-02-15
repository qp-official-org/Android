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
    ):Call<ParentAnswerResponse>

    @GET("/answers/{parentAnswerId}")
    fun getChildAnswer(
        @Path("parentAnswerId")parentAnswerId:Long,
        @Query("page")page:Int,
        @Query("size")size:Int
    ):Call<ChildAnswerResponse>

    @POST("/hashtag/")
    fun postHashtag(
        @Body hashtag:String
    ):Call<HashtagResponse>

    @GET("/hashtag/")
    fun getHashtag(
         @Query("hashtag") hashtag:String
    ):Call<HashtagResponse>

    @POST("/answers/questions/{questionId}")
    fun writeAnswer(
        @Header("accessToken")token:String,
        @Path("questionId")questionId:Long,
        @Body answer:AnswerInfo
    ):Call<WriteAnswerResponse>

    @PATCH("/questions/{questionId}")
    fun modifyQuestion(
        @Header("accessToken")token:String,
        @Path("questionId")questionId:Long,
        @Body questionInfo:ModifyQInfo
    ):Call<ModifyQResponse>
}
