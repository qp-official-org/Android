package com.example.qp

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qp.databinding.ItemQuestionBinding

class QuestionRVAdapter(private val qList: ArrayList<QuestionInfo>)
    : RecyclerView.Adapter<QuestionRVAdapter.ViewHolder>() {

    interface MyItemClickListner{
        fun onItemClick(questionInfo: QuestionInfo)
    }
    private lateinit var myItemClickListner: MyItemClickListner
    fun setMyItemClickListner(itemClickListner: MyItemClickListner){
        myItemClickListner = itemClickListner
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): QuestionRVAdapter.ViewHolder {
        val binding: ItemQuestionBinding =ItemQuestionBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionRVAdapter.ViewHolder, position: Int) {
        holder.bind(qList[position])
        holder.itemView.setOnClickListener{
            myItemClickListner.onItemClick(qList[position])
        }
    }

    override fun getItemCount(): Int = qList.size

    inner class ViewHolder(val binding: ItemQuestionBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(questionInfo: QuestionInfo){
            binding.itemTimeTv.text = questionInfo.createAt.toString()
            binding.itemQuestionTv.text = questionInfo.title
            if(questionInfo.hashtags?.isEmpty() == false){
                for (tagInfo in questionInfo.hashtags!!) {
                    if(tagInfo.hashtagId == 1)
                        binding.itemCategory1Tv.text = tagInfo.hashtag
                    else if(tagInfo.hashtagId == 2)
                        binding.itemCategory2Tv.text = tagInfo.hashtag
                    else if(tagInfo.hashtagId == 3)
                        binding.itemCategory3Tv.text = tagInfo.hashtag
                }
            }
        }
    }

    fun setData(filtered: ArrayList<QuestionInfo>){
        qList.clear()
        qList.addAll(filtered)
        Log.d("filtered 개수", qList.size.toString())
    }

}