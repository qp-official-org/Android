package com.example.qp

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.example.qp.databinding.ActivityProfileBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.regex.Pattern

class ProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileBinding
    var isNick = false
    var isAuthbtn = false
    var isEdit = false
    var isUpload = false

    var selectedImageUri: Uri? = null
    lateinit var tempImage : String

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

        // 로고 클릭 시 홈으로 이동
        binding.profileQpLogo.setOnClickListener {
            AppData.isGoHome = true
            finish()
        }

        binding.profileMainLogoutTv.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        // 로그아웃 버튼
        binding.profileMainLogoutTv.setOnClickListener {
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

                    AppData.qpIsLogin = false

                    AppData.isGoHome = true
                    finish()
                }
            }
        }

        // 유저 데이터 반영
        Glide.with(this).load(AppData.qpProfileImage).into(binding.profileMainImageIv)
        binding.profileMainTv.text = AppData.qpNickname
        binding.profileEditNicknameEt.hint = AppData.qpNickname
        binding.profileMainCoinNumTv.text = AppData.qpPoint.toString()
        binding.profileMainCoinTextTv.text = (AppData.qpPoint / 100).toString()
        var year = AppData.qpCreatedAt.substring(0 until 4)
        var month = AppData.qpCreatedAt.substring(5 until 7)
        var day = AppData.qpCreatedAt.substring(8 until 10)
        binding.profileMainDateTv.text = "${year}년 ${month}월 ${day}일 가입"

        binding.profileCheckExpert2Tv.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        // 전문가 인증 전환
        binding.profileCheckExpert2Tv.setOnClickListener {
            binding.profileAuthEt.setText("")
            // 키보드 내리기
            val inputMethodManager = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.profileAuthEt.windowToken, 0)

            if(isAuthbtn) {
                isAuthbtn = false
                binding.profileChargekBtn.visibility = View.VISIBLE
                binding.profileCharge10kBtn.visibility = View.VISIBLE

                binding.profileAuthEt.visibility = View.GONE
                binding.profileAuthNextInvalidBtn.visibility = View.GONE
            }
            else {
                isAuthbtn = true
                invalidAllbtn()
                binding.profileChargekBtn.visibility = View.GONE
                binding.profileCharge10kBtn.visibility = View.GONE
                binding.profileChargekKakaoBtn.visibility = View.GONE
                binding.profileChargekNaverBtn.visibility = View.GONE
                binding.profileCharge10kKakaoBtn.visibility = View.GONE
                binding.profileCharge10kNaverBtn.visibility = View.GONE

                binding.profileAuthEt.visibility = View.VISIBLE
                binding.profileAuthNextInvalidBtn.visibility = View.VISIBLE
            }
        }

        // 전문가 인증 (임시)
        binding.profileAuthEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                binding.profileAuthFailTv.visibility = View.GONE
                if(binding.profileAuthEt.length() == 8) {
                    binding.profileAuthNextBtn.visibility = View.VISIBLE
                    binding.profileAuthNextInvalidBtn.visibility = View.GONE
                }
                else{
                    binding.profileAuthNextBtn.visibility = View.GONE
                    binding.profileAuthNextInvalidBtn.visibility = View.VISIBLE
                }
            }
        })
        binding.profileAuthNextBtn.setOnClickListener {
            if(binding.profileAuthEt.text.toString() == "qpExpert") {
                binding.profileAuthEt.setText("")
                changeRole(AppData.qpUserID, "EXPERT")
                binding.profileExpertMarkIv.visibility = View.VISIBLE

                isAuthbtn = false
                binding.profileChargekBtn.visibility = View.VISIBLE
                binding.profileCharge10kBtn.visibility = View.VISIBLE

                binding.profileAuthEt.visibility = View.GONE
                binding.profileAuthNextInvalidBtn.visibility = View.GONE
                binding.profileCheckExpertLn.visibility = View.GONE
                binding.profileAuthNextBtn.visibility = View.GONE

                Toast.makeText(this, "전문가 인증이 완료되었습니다.", Toast.LENGTH_SHORT).show()

                binding.profileMainEditBtn.visibility = View.GONE

                // 키보드 내리기
                val inputMethodManager = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.profileAuthEt.windowToken, 0)
            }
            else {
                binding.profileAuthFailTv.visibility = View.VISIBLE
            }
        }
        binding.profileMainImageIv.setOnClickListener {
            if(isEdit) {
                imageEdit()
            }
            else {
                if(AppData.qpRole != "USER") {
                    changeRole(AppData.qpUserID, "USER")
                    binding.profileExpertMarkIv.visibility = View.GONE

                    binding.profileCheckExpertLn.visibility = View.VISIBLE

                    Toast.makeText(this, "인증이 해제되었습니다.", Toast.LENGTH_SHORT).show()

                    invalidAllbtn()
                    binding.profileChargekKakaoBtn.visibility = View.GONE
                    binding.profileChargekNaverBtn.visibility = View.GONE
                    binding.profileCharge10kKakaoBtn.visibility = View.GONE
                    binding.profileCharge10kNaverBtn.visibility = View.GONE

                    binding.profileMainEditBtn.visibility = View.VISIBLE
                }
            }
        }

        // 전문가면 인증버튼 안보이게, 인증마크 달리게
        if(AppData.qpRole == "EXPERT") {
            binding.profileCheckExpertLn.visibility = View.GONE
            binding.profileExpertMarkIv.visibility = View.VISIBLE
        }

        // 충전 버튼 색상 변경 및 Dialog 등장
        binding.profileChargekBtn.setOnClickListener {
            btnActive(binding.profileChargekBtn, binding.profileChargekTv1, binding.profileChargekTv2)
            binding.profileChargekNaverBtn.visibility = View.VISIBLE
            binding.profileChargekKakaoBtn.visibility = View.VISIBLE

            btnInactive(binding.profileCharge10kBtn, binding.profileCharge10kTv1, binding.profileCharge10kTv2)
            btnInactive2(binding.profileCharge10kNaverBtn, binding.profileCharge10kNaverTv)
            btnInactive2(binding.profileCharge10kKakaoBtn, binding.profileCharge10kKakaoTv)
            binding.profileCharge10kNaverBtn.visibility = View.GONE
            binding.profileCharge10kKakaoBtn.visibility = View.GONE
        }
        binding.profileChargekNaverBtn.setOnClickListener {
            btnActive2(binding.profileChargekNaverBtn, binding.profileChargekNaverTv)
            btnInactive2(binding.profileChargekKakaoBtn, binding.profileChargekKakaoTv)
            madeDialog(1000, "네이버 페이")
        }
        binding.profileChargekKakaoBtn.setOnClickListener {
            btnInactive2(binding.profileChargekNaverBtn, binding.profileChargekNaverTv)
            btnActive2(binding.profileChargekKakaoBtn, binding.profileChargekKakaoTv)
            madeDialog(1000, "카카오 페이")
        }

        binding.profileCharge10kBtn.setOnClickListener {
            btnActive(binding.profileCharge10kBtn, binding.profileCharge10kTv1, binding.profileCharge10kTv2)
            binding.profileCharge10kNaverBtn.visibility = View.VISIBLE
            binding.profileCharge10kKakaoBtn.visibility = View.VISIBLE

            btnInactive(binding.profileChargekBtn, binding.profileChargekTv1, binding.profileChargekTv2)
            btnInactive2(binding.profileChargekNaverBtn, binding.profileChargekNaverTv)
            btnInactive2(binding.profileChargekKakaoBtn, binding.profileChargekKakaoTv)
            binding.profileChargekNaverBtn.visibility = View.GONE
            binding.profileChargekKakaoBtn.visibility = View.GONE
        }
        binding.profileCharge10kNaverBtn.setOnClickListener {
            btnActive2(binding.profileCharge10kNaverBtn, binding.profileCharge10kNaverTv)
            btnInactive2(binding.profileCharge10kKakaoBtn, binding.profileCharge10kKakaoTv)
            madeDialog(10000, "네이버 페이")
        }
        binding.profileCharge10kKakaoBtn.setOnClickListener {
            btnInactive2(binding.profileCharge10kNaverBtn, binding.profileCharge10kNaverTv)
            btnActive2(binding.profileCharge10kKakaoBtn, binding.profileCharge10kKakaoTv)
            madeDialog(10000, "카카오 페이")
        }

        if(AppData.qpRole == "EXPERT") {        // 전문가일 경우 프로필 수정 불가
            binding.profileMainEditBtn.visibility = View.GONE
        }

        // 프로필 수정버튼
        binding.profileMainEditBtn.setOnClickListener {
            isEdit = true
            invalidAllbtn()
            binding.profileChargekKakaoBtn.visibility = View.GONE
            binding.profileChargekNaverBtn.visibility = View.GONE
            binding.profileCharge10kKakaoBtn.visibility = View.GONE
            binding.profileCharge10kNaverBtn.visibility = View.GONE

            binding.profileEditSettingIv.visibility = View.VISIBLE
            binding.profileMainTv.visibility = View.GONE
            binding.profileEditNicknameEt.visibility = View.VISIBLE
            binding.profileMainEditBtn.visibility = View.GONE
            binding.profileEditYesBtn.visibility = View.VISIBLE
            binding.profileEditNoBtn.visibility = View.VISIBLE
            binding.profileMainImageIv.setAlpha(0.7f)
            binding.profileMainbackBoxIv.setAlpha(0.7f)
        }

        // editText 입력마다 체크
        binding.profileEditNicknameEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // 닉네임 유효성 검사
                val nickPattern = "^[가-힣a-zA-Z0-9]{1,6}$"   // 영문, 한글, 숫자 1~6자
                val pattern = Pattern.compile(nickPattern)
                val matcher = pattern.matcher(binding.profileEditNicknameEt.text)

                isNick = matcher.find()
                if(isNick) {
                    binding.profileNicknameValidTv.visibility = View.VISIBLE
                    binding.profileNicknameInvalidTv.visibility = View.INVISIBLE
                }
                else {
                    binding.profileNicknameValidTv.visibility = View.INVISIBLE
                    binding.profileNicknameInvalidTv.visibility = View.VISIBLE
                }
            }
        })

        binding.profileEditYesBtn.setOnClickListener {
            val editname : String = binding.profileEditNicknameEt.text.toString()
            if(isNick) {
                isEdit = false
                AppData.qpNickname = editname
                binding.profileMainTv.text = AppData.qpNickname
                binding.profileEditNicknameEt.hint = AppData.qpNickname

                binding.profileEditSettingIv.visibility = View.GONE
                binding.profileMainTv.visibility = View.VISIBLE
                binding.profileEditNicknameEt.visibility = View.GONE
                binding.profileMainEditBtn.visibility = View.VISIBLE
                binding.profileEditYesBtn.visibility = View.GONE
                binding.profileEditNoBtn.visibility = View.GONE
                binding.profileMainImageIv.setAlpha(1f)
                binding.profileMainbackBoxIv.setAlpha(1f)
                binding.profileNicknameInvalidTv.visibility = View.INVISIBLE
                binding.profileNicknameValidTv.visibility = View.INVISIBLE

                if(isUpload) {
                    // 기존 사진 삭제
                    var url = Url(AppData.qpProfileImage)
                    deleteImage(url)
                    AppData.qpProfileImage = tempImage
                    var userModify = UserModify(AppData.qpNickname, AppData.qpProfileImage)
                    AppData.modifyUserInfo(AppData.qpAccessToken, AppData.qpUserID, userModify)
                    tempImage = ""
                    isUpload = false
                }
            }
            else {
                Toast.makeText(this, "닉네임은 1 ~ 6자, 특수문자 제외로 제한됩니다.",Toast.LENGTH_SHORT).show()
            }
        }
        binding.profileEditNoBtn.setOnClickListener {
            isEdit = false
            binding.profileEditSettingIv.visibility = View.GONE
            binding.profileMainTv.visibility = View.VISIBLE
            binding.profileEditNicknameEt.visibility = View.GONE
            binding.profileMainEditBtn.visibility = View.VISIBLE
            binding.profileEditYesBtn.visibility = View.GONE
            binding.profileEditNoBtn.visibility = View.GONE
            binding.profileMainImageIv.setAlpha(1f)
            binding.profileMainbackBoxIv.setAlpha(1f)
            binding.profileNicknameInvalidTv.visibility = View.INVISIBLE
            binding.profileNicknameValidTv.visibility = View.INVISIBLE

            if(isUpload) {
                var url = Url(tempImage)
                deleteImage(url)
                tempImage = ""
                Glide.with(this).load(AppData.qpProfileImage).into(binding.profileMainImageIv)
                isUpload = true
            }
        }
    }

    override fun onRestart() {
        if(AppData.isGoHome)    finish()

        super.onRestart()
    }

    private fun btnActive(btn: View, t1: TextView, t2: TextView) {
        btn.setBackgroundResource(R.drawable.box_orange)
        t1.setTextColor(Color.WHITE)
        t2.setTextColor(Color.WHITE)
    }

    private fun btnInactive(btn: View, t1: TextView, t2: TextView) {
        btn.setBackgroundResource(R.drawable.box_white_lined)
        t1.setTextColor(Color.BLACK)
        t2.setTextColor(Color.BLACK)
    }

    private fun btnActive2(btn: View, t1: TextView) {
        btn.setBackgroundResource(R.drawable.box_orange)
        t1.setTextColor(Color.WHITE)
    }

    private fun btnInactive2(btn: View, t1: TextView) {
        btn.setBackgroundResource(R.drawable.box_white_lined)
        t1.setTextColor(Color.BLACK)
    }

    private fun invalidAllbtn() {
        btnInactive(binding.profileChargekBtn,binding.profileChargekTv1, binding.profileChargekTv2)
        btnInactive(binding.profileCharge10kBtn,binding.profileCharge10kTv1, binding.profileCharge10kTv2)
        btnInactive2(binding.profileChargekKakaoBtn, binding.profileChargekKakaoTv)
        btnInactive2(binding.profileChargekNaverBtn, binding.profileChargekNaverTv)
        btnInactive2(binding.profileCharge10kKakaoBtn, binding.profileCharge10kKakaoTv)
        btnInactive2(binding.profileCharge10kNaverBtn, binding.profileCharge10kNaverTv)
    }


    // 계정 Role 변경 (개발용)
    private fun changeRole(userId: Int, role: String) {
        val userService = getRetrofit().create(UserInterface::class.java)

        userService.changeRole(userId, role).enqueue(object : Callback<UserResponse<UserModifyResult>> {
            override fun onResponse(
                call: Call<UserResponse<UserModifyResult>>,
                response: Response<UserResponse<UserModifyResult>>
            ) {
                Log.d("cchange Success", response.toString())
                val resp = response.body()
                if(resp!=null) {
                    when(resp.code) {
                        "USER_1000" -> {
                            Log.d("cchange Result1", resp.message)
                            AppData.qpRole = role
                            Log.d("cchange Result2", AppData.qpRole)
                        }
                        else -> Log.d("cchange Result", resp.message)
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse<UserModifyResult>>, t: Throwable) {
                Log.d("cchange Fail", t.message.toString())
            }

        })
    }

    // Dialog 호출 함수
    fun madeDialog(num: Long, str: String) {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_charge, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setCancelable(false)

        if(num == 1000L)    mDialogView.findViewById<TextView>(R.id.charge_text1_tv).text = "선택하신 금액 '1,000원'을"
        else if(num == 10000L)    mDialogView.findViewById<TextView>(R.id.charge_text1_tv).text = "선택하신 금액 '10,000원'을"
        else    mDialogView.findViewById<TextView>(R.id.charge_text1_tv).text = "선택하신 금액 '${num}원'을"
        mDialogView.findViewById<TextView>(R.id.charge_text2_tv).text = "'${str}'로"

        val mAlertDialog = mBuilder.show()

        val okButton = mDialogView.findViewById<AppCompatButton>(R.id.charge_yes_btn)
        okButton.setOnClickListener {
            mDialogView.findViewById<TextView>(R.id.charge_text1_tv).visibility = View.GONE
            mDialogView.findViewById<TextView>(R.id.charge_text2_tv).visibility = View.GONE
            mDialogView.findViewById<TextView>(R.id.charge_text3_tv).visibility = View.GONE
            mDialogView.findViewById<AppCompatButton>(R.id.charge_yes_btn).visibility = View.GONE
            mDialogView.findViewById<AppCompatButton>(R.id.charge_no_btn).visibility = View.GONE

            mDialogView.findViewById<TextView>(R.id.charge_text4_tv).visibility = View.VISIBLE
            mDialogView.findViewById<TextView>(R.id.charge_text5_tv).visibility = View.VISIBLE
            mDialogView.findViewById<TextView>(R.id.charge_text6_tv).visibility = View.VISIBLE
            mDialogView.findViewById<TextView>(R.id.charge_text7_tv).visibility = View.VISIBLE
            mDialogView.findViewById<AppCompatButton>(R.id.charge_next_btn).visibility = View.VISIBLE

            var userPoint = UserPoint(num)
            AppData.changePoint(AppData.qpAccessToken, AppData.qpUserID, userPoint)

            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                mDialogView.findViewById<TextView>(R.id.charge_text5_tv)
                    .text = "현재 '${AppData.qpNickname}'님의 잔여 포인트는"
                mDialogView.findViewById<TextView>(R.id.charge_text6_tv)
                    .text = "${AppData.qpPoint} point입니다."
                mDialogView.findViewById<TextView>(R.id.charge_text7_tv)
                    .text = "${AppData.qpPoint / 100}개의 답변을 확인할 수 있어요!"
            }, 300)
        }

        val exitButton = mDialogView.findViewById<AppCompatButton>(R.id.charge_next_btn)
        exitButton.setOnClickListener {
            mAlertDialog.dismiss()
            invalidAllbtn()
            binding.profileChargekNaverBtn.visibility = View.GONE
            binding.profileCharge10kNaverBtn.visibility = View.GONE
            binding.profileChargekKakaoBtn.visibility = View.GONE
            binding.profileCharge10kKakaoBtn.visibility = View.GONE

            binding.profileMainCoinNumTv.text = AppData.qpPoint.toString()
            binding.profileMainCoinTextTv.text = (AppData.qpPoint / 100).toString()
        }

        val noButton = mDialogView.findViewById<AppCompatButton>(R.id.charge_no_btn)
        noButton.setOnClickListener {
            mAlertDialog.dismiss()
            btnInactive2(binding.profileChargekNaverBtn, binding.profileChargekNaverTv)
            btnInactive2(binding.profileCharge10kNaverBtn, binding.profileCharge10kNaverTv)
            btnInactive2(binding.profileChargekKakaoBtn, binding.profileChargekKakaoTv)
            btnInactive2(binding.profileCharge10kKakaoBtn, binding.profileCharge10kKakaoTv)
        }
    }

    companion object {
        const val REQUEST_CODE_IMAGE = 101
    }

    //이미지 설정 클릭 시 갤러리 이동
    fun imageEdit() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mineTypes = arrayOf("image/jpeg", "image/png")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mineTypes)
            startActivityForResult(it, REQUEST_CODE_IMAGE)
        }
    }

    // 파일 URI를 실제 파일 경로로 변환
    private fun getPathFromUri(uri: Uri): String? {
        val cursor = contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            it.moveToFirst()
            val filePathColumnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
            return@use it.getString(filePathColumnIndex)
        }
    }

    //파일 경로로 multipart 변환
    fun fileToMultipartBodyPart(filePath: String, partName: String): MultipartBody.Part {
        val file = File(filePath)
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestBody)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            binding.profileMainImageIv.setImageURI(selectedImageUri)
            Log.d("imageResponse_d", selectedImageUri.toString())
            // 읽어온 파일의 URI를 파일 경로로 변환
            val filePath = selectedImageUri?.let { getPathFromUri(it) }
            Log.d("imageResponse_p", filePath.toString())

            if (!filePath.isNullOrEmpty()) {
                // 파일 경로를 사용하여 MultipartBody.Part 생성
                val imagePart = fileToMultipartBodyPart(filePath, "image")
                Log.d("imageResponse_m", imagePart.toString())

                val imageService = getRetrofit().create(ImageInterface::class.java)
                imageService.uploadImage(imagePart).enqueue(object : Callback<ImageResponse> {
                    override fun onResponse(call: Call<ImageResponse>, response: Response<ImageResponse>) {
                        Log.d("imageResponse_s", response.isSuccessful.toString())
                        Log.d("imageResponse_s", response.code().toString())
                        Log.d("imageResponse_s", response.body()?.message.toString())
                        Log.d("imageResponse_s", response.body()?.result.toString())
                        tempImage = response.body()?.result?.url ?: ""
                        binding.profileEditNicknameEt.setText(AppData.qpNickname)
                        isUpload = true
                    }

                    override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                        Log.d("imageResponse_Fail", t.message.toString())
                        Toast.makeText(this@ProfileActivity, "프로필사진 등록에 실패했습니다. ${t.message.toString()}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "파일을 읽을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteImage(url: Url) {
        val imageService = getRetrofit().create(ImageInterface::class.java)

        imageService.deleteImage(url).enqueue(object: Callback<DeleteResponse>{
            override fun onResponse(
                call: Call<DeleteResponse>,
                response: Response<DeleteResponse>
            ) {
                Log.d("ddelete Success", response.toString())
                val resp = response.body()
                if(resp!=null){
                    when(resp.code){
                        "IMAGE_5000"-> {
                            Log.d("ddelete Result", resp.message)
                        }
                        "IMAGE_5001" -> {
                            Log.d("ddelete Result", resp.message)
                        }
                        else->Log.d("ddelete Result", resp.message)
                    }
                }
            }

            override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                Log.d("ddelete Fail", t.message.toString())
            }
        })
    }
}