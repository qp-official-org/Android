package com.example.qp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.qp.databinding.ActivityDetailedBinding
import com.google.gson.Gson

class DetailedActivity : AppCompatActivity(){

    lateinit var binding: ActivityDetailedBinding
    private var gson: Gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val qJson = intent.getStringExtra("question")
        val question = gson.fromJson(qJson, Question::class.java)

        binding.detailedQuestionTv.text = question.content
        binding.detailedTimeTv.text = question.time
    }

}