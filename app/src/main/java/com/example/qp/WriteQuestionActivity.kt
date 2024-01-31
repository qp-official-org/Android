package com.example.qp

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.qp.databinding.ActivityWriteQuestionBinding

class WriteQuestionActivity: AppCompatActivity() {
    private lateinit var binding:ActivityWriteQuestionBinding
    private lateinit var adapter:WriteQuestionTagRVAdapter
    private var isChild=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter=WriteQuestionTagRVAdapter(applicationContext)
        binding.hashtagRv.adapter=adapter

        binding.levelBtnOrange.visibility=View.GONE
        binding.hashtagCancelBtn.visibility=View.GONE
        setHashtagEdit()
        setChild()
        checkTitleEdit()
        checkContentEdit()


    }

    private fun setChild(){
        binding.levelBtn.setOnClickListener {
            isChild=true
            binding.levelBtn.visibility= View.GONE
            binding.levelBtnOrange.visibility=View.VISIBLE
        }
        binding.levelBtnOrange.setOnClickListener {
            isChild=false
            binding.levelBtn.visibility= View.VISIBLE
            binding.levelBtnOrange.visibility=View.GONE
        }
    }

    private fun checkTitleEdit(){
        var editText=binding.titleEdit
        editText.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if(s!=null){
                    if(s.length<5 || s[s.length-1]!='?'){
                        binding.titleInfoTv.setTextColor(ContextCompat.getColor(applicationContext,R.color.red))
                    }
                    else{
                        binding.titleInfoTv.setTextColor(ContextCompat.getColor(applicationContext,R.color.green))
                    }
                }
            }

        })
    }

    private fun checkContentEdit(){
        var editText=binding.contentEdit
        editText.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                if(s!=null){
                    if(s.length<10){
                        binding.contentEditInfoTv.setTextColor(ContextCompat.getColor(applicationContext,R.color.red))
                    }
                    else{
                        binding.contentEditInfoTv.setTextColor(ContextCompat.getColor(applicationContext,R.color.green))

                    }
                }
            }

        })
    }

    private fun setHashtagEdit(){
        var edittext=binding.hashtagEdit
        edittext.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    if(s.isNotEmpty())
                        if((s[s.length-1].code ==32 || s[s.length-1] =='\n') && s.isNotBlank()) {
                            if(!adapter.dupCheck(s.toString().trim())){
                                Toast.makeText(applicationContext,"이미 추가함",Toast.LENGTH_SHORT).show()
                                return
                            }
                            adapter.addItem(s.toString().trim())
                            edittext.text=Editable.Factory.getInstance().newEditable("")
                            if(adapter.itemCount>=3){
                                edittext.isFocusable=false
                                edittext.isClickable=false
                                edittext.isFocusableInTouchMode=false
                                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                inputMethodManager.hideSoftInputFromWindow(edittext.windowToken, 0)
                            }
                        }
                }
            }
        })

        var cancelBtn=binding.hashtagCancelBtn

        edittext.setOnClickListener {
            if(adapter.itemCount>=3){
                Toast.makeText(applicationContext,"다시 입력하려면 기존 텍스트를 지워주세요",Toast.LENGTH_SHORT).show()
            }
            else{
                edittext.isFocusable=true
                edittext.isClickable=true
                edittext.isFocusableInTouchMode=true
                edittext.requestFocus()

            }
        }
        edittext.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    cancelBtn.visibility = View.VISIBLE
                    cancelBtn.setOnClickListener {
                        edittext.text = Editable.Factory.getInstance().newEditable("")
                    }
                } else {
                    cancelBtn.visibility = View.GONE
                }

        }


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