package com.example.qp

data class UserResponse<T>(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: T
)

data class UserToken(
    val userId: Int,
    val isNew: Boolean,
    val accessToken: String,
    val refreshToken: String
)
data class AutoSignIn(
    val userId: Int,
    val accessToken: String,
    val refreshToken: String
)
data class UserTestResult(
    val userId: Int,
    val createdAt: String
)
data class UserDeleteResult(
    val userId: Int,
    val updatedAt: String,
    val status: String
)

data class UserModifyResult(
    val userId: Int,
    val nickname: String,
    val profileImage: String,
    val updatedAt: String
)


data class User(
    var nickname: String,
    var profileImage: String,
    var email: String,
    var role: String,
    var gender: String,
    var point: Long,
    var createdAt: String
)

data class UserModify(
    var nickname: String?,
    var profileImage: String
)

data class UserAuto(
    var userId: Int
)