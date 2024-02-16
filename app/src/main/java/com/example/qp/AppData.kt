package com.example.qp

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppData : Application() {
    companion object {
        //전역 변수 정의
        var qpAccessToken = ""
        var qpUserID = 0

        var qpNickname = ""
        var qpProfileImage = ""
        var qpEmail = ""
        var qpGender = ""
        var qpPoint = 0

        // 유저 정보 수정 함수
        fun modifyUserInfo(token: String, userId: Int, userModify: UserModify) {
            val userService = getRetrofit().create(UserInterface::class.java)

            userService.modifyUserInfo(token, userId, userModify).enqueue(object:
                Callback<UserResponse<UserModifyResult>> {
                override fun onResponse(
                    call: Call<UserResponse<UserModifyResult>>,
                    response: Response<UserResponse<UserModifyResult>>
                ) {
                    Log.d("modify Success", response.toString())
                    val resp = response.body()
                    if(resp!=null) {
                        when(resp.code) {
                            "USER_1000"-> {
                                Log.d("modify Result1", resp.message)
                                Log.d("modify data1", resp.result.userId.toString())
                                Log.d("modify data2", resp.result.nickname)
                                Log.d("modify data3", resp.result.profileImage)
                                Log.d("modify data4", resp.result.updatedAt)

                                AppData.qpNickname = resp.result.nickname
                                AppData.qpProfileImage = resp.result.profileImage
                            }
                            else-> Log.d("modify Result2", resp.message)
                        }
                    }
                }

                override fun onFailure(call: Call<UserResponse<UserModifyResult>>, t: Throwable) {
                    Log.d("modify Fail", t.message.toString())
                }

            })
        }

        // 유저 정보 조회 함수
        fun searchUserInfo(token: String, userId: Int) {
            val userService = getRetrofit().create(UserInterface::class.java)

            userService.searchUserInfo(token, userId).enqueue(object :Callback<UserResponse<User>> {
                override fun onResponse(call: Call<UserResponse<User>>, response: Response<UserResponse<User>>) {
                    Log.d("ssearch Success", response.toString())
                    val resp = response.body()
                    if(resp!=null) {
                        when(resp.code) {
                            "USER_1000"-> {
                                Log.d("ssearch Result1", resp.message)
                                Log.d("ssearch Data1", resp.result.nickname)
                                Log.d("ssearch Data2", resp.result.profileImage)
                                Log.d("ssearch Data3", resp.result.email)
                                Log.d("ssearch Data4", resp.result.gender)
                                Log.d("ssearch Data5", resp.result.point.toString())
                            }
                            else-> Log.d("ssearch Result2", resp.message)
                        }
                    }
                }

                override fun onFailure(call: Call<UserResponse<User>>, t: Throwable) {
                    Log.d("ssearch Fail", t.message.toString())
                }
            })
        }
    }
}