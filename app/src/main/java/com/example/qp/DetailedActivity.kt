package com.example.qp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.qp.databinding.ActivityDetailedBinding
import com.example.qp.databinding.ItemAnswerBinding
import com.google.gson.Gson


class DetailedActivity : AppCompatActivity(){

    lateinit var binding: ActivityDetailedBinding
    private var gson: Gson = Gson()
    private lateinit var answerAdapter:DetailedQuestionRVAdapter
    private  var answerList:ArrayList<Answer> =arrayListOf<Answer>()
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
            add(Answer("답변내용3",null))
        }
        answerAdapter=DetailedQuestionRVAdapter()
        binding.answerRv.adapter=answerAdapter
        answerAdapter.addItemList(answerList)

    }
    private fun onClickAnswerBtn(){
        val answerBtn=binding.answerBtn
        answerBtn.setOnClickListener {
            answerBtn.visibility=View.GONE
            val container=findViewById<ConstraintLayout>(R.id.write_answer_container)
            val inflater:LayoutInflater=getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.item_write_answer,container,true)
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

}