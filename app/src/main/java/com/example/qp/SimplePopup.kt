package com.example.qp

import android.content.Context
import android.text.Layout
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.example.qp.databinding.LayoutPopupBinding

class SimplePopup(context:Context,popupList:MutableList<String>,menuItemListener:(View?,String,Int)->Unit):PopupWindow(context) {
    init{
        val inflater=LayoutInflater.from(context)
        val binding=LayoutPopupBinding.inflate(inflater,null,false)
        contentView=binding.root
        width=getDp(context,100f)

        val layoutParam=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        for(i in 0 until popupList.size){
            val view=inflater.inflate(R.layout.item_popup,null,false)
            val menuTitle=view.findViewById<TextView>(R.id.tv_menu_title)
            menuTitle.text=popupList[i]

            view.setOnClickListener {
                menuItemListener.invoke(it,popupList[i],i)
                dismiss()
            }
            binding.llPopup.addView(view,layoutParam)

        }
        val layout=contentView
        layout.measure(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        height=layout.measuredHeight+20

    }

    private fun getDp(context: Context, value: Float): Int {
        val dm = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, dm).toInt()
    }
}