package com.example.qp

import android.content.Context
import android.graphics.Rect
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.qp.databinding.ActivityDetailedBinding
import com.google.gson.Gson
import java.io.Serializable


class DetailedActivity : AppCompatActivity(){

    private lateinit var binding: ActivityDetailedBinding
    private var gson: Gson = Gson()
    private lateinit var answerAdapter:DetailedQuestionRVAdapter
    private lateinit var question:Question
    private var isNotified:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra("question")){
            val qJson = intent.getStringExtra("question")
            question = gson.fromJson(qJson, Question::class.java)
        }

        binding.detailedSearchBt.setOnClickListener {
            val qDatas = intent.getSerializableExtra("qDatas") as ArrayList<Question>
            val intent = Intent(this@DetailedActivity, SearchActivity::class.java)
            intent.putExtra("qDatas", qDatas)
            startActivity(intent)
        }

        setInit()
        setOnClickListeners()

        setQuestionMorePopup()


        binding.profileBar.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }


    private fun setInit(){
        isNotified=isNotified()
        initView()

        answerAdapter=DetailedQuestionRVAdapter(applicationContext)
        binding.answerRv.adapter=answerAdapter

        initAnswerData()

        answerAdapter.setMyItemClickListener(object :
            DetailedQuestionRVAdapter.ItemClickListener{
            //답변 삭제
            override fun onItemRemove(position:Int) {
                answerAdapter.removeItem(position)
                updateExpertNum()
                if(answerAdapter.isItemListEmpty()){
                    binding.answerBtn.visibility=View.VISIBLE
                    updateNotifyView()
                }
            }
            //답변 수정
            override fun onAnswerModify(position: Int) {
                val container=binding.writeAnswerContainer
                var content=answerAdapter.getContent(position)

                val inflater:LayoutInflater=getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                inflater.inflate(R.layout.item_write_answer,container,true)

                val writeBtn=findViewById<TextView>(R.id.write_answer_btn)
                val writeLayout=findViewById<EditText>(R.id.write_answer_edit)
                writeLayout.text= Editable.Factory.getInstance().newEditable(content)

                writeBtn.setOnClickListener {
                    val newContent=writeLayout.text.toString()
                    answerAdapter.modifyAnswer(position,newContent)
                    container.removeAllViews()
                }

            }

        })

        updateNotifyView()

    }

    private fun initView(){
        binding.detailedQuestionTitleTv.text=question.title
        binding.detailedQuestionContentTv.text = question.content
        binding.detailedQuestionTimeTv.text = question.time

//        binding.hashtag1.text=question.tag1
//        binding.hashtag2.text=question.tag2
//        binding.hashtag3.text=question.tag3
        val tagListSize=question.hashtags.size
        when(tagListSize){
            1->binding.hashtag1.text=question.hashtags[0].hashtag
            2->{
                binding.hashtag1.text=question.hashtags[0].hashtag
                binding.hashtag2.text=question.hashtags[1].hashtag
            }
            3->{
                binding.hashtag1.text=question.hashtags[0].hashtag
                binding.hashtag2.text=question.hashtags[1].hashtag
                binding.hashtag3.text=question.hashtags[2].hashtag
            }
        }


    }
    private fun initAnswerData(){
        val answerList =ArrayList<Answer>()

        val commentList=ArrayList<Comment>()
        val commentList2=ArrayList<Comment>()

        commentList.apply {
            add(Comment("댓글1"))
            add(Comment("댓글2"))
        }
        commentList2.apply {
            add(Comment("댓글2-1"))
            add(Comment("댓글2-2"))
        }

        answerList.apply {
            add(Answer("답변내용1",commentList))
            add(Answer("답변내용",commentList2))
            add(Answer("답변내용3"))
        }
        answerAdapter.addItemList(answerList)
        updateExpertNum()

    }
    private fun isNotified(): Boolean {
        return false     //서버에서 정보 받아와 설정
    }

    private fun setOnClickListeners(){
        binding.answerBtn.setOnClickListener {
            showWriteAnswerEdit(true)
        }
        binding.answerNoticeBtn.setOnClickListener {
            if(!isNotified){
                notifyQuestion(true)
                val dialog=SimpleDialog()
                dialog.show(supportFragmentManager,"dialog")
            }
            else{
                notifyQuestion(false)
            }
        }
    }


    private fun updateNotifyView(){
        var container=binding.noticeContainer
        if(answerAdapter.isItemListEmpty()){
            val inflater:LayoutInflater=getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.item_notice,container,true)

            val notifyBtn=findViewById<TextView>(R.id.notify_btn)
            notifyBtn.setOnClickListener {
                notifyQuestion(true)
                container.removeAllViews()
            }
        }
        else{
            container.removeAllViews()
        }


    }
    private fun updateExpertNum(){
        binding.answerCountTv.text=answerAdapter.itemCount.toString()+"명의 전문가가 답변을 했어요"
    }

    private fun notifyQuestion(toNotify:Boolean){
        if(toNotify){
            binding.answerNoticeBtn.setImageResource(R.drawable.notification_on)
            isNotified=true
            Toast.makeText(applicationContext,"답변 알림 설정",Toast.LENGTH_SHORT).show()
        }
        else{
            binding.answerNoticeBtn.setImageResource(R.drawable.notification_off)
            isNotified=false
            Toast.makeText(applicationContext,"답변 알림 해제",Toast.LENGTH_SHORT).show()
        }
    }





    private fun writeAnswer(){
        val btn=findViewById<TextView>(R.id.write_answer_btn)
        val editText=findViewById<EditText>(R.id.write_answer_edit)

        btn.setOnClickListener {
            var content=editText.text.toString()
            answerAdapter.addItem(Answer(content))  //임시로 구현..
            showWriteAnswerEdit(false)
            updateNotifyView()
            updateExpertNum()
            Toast.makeText(applicationContext,"답변이 등록되었습니다.",Toast.LENGTH_SHORT).show()
        }
    }

    private fun showWriteAnswerEdit(toShow:Boolean){
        val container=binding.writeAnswerContainer
        if(toShow){
            binding.answerBtn.visibility=View.GONE
            val inflater:LayoutInflater=getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.item_write_answer,container,true)
            writeAnswer()
        }
        else{
            container.removeAllViews()
        }

    }



    private fun setQuestionMorePopup(){
        lateinit var popupWindow: SimplePopup
        if(answerAdapter.isItemListEmpty()){
            binding.questionMoreBtn.setOnClickListener {
                val list= mutableListOf<String>().apply {
                    add("수정하기")
                    add("삭제하기")
                    add("신고하기")
                }
                popupWindow=SimplePopup(applicationContext,list){_,_,position->
                    when(position){
                        0-> {
                            val gson= Gson()
                            val qJson=gson.toJson(question)
                            val intent=Intent(this@DetailedActivity,ModifyQuestionActivity::class.java)
                            intent.putExtra("modifyQuestion",qJson)
                            startActivity(intent)
                            Log.d("modifyLog",question.toString())
                            Toast.makeText(applicationContext, "수정하기", Toast.LENGTH_SHORT).show()
                        }
                        1->Toast.makeText(applicationContext,"삭제하기",Toast.LENGTH_SHORT).show()
                        2-> {
                            Toast.makeText(applicationContext, "신고하기", Toast.LENGTH_SHORT).show()
                            val dialog=SimpleDialog()
                            dialog.show(supportFragmentManager,"dialog")
                        }
                    }
                }
                popupWindow.isOutsideTouchable=true
                popupWindow.showAsDropDown(it,40,10)
            }
            }
        else{
            binding.questionMoreBtn.setOnClickListener {

                val list= mutableListOf<String>().apply {
                        add("신고하기")
                }
                popupWindow=SimplePopup(applicationContext,list){_,_,position->
                    when(position){
                        0-> {
                            Toast.makeText(applicationContext, "신고하기", Toast.LENGTH_SHORT).show()
                            val dialog=SimpleDialog()
                            dialog.show(supportFragmentManager,"dialog")
                        }
                    }
                }
                popupWindow.isOutsideTouchable=true
                popupWindow.showAsDropDown(it,40,10)
            }

        }

    }

    //editText 밖을 터치하면 키보드 내려감
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