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

    @GET("/questions/user/{userId}") //작성질문 조회
    fun getWriteQuestions(
        @Header("accessToken") token: String,
        @Path("userId") userId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<QuestionResponse>

    @GET("/questions/alarms/user/{userId}") //알람질문 조회
    fun getAlarmQuestions(
        @Header("accessToken") token: String,
        @Path("userId") userId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<QuestionResponse>

    @POST("/questions") //질문 등록
    fun writeQ(
        @Header("accessToken") token:String,
        @Body question: QuestionPost
    ): Call<WriteQResponse>

    @DELETE("/questions/{questionId}")
    fun deleteQ(
        @Header("accessToken") token: String,
        @Path("questionId") questionId: Long?,
        @Query("userId") userId: Int
    ): Call<QuestionResponse>

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

    @PATCH("/answers/{answerId}")
    fun modifyAnswer(
        @Header("accessToken")token:String,
        @Path("answerId")answerId:Long,
        @Body answerInfo:ModifyQInfo
    ):Call<ModifyAnswerResponse>

    @DELETE("/answers/{answerId}")
    fun deleteAnswer(
        @Header("accessToken")token:String,
        @Path("answerId")answerId:Long,
        @Query("userId")userId:Long
    ):Call<ModifyAnswerResponse>

    @POST("/answers/{answerId}/users/{userId}")
    fun likeAnswer(
        @Header("accessToken")token:String,
        @Path("userId")userId:Int,
        @Path("answerId")answerId:Long
    ):Call<LikeAnswerResponse>

    @POST("/report/question/{questionId}")
    fun reportQ(
        @Path("questionId")questionId:Int,
        @Body report:ReportQ
    ):Call<ReportQResponse>
}
