package com.example.qp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.qp.databinding.ActivityReportBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportActivity:AppCompatActivity() {
    private lateinit var binding:ActivityReportBinding
    private var questionId:Int?=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var intent=intent
        if(intent.hasExtra("report")){
            questionId=intent.getIntExtra("report",0)
        }

        binding.reportBtn.setOnClickListener {
            var content=binding.reportEdit.text.toString()
            reportQService(questionId!!.toLong(),content)
        }

    }

    fun reportQService(questionId:Long,content:String){
        val questionService= getRetrofit().create(QuestionInterface::class.java)
        val reportQ=ReportQ(AppData.qpUserID.toLong(),content)

        questionService.reportQ(questionId.toInt(),reportQ).enqueue(object : Callback<ReportQResponse>{
            override fun onResponse(
                call: Call<ReportQResponse>,
                response: Response<ReportQResponse>
            ) {
                val resp=response.body()
                when(resp?.code){
                    "4000"->{
                        Log.d("reportQ/SUCCESS",resp.toString())
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
}