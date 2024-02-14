package com.example.qp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface UserInterface {
    @GET("/users/sign_up") // 회원가입 및 로그인
    fun signUp(
        @Query("accessToken") accessToken: String?
    ): Call<UserResponse>
}