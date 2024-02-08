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
    private var qDatas = ArrayList<Question>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBuyQuestionBinding.inflate(inflater, container, false)

        qDatas.apply {
            add(Question("2023.12.20","질문 제목2","질문내용2","#태그1","#태그2","#태그3"))
            add(Question("2024.01.04","질문 제목4","질문내용4","#태그1","#태그2","#태그3"))
            add(Question("2024.01.07","질문 제목5","질문내용5","#태그1","#태그2",""))
        }

        val questionRVAdapter = QuestionRVAdapter(qDatas)
        binding.questionRv.adapter = questionRVAdapter
        binding.questionRv.layoutManager = GridLayoutManager(requireContext(), 2)

        questionRVAdapter.setMyItemClickListner(object : QuestionRVAdapter.MyItemClickListner{
            override fun onItemClick(question: Question) {
                val intent = Intent(requireContext(), DetailedActivity::class.java)
                val gson = Gson()
                val qJson = gson.toJson(question)
                intent.putExtra("question", qJson)
                intent.putExtra("qDatas", qDatas)
                startActivity(intent)
            }
        })

        return binding.root
    }
}