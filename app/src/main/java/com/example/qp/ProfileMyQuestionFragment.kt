package com.example.qp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qp.databinding.FragmentProfileMyQuestionBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileMyQuestionFragment : Fragment() {

    lateinit var binding: FragmentProfileMyQuestionBinding
    private var qDatas = ArrayList<QuestionInfo>()
    private var page = 0
    private var last = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileMyQuestionBinding.inflate(inflater, container, false)
        getWriteQuestions(page)
        moreQuestion()

        return binding.root
    }

    private fun getWriteQuestions(p: Int){
        val questionService = getRetrofit().create(QuestionInterface::class.java)

        Log.d("myResponseCode", AppData.qpAccessToken)
        Log.d("myResponseCode", AppData.qpUserID.toString())

        questionService.getWriteQuestions(AppData.qpAccessToken, AppData.qpUserID, p, 10)
            .enqueue(object: Callback<QuestionResponse> {
                override fun onResponse(
                    call: Call<QuestionResponse>,
                    response: Response<QuestionResponse>
                ) {
                    Log.d("myResponseCode", response.code().toString())
                    Log.d("myResponseCode", response.isSuccessful.toString())
                    Log.d("myResponseCode", response.body().toString())
                    if(response.isSuccessful){
                        val questionResponse: QuestionResponse = response.body()!!

                        Log.d("Q-RESPONSE/SUCCESS", questionResponse.toString())

                        when(questionResponse.code){
                            "QUESTION_2000" -> {
                                Log.d("SUCCESS/DATA_LOAD", "내가 한 리사이클러뷰의 데이터로 구성됩니다")
                                qDatas.addAll(questionResponse.result.questions)
                                last = questionResponse.result.last!!
                                if(questionResponse.result.totalElements?.toInt() != 0){
                                    binding.noWriteTv.isVisible = false
                                    binding.questionRv.isVisible = true
                                }
                                setQuestionRVAdapter()
                                Log.d("getQsResp",qDatas.toString())
                            }
                            else -> {
                                Log.d("SUCCESS/DATA_FAILURE", "응답 코드 오류입니다")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<QuestionResponse>, t: Throwable) {
                    Log.d("Q-RESPONSE_W/FAILURE", t.message.toString())
                }

            })
    }

    private fun moreQuestion(){
        binding.questionRv.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val rvPosition = (recyclerView.layoutManager as GridLayoutManager)?.findLastCompletelyVisibleItemPosition()
                val totalCount = binding.questionRv.adapter!!.itemCount - 1
                if (rvPosition==totalCount && !last) {
                    page++
                    getWriteQuestions(page)
                }
            }
        })
    }

    private fun setQuestionRVAdapter(){
        val questionRVAdapter = QuestionRVAdapter(qDatas)
        if(page == 0){
            binding.questionRv.adapter = questionRVAdapter
            binding.questionRv.layoutManager = GridLayoutManager(requireContext(), 2)
        } else {
            binding.questionRv.adapter!!.notifyDataSetChanged()
        }

        questionRVAdapter.setMyItemClickListner(object : QuestionRVAdapter.MyItemClickListner{
            override fun onItemClick(questionInfo: QuestionInfo) {
                val intent = Intent(requireContext(), DetailedActivity::class.java)
                intent.putExtra("question", questionInfo.questionId)
                startActivity(intent)
            }
        })
    }
}