package com.example.qp

import android.app.Dialog
import android.content.ContextWrapper
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.qp.databinding.PopupDialogBinding

class SimpleDialog: DialogFragment() {
    private lateinit var binding:PopupDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= PopupDialogBinding.inflate(inflater,container,false)
        isCancelable=false

        binding.popupButtonOk.setOnClickListener {
            dismiss()
        }

        return binding.root
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(R.color.transparent)
        dialog.window?.setDimAmount(0.99f)

        return dialog

    }

}