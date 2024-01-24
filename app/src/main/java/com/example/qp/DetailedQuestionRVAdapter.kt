package com.example.qp

import android.app.Application
import android.content.Context
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qp.databinding.ItemAnswerBinding
import com.example.qp.databinding.ItemWriteAnswerBinding

class DetailedQuestionRVAdapter(context:Context): RecyclerView.Adapter<DetailedQuestionRVAdapter.ViewHolder>() {
    val items= ArrayList<Answer>()
    private var isCommentShown=false
    private var appContext=context


    interface ItemClickListener{
        fun onItemRemove(position:Int)
    }

    private lateinit var myItemClickListener: ItemClickListener
    fun setMyItemClickListener(itemClickListener: ItemClickListener){
        myItemClickListener=itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):DetailedQuestionRVAdapter.ViewHolder {
        val binding:ItemAnswerBinding=ItemAnswerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: DetailedQuestionRVAdapter.ViewHolder, position: Int) {
        holder.bind(position)

    }
    override fun getItemCount(): Int =items.size

    inner class ViewHolder(val binding:ItemAnswerBinding) : RecyclerView.ViewHolder(binding.root) {
        private val answerContentView=binding.answerContentTv
        private val profileView=itemView.findViewById<ImageView>(R.id.question_user_img)
        fun bind(position: Int) {

            lateinit var commentAdapter:DetailedAnswerCommentRVAdapter
            if(items[position].commentList!=null){
                commentAdapter=DetailedAnswerCommentRVAdapter(appContext,items[position].commentList!!)
                binding.answerCommentRv.adapter=commentAdapter
            }
            commentAdapter.setMyItemClickListener(object :
                DetailedAnswerCommentRVAdapter.CommentClickListener{
                override fun onItemRemove(pos: Int) {
                    commentAdapter.removeItem(pos)
                    commentNumberUpdate(items[position])
                }
            })

            setInit(position)
            setOnclick(position, commentAdapter)

        }

        private fun setInit(position: Int){
            answerContentView.text=items[position].content
            //댓글수
            commentNumberUpdate(items[position])
            //댓글 펼치기/접기
            showComment(true)
            showAnswerMorePopup(items[position],position)
        }
        private fun setOnclick(position: Int,adapter: DetailedAnswerCommentRVAdapter){
            binding.answerCommentBtnLayout.setOnClickListener {
                showComment(isCommentShown)
            }
            //댓글 쓰기
            binding.writeCommentBtn.setOnClickListener {
                writeComment(binding.writeCommentEdit.text.toString(),adapter,items[position])
            }
        }

        //private fun notifyAnswer


        //댓글 펼치기
        private fun showComment(isShown: Boolean){
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
        //댓글 등록
        private fun writeComment(content:String?,adapter:DetailedAnswerCommentRVAdapter,answer:Answer){
            if(content!=null){
                adapter.addItem(content)    //임시로 구현..
                commentNumberUpdate(answer)
                binding.writeCommentEdit.text=Editable.Factory.getInstance().newEditable("")
            }
            else{
                Toast.makeText(appContext,"댓글을 작성하십시오",Toast.LENGTH_SHORT)
            }
        }
        //댓글수 표시
        private fun commentNumberUpdate(answer:Answer){
            binding.answerCommentBtnTv.text=
                when (answer.commentList){
                    null->"0"
                    else->answer.commentList!!.size.toString()
                }

        }

        //더보기 버튼 눌렀을 때 팝업 메뉴
        private fun showAnswerMorePopup(answer:Answer,position: Int){
            lateinit var popupWindow:SimplePopup
            if(answer.commentList.isEmpty()||answer.commentList==null){
                binding.answerMoreBtn.setOnClickListener {
                    Log.d("binding_content",position.toString())
                    val list= mutableListOf<String>().apply {
                        add("수정하기")
                        add("삭제하기")
                        add("신고하기")
                    }
                    popupWindow=SimplePopup(appContext,list){_,_,menuPos->
                        when(menuPos){
                            0->Toast.makeText(appContext,"수정하기",Toast.LENGTH_SHORT).show()
                            1-> {
                                //Toast.makeText(appContext, "삭제하기", Toast.LENGTH_SHORT).show()
                                myItemClickListener.onItemRemove(position)    //임시로 구현
                            }
                            2->Toast.makeText(appContext,"신고하기",Toast.LENGTH_SHORT).show()
                        }
                    }
                    popupWindow.isOutsideTouchable=true
                    popupWindow.showAsDropDown(it,40,10)
                }
            }
            else{
                binding.answerMoreBtn.setOnClickListener {
                    val list= mutableListOf<String>().apply {
                        add("신고하기")
                    }
                    popupWindow=SimplePopup(appContext,list){_,_,position->
                        when(position){
                            0->Toast.makeText(appContext,"신고하기",Toast.LENGTH_SHORT).show()
                        }
                    }
                    popupWindow.isOutsideTouchable=true
                    popupWindow.showAsDropDown(it,40,10)
                }

            }

        }


    }



    fun addItem(item: Answer) {
        this.items.add(item)
        this.notifyDataSetChanged()
    }
    fun addItemList(items:ArrayList<Answer>){
        this.items.addAll(items)
        this.notifyDataSetChanged()
    }

    fun removeItem(position: Int){
        Log.d("removeItem",position.toString())
        this.items.removeAt(position)
        this.notifyDataSetChanged()
    }






}