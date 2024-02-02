package com.example.qp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
        register()
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
                binding.searchRecentWord.isVisible = false
                if(selected.size == 0){
                    //이전 결과
                    binding.searchMatchQuestionRv.isVisible = false
                    //검색 결과 X
                    binding.searchRegisterInfo.isVisible = true
                    binding.searhNoResultTv.isVisible = true
                }
                else{
                    //이전 결과
                    binding.searchRegisterInfo.isVisible = false
                    binding.searhNoResultTv.isVisible = false
                    //검색 결과 O
                    filtered = selected
                    questionRVAdapter.setData(filtered)
                    questionRVAdapter.notifyDataSetChanged()
                    binding.searchMatchQuestionRv.isVisible = true
                }
                // 키보드 숨기기
                binding.searchInputSv.clearFocus();
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                //검색어 변경 시는 별다른 액션 X
                return false
            }

        })
    }

    private fun register(){
        binding.searchRegisterBt.setOnClickListener {
//           Toast.makeText(this, "질문등록 화면으로 전환됩니다!", Toast.LENGTH_LONG).show()
            startActivity(Intent(this@SearchActivity,WriteQuestionActivity::class.java))
        }
    }

}