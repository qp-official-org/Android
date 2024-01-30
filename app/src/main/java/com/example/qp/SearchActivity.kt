package com.example.qp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.example.qp.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    lateinit var binding: ActivitySearchBinding
    private var original = ArrayList<Question>()
    private var filtered = ArrayList<Question>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchBackIv.setOnClickListener{
            startActivity(Intent(this@SearchActivity, MainActivity::class.java))
        }

        searchResult()
    }

    private fun searchResult(){
        original = intent.getSerializableExtra("qDatas") as ArrayList<Question>
        filtered.addAll(original)

        val questionRVAdapter = QuestionRVAdapter(filtered)
        binding.searchMatchQuestionRv.adapter = questionRVAdapter
        binding.searchMatchQuestionRv.layoutManager = GridLayoutManager(applicationContext, 2)

        binding.searchInputSv.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("qDatas 개수", original.size.toString())
                val selected = ArrayList<Question>()
                for(i in original){
                    val temp = i.title
                    if( temp?.contains(query.toString()) == true){
                        selected.add(i)
                    }
                }
                filtered = selected
                questionRVAdapter.setData(filtered)
                questionRVAdapter.notifyDataSetChanged()
                binding.searchRecentWord.isVisible = false
                binding.searchMatchQuestionRv.isVisible = true
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                /*if(newText.isNullOrEmpty()){
                    *//*questionRVAdapter.setData(qDatas)
                    questionRVAdapter.notifyDataSetChanged()*//*
                    binding.searchRecentWord.isVisible = true
                    binding.searchMatchQuestionRv.isVisible = false
                }*/
                return false
            }

        })
    }

}