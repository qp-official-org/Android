package com.example.qp

import android.content.Context
import android.graphics.Rect
import android.content.Intent
import android.graphics.Point
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.example.qp.databinding.ActivityDetailedBinding
import com.google.gson.Gson
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import java.lang.Math.abs


class DetailedActivity : AppCompatActivity(),DetailedQView{

    private lateinit var binding: ActivityDetailedBinding
    private lateinit var answerAdapter:DetailedQuestionRVAdapter
    private var id:Long=0
    private lateinit var questionInfo:QuestionInfo
    private var isNotified:Boolean=false
    private var isLogin:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //질문 정보 넘겨받기
        if(intent.hasExtra("question")){
            id = intent.getLongExtra("question",0)
        }


        // 로고 클릭 시 홈으로 이동
        binding.detailedLogoIv.setOnClickListener {
            AppData.isGoHome = true
            finish()
        }


        CoroutineScope(Dispatchers.Main).launch {

            getQuestion(id)

            binding.mainBarLogoutTv.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            // 로그아웃 버튼
            binding.mainBarLogoutTv.setOnClickListener {
                UserApiClient.instance.logout { error ->
                    if (error != null) {
                        Log.d("TTAG", "로그아웃 실패 $error")
                    }else {
                        Toast.makeText(this@DetailedActivity, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                        // 전역변수 초기화
                        AppData.qpAccessToken = ""
                        AppData.qpUserID = 0
                        AppData.qpNickname = ""
                        AppData.qpProfileImage = ""
                        AppData.qpEmail = ""
                        AppData.qpGender = ""
                        AppData.qpRole = ""
                        AppData.qpPoint = 0
                        AppData.qpCreatedAt = ""
                        AppData.searchRecord.clear()

                        binding.mainLoginBt.visibility = View.VISIBLE
                        binding.mainLoginSuccessBt.visibility = View.GONE
                        binding.mainLoginSuccessUserImg.visibility = View.GONE

                        AppData.qpIsLogin = false
                        isLogin=false
                        binding.answerNoticeBtn.setImageResource(R.drawable.notify_alarm_off)
                    }
                }
            }
        }

        if(AppData.qpIsLogin) {
            isLogin=true
            binding.mainLoginBt.visibility = View.GONE
            binding.mainLoginSuccessBt.visibility = View.VISIBLE
            binding.mainLoginSuccessUserImg.visibility = View.VISIBLE

            // 통신 대기 시간 0.3초
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                // 하단 바에 사용자 닉네임과 포인트 데이터 반영
                binding.mainBarNicknameTv.text = AppData.qpNickname
                binding.mainBarCoinTv.text = AppData.qpPoint.toString()
                Glide.with(this).load(AppData.qpProfileImage).into(binding.mainLoginSuccessUserImg)
            }, 300)
            // getNotifyQ()
        }
        else {
            isLogin=false
            binding.mainLoginBt.visibility = View.VISIBLE
            binding.mainLoginSuccessBt.visibility = View.GONE
            binding.mainLoginSuccessUserImg.visibility = View.GONE
        }
    }

    override fun onRestart() {
        if(AppData.isGoHome)    finish()

        super.onRestart()

        if(AppData.qpIsLogin) {
            isLogin=true
            AppData.searchUserInfo(AppData.qpAccessToken, AppData.qpUserID)

            binding.mainLoginBt.visibility = View.GONE
            binding.mainLoginSuccessBt.visibility = View.VISIBLE
            binding.mainLoginSuccessUserImg.visibility = View.VISIBLE

            // 통신 대기 시간 0.3초
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                // 하단 바에 사용자 닉네임과 포인트 데이터 반영
                binding.mainBarNicknameTv.text = AppData.qpNickname
                binding.mainBarCoinTv.text = AppData.qpPoint.toString()
                Glide.with(this).load(AppData.qpProfileImage).into(binding.mainLoginSuccessUserImg)
            }, 300)
            getNotifyQ()

        }
        else {
            isLogin=false
            binding.mainLoginBt.visibility = View.VISIBLE
            binding.mainLoginSuccessBt.visibility = View.GONE
            binding.mainLoginSuccessUserImg.visibility = View.GONE
        }

    }




    private fun setInit(){


        if(AppData.qpIsLogin) {
            isLogin=true
            AppData.searchUserInfo(AppData.qpAccessToken, AppData.qpUserID)

            binding.mainLoginBt.visibility = View.GONE
            binding.mainLoginSuccessBt.visibility = View.VISIBLE
            binding.mainLoginSuccessUserImg.visibility = View.VISIBLE

            // 통신 대기 시간 0.3초
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                // 하단 바에 사용자 닉네임과 포인트 데이터 반영
                binding.mainBarNicknameTv.text = AppData.qpNickname
                binding.mainBarCoinTv.text = AppData.qpPoint.toString()
                Glide.with(this).load(AppData.qpProfileImage).into(binding.mainLoginSuccessUserImg)
            }, 300)
            getNotifyQ()
            
        }
        else {
            isLogin=false
            binding.mainLoginBt.visibility = View.VISIBLE
            binding.mainLoginSuccessBt.visibility = View.GONE
            binding.mainLoginSuccessUserImg.visibility = View.GONE
        }
        

        //뷰 초기화
        initView()

        answerAdapter.setMyItemClickListener(object :
            DetailedQuestionRVAdapter.ItemClickListener{
            //답변 삭제
            override fun onItemRemove(position:Int,answerId: Long) {
                deleteAnswer(answerId,position)
            }
            //답변 수정
            override fun onAnswerModify(position: Int,answerId:Long,view:View) {
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
                    modifyAnswerService(answerId,"title",newContent,position,view)
                }

                binding.detailedScrollView.scrollTo(0,container.bottom)
            }

            override fun showLoginMsg() {
                QpToast.createToast(applicationContext)?.show()
            }

            override fun reportAnswer(id: Long) {
                val intent= Intent(applicationContext,ReportActivity::class.java)
                intent.putExtra("report",id)
                intent.putExtra("category","answer")
                startActivity(intent)
            }

            override fun scroll2viw(view: View) {
                //수정된 답변의 위치가 화면 중앙에 오도록 스크롤
                fun NestedScrollView.computeDistanceToView(view: View): Int {
                    return abs(calculateRectOnScreen(this).centerY() - (this.scrollY + calculateRectOnScreen(view).top)+
                            view.height)
                }
                fun NestedScrollView.scrollToView(view: View) {
                    val y = computeDistanceToView(view)
                    this.scrollTo(0, y)
                }
                binding.detailedScrollView.scrollToView(view)
            }

            override fun scrollTop(view: View) {
                binding.detailedScrollView.scrollTo(0,view.bottom+view.height)
            }

            override fun usePoint(point: Long) {
                binding.mainBarCoinTv.text=(AppData.qpPoint-point).toString()
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
                if(isNotified)
                    QpToast.createToast(applicationContext,"이미 해당 질문의 알림을 설정하였습니다.")?.show()
                else
                    notifyQService()
            }
        }
        //로그인 버튼
        binding.mainLoginBt.setOnClickListener {
            startActivity(Intent(this@DetailedActivity, LoginActivity::class.java))
        }


        //프로필로 화면 전환
        binding.mainLoginSuccessBt.setOnClickListener {
            startActivity(Intent(this@DetailedActivity, ProfileActivity::class.java))
        }

        //검색으로 화면전환
        binding.detailedSearchBt.setOnClickListener {
            startActivity(Intent(this@DetailedActivity, SearchActivity::class.java))

        }

        setQuestionMorePopup()

        updateNotifyView()

        binding.prevQuestionView.setOnClickListener{
            getOtherQ(0)
        }
        binding.nextQuestionView.setOnClickListener {
            getOtherQ(1)
        }
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
        binding.mainBarNicknameTv.text = AppData.qpNickname
        binding.mainBarCoinTv.text = AppData.qpPoint.toString()

    }





    private fun updateNotifyView(){
        var container=binding.noticeContainer
        if(answerAdapter.isItemListEmpty()&&!isNotified){
            val inflater:LayoutInflater=getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.item_notice,container,true)

            val notifyBtn=findViewById<TextView>(R.id.notify_btn)
            notifyBtn.setOnClickListener {
                if(isLogin){
                    notifyQService()
                    container.removeAllViews()
                }
                else{
                    QpToast.createToast(applicationContext)?.show()
                }

            }
        }
        else if(answerAdapter.isItemListEmpty()&&!isNotified){
            QpToast.createToast(applicationContext,"이미 해당 질문의 알림을 설정하였습니다.")?.show()
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


    private fun writeAnswer(){
        val btn=findViewById<TextView>(R.id.write_answer_btn)
        val editText=findViewById<EditText>(R.id.write_answer_edit)

        btn.setOnClickListener {
            val content=editText.text.toString()
            var answer=AnswerPost(
                userId=AppData.qpUserID.toLong(),
                content=content,
                category = "PARENT",
                answerGroup = 0,
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

        binding.questionMoreBtn.setOnClickListener {
            var isMine=questionInfo.user?.userId==AppData.qpUserID  //본인이 작성한 질문인지 여부
            Log.d("questionMorePopup",answerAdapter.isItemListEmpty().toString()+isMine+isLogin+isLogin)
            if(answerAdapter.isItemListEmpty()&&isMine&&isLogin){
                val list= mutableListOf<String>().apply {
                    add("수정하기")
                    add("삭제하기")
                    add("신고하기")
                }
                popupWindow=SimplePopup(applicationContext,list){_,_,position->
                    when(position){
                        0-> {   //수정하기
                            val gson=Gson()
                            val qJson=gson.toJson(questionInfo)
                            val intent=Intent(this@DetailedActivity,ModifyQuestionActivity::class.java)
                            intent.putExtra("modifyQuestion",qJson)
                            startActivity(intent)
                            Log.d("modifyLog",questionInfo.toString())
                        }
                        1-> {   //삭제하기
                                val dialog=SimpleDialog("질문을 삭제하시겠습니까?")
                                dialog.setMyItem(object :
                                    SimpleDialog.itemListener{
                                    override fun onOk() {
                                        deleteQ(AppData.qpAccessToken, questionInfo.questionId, AppData.qpUserID)
                                    }
                                })
                                dialog.show(supportFragmentManager,"dialog")
                        }
                        2-> {   //신고하기
                            if(!isLogin){
                                QpToast.createToast(applicationContext)?.show()
                            }
                            else{
                                val intent=Intent(this@DetailedActivity,ReportActivity::class.java)
                                intent.putExtra("report",questionInfo.questionId)
                                intent.putExtra("category","question")
                                startActivity(intent)
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
                                if(!isLogin){
                                    QpToast.createToast(applicationContext)?.show()
                                }
                                else{
                                    val intent=Intent(this@DetailedActivity,ReportActivity::class.java)
                                    intent.putExtra("report",questionInfo.questionId)
                                    intent.putExtra("category","question")
                                    startActivity(intent)
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
                Log.d("deletQResp",qId.toString())
                when(resp?.code){
                    "QUESTION_2000"-> {
                        Log.d("SUCCESS/DeleteQ", resp.message)
                        startActivity(Intent(this@DetailedActivity, MainActivity::class.java))
                        finish()
                    }
                    else-> {
                        Log.d("FAILURE/DeleteQ", resp?.message ?: "응답실패")
                        Log.d("deleteQ/FAIL",response.errorBody()?.string().toString())
                    }
                }
            }

            override fun onFailure(call: Call<QuestionResponse>, t: Throwable) {
                Log.d("deleteQFail",t.message.toString())
            }

        })
    }

    fun getQuestion(questionId:Long?){
        val questionService= getRetrofit().create(QuestionInterface::class.java)

        questionService.getQuestion(questionId).enqueue(object :Callback<DetailedQResponse>{
            override fun onResponse(
                call: Call<DetailedQResponse>,
                response: Response<DetailedQResponse>
            ) {

                val resp=response.body()
                Log.d("getDetailedResp",resp.toString())
                when(resp?.code){
                    "QUESTION_2000"-> {
                        Log.d("detailedQ/SUCCESS",resp.toString())
                        questionInfo=resp.result!!
                        questionInfo.user=resp.result!!.user

                        answerAdapter=DetailedQuestionRVAdapter(applicationContext)
                        binding.answerRv.adapter=answerAdapter
                        getParentAnswerService(questionId!!)
                    }
                    else-> {
                        Log.d("detailedQ/FAIL",response.errorBody()?.string().toString().plus(questionId))
                    }
                }
            }

            override fun onFailure(call: Call<DetailedQResponse>, t: Throwable) {
                Log.d("detailedQResp/FAIL",t.message.toString())
            }

        })

    }



    fun writeAnswerService(answer:AnswerPost,token:String){
        val answerService= getRetrofit().create(QuestionInterface::class.java)

        answerService.writeAnswer(token,questionInfo.questionId,answer).enqueue(object : Callback<WriteAnswerResponse> {
            override fun onResponse(
                call: Call<WriteAnswerResponse>,
                response: Response<WriteAnswerResponse>
            ) {
                Log.d("writeAnswerReq",questionInfo.questionId.toString().plus(answer.toString()))
                val resp=response.body()
                when(resp?.code){
                    "ANSWER_3000"->{
                        Log.d("writeAnswer/SUCCESS",resp.toString())
                        var answerInfo=AnswerInfo(
                            user=AnswerUser(AppData.qpUserID.toLong(),AppData.qpNickname,AppData.qpProfileImage,AppData.qpRole),
                            answerId=resp.result.answerId,
                            content=answer.content,
                            category = answer.category,
                            answerGroup = answer.answerGroup,
                            likes=0,
                            childCount = 0
                        )
                        answerAdapter.addItem(answerInfo)
                        Log.d("writeAnswer/answer",answer.toString())

                        showWriteAnswerEdit(false)  //답변 작성 공간 접기
                        updateNotifyView()
                        if(AppData.qpRole=="EXPERT"){
                            questionInfo.expertCount = questionInfo.expertCount?.plus(1)
                            updateExpertNum()   //전문가 수 갱신
                        }
                        QpToast.createToast(applicationContext,"답변이 등록되었습니다")?.show()
                    }
                    else->{
                        val msg=response.errorBody()?.string().toString()
                        Log.d("writeAnswer/FAIL",msg)
                        QpToast.createToast(applicationContext,"답변 등록 실패"+msg)
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
                        setInit()
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

    fun modifyAnswerService(answerId:Long,title:String,content:String,position:Int,view:View){
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
                        Log.d("modifyAnswer/SUCCESS",resp.toString())
                        val container=binding.writeAnswerContainer
                        answerAdapter.modifyAnswer(position,content)
                        container.removeAllViews()
                        binding.answerBtn.visibility=View.VISIBLE

                        //수정된 답변의 위치가 화면 중앙에 오도록 스크롤
                        fun NestedScrollView.computeDistanceToView(view: View): Int {
                            return abs(calculateRectOnScreen(this).centerY() - (this.scrollY + calculateRectOnScreen(view).top)+
                                    view.height)
                        }
                        fun NestedScrollView.scrollToView(view: View) {
                            val y = computeDistanceToView(view)
                            this.scrollTo(0, y)
                        }
                        binding.detailedScrollView.scrollToView(view)
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
                            updateNotifyView()
                            setQuestionMorePopup()
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

    fun notifyQService(){
        val questionService= getRetrofit().create(QuestionInterface::class.java)

        questionService.notifyQ(AppData.qpAccessToken,questionInfo.questionId,AppData.qpUserID.toLong()).enqueue(object :Callback<NotifyQResponse>{
            override fun onResponse(
                call: Call<NotifyQResponse>,
                response: Response<NotifyQResponse>
            ) {
                val resp=response.body()
                when(resp?.code){
                    "QUESTION_2000"->{
                        Log.d("notifyQ/SUCCESS",resp.toString())
                        binding.answerNoticeBtn.setImageResource(R.drawable.notify_alarm_on)
                        isNotified=true
                        QpToast.createToast(applicationContext,"답변 알림 설정")?.show()
                        //updateNotifyView()
                    }
                    else->{
                        Log.d("notifyQ/FAIL",response.errorBody()?.string().toString())
                        QpToast.createToast(applicationContext,"이미 설정한 알림입니다.")?.show()
                    }
                }
            }

            override fun onFailure(call: Call<NotifyQResponse>, t: Throwable) {
                Log.d("notifyQResp/FAIL",t.message.toString())
            }

        })
    }

    fun getNotifyQ(){
        val questionService= getRetrofit().create(QuestionInterface::class.java)

        questionService.getNotifyQ(questionInfo.questionId.toInt()).enqueue(object :Callback<GetNotifyResponse>{
            override fun onResponse(
                call: Call<GetNotifyResponse>,
                response: Response<GetNotifyResponse>
            ) {
                val resp=response.body()
                when(resp?.code){
                    "QUESTION_2000"->{
                        Log.d("getNotifyQ/SUCCESS",resp.toString())
                        val userList=resp.result.questionAlarms
                        var idList=ArrayList<Long>()
                        for(i in 0 until userList.size)
                            idList.add(userList[i].userId)
                        if(AppData.qpUserID.toLong() in idList){
                            isNotified=true
                            binding.answerNoticeBtn.setImageResource(R.drawable.notification_on)
                        }
                        updateNotifyView()
                    }
                    else->{
                        Log.d("getNotifyQ/FAIL",response.errorBody()?.string().toString())
                    }
                }
            }

            override fun onFailure(call: Call<GetNotifyResponse>, t: Throwable) {
                Log.d("getNotifyQ",t.message.toString())
            }

        })
    }

    fun getOtherQ(dir:Int){
        val questionService= getRetrofit().create(QuestionInterface::class.java)

        questionService.getOtherQ(questionInfo.questionId).enqueue(object :Callback<GetOtherQResponse>{
            override fun onResponse(
                call: Call<GetOtherQResponse>,
                response: Response<GetOtherQResponse>
            ) {
                val resp=response.body()
                when(resp?.code){
                    "QUESTION_2000"->{
                        Log.d("getOtherQ/SUCCESS",resp.toString())
                        if(dir==0){
                            if(resp.result.hasPrev){
                                val intent=Intent(this@DetailedActivity,DetailedActivity::class.java)
                                intent.putExtra("question",resp.result.prevQuestion.questionId)
                                startActivity(intent)
                                finish()
                            }
                            else
                                QpToast.createToast(applicationContext,"이전 질문이 없습니다")?.show()
                        }
                        else{
                            if(resp.result.hasNext){
                                val intent=Intent(this@DetailedActivity,DetailedActivity::class.java)
                                intent.putExtra("question",resp.result.nextQuestion.questionId)
                                startActivity(intent)
                                finish()
                            }
                            else
                                QpToast.createToast(applicationContext,"다음 질문이 없습니다")?.show()
                        }
                    }
                    else->{
                        Log.d("getOtherQ/FAIL",response.errorBody()?.string().toString())
                    }
                }
            }

            override fun onFailure(call: Call<GetOtherQResponse>, t: Throwable) {
                Log.d("getOtherQResp/FAIL",t.message.toString())
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


internal fun calculateRectOnScreen(view: View): Rect {
    val location = IntArray(2)
    view.getLocationOnScreen(location)
    return Rect(
        location[0],
        location[1],
        location[0] + view.measuredWidth,
        location[1] + view.measuredHeight
    )
}
