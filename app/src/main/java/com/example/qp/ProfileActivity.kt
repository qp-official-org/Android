package com.example.qp

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.qp.databinding.ActivityProfileBinding
import com.google.android.material.tabs.TabLayoutMediator
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.regex.Pattern

class ProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileBinding
//    lateinit var binding2: DialogChargeBinding
    var isNick = false
    var selectedImageUri: Uri? = null

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

        // 로고 클릭 시 홈으로 이동
        binding.profileQpLogo.setOnClickListener {
            AppData.isGoHome = true
            finish()
        }

        // 유저 데이터 반영
        binding.profileMainTv.text = AppData.qpNickname
        binding.profileEditNicknameEt.hint = AppData.qpNickname
        binding.profileMainCoinNumTv.text = AppData.qpPoint.toString()
        binding.profileMainCoinTextTv.text = (AppData.qpPoint / 10).toString()
        var year = AppData.qpCreatedAt.substring(0 until 4)
        var month = AppData.qpCreatedAt.substring(5 until 7)
        var day = AppData.qpCreatedAt.substring(8 until 10)
        binding.profileMainDateTv.text = "${year}년 ${month}월 ${day}일 가입"

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

        if(AppData.qpRole == "EXPERT") {        // 전문가일 경우 프로필 수정 불가
            binding.profileMainEditBtn.visibility = View.GONE
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

                var userModify = UserModify(editname, AppData.qpProfileImage)
                AppData.modifyUserInfo(AppData.qpAccessToken, AppData.qpUserID, userModify)
            }
            else {
                Toast.makeText(this, "닉네임은 1 ~ 6자, 특수문자 제외로 제한됩니다.",Toast.LENGTH_SHORT).show()
            }
        }
        binding.profileEditNoBtn.setOnClickListener {
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
        }
        imageEdit()
    }

    override fun onRestart() {
        if(AppData.isGoHome)    finish()

        super.onRestart()
    }

    companion object {
        const val REQUEST_CODE_IMAGE = 101
    }

    //이미지 설정 클릭 시 갤러리 이동
    fun imageEdit() {
        binding.profileEditSettingIv.setOnClickListener {
            Intent(Intent.ACTION_PICK).also {
                it.type = "image/*"
                val mineTypes = arrayOf("image/jpeg", "image/png")
                it.putExtra(Intent.EXTRA_MIME_TYPES, mineTypes)
                startActivityForResult(it, REQUEST_CODE_IMAGE)
            }
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
                        AppData.qpProfileImage = response.body()?.result?.url ?: ""
                    }

                    override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                        Log.d("imageResponse_Fail", t.message.toString())
                    }
                })
            } else {
                Toast.makeText(this, "파일을 읽을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
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