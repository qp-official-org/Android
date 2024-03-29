package com.example.qp

import android.app.Application
import android.content.Context
import android.content.Intent
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
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qp.databinding.ItemAnswerBinding
import com.example.qp.databinding.ItemWriteAnswerBinding
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailedQuestionRVAdapter(context:Context): RecyclerView.Adapter<DetailedQuestionRVAdapter.ViewHolder>() {
    val items= ArrayList<AnswerInfo>()
    private var appContext=context
    private var commentAdapterList=ArrayList<DetailedAnswerCommentRVAdapter>()

    private var isLogin=false


    interface ItemClickListener{
        fun onItemRemove(position:Int,answerId:Long)
        fun onAnswerModify(position:Int,answerId:Long,view:View)

        fun showLoginMsg()
        fun reportAnswer(id:Long)

        fun scroll2viw(view:View)
        fun scrollTop(view:View)

        fun usePoint(point:Long)
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


    inner class ViewHolder(val binding:ItemAnswerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {

            commentAdapterList.add(position,DetailedAnswerCommentRVAdapter(appContext))
            commentAdapterList[position].addItems(ArrayList<AnswerInfo>())
            binding.answerCommentRv.adapter=commentAdapterList[position]

            CoroutineScope(Dispatchers.Main).launch{

                //댓글(자식 답변) 받아오기
                CoroutineScope(Dispatchers.IO).async {
                    getChildAnswerService(items[position].answerId!!,position)
                    delay(200)
                }.await()

                //댓글에 대한 동작(수정&삭제)
                commentAdapterList[position].setMyItemClickListener(object :
                    DetailedAnswerCommentRVAdapter.CommentClickListener{

                    override fun onItemRemove(pos: Int,answerId: Long) {   //댓글 삭제시
                        deleteChildAnswerService(answerId,position,pos)
                        showAnswerMorePopup(position)
                    }
                    override fun onCommentModify(pos: Int,answerId: Long,view:View) {   //댓글 수정 시
                        var content=commentAdapterList[position].getContent(pos)

                        //댓글 작성 레이아웃에 텍스트 삽입
                        val writeBtn=itemView.findViewById<TextView>(R.id.write_comment_btn)
                        val writeLayout=binding.writeCommentEdit
                        writeLayout.text= Editable.Factory.getInstance().newEditable(content)

                        //댓글 재등록
                        writeBtn.setOnClickListener {
                            val newContent=writeLayout.text.toString()
                            modifyChildAnswerService(answerId,"title",newContent,position,pos,view,commentAdapterList[position])
                        }
                        myItemClickListener.scroll2viw(binding.writeCommentEdit)
                    }

                    override fun reportChildAnswer(id:Long) {
                        myItemClickListener.reportAnswer(id)
                    }
                })

                var likeNum=items[position].likes
                var isCommentShown=false

                var isExpert=           //전문가 답변 여부
                    when(items[position].user.role){
                        "EXPERT"->true
                        else->false
                    }
                var isBought=false  //구매 여부(서버)
                //binding.commentLayout.visibility=View.GONE


                setOnclick(position, commentAdapterList[position])


                //댓글 펼치기/접기
                binding.answerCommentBtn.setOnClickListener {
                    val commentRv=binding.commentLayout
                    if(isCommentShown){ //접기
                        commentRv.visibility=View.GONE
                        isCommentShown=false
                    }
                    else{               //펼치기
                        commentRv.visibility=View.VISIBLE
                        isCommentShown=true
                        if(AppData.qpProfileImage!=""){
                            setStringImage(AppData.qpProfileImage,binding.writeCommentUserImg,appContext)
                        }
                        binding.writeCommentUserNameTv.text=AppData.qpNickname
                    }
                }

                setInit(position,likeNum?.toInt()?:0,isExpert && !isBought)

            }

        }



        private fun setInit(position: Int,likeNum: Int,isBlur:Boolean){
            binding.answerUserNameTv.text=items[position].user.nickname
            binding.answerContentTv.text=items[position].content     //답변 내용
            binding.commentLayout.visibility=View.GONE      //댓글 접은 상태
            commentNumberUpdate(position)    //댓글 수
            binding.answerLikeTv.text=likeNum.toString()    //좋아요 수
            setBlurText(isBlur,position)
            if(items[position].user.profileImage!=""&&items[position].user.profileImage!=null){
                setStringImage(items[position].user.profileImage!!,binding.answerUserImg,appContext)
            }
        }

        private fun setOnclick(position: Int,adapter: DetailedAnswerCommentRVAdapter){
            //댓글 쓰기
            binding.writeCommentBtn.setOnClickListener {
                var content=binding.writeCommentEdit.text.toString()

                if(content!=""){
                    val answerInfo=AnswerPost(
                        userId=AppData.qpUserID.toLong(),
                        content=content,
                        category = "CHILD",
                        answerGroup = items[position].answerId!!.toLong(),
                    )
                    writeChildAnswerService(answerInfo,position,adapter)
                }
                else{
                    Toast.makeText(appContext,"댓글을 작성하십시오",Toast.LENGTH_SHORT).show()
                }
            }

            //좋아요 누르기
            binding.answerLikeBtn.setOnClickListener {
                if(isLogin)
                    likeAnswerService(items[position].answerId!!.toLong(),binding,position)
                else
                    QpToast.createToast(appContext)?.show()
            }
            //더보기 팝업 메뉴
            showAnswerMorePopup(position)
        }


        //댓글수 표시
       private fun commentNumberUpdate(position: Int){
            binding.answerCommentBtnTv.text=commentAdapterList[position].itemCount.toString()
            Log.d("commentCount",items[position].answerId.toString().plus(commentAdapterList[position].itemCount.toString()))
        }

        //답변 블러 처리
        private fun setBlurText(isBlur:Boolean,position: Int){
            var isMine=items[position].user.userId==AppData.qpUserID.toLong()
            binding.answerContentTv.setLayerType(View.LAYER_TYPE_SOFTWARE,null).apply{
                if(isBlur&&!isMine) binding.answerContentTv.paint.maskFilter=BlurMaskFilter(16f,BlurMaskFilter.Blur.NORMAL)
                else binding.answerContentTv.paint.maskFilter=null
            }
            val container=binding.previewContainer
            container.removeAllViews()

            if(isBlur&&!isMine){
                Log.d("setBlurLog",items[position].content+isBlur.toString()+isMine.toString())
                val inflater:LayoutInflater=appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                inflater.inflate(R.layout.item_answer_preview,container,true)

                var charCount=binding.answerContentTv.text.count()
                var likeCount=items[position].likes
                val textView=itemView.findViewById<TextView>(R.id.priview_tv)
                var text=charCount.toString()+"자, "+likeCount.toString()+"명이 도움이 됐대요!"
                textView.text=text

                binding.answerCommentBtn.isClickable=false
                binding.answerLikeBtn.isClickable=false

                val btn=itemView.findViewById<TextView>(R.id.preview_btn)
                btn.setOnClickListener {
                    if(AppData.qpPoint>100){
                        val userPoint=UserPoint(-100)
                        AppData.changePoint(AppData.qpAccessToken,AppData.qpUserID,userPoint)
                        Log.d("usePoint",AppData.qpPoint.toString())
                        container.removeAllViews()
                        setBlurText(false,position)
                        binding.answerCommentBtn.isClickable=true
                        binding.answerLikeBtn.isClickable=true

                        myItemClickListener.usePoint(100)
                    }
                    else{
                        QpToast.createToast(appContext,"포인트가 부족합니다")?.show()
                    }

                }
            }

        }

        //더보기 버튼 눌렀을 때 팝업 메뉴
        private fun showAnswerMorePopup(position: Int){
            lateinit var popupWindow:SimplePopup

            binding.answerMoreBtn.setOnClickListener {
                var isMine=items[position].user.userId!!.toInt()==AppData.qpUserID
                Log.d("answerMore",commentAdapterList[position].isCommentListEmpty().toString()+isMine)
                if(commentAdapterList[position].isCommentListEmpty()&&isMine&&isLogin){

                    val list= mutableListOf<String>().apply {
                        add("수정하기")
                        add("삭제하기")
                        add("신고하기")
                    }
                    popupWindow=SimplePopup(appContext,list){_,_,menuPos->
                        when(menuPos){
                            0-> {
                                    myItemClickListener.onAnswerModify(position,items[position].answerId!!,itemView)
                            }
                            1-> {
                                myItemClickListener.onItemRemove(position,items[position].answerId!!)
                            }
                            2-> {
                                if(!isLogin){
                                    myItemClickListener.showLoginMsg()
                                }
                                else{
                                    myItemClickListener.reportAnswer(items[position].answerId!!)
                                }
                            }
                        }
                    }
                    popupWindow.isOutsideTouchable=true
                    popupWindow.showAsDropDown(it,40,10)
                }
                else{
                    binding.answerMoreBtn.setOnClickListener {
                        val list= mutableListOf<String>().apply {
                            add("신고하기")
                        }
                        popupWindow=SimplePopup(appContext,list){_,_,position->
                            when(position){
                                0-> {
                                    if(!isLogin){
                                        myItemClickListener.showLoginMsg()
                                    }
                                    else{
                                        myItemClickListener.reportAnswer(items[position].answerId!!)
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
        fun writeChildAnswerService(answer:AnswerPost,position: Int,adapter: DetailedAnswerCommentRVAdapter){
            val questionService= getRetrofit().create(QuestionInterface::class.java)

            questionService.writeAnswer(AppData.qpAccessToken,AppData.qpUserID.toLong(),answer).enqueue(object:Callback<WriteAnswerResponse>{
                override fun onResponse(
                    call: Call<WriteAnswerResponse>,
                    response: Response<WriteAnswerResponse>
                ) {
                    val resp=response.body()
                    when(resp?.code){
                        "ANSWER_3000"->{
                            Log.d("writeChildAnswer/SUCCESS",resp.toString())
                            var answerInfo=AnswerInfo(
                                user=AnswerUser(AppData.qpUserID.toLong(),AppData.qpNickname,AppData.qpProfileImage,AppData.qpRole),
                                answerId=resp.result.answerId,
                                content=answer.content,
                                category = answer.category,
                                answerGroup = answer.answerGroup,
                                likes=0,
                                childCount = 0
                            )
                            adapter.addItem(0,answerInfo)
                            commentNumberUpdate(position)
                            binding.writeCommentEdit.text=Editable.Factory.getInstance().newEditable("")

                            //myItemClickListener.scroll2viw(itemView)
                        }
                        else->{
                            val msg=response.errorBody()?.string().toString()
                            QpToast.createToast(appContext,"댓글 등록 실패:"+msg)?.show()
                            Log.d("writeChildAnswer/FAIL",msg)
                            Log.d("writeAnswer/user","token:"+AppData.qpAccessToken+"userId: "+AppData.qpUserID)
                        }
                    }
                }

                override fun onFailure(call: Call<WriteAnswerResponse>, t: Throwable) {
                    Log.d("writeChildAnswerResp/FAIL",t.message.toString())
                }

            })
        }

        fun modifyChildAnswerService(answerId:Long,title:String,content: String,parentPos: Int,childPos:Int,view:View,adapter:DetailedAnswerCommentRVAdapter){
            val questionService= getRetrofit().create(QuestionInterface::class.java)
            val modifyQInfo=ModifyQInfo(AppData.qpUserID.toLong(),title,content)

            questionService.modifyAnswer(AppData.qpAccessToken,answerId,modifyQInfo).enqueue(object :Callback<ModifyAnswerResponse>{
                override fun onResponse(
                    call: Call<ModifyAnswerResponse>,
                    response: Response<ModifyAnswerResponse>
                ) {
                    val resp=response.body()
                    when(resp?.code){
                        "ANSWER_3000"->{
                            Log.d("modifyChild/SUCCESS",resp.toString())
                            commentAdapterList[parentPos].modifyComment(childPos,content)
                            binding.writeCommentEdit.text= Editable.Factory.getInstance().newEditable("")
                            //myItemClickListener.scroll2viw(view)

                            //댓글 쓰기 기능으로..
                            binding.writeCommentBtn.setOnClickListener {
                                var content=binding.writeCommentEdit.text.toString()

                                if(content!=""){
                                    val answerInfo=AnswerPost(
                                        userId=AppData.qpUserID.toLong(),
                                        content=content,
                                        category = "CHILD",
                                        answerGroup = items[parentPos].answerId!!.toLong(),
                                    )
                                    writeChildAnswerService(answerInfo,parentPos,adapter)
                                }
                                else{
                                    Toast.makeText(appContext,"댓글을 작성하십시오",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        else->{
                            Log.d("modifyChild/FAIL",response.errorBody()?.string().toString())
                        }
                    }
                }

                override fun onFailure(call: Call<ModifyAnswerResponse>, t: Throwable) {
                    Log.d("modifyChildResp/FAIL",t.message.toString())
                }

            })
        }
        fun deleteChildAnswerService(answerId:Long,parentPos: Int,childPos: Int){
            val questionService= getRetrofit().create(QuestionInterface::class.java)
            questionService.deleteAnswer(AppData.qpAccessToken,answerId,AppData.qpUserID.toLong()).enqueue(object :Callback<ModifyAnswerResponse>{
                override fun onResponse(
                    call: Call<ModifyAnswerResponse>,
                    response: Response<ModifyAnswerResponse>
                ) {
                    val resp=response.body()
                    when(resp?.code){
                        "ANSWER_3000"->{
                            Log.d("deleteChild/SUCCESS",resp.toString())
                            commentAdapterList[parentPos].removeItem(childPos)
                            commentNumberUpdate(parentPos)
                        }
                        else->{
                            Log.d("deleteChild/FAIL",response.errorBody()?.string().toString())
                        }
                    }                }

                override fun onFailure(call: Call<ModifyAnswerResponse>, t: Throwable) {
                    Log.d("deleteChildResp/FAIL",t.message.toString())
                }

            })
        }
        fun getChildAnswerService(id:Long,position:Int){
            val questionService= getRetrofit().create(QuestionInterface::class.java)

            questionService.getChildAnswer(id,0,10).enqueue(object : Callback<ChildAnswerResponse>{
                override fun onResponse(
                    call: Call<ChildAnswerResponse>,
                    response: Response<ChildAnswerResponse>
                ) {
                    val resp=response.body()
                    when(resp?.code){
                        "ANSWER_3000"->{
                            Log.d("getChild/SUCCESS",resp.toString())
                            commentAdapterList[position].addItems(resp.result.answerList)
                            commentNumberUpdate(position)
                        }
                        else->{
                            Log.d("getChild/FAIL",response.errorBody()?.string().toString())
                        }
                    }
                }

                override fun onFailure(call: Call<ChildAnswerResponse>, t: Throwable) {
                    Log.d("getChildResp/FAIL",t.message.toString())
                }

            })
        }


    }


    fun isItemListEmpty():Boolean{
        return items.isEmpty()
    }

    fun getContent(position:Int):String{
        return items[position].content
    }

    fun addItem(item: AnswerInfo) {
        this.items.add(0,item)
        this.notifyDataSetChanged()
    }
    fun addItemList(items:ArrayList<AnswerInfo>?){
        if(items!=null){
            this.items.addAll(items)
            this.notifyDataSetChanged()
        }
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








    fun likeAnswerService(answerId:Long,binding:ItemAnswerBinding,position: Int){
        val questionService= getRetrofit().create(QuestionInterface::class.java)

        questionService.likeAnswer(AppData.qpAccessToken,AppData.qpUserID,answerId).enqueue(object:Callback<LikeAnswerResponse>{
            override fun onResponse(
                call: Call<LikeAnswerResponse>,
                response: Response<LikeAnswerResponse>
            ) {
                val resp=response.body()
                Log.d("likeAnswerResp",response.toString().plus("answerId").plus(answerId))
                when(resp?.code){
                    "ANSWERLIKE_7000"->{
                        Log.d("likeAnswer/SUCCESS",resp.toString())
                        if(resp.result.answerLikeStatus=="DELETED"){
                            items[position].likes = items[position].likes?.minus(1)
                            binding.answerLikeTv.text=items[position].likes.toString()
                        }
                        else if(resp.result.answerLikeStatus=="ADDED"){
                            items[position].likes = items[position].likes?.plus(1)
                            binding.answerLikeTv.text=items[position].likes.toString()
                        }
                    }
                    else->{
                        Log.d("likeAnswer/FAIL",response.errorBody()?.string().toString())
                        if(response.code()==400)
                            Toast.makeText(appContext,"로그인이 필요한 기능입니다",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<LikeAnswerResponse>, t: Throwable) {
                Log.d("likeAnswerResp/FAIL",t.message.toString())
            }

        })
    }
}