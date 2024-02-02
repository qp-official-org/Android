package com.example.qp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.qp.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                startActivity(Intent(this, NicknameActivity::class.java))
            }
        }

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

        binding.profileExitBtnIv.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
    }

}