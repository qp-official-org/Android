package com.example.qp

data class UserResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: UserToken
)

data class UserToken(
    val userId: Int,
    val accessToken: String,
    val refreshToken: String
)
