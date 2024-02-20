package com.example.qp

import android.content.Intent
import android.graphics.Paint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.qp.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.kakao.sdk.auth.AuthApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.kakao.sdk.user.UserApiClient

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var qDatas = ArrayList<QuestionInfo>()
    private var page = 0
    private var last = false

    // 뒤로가기 버튼 눌렀을 때 발동되는 함수 (두번 눌러야 종료)
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        var isExit : Boolean = false
        override fun handleOnBackPressed() {
            if(isExit) {
                ActivityCompat.finishAffinity(this@MainActivity)
                System.runFinalization()
                System.exit(0)
            }
            else {
                Toast.makeText(this@MainActivity, "종료하려면 뒤로가기를 한 번 더 누르세요.", Toast.LENGTH_SHORT).show()
            }

            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                isExit = true
            }, 300)

            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                isExit = false
            }, 3000)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 로그인 여부 확인
        UserApiClient.instance.accessTokenInfo { token, error ->
            Log.d("KakaoToken", AuthApiClient.instance.hasToken().toString())
            if (error != null) {
                Log.e("TTAG", "카카오 로그인 실패", error)
                binding.mainLoginBt.visibility = View.VISIBLE
                binding.mainLoginSuccessBt.visibility = View.GONE
                binding.mainLoginSuccessUserImg.visibility = View.GONE
            } else if (token != null) {
                Log.i("TTAG", "카카오 로그인 성공 $token")

                // sharedPreference에 저장된 로컬 데이터 불러오기
                AppData.qpAccessToken = GlobalApplication.preferences.getString("accessToken", "")
                AppData.qpUserID = GlobalApplication.preferences.getInt("userID", 0)

                // 큐피 로그인 시도
                signIn(GlobalApplication.preferences.getString("kakaoToken", ""))

                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    if(AppData.qpIsLogin) {     //
                        autoSignIn(AppData.qpAccessToken, GlobalApplication.preferences.getString("refreshToken", ""), AppData.qpUserID)
                        AppData.searchUserInfo(AppData.qpAccessToken, AppData.qpUserID)

                        binding.mainLoginBt.visibility = View.GONE
                        binding.mainLoginSuccessBt.visibility = View.VISIBLE
                        binding.mainLoginSuccessUserImg.visibility = View.VISIBLE

                        Handler(Looper.getMainLooper()).postDelayed(Runnable {
                            // 하단 바에 사용자 닉네임과 포인트 데이터, 프로필 사진 반영
                            binding.mainBarNicknameTv.text = AppData.qpNickname
                            binding.mainBarCoinTv.text = AppData.qpPoint.toString()
                            Glide.with(this).load(AppData.qpProfileImage).into(binding.mainLoginSuccessUserImg)
                        }, 300)
                    }
                }, 300)
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)    // 종료함수
        
        //백엔드로부터 질문 정보를 가져와 리사이클러뷰를 구성하는 함수
        getQuestions(page)

        binding.mainBarLogoutTv.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        // 로그아웃 버튼
        binding.mainBarLogoutTv.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.d("TTAG", "로그아웃 실패 $error")
                }else {
                    Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                    // 전역변수 초기화
                    AppData.qpAccessToken = ""
                    AppData.qpUserID = 0
                    AppData.qpNickname = ""
                    AppData.qpProfileImage = ""
                    AppData.qpEmail = ""
                    AppData.qpGender = ""
                    AppData.qpRole = ""
                    AppData.qpPoint = 0
                    AppData.qpCreatedAt = ""
                    AppData.searchRecord.clear()

                    binding.mainLoginBt.visibility = View.VISIBLE
                    binding.mainLoginSuccessBt.visibility = View.GONE
                    binding.mainLoginSuccessUserImg.visibility = View.GONE

                    AppData.qpIsLogin = false
                }
            }
        }

        //상단 검색바 클릭 시 검색화면으로 화면 전환
        binding.mainSearchBt.setOnClickListener {
            startActivity(Intent(this@MainActivity, SearchActivity::class.java))
        }

        // 로그인 화면으로 이동
        binding.mainLoginBt.setOnClickListener{
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }

        // 프로필 화면으로 이동
        binding.mainLoginSuccessBt.setOnClickListener{
            startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
        }
        moreQuestion()
    }

    // 다른 페이지로 갔다가 돌아올 때 동작
    override fun onRestart() {
        super.onRestart()
        Log.d("onRestart", "onRestart")

        AppData.isGoHome = false

        page = 0
        qDatas.clear()
        getQuestions(page)
        moreQuestion()

        if(AppData.qpIsLogin) {
            AppData.searchUserInfo(AppData.qpAccessToken, AppData.qpUserID)

            binding.mainLoginBt.visibility = View.GONE
            binding.mainLoginSuccessBt.visibility = View.VISIBLE
            binding.mainLoginSuccessUserImg.visibility = View.VISIBLE

            // 통신 대기 시간 0.3초
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                // 하단 바에 사용자 닉네임과 포인트 데이터 반영
                binding.mainBarNicknameTv.text = AppData.qpNickname
                binding.mainBarCoinTv.text = AppData.qpPoint.toString()
            }, 300)
        }
        else {
            binding.mainLoginBt.visibility = View.VISIBLE
            binding.mainLoginSuccessBt.visibility = View.GONE
            binding.mainLoginSuccessUserImg.visibility = View.GONE
        }
    }

    private fun getQuestions(p: Int) {
        val questionService = getRetrofit().create(QuestionInterface::class.java)

        questionService.getQuestions(p, 10, null)
            .enqueue(object: Callback<QuestionResponse>{
                override fun onResponse(
                    call: Call<QuestionResponse>,
                    response: Response<QuestionResponse>
                ) {
                    if(response.isSuccessful && response.code() == 200){
                        val questionResponse: QuestionResponse = response.body()!!

                        Log.d("Q-RESPONSE/SUCCESS", questionResponse.toString())

                        when(questionResponse.code){
                            "QUESTION_2000" -> {
                                Log.d("SUCCESS/DATA_LOAD", "리사이클러뷰의 데이터로 구성됩니다")
                                qDatas.addAll(questionResponse.result.questions)
                                last = questionResponse.result.last!!
                                setQuestionRVAdapter()
                                Log.d("getQsResp",qDatas.toString())
                            }
                            else -> {
                                Log.d("SUCCESS/DATA_FAILURE", "응답 코드 오류입니다")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<QuestionResponse>, t: Throwable) {
                    Log.d("Q-RESPONSE/FAILURE", t.message.toString())
                }

            })
    }

    //마지막 질문일 때 추가 데이터 조회
    private fun moreQuestion(){
        binding.mainQuestionRv.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val rvPosition = (recyclerView.layoutManager as GridLayoutManager)?.findLastCompletelyVisibleItemPosition()
                val totalCount = binding.mainQuestionRv.adapter!!.itemCount - 1
                if (rvPosition==totalCount && !last) {
                    page++
                    getQuestions(page)
                }
            }
        })
    }

    private fun setQuestionRVAdapter(){
        val questionRVAdapter = QuestionRVAdapter(qDatas)
        if(page == 0){
            binding.mainQuestionRv.adapter = questionRVAdapter
            binding.mainQuestionRv.layoutManager = GridLayoutManager(applicationContext, 2)
        }
        else{
            binding.mainQuestionRv.adapter!!.notifyDataSetChanged()
        }

        //특정 질문 클릭 시 질문상세화면으로 전환
        questionRVAdapter.setMyItemClickListner(object : QuestionRVAdapter.MyItemClickListner{
            override fun onItemClick(questionInfo: QuestionInfo) {
                val intent = Intent(this@MainActivity, DetailedActivity::class.java)
                val gson = Gson()
                val qJson = gson.toJson(questionInfo)
                intent.putExtra("question", qJson)
                startActivity(intent)
            }
        })
    }

    // 자동로그인 함수
    private fun autoSignIn(accessToken: String, refreshToken: String, userID: Int) {
        val userService = getRetrofit().create(UserInterface::class.java)

        var userAuto = UserAuto(userID)
        userService.autoSignIn(accessToken, refreshToken, userAuto).enqueue(object: Callback<UserResponse<AutoSignIn>>{
            override fun onResponse(
                call: Call<UserResponse<AutoSignIn>>,
                response: Response<UserResponse<AutoSignIn>>
            ) {
                Log.d("autoSingUp Success", response.toString())
                val resp = response.body()
                if(resp!=null){
                    when(resp.code){
                        "USER_1000"-> {
                            Log.d("autoSingUp Result1", resp.message)
                            // accessToken, refreshToken, userID 데이터를 로컬에 저장
                            GlobalApplication.preferences.setString("accessToken", resp.result.accessToken)
                            GlobalApplication.preferences.setString("refreshToken", resp.result.refreshToken)
                            GlobalApplication.preferences.setInt("userID", resp.result.userId)
                            AppData.qpAccessToken = resp.result.accessToken
                            AppData.qpUserID = resp.result.userId
                        }
                        "TOKEN_8001"-> {
                            Toast.makeText(this@MainActivity, "토큰이 만료되었습니다. 다시 로그인하기 바랍니다.", Toast.LENGTH_SHORT).show()
                        }
                        else->Log.d("autoSingUp Result2", resp.message)
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse<AutoSignIn>>, t: Throwable) {
                Log.d("autoSingUp Fail", t.message.toString())
            }
        })
    }

    private fun signIn (token: String) {
        val userService = getRetrofit().create(UserInterface::class.java)

        userService.signUp(token).enqueue(object: Callback<UserResponse<UserToken>> {
            override fun onResponse(
                call: Call<UserResponse<UserToken>>,
                response: Response<UserResponse<UserToken>>
            ) {
                Log.d("singIn Success", response.toString())
                val resp = response.body()
                if(resp!=null) {
                    when(resp.code) {
                        "USER_1000" -> {
                            Log.d("singIn Result", resp.message)

                            Toast.makeText(this@MainActivity, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show()

                            GlobalApplication.preferences.setString("accessToken", resp.result.accessToken)
                            GlobalApplication.preferences.setString("refreshToken", resp.result.refreshToken)
                            GlobalApplication.preferences.setInt("userID", resp.result.userId)
                            AppData.qpAccessToken = resp.result.accessToken
                            AppData.qpUserID = resp.result.userId

                            AppData.qpIsLogin = true
                        }
                        else->Log.d("singIn Result", resp.message)
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse<UserToken>>, t: Throwable) {
                Log.d("singIn Fail", t.message.toString())
            }

        })
    }
}