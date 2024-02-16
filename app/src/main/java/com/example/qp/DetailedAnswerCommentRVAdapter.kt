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

class DetailedAnswerCommentRVAdapter(context: Context ):RecyclerView.Adapter<DetailedAnswerCommentRVAdapter.ViewHolder>() {
    private var appContext=context
    private val items=ArrayList<AnswerInfo>()

    interface CommentClickListener{
        fun onItemRemove(position:Int)
        fun onCommentModify(position: Int)
    }
    private lateinit var myItemClickListener: CommentClickListener
    fun setMyItemClickListener(itemClickListener: CommentClickListener){
        myItemClickListener=itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):DetailedAnswerCommentRVAdapter.ViewHolder {
        val binding:ItemAnswerCommentBinding=ItemAnswerCommentBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int =items.size


    inner class ViewHolder(val binding:ItemAnswerCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        val content=binding.commentContentTv

        fun bind(position:Int){
            var comment=items[position]

            content.text=comment.content

            showCommentMorePopup(comment,position)
        }


        private fun showCommentMorePopup(comment: AnswerInfo,position:Int){
            lateinit var popupWindow:SimplePopup
            binding.commentMoreBtn.setOnClickListener {
                val list= mutableListOf<String>().apply {
                    add("수정하기")
                    add("삭제하기")
                    add("신고하기")
                }
                popupWindow=SimplePopup(appContext,list){_,_,menuPos->
                    when(menuPos){
                        0-> {
                            Toast.makeText(appContext, "수정하기", Toast.LENGTH_SHORT).show()
                            myItemClickListener.onCommentModify(position)
                        }
                        1-> {
                            Toast.makeText(appContext, "삭제하기", Toast.LENGTH_SHORT).show()
                            myItemClickListener.onItemRemove(position)
                        }
                        2-> Toast.makeText(appContext,"신고하기", Toast.LENGTH_SHORT).show()
                    }
                }
                popupWindow.isOutsideTouchable=true
                popupWindow.showAsDropDown(it,40,10)
            }
            }


        }


    fun getContent(position: Int):String{
        return items[position].content
    }
    fun modifyComment(position: Int,content:String){
        items[position].content=content
        this.notifyDataSetChanged()
    }
    fun isCommentListEmpty():Boolean{
        return items.isEmpty()||items==null
    }
    fun addItem(position:Int,answer:AnswerInfo){
        //items.add(AnswerInfo(answerId = 0, userId = 0, title = "title1", content=content,category = "CHILD", answerGroup = 0, likes = 0))
        items.add(position,answer)
        notifyDataSetChanged()
    }
    fun addItems(answerList:ArrayList<AnswerInfo>?){
        if(items!=null){
            items.addAll(answerList!!)
            notifyDataSetChanged()
        }
    }
    fun removeItem(position: Int){
        this.items.removeAt(position)
        this.notifyDataSetChanged()
    }

}






