package com.example.qp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserInterface {
    @GET("/users/sign_up")   // 회원가입 및 로그인
    fun signUp(
        @Query("accessToken") accessToken: String?
    ): Call<UserResponse<UserToken>>

    @POST("/users/auto_sign_in")    // 자동 로그인
    fun autoSignIn(
        @Header("accessToken") accessToken: String,
        @Header("refreshToken") refreshToken: String,
        @Field("userId") userId: Int
    ): Call<UserResponse<UserToken>>

    @PATCH("/users/{userId}")     // 유저 정보 수정
    fun modifyUserInfo(
        @Header("accessToken") accessToken: String,
        @Path("userId") userId: Int,
        @Body userModify: UserModify
    ): Call<UserResponse<UserModifyResult>>

    @GET("/users/{userId}")     // 유저 정보 조회
    fun searchUserInfo(
        @Header("accessToken") accessToken: String,
        @Path("userId") userId: Int
    ): Call<UserResponse<User>>

    @PATCH("/users/delete/{userId}")    // 유저 삭제
    fun deleteUser(
        @Path("userId") userId: Int
    ): Call<UserResponse<UserDeleteResult>>

    @POST("/users/test")        // 테스트 유저 생성
    fun signTest(): Call<UserResponse<UserTestResult>>
}