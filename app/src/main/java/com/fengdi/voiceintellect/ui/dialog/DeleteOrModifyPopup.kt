package com.fengdi.voiceintellect.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.fengdi.voiceintellect.R
import com.qmuiteam.qmui.skin.QMUISkinHelper
import com.qmuiteam.qmui.skin.QMUISkinManager
import com.qmuiteam.qmui.skin.QMUISkinValueBuilder
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.widget.popup.QMUIPopup
import com.qmuiteam.qmui.widget.popup.QMUIPopups
import kotlinx.android.synthetic.main.dialog_delete_or_modify.view.*
import org.heiyiren.app.app.callback.MyOnItemClickListener

class DeleteOrModifyPopup(val context: Context, val view: View) : QMUIPopups() {
    var onOkClickListener: MyOnItemClickListener<String>? = null


    fun show() {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_delete_or_modify, null)

        val builder = QMUISkinValueBuilder.acquire()
        builder.textColor(ContextCompat.getColor(context, R.color.c_333333))
        QMUISkinHelper.setSkinValue(dialogView, builder)
        builder.release()
        val deleteDialog = popup(context, QMUIDisplayHelper.dp2px(context, 250))
            .preferredDirection(QMUIPopup.DIRECTION_BOTTOM)
            .view(dialogView)
            .edgeProtection(QMUIDisplayHelper.dp2px(context, 20))
            .dimAmount(0.6f)
            .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
            .skinManager(QMUISkinManager.defaultInstance(context))
            .show(view)

        dialogView.tvDelete.setOnClickListener {
            deleteDialog.dismiss()

            HintDialog(context, "确定要删除？").let {
                it.onOkClickListener = View.OnClickListener {
                    onOkClickListener?.onItemClick(it, "删除", 0)
                }
                it.show()
            }
        }

        dialogView.tvModify.setOnClickListener {
            onOkClickListener?.onItemClick(it, "修改", 1)
        }

    }
}