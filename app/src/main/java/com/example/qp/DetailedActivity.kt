package com.example.qp

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.PopupWindowCompat.showAsDropDown
import androidx.recyclerview.widget.RecyclerView
import com.example.qp.databinding.ActivityDetailedBinding
import com.example.qp.databinding.ItemAnswerBinding
import com.google.gson.Gson


class DetailedActivity : AppCompatActivity(){

    private lateinit var binding: ActivityDetailedBinding
    private var gson: Gson = Gson()
    private lateinit var answerAdapter:DetailedQuestionRVAdapter
    private  var answerList =ArrayList<Answer>()
    private lateinit var question:Question

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra("question")){
            val qJson = intent.getStringExtra("question")
            question = gson.fromJson(qJson, Question::class.java)
        }



        initView(binding)
        initAnswerData()

        initNotifyView()
        onClickAnswerBtn()
        setQuestionMorePopup()
    }



    private fun initView(binding: ActivityDetailedBinding){
        binding.detailedQuestionTitleTv.text=question.title
        binding.detailedQuestionContentTv.text = question.content
        binding.detailedQuestionTimeTv.text = question.time


    }
    private fun initAnswerData(){
        val commentList=ArrayList<Comment>()
        commentList.apply {
            add(Comment("댓글1"))
            add(Comment("댓글2"))
        }
        answerList.apply {
            add(Answer("답변내용1",commentList))
            add(Answer("답변내용2",commentList))
            add(Answer("답변내용3"))
        }
        answerAdapter=DetailedQuestionRVAdapter(applicationContext)
        binding.answerRv.adapter=answerAdapter
        answerAdapter.addItemList(answerList)

    }
    private fun onClickAnswerBtn(){
        binding.answerBtn.setOnClickListener {
            showWriteAnswerEdit(true)
        }
    }
    private fun initNotifyView(){
        if(answerList.isEmpty()){
            val container=findViewById<ConstraintLayout>(R.id.notice_container)
            val inflater:LayoutInflater=getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.item_notice,container,true)
        }

        val noticeBtn=findViewById<TextView>(R.id.notify_btn)
        //noticeBtn.setOnClickListener {  }
    }

    private fun writeAnswer(){
        val btn=findViewById<TextView>(R.id.write_answer_btn)
        val editText=findViewById<EditText>(R.id.write_answer_edit)
        btn.setOnClickListener {
            var content=editText.text.toString()
            answerAdapter.addItem(Answer(content))  //임시로 구현..
            showWriteAnswerEdit(false)
            Toast.makeText(applicationContext,"답변이 등록되었습니다.",Toast.LENGTH_SHORT).show()
        }
    }

    private fun showWriteAnswerEdit(toShow:Boolean){
        val container=findViewById<ConstraintLayout>(R.id.write_answer_container)
        if(toShow){
            binding.answerBtn.visibility=View.GONE
            val inflater:LayoutInflater=getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.item_write_answer,container,true)
            writeAnswer()
        }
        else{
            binding.answerBtn.visibility=View.VISIBLE
            container.removeAllViews()
        }

    }


    private fun setQuestionMorePopup(){
        lateinit var popupWindow: SimplePopup
        if(answerList.isEmpty()||answerList==null){
            binding.questionMoreBtn.setOnClickListener {
                val list= mutableListOf<String>().apply {
                    add("수정하기")
                    add("삭제하기")
                    add("신고하기")
                }
                popupWindow=SimplePopup(applicationContext,list){_,_,position->
                    when(position){
                        0->Toast.makeText(applicationContext,"수정하기",Toast.LENGTH_SHORT).show()
                        1->Toast.makeText(applicationContext,"삭제하기",Toast.LENGTH_SHORT).show()
                        2->Toast.makeText(applicationContext,"신고하기",Toast.LENGTH_SHORT).show()
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
                        0->Toast.makeText(applicationContext,"신고하기",Toast.LENGTH_SHORT).show()
                    }
                }
                popupWindow.isOutsideTouchable=true
                popupWindow.showAsDropDown(it,40,10)
            }

        }

    }

}