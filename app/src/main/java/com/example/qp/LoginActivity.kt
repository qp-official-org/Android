package com.example.qp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.qp.databinding.ActivityLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    // 뒤로가기 버튼 눌렀을 때 발동되는 함수
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 전문가 로그인 관련 버튼 비활성화
        binding.loginAuthenticationEmailEt.visibility = View.GONE
        binding.loginNextBtn.visibility = View.GONE
        binding.loginNextInvalidBtn.visibility = View.GONE
        binding.loginAuthenticationFailTv.visibility = View.INVISIBLE

        // X 버튼 클릭 시 Activity Main으로 복귀 후 Activity 종료
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)    // 종료함수
        binding.loginCloseIv.setOnClickListener {
            finish()
        }

        // 전문가로 로그인 텍스트 클릭 시 관련 버튼 활성화
        binding.loginExpert2Tv.setOnClickListener {
            binding.loginAuthenticationEmailEt.visibility = View.VISIBLE
            binding.loginAuthenticationFailTv.visibility = View.VISIBLE
            binding.loginNextInvalidBtn.visibility = View.VISIBLE
        }


        // 전문가 로그인
        binding.loginAuthenticationEmailEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
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
        // 인증번호 맞는 경우, 다음 Activity 전환
        binding.loginNextBtn.setOnClickListener {
            startActivity(Intent(this,SetNicknameActivity::class.java))
        }
        // 인증번호 틀린 경우
        binding.loginNextInvalidBtn.setOnClickListener {
            binding.loginAuthenticationFailTv.visibility = View.VISIBLE
        }


        // 소셜 로그인 (현재 카카오만 가능)
        binding.loginNaverBtn.setOnClickListener {
            Toast.makeText(this, "네이버 로그인", Toast.LENGTH_SHORT).show()
        }
        binding.loginGoogleBtn.setOnClickListener {
            Toast.makeText(this, "구글 로그인", Toast.LENGTH_SHORT).show()
        }
        binding.loginKakaoBtn.setOnClickListener {
            Toast.makeText(this, "카카오 로그인", Toast.LENGTH_SHORT).show()

            UserApiClient.instance.loginWithKakaoAccount(this, callback = kakaoCallback)
        }
    }

    // 카카오 콜백 함수 (로그인 동작 관련)
    val kakaoCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Toast.makeText(this, "카카오계정으로 로그인 실패 $error", Toast.LENGTH_SHORT).show()
        } else if (token != null) {
            Log.i("Login TAG", "카카오계정으로 로그인 성공 ${token.accessToken}")

            signUp(token.accessToken)
        }
    }

    // 회원가입 함수
    private fun signUp(token: String) {
        val userService = getRetrofit().create(UserInterface::class.java)

        userService.signUp(token).enqueue(object: Callback<UserResponse<UserToken>>{
            override fun onResponse(call: Call<UserResponse<UserToken>>, response: Response<UserResponse<UserToken>>) {
                Log.d("singUp Success", response.toString())
                val resp = response.body()
                if(resp!=null){
                    when(resp.code){
                        "USER_1000"-> {
                            Log.d("singUp Result", resp.message)

                            // accessToken, refreshToken, userID 데이터를 로컬에 저장
                            GlobalApplication.preferences.setString("accessToken", resp.result.accessToken)
                            GlobalApplication.preferences.setString("refreshToken", resp.result.refreshToken)
                            GlobalApplication.preferences.setInt("userID", resp.result.userId)

                            Log.d("sharedpp5", GlobalApplication.preferences.getString("accessToken", ""))
                            Log.d("sharedpp6", GlobalApplication.preferences.getString("refreshToken", ""))
                            Log.d("sharedpp7", GlobalApplication.preferences.getInt("userID", 0).toString())

                            // 전역변수 사용
                            AppData.qpAccessToken = resp.result.accessToken
                            AppData.qpUserID = resp.result.userId
                            AppData.searchRecord.clear()

                            Log.d("isNew", resp.result.isNew.toString())
                            if(resp.result.isNew) {     // 새로 가입한 계정
                                startActivity(Intent(this@LoginActivity, SetNicknameActivity::class.java))
                            }
                            else {      // 기존에 존재하던 계정
                                Toast.makeText(this@LoginActivity, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show()
                                AppData.qpIsLogin = true
                                finish()
                            }
                        }
                        else->Log.d("singUp Result", resp.message)
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse<UserToken>>, t: Throwable) {
                Log.d("singUp Fail", t.message.toString())
            }
        })
    }
}