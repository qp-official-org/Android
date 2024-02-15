package com.example.qp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.qp.databinding.ActivitySetprofileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivitySetprofileBinding

    // 뒤로가기 버튼 눌렀을 때 발동되는 함수
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetprofileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SetNicknameActivity에서 넘겨준 객체를 받기 위함
        val intent = intent
        val qpUserData = intent.getSerializableExtra("data", QpUserData::class.java)

        // 프로필 설정 페이지와 회원가입절차 종료 페이지 구분
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)    // 종료함수
        binding.profileBackIv.setOnClickListener {
            if(binding.profileMainTv.visibility == View.GONE) {
                binding.profileMainTv.visibility = View.VISIBLE
                binding.profileImageIv.visibility = View.VISIBLE
                binding.profileImageEditIv.visibility = View.VISIBLE
                binding.profileNextBtnIv.visibility = View.VISIBLE
                binding.profileBtnTv.visibility = View.VISIBLE
                binding.profileWelcomeTv.visibility = View.GONE
                binding.profileExitBtnTv.visibility = View.GONE
                binding.profileExitBtnIv.visibility = View.GONE
            }
            else {
                finish()
            }
        }

        // 회원가입절차 종료 페이지로 전환
        binding.profileNextBtnIv.setOnClickListener {
            binding.profileMainTv.visibility = View.GONE
            binding.profileImageIv.visibility = View.GONE
            binding.profileImageEditIv.visibility = View.GONE
            binding.profileNextBtnIv.visibility = View.GONE
            binding.profileBtnTv.visibility = View.GONE
            binding.profileWelcomeTv.visibility = View.VISIBLE
            binding.profileExitBtnTv.visibility = View.VISIBLE
            binding.profileExitBtnIv.visibility = View.VISIBLE
        }

        // 회원가입절차 종료, 메인 페이지로 복귀
        binding.profileExitBtnIv.setOnClickListener {
            Toast.makeText(this, "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("data", qpUserData)
            startActivity(intent)
            finishAffinity()    // 쌓인 모든 Activity 종료
        }

        // 임시 유저정보조회 설정
        binding.profileQpLogoTv.setOnClickListener {
            if (qpUserData != null) {
                searchUserInfo(qpUserData.accessToken,qpUserData.userId)
            }
            else {
                Log.d("errorMessage", "search qpUserData is Null")
            }
        }

    }

    private fun searchUserInfo(token: String, userId: Int) {
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