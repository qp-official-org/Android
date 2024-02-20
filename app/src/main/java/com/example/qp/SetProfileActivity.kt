package com.example.qp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.qp.databinding.ActivitySetprofileBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SetProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivitySetprofileBinding

    var selectedImageUri: Uri? = null
    lateinit var tempImage : String

    // 뒤로가기 버튼 눌렀을 때 발동되는 함수
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetprofileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 프로필 설정 페이지와 회원가입절차 종료 페이지 구분
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)    // 종료함수
        binding.profileBackIv.setOnClickListener {
            if(binding.profileMainTv.visibility == View.GONE) {
                binding.profileMainTv.visibility = View.VISIBLE
                binding.profileImageIv.visibility = View.VISIBLE
                binding.profileImageEditIv.visibility = View.VISIBLE
                binding.profileNextBtnIv.visibility = View.VISIBLE
                binding.profileBtnTv.visibility = View.VISIBLE
                binding.profileWelcomeTv.visibility = View.GONE
                binding.profileExitBtnTv.visibility = View.GONE
                binding.profileExitBtnIv.visibility = View.GONE
                binding.profileWelcomeIv.visibility = View.GONE
            }
            else {
                var url = Url(tempImage)
                deleteImage(url)
                tempImage = ""
                binding.profileImageIv.setImageResource(R.drawable.profile_def)

                finish()
            }
        }

        // 회원가입절차 종료 페이지로 전환
        binding.profileNextBtnIv.setOnClickListener {
            binding.profileMainTv.visibility = View.GONE
            binding.profileImageIv.visibility = View.GONE
            binding.profileImageEditIv.visibility = View.GONE
            binding.profileNextBtnIv.visibility = View.GONE
            binding.profileBtnTv.visibility = View.GONE
            binding.profileWelcomeTv.visibility = View.VISIBLE
            binding.profileExitBtnTv.visibility = View.VISIBLE
            binding.profileExitBtnIv.visibility = View.VISIBLE
            binding.profileWelcomeIv.visibility = View.VISIBLE
        }

        // 회원가입절차 종료, 메인 페이지로 복귀
        binding.profileExitBtnIv.setOnClickListener {
            AppData.qpProfileImage = tempImage
            tempImage = ""
            var userModify = UserModify(AppData.qpNickname, AppData.qpProfileImage)
            AppData.modifyUserInfo(AppData.qpAccessToken, AppData.qpUserID, userModify)

            Toast.makeText(this, "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show()

            AppData.qpIsLogin = true
            AppData.isGoHome = true

            finish()
        }

        binding.profileImageCl.setOnClickListener {
            imageEdit()
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
            binding.profileImageIv.setImageURI(selectedImageUri)
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
                    }

                    override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                        Log.d("imageResponse_Fail", t.message.toString())
                        Toast.makeText(this@SetProfileActivity, "프로필사진 등록에 실패했습니다. ${t.message.toString()}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "파일을 읽을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteImage(url: Url) {
        val imageService = getRetrofit().create(ImageInterface::class.java)

        imageService.deleteImage(url).enqueue(object: Callback<DeleteResponse> {
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
                        else-> Log.d("ddelete Result", resp.message)
                    }
                }
            }

            override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                Log.d("ddelete Fail", t.message.toString())
            }
        })
    }
}