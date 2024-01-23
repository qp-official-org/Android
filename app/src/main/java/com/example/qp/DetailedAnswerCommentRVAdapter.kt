package com.example.qp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.qp.databinding.ItemAnswerCommentBinding

class DetailedAnswerCommentRVAdapter(context: Context, private val items:ArrayList<Comment>):RecyclerView.Adapter<DetailedAnswerCommentRVAdapter.ViewHolder>() {
    private var appContext=context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):DetailedAnswerCommentRVAdapter.ViewHolder {
        val binding:ItemAnswerCommentBinding=ItemAnswerCommentBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int =items.size


    inner class ViewHolder(val binding:ItemAnswerCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        val content=binding.commentContentTv
        fun bind(comment:Comment){

            content.text=comment.content
            showCommentMorePopup(comment)
        }


        private fun showCommentMorePopup(comment: Comment){
            lateinit var popupWindow:SimplePopup
            //if(answer.commentList.isEmpty()||answer.commentList==null){
                binding.commentMoreBtn.setOnClickListener {
                    val list= mutableListOf<String>().apply {
                        add("수정하기")
                        add("삭제하기")
                        add("신고하기")
                    }
                    popupWindow=SimplePopup(appContext,list){_,_,position->
                        when(position){
                            0-> Toast.makeText(appContext,"수정하기", Toast.LENGTH_SHORT).show()
                            1-> Toast.makeText(appContext,"삭제하기", Toast.LENGTH_SHORT).show()
                            2-> Toast.makeText(appContext,"신고하기", Toast.LENGTH_SHORT).show()
                        }
                    }
                    popupWindow.isOutsideTouchable=true
                    popupWindow.showAsDropDown(it,40,10)
                }
            }
            /*else{
                binding.commentMoreBtn.setOnClickListener {
                    val list= mutableListOf<String>().apply {
                        add("신고하기")
                    }
                    popupWindow=SimplePopup(appContext,list){_,_,position->
                        when(position){
                            0-> Toast.makeText(appContext,"신고하기", Toast.LENGTH_SHORT).show()
                        }
                    }
                    popupWindow.isOutsideTouchable=true
                    popupWindow.showAsDropDown(it,40,10)
                }

            }*/

        }
    fun addItems(commentList:ArrayList<Comment>) {
        items.addAll(commentList)
    }

    fun addItem(content:String){
        items.add(Comment(content))
        notifyDataSetChanged()
    }
    }






