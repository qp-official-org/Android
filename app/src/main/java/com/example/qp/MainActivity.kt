package com.example.qp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.qp.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var qDatas = ArrayList<Question>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        qDatas.apply {
            add(Question("2023.11.16","질문 상세1","#태그1","#태그2","#태그3"))
            add(Question("2023.12.20","질문 상세2","#태그1","#태그2","#태그3"))
            add(Question("2024.01.01","질문 상세3","#태그1","#태그2","#태그3"))
            add(Question("2024.01.04","질문 상세4","#태그1","#태그2","#태그3"))
            add(Question("2024.01.07","질문 상세5","#태그1","#태그2",""))
        }

        val questionRVAdapter = QuestionRVAdapter(qDatas)
        binding.mainQuestionRv.adapter = questionRVAdapter
        binding.mainQuestionRv.layoutManager = GridLayoutManager(applicationContext, 2)

        questionRVAdapter.setMyItemClickListner(object : QuestionRVAdapter.MyItemClickListner{
            override fun onItemClick(question: Question) {
                val intent = Intent(this@MainActivity, DetailedActivity::class.java)
                val gson = Gson()
                val qJson = gson.toJson(question)
                intent.putExtra("question", qJson)
                startActivity(intent)
            }
        })

    }
}