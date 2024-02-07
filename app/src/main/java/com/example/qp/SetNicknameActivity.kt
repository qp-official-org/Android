package com.example.qp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qp.databinding.ActivitySetnicknameBinding

class SetNicknameActivity : AppCompatActivity() {
    lateinit var binding: ActivitySetnicknameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetnicknameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nicknameBackIv.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
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

        binding.nicknameNextBtn.setOnClickListener {
            var userName : String = binding.nicknameInputEt.text.toString()
            Toast.makeText(this, userName, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,SetProfileActivity::class.java))
        }

        binding.nicknameNextInvalidBtn.setOnClickListener{
            binding.nicknameInvalidTv.visibility = View.VISIBLE
        }
    }
}