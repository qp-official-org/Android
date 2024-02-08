package com.example.qp

import android.app.Application
import android.content.Context
import android.graphics.BlurMaskFilter
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
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
        fun onAnswerModify(position:Int)
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

                override fun onCommentModify(position: Int) {
                    var content=commentAdapter.getContent(position)

                    val writeBtn=itemView.findViewById<TextView>(R.id.write_comment_btn)
                    val writeLayout=binding.writeCommentEdit
                    writeLayout.text= Editable.Factory.getInstance().newEditable(content)

                    writeBtn.setOnClickListener {
                        val newContent=writeLayout.text.toString()
                        commentAdapter.modifyComment(position,newContent)
                        writeLayout.text= Editable.Factory.getInstance().newEditable("")
                    }
                }
            })

            var isLiked=isLiked()
            setInit(position)
            setOnclick(position, commentAdapter)

            //좋아요
            binding.answerLikeBtn.setOnClickListener {
                if(isLiked){
                    likeAnswer(false)
                    binding.answerLikeBtn.setImageResource(R.drawable.like_off)
                    isLiked=false
                }
                else{
                    likeAnswer(true)
                    binding.answerLikeBtn.setImageResource(R.drawable.like_on)
                    isLiked=true
                }
            }

        }

        //답변 내용 블러 처리


        private fun setInit(position: Int){
            answerContentView.text=items[position].content
            //댓글수
            commentNumberUpdate(items[position])
            //댓글 펼치기
            showComment(true)
            setBlurText(true)

        }
        private fun setOnclick(position: Int,adapter: DetailedAnswerCommentRVAdapter){
            //댓글 펼치기/접기
            binding.answerCommentBtnLayout.setOnClickListener {
                showComment(isCommentShown)
            }
            //댓글 쓰기
            binding.writeCommentBtn.setOnClickListener {
                writeComment(binding.writeCommentEdit.text.toString(),adapter,items[position])
            }
            //더보기 팝업 메뉴
            showAnswerMorePopup(position,adapter)

        }

        //좋아요 업데이트, 자신이 '좋아요'한 상태 반환
        private fun isLiked():Boolean{
            var liked=false
            var likeNum=0

            binding.answerLikeTv.text=likeNum.toString()
            return liked
        }
        //좋아요/해제
        private fun likeAnswer(toLike:Boolean){
            if(toLike){
                //서버에 post
                val text=binding.answerLikeTv.text.toString()
                val likeNum=text.toInt()+1
                binding.answerLikeTv.text=likeNum.toString()
            }
            else{
                //서버에 post
                val text=binding.answerLikeTv.text.toString()
                val likeNum=text.toInt()-1
                binding.answerLikeTv.text=likeNum.toString()
            }
        }

        private fun getLikeCount():Int{
            var likeCount=0
            return likeCount
        }

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
        private fun writeComment(content:String,adapter:DetailedAnswerCommentRVAdapter,answer:Answer){
            if(content!=""){
                adapter.addItem(content)    //임시로 구현..
                commentNumberUpdate(answer)
                binding.writeCommentEdit.text=Editable.Factory.getInstance().newEditable("")
            }
            else{
                Toast.makeText(appContext,"댓글을 작성하십시오",Toast.LENGTH_SHORT).show()
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

        private fun setBlurText(isBlur:Boolean){
            binding.answerContentTv.setLayerType(View.LAYER_TYPE_SOFTWARE,null).apply{
                if(isBlur) binding.answerContentTv.paint.maskFilter=BlurMaskFilter(16f,BlurMaskFilter.Blur.NORMAL)
                else binding.answerContentTv.paint.maskFilter=null
            }
            val container=binding.previewContainer
            val inflater:LayoutInflater=appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.item_answer_preview,container,true)

            var charCount=binding.answerContentTv.text.count()
            var likeCount=getLikeCount()
            val textView=itemView.findViewById<TextView>(R.id.priview_tv)
            var text=charCount.toString()+"자, "+likeCount.toString()+"명이 도움이 됐대요!"
            textView.text=text


        }

        //더보기 버튼 눌렀을 때 팝업 메뉴
        private fun showAnswerMorePopup(position: Int,adapter: DetailedAnswerCommentRVAdapter){
            lateinit var popupWindow:SimplePopup
            if(adapter.isCommentListEmpty()){
                binding.answerMoreBtn.setOnClickListener {
                    Log.d("binding_content",position.toString())
                    val list= mutableListOf<String>().apply {
                        add("수정하기")
                        add("삭제하기")
                        add("신고하기")
                    }
                    popupWindow=SimplePopup(appContext,list){_,_,menuPos->
                        when(menuPos){
                            0-> {
                                Toast.makeText(appContext, "수정하기", Toast.LENGTH_SHORT).show()
                                myItemClickListener.onAnswerModify(position)
                            }
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


    fun isItemListEmpty():Boolean{
        return items.isEmpty()||items==null
    }

    fun getContent(position:Int):String{
        return items[position].content
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

    fun modifyAnswer(position: Int,content:String){
        this.items[position].content=content
        this.notifyDataSetChanged()
    }






}