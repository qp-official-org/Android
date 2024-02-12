package com.example.qp

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.qp.databinding.ActivityModifyQuestionBinding
import com.google.gson.Gson

class ModifyQuestionActivity:AppCompatActivity() {
    private lateinit var binding:ActivityModifyQuestionBinding
    private var gson= Gson()
    private lateinit var questionInfo:QuestionInfo
    private var isTitleValid=true
    private var isContentValid=true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityModifyQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if(intent.hasExtra("modifyQuestion")){
            val qJson = intent.getStringExtra("modifyQuestion")
            Log.d("getquestion",qJson.toString())
            questionInfo = gson.fromJson(qJson, QuestionInfo::class.java)
        }

        initView()
        checkTitleEdit()
        checkContentEdit()
        updateQuestion()

    }
    private fun initView(){
        binding.titleEdit.text=Editable.Factory.getInstance().newEditable(questionInfo.title)
        binding.contentEdit.text=Editable.Factory.getInstance().newEditable(questionInfo.content)

    }

    private fun updateQuestion(){
        binding.registerBtn.setOnClickListener {
            val title=binding.titleEdit.text.toString()
            val content=binding.contentEdit.text.toString()

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
}