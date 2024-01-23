package com.example.qp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qp.databinding.ItemAnswerCommentBinding

class DetailedAnswerCommentRVAdapter(private val items:ArrayList<Comment>):RecyclerView.Adapter<DetailedAnswerCommentRVAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):DetailedAnswerCommentRVAdapter.ViewHolder {
        val binding:ItemAnswerCommentBinding=ItemAnswerCommentBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int =items.size


    class ViewHolder(binding:ItemAnswerCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        val content=binding.commentContentTv
        fun bind(comment:Comment){
            content.text=comment.content
        }
    }



    fun addItems(commentList:ArrayList<Comment>) {
        items.addAll(commentList)
    }

    fun addItem(content:String){
        items.add(Comment(content))
        notifyDataSetChanged()
    }


}