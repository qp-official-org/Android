package com.example.qp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.qp.databinding.ActivityDetailedBinding
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
        binding.detailedQuestionTitleTv.text=question.title
        binding.detailedQuestionContentTv.text = question.content
        binding.detailedQuestionTimeTv.text = question.time

        answerAdapter=DetailedQuestionRVAdapter()
        binding.answerRv.adapter=answerAdapter


        initNotifyView(false)
        onClickAnswerBtn()
        initQuestionData()

    }
    private fun initQuestionData(){
        var is_answered=true

        answerList.apply {
            add(Answer("답변내용1"))
            add(Answer("답변내용2"))
            add(Answer("답변내용3"))
        }
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
    private fun initNotifyView(isAnswered:Boolean){
        var notifyView=binding.noticeView
        if(isAnswered)
            notifyView.visibility= View.GONE
    }

}