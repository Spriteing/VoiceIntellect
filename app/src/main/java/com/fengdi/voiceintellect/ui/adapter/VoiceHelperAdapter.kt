package com.fengdi.voiceintellect.ui.adapter

import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.fengdi.voiceintellect.R
import com.fengdi.voiceintellect.data.model.bean.DeviceBean
import com.fengdi.voiceintellect.data.model.bean.DialogueBean
import kotlinx.android.synthetic.main.item_device.view.*
import kotlinx.android.synthetic.main.item_dialogue.view.*

class VoiceHelperAdapter(data: ArrayList<DialogueBean>) : BaseQuickAdapter<DialogueBean, BaseViewHolder>(R.layout.item_dialogue, data) {

    override fun convert(holder: BaseViewHolder, item: DialogueBean) {
        holder.itemView.tvMessage.run {
            text = item.message
            if (item.type == 0) {
                setTextColor(ContextCompat.getColor(context, R.color.c_cccccc))
            } else {
                setTextColor(ContextCompat.getColor(context, R.color.white))
            }

        }
    }
}