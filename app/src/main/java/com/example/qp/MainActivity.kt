package com.example.qp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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
            add(Question("2023.11.16","질문 제목1","질문내용1","#태그1","#태그2","#태그3"))
            add(Question("2023.12.20","질문 제목2","질문내용2","#태그1","#태그2","#태그3"))
            add(Question("2024.01.01","질문 제목3","질문내용3","#태그1","#태그2","#태그3"))
            add(Question("2024.01.04","질문 제목4","질문내용4","#태그1","#태그2","#태그3"))
            add(Question("2024.01.07","질문 제목5","질문내용5","#태그1","#태그2",""))
        }

        val questionRVAdapter = QuestionRVAdapter(qDatas)
        binding.mainQuestionRv.adapter = questionRVAdapter
        binding.mainQuestionRv.layoutManager = GridLayoutManager(applicationContext, 2)

        questionRVAdapter.setMyItemClickListner(object : QuestionRVAdapter.MyItemClickListner{
            override fun onItemClick(question: Question) {
                //val intent = Intent(this@MainActivity, DetailedActivity::class.java)
                val intent = Intent(this@MainActivity, WriteQuestionActivity::class.java)
                val gson = Gson()
                val qJson = gson.toJson(question)
                intent.putExtra("question", qJson)
                startActivity(intent)
            }
        })

        binding.mainSearchBt.setOnClickListener {
            Toast.makeText(this, "검색버튼 클릭!", Toast.LENGTH_LONG).show()
        }

        binding.mainLoginBt.setOnClickListener{
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }

    }
}