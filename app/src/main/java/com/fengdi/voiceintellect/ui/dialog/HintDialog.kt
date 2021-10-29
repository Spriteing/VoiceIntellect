package com.fengdi.voiceintellect.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.fengdi.voiceintellect.R
import com.fengdi.voiceintellect.ui.dialog.MyBaseDialog
import kotlinx.android.synthetic.main.dialog_hint.view.*


open class HintDialog(context: Context, var msg: String) : MyBaseDialog(context) {
    var onOkClickListener: View.OnClickListener? = null
    var onCancelClickListener: View.OnClickListener? = null

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_hint, null)

        //内容
        view.tvMessage.text = msg

        view.btCancel.setOnClickListener {
            dismiss()
            onCancelClickListener?.onClick(it)
        }
        view.btConfirm.setOnClickListener {
            dismiss()
            onOkClickListener?.onClick(it)
        }


        addContentView(view)
    }


}