package com.example.qp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.qp.databinding.ActivityProfileBinding
//import com.example.qp.databinding.DialogChargeBinding
import com.google.android.material.tabs.TabLayoutMediator
import org.w3c.dom.Text

class ProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileBinding
//    lateinit var binding2: DialogChargeBinding

    private val information = arrayListOf("내가 한 질문", "내가 구매한 답변", "알림신청한 질문")

    // 뒤로가기 버튼 눌렀을 때 발동되는 함수
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
//        binding2 = DialogChargeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)    // 종료함수

        val profileVPAdapter = ProfileVPAdapter(this)
        binding.profileQuestionVp.adapter = profileVPAdapter

        TabLayoutMediator(binding.profileQuestionTb,binding.profileQuestionVp){
            tab, position ->
            tab.text = information[position]
        }.attach()

        binding.mainSearchBt.setOnClickListener{
            startActivity(Intent(this, SearchActivity::class.java))
        }

        // 유저 데이터 반영
        binding.profileMainTv.text = AppData.qpNickname
        binding.profileMainCoinNumTv.text = AppData.qpPoint.toString()
        binding.profileMainCoinTextTv.text = (AppData.qpPoint / 10).toString()
        //binding.profileMainDateTv.text

        // 충전 버튼 색상 변경 및 Dialog 등장
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
            //madeDialog(1000, "네이버 페이")
        }
        binding.profileChargekKakaoBtn.setOnClickListener {
            binding.profileChargekNaverBtn.setBackgroundResource(R.drawable.box_white_lined)
            binding.profileChargekNaverTv.setTextColor(Color.BLACK)
            binding.profileChargekKakaoBtn.setBackgroundResource(R.drawable.box_orange)
            binding.profileChargekKakaoTv.setTextColor(Color.WHITE)
            //madeDialog(1000, "카카오 페이")
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
            //madeDialog(10000, "네이버 페이")
        }
        binding.profileCharge10kKakaoBtn.setOnClickListener {
            binding.profileCharge10kNaverBtn.setBackgroundResource(R.drawable.box_white_lined)
            binding.profileCharge10kNaverTv.setTextColor(Color.BLACK)
            binding.profileCharge10kKakaoBtn.setBackgroundResource(R.drawable.box_orange)
            binding.profileCharge10kKakaoTv.setTextColor(Color.WHITE)
            //madeDialog(10000, "카카오 페이")
        }


        // 프로필 수정버튼
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


        binding.profileQpLogo.setOnClickListener {
            Log.d("proffile Data1", AppData.qpAccessToken)
            Log.d("proffile Data2", AppData.qpUserID.toString())
        }
    }

//    // Dialog 호출 함수 (개발중)
//    fun madeDialog(num : Int, str : String) {
//        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_charge, null)
//
//        binding2.chargeText1Tv.text = "선택하신 금액 '${num}원'을"
//        binding2.chargeText2Tv.text = "'${str}'로"
//
//        val mBuilder = AlertDialog.Builder(this)
//            .setView(mDialogView)
//
//        mBuilder.show()
//
//        binding2.chargeYesBtn.setOnClickListener {
//            binding2.chargeText1Tv.visibility = View.GONE
//            binding2.chargeText2Tv.visibility = View.GONE
//            binding2.chargeText3Tv.visibility = View.GONE
//            binding2.chargeYesBtn.visibility = View.GONE
//            binding2.chargeNoBtn.visibility = View.GONE
//
//            binding2.chargeText4Tv.visibility = View.VISIBLE
//            binding2.chargeText5Tv.visibility = View.VISIBLE
//            binding2.chargeText6Tv.visibility = View.VISIBLE
//            binding2.chargeText7Tv.visibility = View.VISIBLE
//            binding2.chargeNextBtn.visibility = View.VISIBLE
//
//            binding2.chargeText5Tv.text = "현재 '큐피'님의 잔여 포인트는"
//            val pnum : Int = num+800
//            binding2.chargeText6Tv.text = "${pnum} point입니다."
//            val pque : Int = pnum/10
//            binding2.chargeText7Tv.text = "${pque}개의 답변을 확인할 수 있어요!"
//
//            binding2.chargeNextBtn.setOnClickListener {
//                mBuilder.show().dismiss()
//
//                binding.profileMainCoinNumTv.text = "$pnum"
//            }
//        }
//        binding2.chargeNoBtn.setOnClickListener {
//            mBuilder.show().dismiss()
//        }
//    }
}