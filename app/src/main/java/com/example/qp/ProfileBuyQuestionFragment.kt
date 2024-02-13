package com.example.qp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.qp.databinding.FragmentProfileBuyQuestionBinding
import com.google.gson.Gson

class ProfileBuyQuestionFragment : Fragment() {

    lateinit var binding: FragmentProfileBuyQuestionBinding
    private var qDatas = ArrayList<QuestionInfo>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBuyQuestionBinding.inflate(inflater, container, false)


        //수정 임시 데이터(삭제해도 무관합니다)
        var tagList= arrayListOf<TagInfo>(TagInfo(0,"tag1"),TagInfo(1,"tag2"))
        qDatas.apply {
            add(QuestionInfo(UserInfo(1,"1","USER"),1,"질문제목1","질문내용1",1,2,3,"2024-01-01","2024-01-01",tagList))
            add(QuestionInfo(UserInfo(1,"1","USER"),1,"질문제목2","질문내용2",1,2,3,"2024-01-01","2024-01-01",tagList))
            add(QuestionInfo(UserInfo(1,"1","USER"),0,"질문제목3","질문내용3",1,2,3,"2024-01-01","2024-01-01",tagList))


        val questionRVAdapter = QuestionRVAdapter(qDatas)
        binding.questionRv.adapter = questionRVAdapter
        binding.questionRv.layoutManager = GridLayoutManager(requireContext(), 2)

        questionRVAdapter.setMyItemClickListner(object : QuestionRVAdapter.MyItemClickListner{
            override fun onItemClick(questionInfo: QuestionInfo) {
                val intent = Intent(requireContext(), DetailedActivity::class.java)
                val gson = Gson()
                val qJson = gson.toJson(questionInfo)
                intent.putExtra("question", qJson)
                intent.putExtra("qDatas", qDatas)
                startActivity(intent)
            }
        })

        return binding.root
    }
}