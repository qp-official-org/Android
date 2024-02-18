package com.example.qp

import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.qp.databinding.ItemToastBinding

object QpToast {
    fun createToast(context:Context,message:String="로그인이 필요한 기능입니다"): Toast?{
        val inflater=LayoutInflater.from(context)
        val binding:ItemToastBinding=ItemToastBinding.inflate(LayoutInflater.from(context))

        binding.toastTv.text=message

        return Toast(context).apply {
            setGravity(Gravity.BOTTOM or Gravity.CENTER,0,50.toPx())
            duration=Toast.LENGTH_SHORT
            view=binding.root
        }
    }
    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

}