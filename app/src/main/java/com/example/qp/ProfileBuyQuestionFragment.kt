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
            add(QuestionInfo(UserInfo(1,"1","USER"),1,"질문제목3","질문내용3",1,2,3,"2024-01-01","2024-01-01",tagList))

            /*
                add(Question("2023.12.20","질문 제목2","질문내용2","#태그1","#태그2","#태그3"))
                add(Question("2024.01.04","질문 제목4","질문내용4","#태그1","#태그2","#태그3"))
                add(Question("2024.01.07","질문 제목5","질문내용5","#태그1","#태그2",""))
                add(Question("2023.12.20","질문 제목2","질문내용2"))
                add(Question("2024.01.04","질문 제목4","질문내용4"))
                add(Question("2024.01.07","질문 제목5","질문내용5"))

                add(Question("2023.12.20","퓨전 한복이란?","퓨전한복이랑 개량한복의 차이점이 뭔가요?","#한복","#옷","#전통"))
                add(Question("2024.01.04","가장 빠른 동물","가장 빠른 동물이 무엇인가요?","#동물","#환경생물학",""))
                add(Question("2024.01.07","필름카메라 필름 보관","필름카메라는 처음인데, 보관을 어떻게 해야 안전한가요?","#카메라","#필름","필름카메라"))
            }*/
        }

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