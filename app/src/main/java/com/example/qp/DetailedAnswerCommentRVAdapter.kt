package com.example.qp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DetailedAnswerCommentRVAdapter(val items:ArrayList<Items_Answer>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when(viewType){
            TYPE_COMMENT->{
                CommentHolder.create(parent)
            }
            /*TYPE_WRITE_COMMENT->{
                WriteCommentHolder.create(parent)
            }*/
            else -> {
                throw IllegalStateException("Not Found ViewHolder Type $viewType")
            }

        }


    override fun getItemCount(): Int =items.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CommentHolder -> {
                holder.bind(items[position] as ItemAnswer)
            }
            /*is WriteCommentHolder -> {
                holder.bind(items[position] as ItemWriteAnswer)
            }*/
        }
    }

    companion object {
        private const val TYPE_COMMENT = 0
        private const val TYPE_WRITE_COMMENT = 1
    }
    override fun getItemViewType(position: Int) = when (items[position]) {
        is ItemComment -> {
            TYPE_COMMENT
        }
        is ItemWriteAnswer -> {
            TYPE_WRITE_COMMENT
        }
        else -> {
            throw IllegalStateException("Not Found ViewHolder Type")
        }
    }

    class CommentHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameView=itemView.findViewById<TextView>(R.id.comment_user_name_tv)
        private val commentContentView=itemView.findViewById<TextView>(R.id.comment_content_tv)
        private val profileView=itemView.findViewById<ImageView>(R.id.comment_user_iv)
        fun bind(item: ItemAnswer) {
        }
        companion object Factory {
            fun create(parent: ViewGroup): CommentHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.item_answer_comment, parent, false)

                return CommentHolder(view)
            }
        }
    }
    /*class WriteCommentHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameView=itemView.findViewById<TextView>(R.id.answer_user_name_tv)
        fun bind(item: ItemWriteAnswer) {
//                imageView.setImageResource(item.image)
//                textView.text = item.desc
        }
        companion object Factory {
            fun create(parent: ViewGroup): WriteCommentHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.item_answer_btn, parent, false)

                return WriteCommentHolder(view)
            }
        }
    }*/


//    fun addItems(item: Items_Answer) {
//            this.items.add(item)
//            this.notifyDataSetChanged()
//    }


}