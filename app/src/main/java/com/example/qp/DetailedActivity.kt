package com.example.qp

import android.content.Context
import android.graphics.Rect
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.qp.databinding.ActivityDetailedBinding
import com.example.qp.databinding.ItemAnswerBinding
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.Serializable


class DetailedActivity : AppCompatActivity(),DetailedQView{

    private lateinit var binding: ActivityDetailedBinding
    private var gson: Gson = Gson()
    private lateinit var answerAdapter:DetailedQuestionRVAdapter
    private lateinit var questionInfo:QuestionInfo
    private var isNotified:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra("question")){
            val qJson = intent.getStringExtra("question")
            questionInfo = gson.fromJson(qJson, QuestionInfo::class.java)
            Log.d("DetailedQInfo",questionInfo.toString())
        }
        else{
            questionInfo= QuestionInfo(title="",content="")
        }


        CoroutineScope(Dispatchers.Main).launch {
            val server= CoroutineScope(Dispatchers.IO).async {
                getQ()
                Log.d("getServer","server")
                delay(200)
            }.await()

            Log.d("view","view")

            //검색으로 화면전환
            binding.detailedSearchBt.setOnClickListener {
                val qDatas = intent.getSerializableExtra("qDatas") as ArrayList<QuestionInfo>
                val intent = Intent(this@DetailedActivity, SearchActivity::class.java)
                intent.putExtra("qDatas", qDatas)
                startActivity(intent)
            }

            setInit()
            setOnClickListeners()

            setQuestionMorePopup()

            //프로필로 화면 전환
            binding.detailedProfileBtn.setOnClickListener {
                startActivity(Intent(this@DetailedActivity, ProfileActivity::class.java))
                finish()
            }

            Log.d("detailedQOncreate",questionInfo.toString())
        }

    }


    fun getQ()= CoroutineScope(Dispatchers.IO).launch{
        //서버에서 질문 조회 응답
        val detailedQService=QuestionService()
        detailedQService.setDetailedQView(this@DetailedActivity)

        detailedQService.getQuestion(questionInfo.questionId?.toLong())

        //서버에서 답변 받아오기
        answerAdapter=DetailedQuestionRVAdapter(applicationContext,this@DetailedActivity)
        binding.answerRv.adapter=answerAdapter

        detailedQService.getParentAnswer(questionInfo.questionId!!.toLong(),true)

    }


    private fun setInit(){
        isNotified=isNotified()
        initView()

        initAnswerData()

        answerAdapter.setMyItemClickListener(object :
            DetailedQuestionRVAdapter.ItemClickListener{
            //답변 삭제
            override fun onItemRemove(position:Int) {
                answerAdapter.removeItem(position)
                updateExpertNum()
                if(answerAdapter.isItemListEmpty()){
                    binding.answerBtn.visibility=View.VISIBLE
                    updateNotifyView()
                }
            }
            //답변 수정
            override fun onAnswerModify(position: Int) {
                val container=binding.writeAnswerContainer
                var content=answerAdapter.getContent(position)

                val inflater:LayoutInflater=getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                inflater.inflate(R.layout.item_write_answer,container,true)

                val writeBtn=findViewById<TextView>(R.id.write_answer_btn)
                val writeLayout=findViewById<EditText>(R.id.write_answer_edit)
                writeLayout.text= Editable.Factory.getInstance().newEditable(content)

                writeBtn.setOnClickListener {
                    val newContent=writeLayout.text.toString()
                    answerAdapter.modifyAnswer(position,newContent)
                    container.removeAllViews()
                }

            }

        })

        //updateNotifyView()


    }

    private fun initView(){
        binding.detailedQuestionTitleTv.text = questionInfo.title
        binding.detailedQuestionContentTv.text = questionInfo.content
        binding.detailedQuestionTimeTv.text = questionInfo.createAt.toString()

        val tagListSize = questionInfo.hashtags?.size
        when(tagListSize){
            1->binding.hashtag1.text = questionInfo.hashtags!![0].hashtag
            2->{
                binding.hashtag1.text = questionInfo.hashtags!![0].hashtag
                binding.hashtag2.text = questionInfo.hashtags!![1].hashtag
            }
            3->{
                binding.hashtag1.text = questionInfo.hashtags!![0].hashtag
                binding.hashtag2.text = questionInfo.hashtags!![1].hashtag
                binding.hashtag3.text = questionInfo.hashtags!![2].hashtag
            }
        }

        val isLogin = intent.getIntExtra("isLogin", 0)
        if(isLogin == 1) {
            binding.detailedLoginBtn.visibility = View.GONE
            binding.detailedProfileBtn.visibility = View.VISIBLE
        }
        else {
            binding.detailedLoginBtn.visibility = View.VISIBLE
            binding.detailedProfileBtn.visibility = View.GONE
        }



    }
    private fun initAnswerData(){
        val answerList =ArrayList<AnswerInfo>()

        /*val commentList=ArrayList<Comment>()
        val commentList2=ArrayList<Comment>()

        commentList.apply {
            add(Comment("댓글1"))
            add(Comment("댓글2"))
        }
        commentList2.apply {
            add(Comment("댓글2-1"))
            add(Comment("댓글2-2"))
        }*/

        answerList.apply {
            add(AnswerInfo(0,0,"제목1","답변내용1","PARENT",0,0))
            /*add(Answer("답변내용",commentList2))
            add(Answer("답변내용3"))*/
        }
        answerAdapter.addItemList(answerList)
        updateExpertNum()

    }
    private fun isNotified(): Boolean {
        return false     //서버에서 정보 받아와 설정
    }

    private fun setOnClickListeners(){
        binding.answerBtn.setOnClickListener {
            showWriteAnswerEdit(true)
        }
        binding.answerNoticeBtn.setOnClickListener {
            if(!isNotified){
                notifyQuestion(true)
                val dialog=SimpleDialog()
                dialog.show(supportFragmentManager,"dialog")
            }
            else{
                notifyQuestion(false)
            }
        }

        binding.detailedLoginBtn.setOnClickListener {
            startActivity(Intent(this@DetailedActivity, LoginActivity::class.java))
        }
        binding.detailedProfileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))

        }
    }


    private fun updateNotifyView(){
        var container=binding.noticeContainer
        if(answerAdapter.isItemListEmpty()){
            val inflater:LayoutInflater=getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.item_notice,container,true)

            val notifyBtn=findViewById<TextView>(R.id.notify_btn)
            notifyBtn.setOnClickListener {
                notifyQuestion(true)
                container.removeAllViews()
            }
        }
        else{
            container.removeAllViews()
        }


    }
    private fun updateExpertNum(){
        binding.answerCountTv.text=answerAdapter.itemCount.toString()+"명의 전문가가 답변을 했어요"
    }

    private fun notifyQuestion(toNotify:Boolean){
        if(toNotify){
            binding.answerNoticeBtn.setImageResource(R.drawable.notification_on)
            isNotified=true
            Toast.makeText(applicationContext,"답변 알림 설정",Toast.LENGTH_SHORT).show()
        }
        else{
            binding.answerNoticeBtn.setImageResource(R.drawable.notification_off)
            isNotified=false
            Toast.makeText(applicationContext,"답변 알림 해제",Toast.LENGTH_SHORT).show()
        }
    }





    private fun writeAnswer(){
        val btn=findViewById<TextView>(R.id.write_answer_btn)
        val editText=findViewById<EditText>(R.id.write_answer_edit)

        btn.setOnClickListener {
            var content=editText.text.toString()
            answerAdapter.addItem(AnswerInfo(0,0,"title",content,"PARENT",0,0))  //임시로 구현..
            showWriteAnswerEdit(false)
            updateNotifyView()
            updateExpertNum()
            Toast.makeText(applicationContext,"답변이 등록되었습니다.",Toast.LENGTH_SHORT).show()
        }
    }

    private fun showWriteAnswerEdit(toShow:Boolean){
        val container=binding.writeAnswerContainer
        if(toShow){
            binding.answerBtn.visibility=View.GONE
            val inflater:LayoutInflater=getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.item_write_answer,container,true)
            writeAnswer()
        }
        else{
            container.removeAllViews()
        }

    }



    private fun setQuestionMorePopup(){
        lateinit var popupWindow: SimplePopup
        if(answerAdapter.isItemListEmpty()){
            binding.questionMoreBtn.setOnClickListener {
                val list= mutableListOf<String>().apply {
                    add("수정하기")
                    add("삭제하기")
                    add("신고하기")
                }
                popupWindow=SimplePopup(applicationContext,list){_,_,position->
                    when(position){
                        0-> {
                            val gson= Gson()
                            val qJson=gson.toJson(questionInfo)
                            val intent=Intent(this@DetailedActivity,ModifyQuestionActivity::class.java)
                            intent.putExtra("modifyQuestion",qJson)
                            startActivity(intent)
                            Log.d("modifyLog",questionInfo.toString())
                            Toast.makeText(applicationContext, "수정하기", Toast.LENGTH_SHORT).show()
                        }
                        1->Toast.makeText(applicationContext,"삭제하기",Toast.LENGTH_SHORT).show()
                        2-> {
                            Toast.makeText(applicationContext, "신고하기", Toast.LENGTH_SHORT).show()
                            val dialog=SimpleDialog()
                            dialog.show(supportFragmentManager,"dialog")
                        }
                    }
                }
                popupWindow.isOutsideTouchable=true
                popupWindow.showAsDropDown(it,40,10)
            }
            }
        else{
            binding.questionMoreBtn.setOnClickListener {

                val list= mutableListOf<String>().apply {
                        add("신고하기")
                }
                popupWindow=SimplePopup(applicationContext,list){_,_,position->
                    when(position){
                        0-> {
                            Toast.makeText(applicationContext, "신고하기", Toast.LENGTH_SHORT).show()
                            val dialog=SimpleDialog()
                            dialog.show(supportFragmentManager,"dialog")
                        }
                    }
                }
                popupWindow.isOutsideTouchable=true
                popupWindow.showAsDropDown(it,40,10)
            }

        }

    }

    //editText 밖을 터치하면 키보드 내려감
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action === MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onGetQSuccess(questionResp:QuestionInfo?) {
        questionInfo.content=(questionResp?.content.toString())
        Log.d("getQ/SUCCESS",questionResp?.content.toString()+"questionInfo: ".plus(questionInfo.content))
    }

    override fun onGetQFailure(msg:String) {
        Log.d("getQ/FAIL",msg)
    }

    override fun onGetParentSuccess(answerList:ArrayList<AnswerInfo>?) {
        Log.d("getParent/SUCCESS",answerList.toString())
        answerAdapter.addItemList(answerList)
    }

    override fun onGetaParentFailure(msg:String) {
        Log.d("getParent/FAIL",msg)
    }

    override fun onGetChildSuccess(answerList: ArrayList<AnswerInfo>?, id: Long, position: Int) {
        answerAdapter.getChildAnswer(answerList,id,position)
        Log.d("getChild/SUCCESS",answerList.toString())
    }

    override fun onGetChildFailure(msg:String) {
        Log.d("getChild/FAIL",msg)
    }

//    override fun getChildAnswer(binding: ItemAnswerBinding, id: Long, position: Int) {
//
//    }


}