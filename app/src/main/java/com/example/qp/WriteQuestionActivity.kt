package com.example.qp

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.nfc.Tag
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.qp.databinding.ActivityWriteQuestionBinding
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Date

class WriteQuestionActivity: AppCompatActivity(),WriteQView {
    private lateinit var binding:ActivityWriteQuestionBinding
    private lateinit var adapter:WriteQuestionTagRVAdapter
    private var isChild=false
    private var isTitleValid=false
    private var isContentValid=false
    private  var tagList = ArrayList<String>()
    private var tagNum=0
    private var tagPost=false


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

        binding.writeSearchBt.setOnClickListener {
            val qDatas=intent.getSerializableExtra("qDatas") as ArrayList<QuestionInfo>
            val intent=Intent(this@WriteQuestionActivity,SearchActivity::class.java)
            intent.putExtra("qDatas",qDatas)
            startActivity(intent)
        }

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

                    CoroutineScope(Dispatchers.Main).launch {

                        tagList = adapter.getItems()
                        val newTagList = ArrayList<TagInfo>()
                        for (i in 0 until tagList.size) {
                            postTag(tagList[i])
                            delay(400)

                            newTagList.add(TagInfo(tagNum, tagList[i]))
                            Log.d("hashtagNum",tagNum.toString())

                        }

                        delay(1200)
                        val job3= CoroutineScope(Dispatchers.Main).launch{

                            var tagIds = ArrayList<Int>()
                            for (i in 0 until newTagList.size) {
                                tagIds.add(newTagList[i].hashtagId)
                            }

                            val questionPost = QuestionPost(
                                userId = "1",
                                title = titleText,
                                content = contentText,
                                hashtag = tagIds
                            )
                            questionService.writeQ(questionPost)
                            


                            val questionInfo = QuestionInfo(
                                title = titleText,
                                content = contentText,
                                hashtags = newTagList
                            )

                            val intent =
                                Intent(this@WriteQuestionActivity, DetailedActivity::class.java)
                            val gson = Gson()
                            val qJson = gson.toJson(questionInfo)
                            intent.putExtra("question", qJson)
                            startActivity(intent)
                            finish()
                            Toast.makeText(applicationContext, "등록 완료", Toast.LENGTH_SHORT).show()
                        }

                    }



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


    suspend fun postTag(tag:String):Int{
        val questionService=QuestionService()
        questionService.setWriteQView(this)

        questionService.postHashtag(tag)
        return tagNum
    }


    override fun onWriteSuccess() {
    }

    override fun onWriteFailure() {
    }

    override fun onPostHashtagSuccess(response: HashtagResponse) {
        tagPost=true
        tagNum= response.result!!.hashtagId
        Log.d("postHashtag/SUCCESS",response.toString().plus("tagNum="+tagNum))
    }

    override fun onPostHashtagFailure(tag: String, msg: String) {
        tagPost=false
        Log.d("postHashtag/FAIL",tag.plus(msg))
    }

    override fun onGetHashtagSuccess(response: HashtagResponse) {
        tagNum=response.result?.hashtagId?:0
        Log.d("getHashtag/SUCCESS",response.toString())
    }

    override fun onGetHashtagFailure(tag: String, msg: String) {
        Log.d("getHashtag/FAIL",tag.plus(msg))
    }


}

