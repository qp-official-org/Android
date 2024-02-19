package com.example.qp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.qp.databinding.ItemAnswerCommentBinding
import com.kakao.sdk.user.UserApiClient

class DetailedAnswerCommentRVAdapter(context: Context ):RecyclerView.Adapter<DetailedAnswerCommentRVAdapter.ViewHolder>() {
    private var appContext=context
    private val items=ArrayList<AnswerInfo>()
    private var isLogin=false

    interface CommentClickListener{
        fun onItemRemove(position:Int,answerId:Long)
        fun onCommentModify(pos: Int,answerId:Long)
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
        checkLogin()
        holder.bind(position)
    }

    override fun getItemCount(): Int =items.size


    fun checkLogin(){
        UserApiClient.instance.accessTokenInfo { token, error ->
            if (error != null) {
                Log.e("TAG", "로그인 실패", error)
            } else if (token != null) {
                isLogin=true
                Log.i("TAG", "로그인 성공 $token")
            }
        }
    }

    inner class ViewHolder(val binding:ItemAnswerCommentBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position:Int){
            var answer=items[position]
            binding.commentContentTv.text=answer.content
            binding.commentUserNameTv.text=answer.nickname
            if(answer.profileImage!=""){
                setStringImage(answer.profileImage!!,binding.commentUserIv,appContext)
            }
            answer.profileImage?.let { setStringImage(it,binding.commentUserIv,appContext) }

            showCommentMorePopup(answer,position)
        }


        private fun showCommentMorePopup(answer: AnswerInfo,position:Int){
            lateinit var popupWindow:SimplePopup
            binding.commentMoreBtn.setOnClickListener {
                if(AppData.qpUserID==answer.userId.toInt()&&isLogin){

                    val list= mutableListOf<String>().apply {
                        add("수정하기")
                        add("삭제하기")
                        add("신고하기")
                    }
                    popupWindow=SimplePopup(appContext,list){_,_,menuPos->
                        when(menuPos){
                            0-> {
                                Toast.makeText(appContext, "수정하기", Toast.LENGTH_SHORT).show()
                                myItemClickListener.onCommentModify(position,items[position].answerId!!.toLong())
                            }
                            1-> {
                                Toast.makeText(appContext, "삭제하기", Toast.LENGTH_SHORT).show()
                                myItemClickListener.onItemRemove(position,items[position].answerId!!)
                            }
                            2-> {
                                val intent= Intent(appContext,ReportActivity::class.java)
                                intent.putExtra("report",items[position].answerId)
                                intent.putExtra("category","answer")
                                ContextCompat.startActivity(appContext, intent, null)
                            }
                        }
                    }
                    popupWindow.isOutsideTouchable=true
                    popupWindow.showAsDropDown(it,40,10)
                }
                else{
                    binding.commentMoreBtn.setOnClickListener {
                        val list= mutableListOf<String>().apply {
                            add("신고하기")
                        }
                        popupWindow=SimplePopup(appContext,list){_,_,menuPos->
                            when(menuPos){
                                0-> {
                                    if(!isLogin)
                                        QpToast.createToast(appContext)
                                    else{
                                        val intent= Intent(appContext,ReportActivity::class.java)
                                        intent.putExtra("report",items[position].answerId)
                                        intent.putExtra("category","answer")
                                        ContextCompat.startActivity(appContext, intent, null)
                                    }
                                }
                            }
                        }
                        popupWindow.isOutsideTouchable=true
                        popupWindow.showAsDropDown(it,40,10)
                    }
                }

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
        return items.isEmpty()
    }
    fun addItem(position:Int,answer:AnswerInfo){
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






