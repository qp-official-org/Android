package com.example.qp

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.qp.databinding.ActivityWriteQuestionBinding
import com.google.gson.Gson
import java.util.Date

class WriteQuestionActivity: AppCompatActivity(),WriteQView {
    private lateinit var binding:ActivityWriteQuestionBinding
    private lateinit var adapter:WriteQuestionTagRVAdapter
    private var isChild=false
    private var isTitleValid=false
    private var isContentValid=false
    private  var tagList = ArrayList<TagInfo>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.titleEdit.text.clear()
        binding.contentEdit.text.clear()
        adapter=WriteQuestionTagRVAdapter(applicationContext)
        binding.hashtagRv.adapter=adapter

        binding.levelBtnOrange.visibility=View.GONE
        binding.hashtagCancelBtn.visibility=View.GONE
        setHashtagEdit()
        setChild()
        checkTitleEdit()
        checkContentEdit()
        registerQuestion()

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
        editText.addTextChangedListener(object:TextWatcher{
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

    private fun registerQuestion(){
        val questionService=QuestionService()
        questionService.setWriteQView(this)

        binding.registerBtn.setOnClickListener {
            val titleText=findViewById<EditText>(R.id.title_edit).text.toString()
            val contentText=binding.contentEdit.text.toString()
            val checkBox=binding.noteCheckbox

            if(isTitleValid &&isContentValid){
                if(checkBox.isChecked){

                    val questionInfo = QuestionInfo(
                        UserInfo(0,"","student"), //사용자 정보 임의 설정
                        0, //임의 설정
                        titleText,
                        contentText,
                        0, // 초기값 설정
                        0, // 초기값 설정
                        0, // 초기값 설정
                        Date().getTime().toString(),
                        updateAt = null, // 초기값 설정
                        tagList
                    )
                    /*when(tagList.size){
                        1->question.tag1=tagList[0]
                        2-> {
                            question.tag1=tagList[0]
                            question.tag2 = tagList[1]
                        }
                        3->{
                            question.tag1=tagList[0]
                            question.tag2 = tagList[1]
                            question.tag3=tagList[2]
                        }
                    }*/

                    questionService.writeQ(questionInfo)

                    val intent= Intent(this@WriteQuestionActivity,DetailedActivity::class.java)
                    val gson= Gson()
                    val qJson=gson.toJson(questionInfo)
                    intent.putExtra("question",qJson)
                    startActivity(intent)
                    Toast.makeText(applicationContext,"등록 완료",Toast.LENGTH_SHORT).show()

                }
                else Toast.makeText(applicationContext,"동의가 체크되지 않음",Toast.LENGTH_SHORT).show()

            }
            else{
                Toast.makeText(applicationContext,"제목 또는 본문 형식이 유효하지 않습니다.",Toast.LENGTH_SHORT).show()
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

    override fun onWriteSuccess() {
        TODO("Not yet implemented")
    }

    override fun onWriteFailure() {
        TODO("Not yet implemented")
    }


}