package com.example.qp

import android.content.Context
import android.graphics.Rect
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qp.databinding.ActivityDetailedBinding
import com.google.gson.Gson
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions


class DetailedActivity : AppCompatActivity(),DetailedQView{

    private lateinit var binding: ActivityDetailedBinding
    private var gson: Gson = Gson()
    private lateinit var answerAdapter:DetailedQuestionRVAdapter
    private lateinit var questionInfo:QuestionInfo
    private var isNotified:Boolean=false
    private var isLogin:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //질문 정보 넘겨받기
        if(intent.hasExtra("question")){
            val qJson = intent.getStringExtra("question")
            questionInfo = gson.fromJson(qJson, QuestionInfo::class.java)
            Log.d("DetailedQInfo",questionInfo.toString())
        }
        else{
            questionInfo= QuestionInfo(title="",content="")
        }


        // 임시 로그아웃 (로고 클릭시)
        binding.detailedLogoIv.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    QpToast.createToast(applicationContext,"로그아웃 실패 $error")?.show()
                }else {
                    QpToast.createToast(applicationContext,"로그아웃 성공")?.show()
                    isLogin=false
                    AppData.qpUserID = 0
                    AppData.qpAccessToken = ""
                    AppData.searchRecord.clear()
                    binding.detailedLoginBtn.visibility = View.VISIBLE
                    binding.detailedLoginSuccessBt.visibility = View.GONE
                }
            }
        }


        CoroutineScope(Dispatchers.Main).launch {
            //서버에서 질문 & 답변 정보 받아오기
            CoroutineScope(Dispatchers.IO).async {
                getQ()
                Log.d("getServer","server")
                delay(200)
            }.await()
            Log.d("view","view")

            setInit()


            //프로필로 화면 전환
            binding.detailedLoginSuccessBt.setOnClickListener {
                startActivity(Intent(this@DetailedActivity, ProfileActivity::class.java))
            }

            //검색으로 화면전환
            binding.detailedSearchBt.setOnClickListener {
                startActivity(Intent(this@DetailedActivity, SearchActivity::class.java))
            }
        }

        Log.d("detailedQOncreate", questionInfo.toString())

        Log.d("userInfo",AppData.qpUserID.toString()+AppData.qpNickname)
    }


    //서버에서 질문, 답변 정보 응답 받아오기
    fun getQ()= CoroutineScope(Dispatchers.IO).launch{
        CoroutineScope(Dispatchers.IO).async {
            //서버에서 질문 조회 응답
            val detailedQService=QuestionService()
            detailedQService.setDetailedQView(this@DetailedActivity)

            detailedQService.getQuestion(questionInfo.questionId?.toLong())
        }.await()

        //서버에서 답변 받아오기
        //답변 리사이클러뷰 어댑터 설정
        answerAdapter=DetailedQuestionRVAdapter(applicationContext)
        binding.answerRv.adapter=answerAdapter

        getParentAnswerService(questionInfo.questionId!!.toLong())

    }


    private fun setInit(){

        // 로그인 여부 확인
        UserApiClient.instance.accessTokenInfo { token, error ->
            if (error != null) {
                Log.e("TAG", "로그인 실패", error)
                binding.detailedLoginBtn.visibility = View.VISIBLE
                binding.detailedLoginSuccessBt.visibility = View.GONE
            } else if (token != null) {
                isLogin=true
                Log.i("TAG", "로그인 성공 $token")
                binding.detailedLoginBtn.visibility = View.GONE
                binding.detailedLoginSuccessBt.visibility = View.VISIBLE
            }
        }

        isNotified=isNotified()

        //뷰 초기화
        initView()

        answerAdapter.setMyItemClickListener(object :
            DetailedQuestionRVAdapter.ItemClickListener{
            //답변 삭제
            override fun onItemRemove(position:Int,answerId: Long) {
                deleteAnswer(answerId,position)
            }
            //답변 수정
            override fun onAnswerModify(position: Int,answerId:Long) {
                val container=binding.writeAnswerContainer
                var content=answerAdapter.getContent(position)

                container.removeAllViews()
                val inflater:LayoutInflater=getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                inflater.inflate(R.layout.item_write_answer,container,true)
                binding.answerBtn.visibility=View.GONE

                val userNickname=findViewById<TextView>(R.id.write_answer_user_name_tv)
                userNickname.text=AppData.qpNickname

                val userImgView=findViewById<ImageView>(R.id.write_answer_user_img)
                if(AppData.qpProfileImage!=""){
                    setStringImage(AppData.qpProfileImage,userImgView,applicationContext)
                }
                val writeBtn=findViewById<TextView>(R.id.write_answer_btn)
                val writeLayout=findViewById<EditText>(R.id.write_answer_edit)
                writeLayout.text= Editable.Factory.getInstance().newEditable(content)

                writeBtn.setOnClickListener {
                    val newContent=writeLayout.text.toString()
                    modifyAnswerService(answerId,"title",newContent,position)
                }
            }

            override fun showLoginMsg() {
                QpToast.createToast(applicationContext)?.show()
            }
        })

        //답변 공간 펼치기
        binding.answerBtn.setOnClickListener {
            if(isLogin)
                showWriteAnswerEdit(true)
            else
                QpToast.createToast(applicationContext)?.show()
        }
        //질문 알림 설정
        binding.answerNoticeBtn.setOnClickListener {
            Log.d("isLogin",isLogin.toString())
            if(!isLogin){
                QpToast.createToast(applicationContext)?.show()
            }
            else{
                if(!isNotified){
                    notifyQuestion(true)
                }
                else{
                    notifyQuestion(false)
                }
            }

        }
        //로그인 버튼
        binding.detailedLoginBtn.setOnClickListener {
            startActivity(Intent(this@DetailedActivity, LoginActivity::class.java))
        }
        //프로필 화면으로
        binding.detailedLoginSuccessBt.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        setQuestionMorePopup()

        //updateNotifyView()
    }

    private fun initView(){
        binding.detailedQuestionTitleTv.text = questionInfo.title
        binding.detailedQuestionContentTv.text = questionInfo.content
        binding.detailedQuestionTimeTv.text =getTime(questionInfo.createdAt.toString())
        if(questionInfo.user!!.profileImage!=""){
            setStringImage(questionInfo.user!!.profileImage,binding.questionUserImg,applicationContext)
        }

        if(questionInfo.childStatus=="ACTIVE")
            binding.detailedChildStatusTv.visibility=View.VISIBLE
        else
            binding.detailedChildStatusTv.visibility=View.GONE

        updateExpertNum()

        val tagListSize = questionInfo.hashtags?.size
        when(tagListSize){
            1->binding.hashtag1.text = "#".plus(questionInfo.hashtags!![0].hashtag)
            2->{
                binding.hashtag1.text = "#".plus(questionInfo.hashtags!![0].hashtag)
                binding.hashtag2.text = "#".plus(questionInfo.hashtags!![1].hashtag)
            }
            3->{
                binding.hashtag1.text = "#".plus(questionInfo.hashtags!![0].hashtag)
                binding.hashtag2.text = "#".plus(questionInfo.hashtags!![1].hashtag)
                binding.hashtag3.text = "#".plus(questionInfo.hashtags!![2].hashtag)
            }
        }
        // 하단 바에 사용자 닉네임과 포인트 데이터 반영
        binding.detailedBarNicknameTv.text = AppData.qpNickname
        binding.detailedBarCoinTv.text = AppData.qpPoint.toString()

    }



    private fun isNotified(): Boolean {
        return false     //서버에서 정보 받아와 설정
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
        binding.answerCountTv.text=
            when(questionInfo.expertCount){
                0->"전문가의 답변이 필요합니다"
                else->questionInfo.expertCount.toString()+"명의 전문가가 답변을 했어요"
            }
    }

    private fun notifyQuestion(toNotify:Boolean){
        if(toNotify){
            binding.answerNoticeBtn.setImageResource(R.drawable.notification_on)
            isNotified=true
            QpToast.createToast(applicationContext,"답변 알림 설정")?.show()
        }
        else{
            binding.answerNoticeBtn.setImageResource(R.drawable.notification_off)
            isNotified=false
            QpToast.createToast(applicationContext,"답변 알림 해제")?.show()

        }
    }

    private fun writeAnswer(){
        val btn=findViewById<TextView>(R.id.write_answer_btn)
        val editText=findViewById<EditText>(R.id.write_answer_edit)

        btn.setOnClickListener {
            var content=editText.text.toString()
            var answer=AnswerInfo(
                0,
                AppData.qpUserID.toLong(),
                AppData.qpNickname,
                "USER",
                AppData.qpProfileImage,
                "title11",
                 content,
                "PARENT",
                0,
                0
            )
            writeAnswerService(answer,AppData.qpAccessToken)
        }
    }

    //답변 작성 공간 펼치기
    private fun showWriteAnswerEdit(toShow:Boolean){
        val container=binding.writeAnswerContainer
        if(toShow){
            binding.answerBtn.visibility=View.GONE
            val inflater:LayoutInflater=getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.item_write_answer,container,true)

            val userNickname=findViewById<TextView>(R.id.write_answer_user_name_tv)
            userNickname.text=AppData.qpNickname

            val userImgView=findViewById<ImageView>(R.id.write_answer_user_img)
            if(AppData.qpProfileImage!=""){
                setStringImage(AppData.qpProfileImage,userImgView,applicationContext)
            }
            writeAnswer()
        }
        else{
            container.removeAllViews()
            binding.answerBtn.visibility=View.VISIBLE
        }
    }


    //더보기 버튼
    private fun setQuestionMorePopup(){
        lateinit var popupWindow: SimplePopup
        var isMine=questionInfo.user?.userId==AppData.qpUserID  //본인이 작성한 질문인지 여부

        binding.questionMoreBtn.setOnClickListener {
            if(answerAdapter.isItemListEmpty()&&isMine&&isLogin){
                val list= mutableListOf<String>().apply {
                    add("수정하기")
                    add("삭제하기")
                    add("신고하기")
                }
                popupWindow=SimplePopup(applicationContext,list){_,_,position->
                    when(position){
                        0-> {   //수정하기
                            val intent=Intent(this@DetailedActivity,ModifyQuestionActivity::class.java)
                            intent.putExtra("modifyQuestion",questionInfo)
                            startActivity(intent)
                            Log.d("modifyLog",questionInfo.toString())
                            Toast.makeText(applicationContext, "수정하기", Toast.LENGTH_SHORT).show()
                        }
                        1-> {   //삭제하기
                                QpToast.createToast(applicationContext,"질문 삭제")?.show()
                                deleteQ(AppData.qpAccessToken, questionInfo.questionId, AppData.qpUserID)
                        }
                        2-> {   //신고하기
                            Toast.makeText(applicationContext, "신고하기", Toast.LENGTH_SHORT).show()
                            if(!isLogin){
                                QpToast.createToast(applicationContext)?.show()
                            }
                        }
                    }
                }
                popupWindow.isOutsideTouchable=true
                popupWindow.showAsDropDown(it,40,10)
            }
            else{                   //답변이 한개라도 있으면 수정&삭제 불가
                binding.questionMoreBtn.setOnClickListener {
                    val list= mutableListOf<String>().apply {
                        add("신고하기")
                    }
                    popupWindow=SimplePopup(applicationContext,list){_,_,position->
                        when(position){
                            0-> {
                                Toast.makeText(applicationContext, "신고하기", Toast.LENGTH_SHORT).show()
                                if(!isLogin){
                                    QpToast.createToast(applicationContext)?.show()
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

    override fun onGetQSuccess(questionResp:QuestionInfo?) {
        questionInfo.content=(questionResp?.content.toString())
        Log.d("getQ/SUCCESS",questionResp?.content.toString()+"questionInfo: ".plus(questionInfo.content))
    }
    override fun onGetQFailure(msg:String) {
        Log.d("getQ/FAIL",msg)
    }

    fun deleteQ(accessToken: String, qId: Long?, userId: Int){
        val questionService= getRetrofit().create(QuestionInterface::class.java)

        questionService.deleteQ(accessToken, qId, userId).enqueue(object :Callback<QuestionResponse>{
            override fun onResponse(
                call: Call<QuestionResponse>,
                response: Response<QuestionResponse>
            ) {
                val resp=response.body()
                when(resp?.code){
                    "QUESTION_2000"-> {
                        Log.d("SUCCESS/DeleteQ", resp.message)
                        startActivity(Intent(this@DetailedActivity, MainActivity::class.java))
                    }
                    else-> {
                        Log.d("FAILURE/DeleteQ", resp?.message ?: "응답실패")
                    }
                }
            }

            override fun onFailure(call: Call<QuestionResponse>, t: Throwable) {
                Log.d("deleteQFail",t.message.toString())
            }

        })
    }



    fun writeAnswerService(answer:AnswerInfo,token:String){
        val answerService= getRetrofit().create(QuestionInterface::class.java)

        answerService.writeAnswer(token,questionInfo.questionId,answer).enqueue(object : Callback<WriteAnswerResponse> {
            override fun onResponse(
                call: Call<WriteAnswerResponse>,
                response: Response<WriteAnswerResponse>
            ) {
                Log.d("writeAnswerReq",token.plus(questionInfo.questionId).plus(answer.toString()))
                val resp=response.body()
                when(resp?.code){
                    "ANSWER_3000"->{
                        Log.d("writeAnswer/SUCCESS",resp.toString())
                        answer.answerId=resp.result.answerId    //답변 번호 부여
                        answerAdapter.addItem(answer)
                        Log.d("writeAnswer/answer",answer.toString())

                        showWriteAnswerEdit(false)  //답변 작성 공간 접기
                        //updateNotifyView()
                        updateExpertNum()   //전문가 수 갱신
                        QpToast.createToast(applicationContext,"답변이 등록되었습니다")?.show()
                    }
                    else->{
                        Log.d("writeAnswer/FAIL",response.errorBody()?.string().toString())
                    }
                }
            }

            override fun onFailure(call: Call<WriteAnswerResponse>, t: Throwable) {
                Log.d("writeAnswerResp/FAIL",t.message.toString())
            }
        })
    }

    fun getParentAnswerService(id:Long){
        val questionService= getRetrofit().create(QuestionInterface::class.java)

        questionService.getParentAnswer(id,0,10).enqueue(object :Callback<ParentAnswerResponse>{
            override fun onResponse(
                call: Call<ParentAnswerResponse>,
                response: Response<ParentAnswerResponse>
            ) {
                Log.d("getParentReq",id.toString())
                val resp=response.body()
                Log.d("getParentResp",resp.toString())
                when(resp?.code){
                    "ANSWER_3000"-> {
                        answerAdapter.addItemList(resp.result.answerList)
                    }
                    else-> {
                        Log.d("getParent/FAIL",response.errorBody()?.string().toString())
                    }
                }
            }

            override fun onFailure(call: Call<ParentAnswerResponse>, t: Throwable) {
                Log.d("getParentResp/FAIL",t.message.toString())
            }

        })
    }

    fun modifyAnswerService(answerId:Long,title:String,content:String,position:Int){
        val questionService= getRetrofit().create(QuestionInterface::class.java)
        var answerPost=ModifyQInfo(AppData.qpUserID.toLong(),title,content)

        questionService.modifyAnswer(AppData.qpAccessToken,answerId,answerPost).enqueue(object :Callback<ModifyAnswerResponse>{
            override fun onResponse(
                call: Call<ModifyAnswerResponse>,
                response: Response<ModifyAnswerResponse>
            ) {
                val resp=response.body()
                when(resp?.code){
                    "ANSWER_3000"->{
                        val container=binding.writeAnswerContainer
                        answerAdapter.modifyAnswer(position,content)
                        container.removeAllViews()
                        binding.answerBtn.visibility=View.VISIBLE
                    }
                    else->{
                        Log.d("modifyAnswer/FAIL",response.errorBody()?.string().toString())
                        QpToast.createToast(applicationContext,"답변 수정 실패+${response.errorBody()?.string().toString()}")?.show()
                    }
                }

            }

            override fun onFailure(call: Call<ModifyAnswerResponse>, t: Throwable) {
                Log.d("modifyAnswerResp/FAIL",t.message.toString())
            }

        })
    }

    fun deleteAnswer(answerId:Long,position: Int){
        val questionService= getRetrofit().create(QuestionInterface::class.java)

        questionService.deleteAnswer(AppData.qpAccessToken,answerId,AppData.qpUserID.toLong()).enqueue(object:Callback<ModifyAnswerResponse>{
            override fun onResponse(
                call: Call<ModifyAnswerResponse>,
                response: Response<ModifyAnswerResponse>
            ) {
                val resp=response.body()
                when(resp?.code){
                    "ANSWER_3000"->{
                        answerAdapter.removeItem(position)
                        updateExpertNum()
                        if(answerAdapter.isItemListEmpty()){
                            binding.answerBtn.visibility=View.VISIBLE
                            //updateNotifyView()
                        }
                    }
                    else->{
                        QpToast.createToast(applicationContext,"답변 삭제 실패:${response.errorBody()?.string().toString()}")?.show()
                        Log.d("deleteAnswer/FAIL",response.errorBody()?.string().toString())
                    }
                }
            }

            override fun onFailure(call: Call<ModifyAnswerResponse>, t: Throwable) {
                Log.d("deleteAnswerResp/FAIL",t.message.toString())
            }

        })
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

}

//시간 표기 자르기
fun getTime(time:String):String{
    val newTime=time.substring(0,10)+" "+time.substring(11,19)
    return newTime
}

// 이미지뷰에 문자열 형태의 이미지를 설정
fun setStringImage(imageUrl: String, imageView: ImageView, con: Context) {
    Glide.with(con)
        .load(imageUrl)
        .apply(RequestOptions().transform(CircleCrop()))
        .into(imageView)
}
