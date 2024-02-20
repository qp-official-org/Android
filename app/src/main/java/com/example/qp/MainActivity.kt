package com.example.qp

import android.content.Intent
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
import com.example.qp.databinding.ActivityMainBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.kakao.sdk.common.util.Utility
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
            }, 500)

            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                isExit = false
            }, 4000)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // sharedPreference에 저장된 로컬 데이터 불러오기
        AppData.qpAccessToken = GlobalApplication.preferences.getString("accessToken", "")
        AppData.qpUserID = GlobalApplication.preferences.getInt("userID", 0)
        AppData.searchUserInfo(AppData.qpAccessToken, AppData.qpUserID)
        Log.d("sharedpp1", AppData.qpAccessToken)
        Log.d("sharedpp2", AppData.qpUserID.toString())

        // 로그인 여부 확인
        UserApiClient.instance.accessTokenInfo { token, error ->
            if (error != null) {
                Log.e("TAG", "로그인 실패", error)
                binding.mainLoginBt.visibility = View.VISIBLE
                binding.mainLoginSuccessBt.visibility = View.GONE
                binding.mainLoginSuccessUserImg.visibility = View.GONE
            } else if (token != null) {
                Log.i("TAG", "로그인 성공 $token")

                var refreshToken: String = GlobalApplication.preferences.getString("refreshToken", "")
                autoSignIn(AppData.qpAccessToken, refreshToken, AppData.qpUserID)

                Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                binding.mainLoginBt.visibility = View.GONE
                binding.mainLoginSuccessBt.visibility = View.VISIBLE
                binding.mainLoginSuccessUserImg.visibility = View.VISIBLE

                // 하단 바에 사용자 닉네임과 포인트 데이터 반영
                binding.mainBarNicknameTv.text = AppData.qpNickname
                binding.mainBarCoinTv.text = AppData.qpPoint.toString()
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)    // 종료함수
        
        //백엔드로부터 질문 정보를 가져와 리사이클러뷰를 구성하는 함수
        getQuestions(page)

        //Toast.makeText(applicationContext, "로그인한 유저 아이디: "+AppData.qpUserID.toString(),Toast.LENGTH_SHORT).show()

        // 키 해시 확인용
        val keyHash = Utility.getKeyHash(this)
        Log.d("Hash", keyHash)

        // 임시 로그아웃 (로고 클릭시)
        binding.mainLogoIv.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Toast.makeText(this, "로그아웃 실패 $error", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                    AppData.qpUserID = 0
                    AppData.qpAccessToken = ""
                    AppData.searchRecord.clear()
                    binding.mainLoginBt.visibility = View.VISIBLE
                    binding.mainLoginSuccessBt.visibility = View.GONE
                    binding.mainLoginSuccessUserImg.visibility = View.GONE
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
    override fun onStart() {
        super.onStart()

        AppData.searchUserInfo(AppData.qpAccessToken, AppData.qpUserID)

        // 로그인 여부 확인
        UserApiClient.instance.accessTokenInfo { token, error ->
            if (error != null) {
                Log.e("TAG", "로그인 실패", error)
                binding.mainLoginBt.visibility = View.VISIBLE
                binding.mainLoginSuccessBt.visibility = View.GONE
            } else if (token != null) {
                Log.i("TAG", "로그인 성공 $token")
                binding.mainLoginBt.visibility = View.GONE
                binding.mainLoginSuccessBt.visibility = View.VISIBLE

                Log.d("DData", token.toString())
            }
        }

        // 하단 바에 사용자 닉네임과 포인트 데이터 반영
        binding.mainBarNicknameTv.text = AppData.qpNickname
        binding.mainBarCoinTv.text = AppData.qpPoint.toString()
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
                //val gson = Gson()
                //val qJson = gson.toJson(questionInfo)
                //intent.putExtra("question", qJson)
                intent.putExtra("question",questionInfo.questionId)
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
                Log.d("autosingUp Success", response.toString())
                val resp = response.body()
                if(resp!=null){
                    when(resp.code){
                        "USER_1000"-> {
                            Log.d("autoSingUp Result1", resp.message)
                        }
                        "TOKEN_8001"-> {
                            Toast.makeText(this@MainActivity, "토큰이 만료되었습니다. 다시 로그인하기 바랍니다.", Toast.LENGTH_SHORT).show()
                        }
                        else->Log.d("autoSingUp Result2", resp.message)
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse<AutoSignIn>>, t: Throwable) {
                Log.d("autosingUp Fail", t.message.toString())
            }
        })
    }
}