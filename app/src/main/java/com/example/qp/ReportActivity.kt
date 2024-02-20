package com.example.qp

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.qp.databinding.ActivityReportBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportActivity:AppCompatActivity() {
    private lateinit var binding:ActivityReportBinding
    private var id:Int?=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var intent=intent
        if(intent.hasExtra("report")){
            id=intent.getLongExtra("report",0).toInt()
        }
        var category=intent.getStringExtra("category")


        binding.reportBtn.setOnClickListener {
            var content=binding.reportEdit.text.toString()
            if(category=="question"){
                reportQService(content)
            }
            else if(category=="answer"){
                reportAnswerService(content)
            }

        }

        binding.reportCancelBtn.setOnClickListener {
            finish()
        }

    }

    fun reportQService(content:String){
        val questionService= getRetrofit().create(QuestionInterface::class.java)
        val reportQ=ReportQ(AppData.qpUserID.toLong(),content)

        questionService.reportQ(id!!.toInt(),reportQ).enqueue(object : Callback<ReportQResponse>{
            override fun onResponse(
                call: Call<ReportQResponse>,
                response: Response<ReportQResponse>
            ) {
                val resp=response.body()
                when(resp?.code){
                    "REPORT_4000"->{
                        Log.d("reportQ/SUCCESS",resp.toString())
                        QpToast.createToast(applicationContext,"신고 완료")?.show()
                        finish()
                    }
                    else->{
                        Log.d("reportQ/FAIL",response.errorBody()?.string().toString())
                    }
                }
            }

            override fun onFailure(call: Call<ReportQResponse>, t: Throwable) {
                Log.d("reportQResp/FAIL",t.message.toString())
            }

        })
    }
    fun reportAnswerService(content:String){
        val questionService= getRetrofit().create(QuestionInterface::class.java)
        val reportQ=ReportQ(AppData.qpUserID.toLong(),content)

        questionService.reportAnswer(AppData.qpAccessToken,id!!.toInt(),reportQ).enqueue(object : Callback<ReportAnswerResponse>{
            override fun onResponse(
                call: Call<ReportAnswerResponse>,
                response: Response<ReportAnswerResponse>
            ) {
                val resp=response.body()
                when(resp?.code){
                    "REPORT_4000"->{
                        Log.d("reportA/SUCCESS",resp.toString())
                        QpToast.createToast(applicationContext,"신고 완료")?.show()
                        finish()
                    }
                    else->{
                        Log.d("reportA/FAIL",response.errorBody()?.string().toString())
                    }
                }
            }

            override fun onFailure(call: Call<ReportAnswerResponse>, t: Throwable) {
                Log.d("reportA/FAIL",t.message.toString())
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