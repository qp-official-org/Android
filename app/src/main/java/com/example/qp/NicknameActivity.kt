package com.example.qp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.qp.databinding.ActivityNicknameBinding

class NicknameActivity : AppCompatActivity() {
    lateinit var binding: ActivityNicknameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNicknameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nicknameBackIv.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.nicknameInvalidNextBtnIv.setOnClickListener {
            binding.nicknameInvalidNextBtnIv.visibility = View.GONE
            binding.loginInvalidBtnTv.visibility = View.GONE
            binding.nicknameNextBtnIv.visibility = View.VISIBLE
            binding.loginBtnTv.visibility = View.VISIBLE
        }

        binding.nicknameNextBtnIv.setOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java))
        }
    }
}