package com.fengdi.voiceintellect.ui.dialog

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.fengdi.voiceintellect.R
import com.fengdi.voiceintellect.app.utils.ArmsDeviceUtils
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.widget.dialog.QMUIBaseDialog

open class MyBaseDialog(context: Context) : QMUIBaseDialog(context, R.style.QMUI_Dialog) {

    fun addContentView(view: View) {
        addContentView(view, ViewGroup.LayoutParams(context.resources.getDimensionPixelOffset(R.dimen.dp_500), ViewGroup.LayoutParams.WRAP_CONTENT))
    }


}