package com.example.qp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.qp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginCloseIv.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.loginExpert2Tv.setOnClickListener {
            binding.loginAuthenticationEmailEt.visibility = View.VISIBLE
            binding.loginAuthenticationFailTv.visibility = View.VISIBLE
            binding.loginBtnIv.visibility = View.VISIBLE
            binding.loginBtnTv.visibility = View.VISIBLE
        }

        binding.loginBtnIv.setOnClickListener {
            startActivity(Intent(this,NicknameActivity::class.java))
        }
    }
}