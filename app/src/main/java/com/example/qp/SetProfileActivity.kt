package com.example.qp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.qp.databinding.ActivitySetprofileBinding

class SetProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivitySetprofileBinding

    // 뒤로가기 버튼 눌렀을 때 발동되는 함수
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetprofileBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                binding.profileWelcomeIv.visibility = View.GONE
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
            binding.profileWelcomeIv.visibility = View.VISIBLE
        }

        // 회원가입절차 종료, 메인 페이지로 복귀
        binding.profileExitBtnIv.setOnClickListener {
            Toast.makeText(this, "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show()

            AppData.qpIsLogin = true
            AppData.isGoHome = true

            finish()
        }
    }
}