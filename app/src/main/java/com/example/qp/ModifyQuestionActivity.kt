package com.example.qp

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.qp.databinding.ActivityModifyQuestionBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModifyQuestionActivity:AppCompatActivity() {
    private lateinit var binding:ActivityModifyQuestionBinding
    private var gson= Gson()
    private var questionInfo:QuestionInfo?=null
    private var isTitleValid=true
    private var isContentValid=true
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityModifyQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if(intent.hasExtra("modifyQuestion")){
            questionInfo=intent.getSerializableExtra("modifyQuestion",QuestionInfo::class.java)
            Log.d("intentQ",questionInfo.toString())
        }


        binding.modifyBackBtn.setOnClickListener {
            finish()
        }

        initView()
        checkTitleEdit()
        checkContentEdit()
        updateQuestion()

    }
    private fun initView(){
        binding.titleEdit.text=Editable.Factory.getInstance().newEditable(questionInfo?.title)
        binding.contentEdit.text=Editable.Factory.getInstance().newEditable(questionInfo?.content)

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun updateQuestion(){
        binding.registerBtn.setOnClickListener {
            val title=binding.titleEdit.text.toString()
            val content=binding.contentEdit.text.toString()

            if(isTitleValid&&isContentValid){
                registerService(title,content)
            }
        }
    }

    private fun checkTitleEdit(){
        var editText=binding.titleEdit
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if(s!=null){
                    if(s.length<5 || s[s.length-1]!='?'){
                        binding.titleInfoTv.setTextColor(ContextCompat.getColor(applicationContext,R.color.red))
                        isTitleValid=false
                    }
                    else{
                        binding.titleInfoTv.setTextColor(ContextCompat.getColor(applicationContext,R.color.green))
                        isTitleValid=true
                    }
                }
            }

        })
    }

    private fun checkContentEdit(){
        var editText=binding.contentEdit
        editText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                if(s!=null){
                    if(s.length<10){
                        binding.contentEditInfoTv.setTextColor(ContextCompat.getColor(applicationContext,R.color.red))
                        isContentValid=false
                    }
                    else{
                        binding.contentEditInfoTv.setTextColor(ContextCompat.getColor(applicationContext,R.color.green))
                        isContentValid=true
                    }
                }
            }

        })
    }


    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action === MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun registerService(title:String, content:String){
        var modifyQInfo:ModifyQInfo?

        modifyQInfo=ModifyQInfo(AppData.qpUserID.toLong(),title,content)

        val questionService= getRetrofit().create(QuestionInterface::class.java)
        questionService.modifyQuestion(AppData.qpAccessToken,questionInfo?.questionId!!,modifyQInfo!!).enqueue(object : Callback<ModifyQResponse>{
            override fun onResponse(
                call: Call<ModifyQResponse>,
                response: Response<ModifyQResponse>
            ) {
                Log.d("modifyQReq",questionInfo?.questionId.toString().plus(AppData.qpUserID).plus(title))
                val resp=response.body()
                when(resp?.code){
                    "QUESTION_2000"->{
                        Log.d("modifyQ/SUCCESS",resp.toString())
                        questionInfo?.title=title
                        questionInfo?.content=content

                        intent= Intent(this@ModifyQuestionActivity,DetailedActivity::class.java)
                        val gson = Gson()
                        val qJson = gson.toJson(questionInfo)
                        intent.putExtra("question", qJson)
                        startActivity(intent)
                        finish()
                    }
                    else->{
                        Log.d("modifyQ/FAIL",response.errorBody()?.string().toString())
                        Toast.makeText(applicationContext,"질문 등록 실패",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ModifyQResponse>, t: Throwable) {
                Log.d("modifyQResp/FAIL",t.message.toString())
            }

        })
    }
}