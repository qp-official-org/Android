package com.example.qp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qp.databinding.ItemAnswerBinding
import com.example.qp.databinding.ItemWriteAnswerBinding

class DetailedQuestionRVAdapter(): RecyclerView.Adapter<DetailedQuestionRVAdapter.ViewHolder>() {
    private val items= ArrayList<Answer>()
    private var isCommentShown=false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):DetailedQuestionRVAdapter.ViewHolder {
        val binding:ItemAnswerBinding=ItemAnswerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: DetailedQuestionRVAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }
    override fun getItemCount(): Int =items.size

    inner class ViewHolder(val binding:ItemAnswerBinding) : RecyclerView.ViewHolder(binding.root) {
        private val answerContentView=itemView.findViewById<TextView>(R.id.answer_content_tv)
        private val answerCommentNumber=itemView.findViewById<TextView>(R.id.answer_comment_btn_tv)
        private val profileView=itemView.findViewById<ImageView>(R.id.question_user_img)
        fun bind(answer:Answer) {
            answerContentView.text=answer.content

            answerCommentNumber.text=
                    when (answer.commentList){
                        null->"0"
                        else->answer.commentList!!.size.toString()
                    }

                    //댓글 보이기
            if(answer.commentList!=null){
                val commentAdapter=DetailedAnswerCommentRVAdapter(answer.commentList!!)
                binding.answerCommentRv.adapter=commentAdapter
            }

            showComment(true,binding)
            binding.answerCommentBtnLayout.setOnClickListener {
               showComment(isCommentShown,binding)
            }

        }
        private fun showComment(isShown: Boolean,binding:ItemAnswerBinding){
            val commentRv=binding.commentLayout
            if(isShown){
                commentRv.visibility=View.GONE
                isCommentShown=false
            }
            else{
                commentRv.visibility=View.VISIBLE
                isCommentShown=true
            }
        }
    }

/*    fun addItems(item: Items_Answer) {
        this.items.add(item)
        this.notifyDataSetChanged()
    }*/
    fun addItemList(items:ArrayList<Answer>){
        this.items.addAll(items)
        this.notifyDataSetChanged()
    }
    /*
    fun removeItem(position: Int){
        this.items.removeAt(position)
        this.notifyDataSetChanged()
    }

 */




}