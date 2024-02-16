package com.example.qp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.example.qp.databinding.ActivitySearchBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    lateinit var binding: ActivitySearchBinding
    private var filtered = ArrayList<QuestionInfo>()
    private lateinit var adapter: WriteQuestionTagRVAdapter

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //최근 검색어 리사이클러뷰 설정
        val layoutManager = FlexboxLayoutManager(this)
        layoutManager.flexDirection = FlexDirection.ROW    //아이템 가로 나열
        layoutManager.flexWrap = FlexWrap.WRAP             //필요에 따라 다음 줄 이동
        binding.searchRecentRecordRv.layoutManager = layoutManager
        Log.d("최근 검색어 개수", AppData.searchRecord.size.toString())
        adapter = WriteQuestionTagRVAdapter(this@SearchActivity, AppData.searchRecord)
        binding.searchRecentRecordRv.adapter = adapter
        //검색기록 없을 때 안내 멘트
        binding.searchNoRecordTv.isVisible = AppData.searchRecord.size == 0

        binding.searchBackIv.setOnClickListener{
            val intent = Intent(this@SearchActivity, MainActivity::class.java)
            startActivity(intent)
        }

        searchResult()
        register()
    }

    private fun searchResult(){
        val textListner = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                getfilteredQuestions(query)
                record(query)
                binding.searchRecentWord.isVisible = false
                binding.searchInputSv.clearFocus() // 키보드 숨기기
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                //검색어 변경 시는 별다른 액션 X
                return false
            }
        }

        binding.searchInputSv.setOnQueryTextListener(textListner)
        binding.searchImageBt.setOnClickListener {
            textListner.onQueryTextSubmit(binding.searchInputSv.query.toString())
        }

    }

    private fun record(input: String?){
        if(!input.isNullOrEmpty()){
            if(adapter.dupCheck(input.trim())){
                AppData.searchRecord.add(input.trim())
                adapter.addItem(input.trim())
            }
        }
    }

    private fun getfilteredQuestions(query: String?){
        val questionService = getRetrofit().create(QuestionInterface::class.java)

        questionService.getQuestions(0, 10, query) //스크롤에 따라 추가 페이징 할 것!
            .enqueue(object: Callback<QuestionResponse> {
                override fun onResponse(
                    call: Call<QuestionResponse>,
                    response: Response<QuestionResponse>
                ) {
                    if(response.isSuccessful && response.code() == 200){
                        val questionResponse: QuestionResponse = response.body()!!

                        Log.d("Q-RESPONSE/SUCCESS", questionResponse.toString())

                        when(questionResponse.code){
                            "QUESTION_2000" -> {
                                Log.d("SUCCESS/DATA_LOAD", "리사이클러뷰의 데이터로 구성됩니다")
                                filtered.clear()
                                filtered.addAll(questionResponse.result.questions)
                                setQuestionRVAdapter(filtered)
                                Log.d("getFdResp",filtered.toString())
                            }
                            else -> {
                                Log.d("SUCCESS/DATA_FAILURE", "응답 코드 오류입니다")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<QuestionResponse>, t: Throwable) {
                    Log.d("Q-RESPONSE/FAILURE", t.message.toString())
                }

            })
    }

    private fun setQuestionRVAdapter(filtered: ArrayList<QuestionInfo>){
        if(filtered.size == 0){
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
            binding.searchMatchQuestionRv.isVisible = true

            val questionRVAdapter = QuestionRVAdapter(filtered)
            binding.searchMatchQuestionRv.adapter = questionRVAdapter
            binding.searchMatchQuestionRv.layoutManager = GridLayoutManager(applicationContext, 2)

            questionRVAdapter.setMyItemClickListner(object : QuestionRVAdapter.MyItemClickListner{
                @RequiresApi(Build.VERSION_CODES.TIRAMISU)
                override fun onItemClick(questionInfo: QuestionInfo) {
                    val intent = Intent(this@SearchActivity, DetailedActivity::class.java)
                    val gson = Gson()
                    val qJson = gson.toJson(questionInfo)
                    intent.putExtra("question", qJson)
                    startActivity(intent)
                }
            })
        }
    }

    private fun register(){
        binding.searchRegisterBt.setOnClickListener {
            if(AppData.qpUserID != 0){
                val intent = Intent(this@SearchActivity, WriteQuestionActivity::class.java)
                startActivity(intent)
            }
            else{
                val dialog = SimpleDialog()
                dialog.show(supportFragmentManager,"dialog")
            }
        }
    }

}