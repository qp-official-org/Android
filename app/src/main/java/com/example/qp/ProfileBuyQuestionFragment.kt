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


//        // 임시 데이터 (삭제 요망)
//        qDatas.apply {
//            add(Question("2023.12.20","퓨전 한복이란?","퓨전한복이랑 개량한복의 차이점이 뭔가요?"))
//            add(Question("2024.01.04","가장 빠른 동물","가장 빠른 동물이 무엇인가요?"))
//            add(Question("2024.01.07","필름카메라 필름 보관","필름카메라는 처음인데, 보관을 어떻게 해야 안전한가요?"))
//        }

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