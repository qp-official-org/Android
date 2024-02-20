package com.example.qp

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

        // X 버튼 클릭 시 Activity 종료
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)    // 종료함수
        binding.loginCloseIv.setOnClickListener {
            finish()
        }

        // 소셜 로그인 (현재 카카오만 가능)
        binding.loginNaverBtn.setOnClickListener {
            Toast.makeText(this, "네이버 로그인 (지원 X)", Toast.LENGTH_SHORT).show()
        }
        binding.loginGoogleBtn.setOnClickListener {
            Toast.makeText(this, "구글 로그인 (지원 X)", Toast.LENGTH_SHORT).show()
        }
        binding.loginKakaoBtn.setOnClickListener {
            Toast.makeText(this, "카카오 로그인", Toast.LENGTH_SHORT).show()

            UserApiClient.instance.loginWithKakaoAccount(this, callback = kakaoCallback)
        }
    }

    override fun onRestart() {
        if(AppData.isGoHome)    finish()

        super.onRestart()
    }

    // 카카오 콜백 함수 (로그인 동작 관련)
    val kakaoCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Toast.makeText(this, "카카오계정으로 로그인 실패 $error", Toast.LENGTH_SHORT).show()
        } else if (token != null) {
            Log.i("Login TAG", "카카오계정으로 로그인 성공 ${token.accessToken}")

            signUp(token.accessToken)
            GlobalApplication.preferences.setString("kakaoToken", token.accessToken)
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

                            // 전역변수 사용
                            AppData.qpAccessToken = resp.result.accessToken
                            AppData.qpUserID = resp.result.userId
                            AppData.searchRecord.clear()

                            if(resp.result.isNew) {     // 새로 가입한 계정
                                startActivity(Intent(this@LoginActivity, SetNicknameActivity::class.java))
                            }
                            else {      // 기존에 존재하던 계정
                                Toast.makeText(this@LoginActivity, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show()
                                AppData.qpIsLogin = true
                                finish()
                            }

                            // 회원가입 테스트 시 위의 if문 주석처리하고 사용
                            //startActivity(Intent(this@LoginActivity, SetNicknameActivity::class.java))
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