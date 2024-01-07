package com.example.qp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qp.databinding.ItemQuestionBinding

class QuestionRVAdapter(private val qList: ArrayList<Question>)
    : RecyclerView.Adapter<QuestionRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): QuestionRVAdapter.ViewHolder {
        val binding: ItemQuestionBinding =ItemQuestionBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionRVAdapter.ViewHolder, position: Int) {
        holder.bind(qList[position])
    }

    override fun getItemCount(): Int = qList.size

    inner class ViewHolder(val binding: ItemQuestionBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(question: Question){
            binding.itemTimeTv.text = question.time
            binding.itemQuestionTv.text = question.content
            binding.itemCategory1Tv.text = question.tag1
            binding.itemCategory2Tv.text = question.tag2
            binding.itemCategory3Tv.text = question.tag3
        }
    }
}