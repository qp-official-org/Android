package com.example.qp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.qp.databinding.FragmentProfileMyQuestionBinding
import com.google.gson.Gson

class ProfileMyQuestionFragment : Fragment() {

    lateinit var binding: FragmentProfileMyQuestionBinding
    private var qDatas = ArrayList<QuestionInfo>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileMyQuestionBinding.inflate(inflater, container, false)

        /*qDatas.apply {
//            add(Question("2023.11.16","질문 제목1","질문내용1","#태그1","#태그2","#태그3"))
//            add(Question("2024.01.04","질문 제목4","질문내용4","#태그1","#태그2","#태그3"))
            add(Question("2023.11.16","질문 제목1","질문내용1"))
            add(Question("2024.01.04","질문 제목4","질문내용4"))*/
        /*qDatas.apply {
            add(Question("2023.11.16","질문 제목1","질문내용1","#태그1","#태그2","#태그3"))
            add(Question("2024.01.04","질문 제목4","질문내용4","#태그1","#태그2","#태그3"))
        }*/
        
        // 임시 데이터 (삭제 요망)
        /*qDatas.apply {
            add(Question("2023.11.16","아르테미스 계획","현재 아르테미스 계획은 어떻게 되어 가고 있나요?","#우주","달",""))
            add(Question("2024.01.04","가장 빠른 동물","가장 빠른 동물이 무엇인가요?","#동물","#환경생물학",""))
        }*/


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