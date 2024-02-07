package com.example.qp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginAuthenticationEmailEt.visibility = View.GONE
        binding.loginNextBtn.visibility = View.GONE
        binding.loginNextInvalidBtn.visibility = View.GONE
        binding.loginAuthenticationFailTv.visibility = View.INVISIBLE

        binding.loginCloseIv.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.loginExpert2Tv.setOnClickListener {
            binding.loginAuthenticationEmailEt.visibility = View.VISIBLE
            binding.loginAuthenticationFailTv.visibility = View.VISIBLE
            binding.loginNextInvalidBtn.visibility = View.VISIBLE
        }


        // 여기부터 전문가 로그인

        binding.loginAuthenticationEmailEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                binding.loginAuthenticationFailTv.visibility = View.INVISIBLE
                if(binding.loginAuthenticationEmailEt.length() == 8) {
                    binding.loginNextBtn.visibility = View.VISIBLE
                    binding.loginNextInvalidBtn.visibility = View.GONE
                }
                else{
                    binding.loginNextBtn.visibility = View.GONE
                    binding.loginNextInvalidBtn.visibility = View.VISIBLE
                }
            }

        })

        binding.loginNextBtn.setOnClickListener {
            startActivity(Intent(this,SetNicknameActivity::class.java))
        }

        binding.loginNextInvalidBtn.setOnClickListener {
            binding.loginAuthenticationFailTv.visibility = View.VISIBLE
        }


        binding.loginNaverBtn.setOnClickListener {
            Toast.makeText(this, "네이버 로그인", Toast.LENGTH_SHORT).show()
        }

        binding.loginGoogleBtn.setOnClickListener {
            Toast.makeText(this, "구글 로그인", Toast.LENGTH_SHORT).show()
        }

        binding.loginKakaoBtn.setOnClickListener {
            Toast.makeText(this, "카카오 로그인", Toast.LENGTH_SHORT).show()
        }
    }
}