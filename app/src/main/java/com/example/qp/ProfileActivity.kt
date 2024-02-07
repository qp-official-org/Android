package com.example.qp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.qp.databinding.ActivityProfileBinding
import com.google.android.material.tabs.TabLayoutMediator

class ProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileBinding

    private val information = arrayListOf("내가 한 질문", "내가 구매한 답변", "알림신청한 질문")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val profileVPAdapter = ProfileVPAdapter(this)
        binding.profileQuestionVp.adapter = profileVPAdapter

        TabLayoutMediator(binding.profileQuestionTb,binding.profileQuestionVp){
            tab, position ->
            tab.text = information[position]
        }.attach()

        binding.mainSearchBt.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("isLogin", 1)
            startActivity(intent)
            finish()
        }


        binding.profileChargekBtn.setOnClickListener {
            binding.profileChargekBtn.setBackgroundResource(R.drawable.box_orange)
            binding.profileChargekNaverBtn.visibility = View.VISIBLE
            binding.profileChargekKakaoBtn.visibility = View.VISIBLE
        }

        binding.profileChargekNaverBtn.setOnClickListener {
            binding.profileChargekNaverBtn.setBackgroundResource(R.drawable.box_orange)
        }


        binding.profileMainEditBtn.setOnClickListener {
            binding.profileEditSettingIv.visibility = View.VISIBLE
            binding.profileMainTv.visibility = View.GONE
            binding.profileEditNicknameEt.visibility = View.VISIBLE
            binding.profileMainEditBtn.visibility = View.GONE
            binding.profileEditYesBtn.visibility = View.VISIBLE
            binding.profileEditNoBtn.visibility = View.VISIBLE
            binding.profileMainImageIv.setAlpha(0.7f)
        }

        binding.profileEditYesBtn.setOnClickListener {
            val editname : String = binding.profileEditNicknameEt.text.toString()
            if(editname == null || editname.isEmpty())
            else {
                binding.profileMainTv.text = editname
                binding.profileEditNicknameEt.hint = editname
            }
            binding.profileEditSettingIv.visibility = View.GONE
            binding.profileMainTv.visibility = View.VISIBLE
            binding.profileEditNicknameEt.visibility = View.GONE
            binding.profileMainEditBtn.visibility = View.VISIBLE
            binding.profileEditYesBtn.visibility = View.GONE
            binding.profileEditNoBtn.visibility = View.GONE
            binding.profileMainImageIv.setAlpha(1f)
        }
        binding.profileEditNoBtn.setOnClickListener {
            binding.profileEditSettingIv.visibility = View.GONE
            binding.profileMainTv.visibility = View.VISIBLE
            binding.profileEditNicknameEt.visibility = View.GONE
            binding.profileMainEditBtn.visibility = View.VISIBLE
            binding.profileEditYesBtn.visibility = View.GONE
            binding.profileEditNoBtn.visibility = View.GONE
            binding.profileMainImageIv.setAlpha(1f)
        }
    }
}