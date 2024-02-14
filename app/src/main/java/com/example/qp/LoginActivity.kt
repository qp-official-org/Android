package com.example.qp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qp.databinding.ActivityLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var qpAccessToken : String     // 카카오에서 발급받은 것과 다른 자체 Token

    val kakaoCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Toast.makeText(this, "카카오계정으로 로그인 실패 $error", Toast.LENGTH_SHORT).show()
        } else if (token != null) {
            Log.i("Login TAG", "카카오계정으로 로그인 성공 ${token.accessToken}")
            Toast.makeText(this, "카카오계정으로 로그인 성공 ${token.accessToken}", Toast.LENGTH_SHORT).show()

            signUp(token.accessToken, this)
        }
    }

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
            finish()
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
            qpAccessToken = ""
            Toast.makeText(this, "카카오 로그인", Toast.LENGTH_SHORT).show()

            UserApiClient.instance.loginWithKakaoAccount(this, callback = kakaoCallback)
        }
    }

    private fun signUp(token: String, context: Context) {
        val userService = getRetrofit().create(UserInterface::class.java)

        userService.signUp(token).enqueue(object: Callback<UserResponse>{
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                Log.d("singUp Success", response.toString())
                val resp = response.body()
                if(resp!=null){
                    when(resp.code){
                        "USER_1000"-> {
                            Log.d("singUp Result", resp.message)
                            qpAccessToken = resp.result.accessToken
                            startActivity(Intent(context, SetNicknameActivity::class.java))
                        }
                        else->Log.d("singUp Result", resp.message)
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.d("singUp Fail", t.message.toString())
            }
        })
    }
}