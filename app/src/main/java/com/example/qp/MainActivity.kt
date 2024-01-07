package com.example.qp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.qp.databinding.ActivityMainBinding

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
    }
}