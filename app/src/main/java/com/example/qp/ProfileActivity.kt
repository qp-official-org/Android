package com.example.qp

import android.content.Intent
import android.graphics.Color
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
            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra("isLogin", 1)
            startActivity(intent)
            finish()
        }

        // 충전 버튼 색상 변경
        binding.profileChargekBtn.setOnClickListener {
            binding.profileChargekBtn.setBackgroundResource(R.drawable.box_orange)
            binding.profileChargekTv1.setTextColor(Color.WHITE)
            binding.profileChargekTv2.setTextColor(Color.WHITE)
            binding.profileChargekNaverBtn.visibility = View.VISIBLE
            binding.profileChargekKakaoBtn.visibility = View.VISIBLE

            binding.profileCharge10kBtn.setBackgroundResource(R.drawable.box_white_lined)
            binding.profileCharge10kTv1.setTextColor(Color.BLACK)
            binding.profileCharge10kTv2.setTextColor(Color.BLACK)
            binding.profileCharge10kNaverBtn.setBackgroundResource(R.drawable.box_white_lined)
            binding.profileCharge10kNaverBtn.visibility = View.GONE
            binding.profileCharge10kNaverTv.setTextColor(Color.BLACK)
            binding.profileCharge10kKakaoBtn.setBackgroundResource(R.drawable.box_white_lined)
            binding.profileCharge10kKakaoBtn.visibility = View.GONE
            binding.profileCharge10kKakaoTv.setTextColor(Color.BLACK)
        }
        binding.profileChargekNaverBtn.setOnClickListener {
            binding.profileChargekNaverBtn.setBackgroundResource(R.drawable.box_orange)
            binding.profileChargekNaverTv.setTextColor(Color.WHITE)
            binding.profileChargekKakaoBtn.setBackgroundResource(R.drawable.box_white_lined)
            binding.profileChargekKakaoTv.setTextColor(Color.BLACK)
        }
        binding.profileChargekKakaoBtn.setOnClickListener {
            binding.profileChargekNaverBtn.setBackgroundResource(R.drawable.box_white_lined)
            binding.profileChargekNaverTv.setTextColor(Color.BLACK)
            binding.profileChargekKakaoBtn.setBackgroundResource(R.drawable.box_orange)
            binding.profileChargekKakaoTv.setTextColor(Color.WHITE)
        }

        binding.profileCharge10kBtn.setOnClickListener {
            binding.profileCharge10kBtn.setBackgroundResource(R.drawable.box_orange)
            binding.profileCharge10kTv1.setTextColor(Color.WHITE)
            binding.profileCharge10kTv2.setTextColor(Color.WHITE)
            binding.profileCharge10kNaverBtn.visibility = View.VISIBLE
            binding.profileCharge10kKakaoBtn.visibility = View.VISIBLE

            binding.profileChargekBtn.setBackgroundResource(R.drawable.box_white_lined)
            binding.profileChargekTv1.setTextColor(Color.BLACK)
            binding.profileChargekTv2.setTextColor(Color.BLACK)
            binding.profileChargekNaverBtn.setBackgroundResource(R.drawable.box_white_lined)
            binding.profileChargekNaverBtn.visibility = View.GONE
            binding.profileChargekNaverTv.setTextColor(Color.BLACK)
            binding.profileChargekKakaoBtn.setBackgroundResource(R.drawable.box_white_lined)
            binding.profileChargekKakaoBtn.visibility = View.GONE
            binding.profileChargekKakaoTv.setTextColor(Color.BLACK)
        }
        binding.profileCharge10kNaverBtn.setOnClickListener {
            binding.profileCharge10kNaverBtn.setBackgroundResource(R.drawable.box_orange)
            binding.profileCharge10kNaverTv.setTextColor(Color.WHITE)
            binding.profileCharge10kKakaoBtn.setBackgroundResource(R.drawable.box_white_lined)
            binding.profileCharge10kKakaoTv.setTextColor(Color.BLACK)
        }
        binding.profileCharge10kKakaoBtn.setOnClickListener {
            binding.profileCharge10kNaverBtn.setBackgroundResource(R.drawable.box_white_lined)
            binding.profileCharge10kNaverTv.setTextColor(Color.BLACK)
            binding.profileCharge10kKakaoBtn.setBackgroundResource(R.drawable.box_orange)
            binding.profileCharge10kKakaoTv.setTextColor(Color.WHITE)
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