package com.example.qp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.qp.databinding.ActivitySetnicknameBinding
import com.kakao.sdk.user.UserApiClient

class SetNicknameActivity : AppCompatActivity() {
    lateinit var binding: ActivitySetnicknameBinding

    // 뒤로가기 버튼 눌렀을 때 발동되는 함수
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetnicknameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 이전버튼 누르면 로그인 취소되고 로그인 화면으로 돌아감
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)    // 종료함수
        binding.nicknameBackIv.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Toast.makeText(this, "로그아웃 실패 $error", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                }
            }

            finish()
        }

        // editText 입력마다 체크
        binding.nicknameInputEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if(binding.nicknameInputEt.length() in 1..6) {
                    binding.nicknameNextBtn.visibility = View.VISIBLE
                    binding.nicknameNextInvalidBtn.visibility = View.GONE
                    binding.nicknameValidTv.visibility = View.VISIBLE
                    binding.nicknameInvalidTv.visibility = View.INVISIBLE
                }
                else {
                    binding.nicknameNextBtn.visibility = View.GONE
                    binding.nicknameNextInvalidBtn.visibility = View.VISIBLE
                    binding.nicknameValidTv.visibility = View.INVISIBLE
                }
            }

        })

        // 닉네임 입력 완료 후 다음버튼 누르면 데이터 수정됨
        binding.nicknameNextBtn.setOnClickListener {
            var userName : String = binding.nicknameInputEt.text.toString()
            Toast.makeText(this, userName, Toast.LENGTH_SHORT).show()

            val userModify = UserModify(userName, "")
            AppData.modifyUserInfo(AppData.qpAccessToken, AppData.qpUserID, userModify)
            startActivity(Intent(this, SetProfileActivity::class.java))
        }

        binding.nicknameNextInvalidBtn.setOnClickListener{
            binding.nicknameInvalidTv.visibility = View.VISIBLE
        }
    }
}