package com.fengdi.voiceintellect.ui.adapter

import android.annotation.SuppressLint
import android.view.MotionEvent
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.fengdi.voiceintellect.R
import com.fengdi.voiceintellect.data.model.bean.DeviceBean
import com.fengdi.voiceintellect.data.model.bean.SceneBean
import com.fengdi.voiceintellect.data.model.bean.SwitchBean
import com.fengdi.voiceintellect.data.model.bean.SwitchEntity
import kotlinx.android.synthetic.main.item_switch.view.*


class SwitchAdapter(data: ArrayList<SwitchEntity>) : BaseQuickAdapter<SwitchEntity, BaseViewHolder>(R.layout.item_switch, data) {

    @SuppressLint("ClickableViewAccessibility")
    override fun convert(holder: BaseViewHolder, item: SwitchEntity) {
        holder.itemView.tvName.text = item.deviceAttributeName

        holder.itemView.swStatus.isChecked = item.deviceAttributeValue == "ON"

        holder.itemView.swStatus.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                setOnItemChildClick(v,getItemPosition(item))
            }
            return@setOnTouchListener false
        }

    }
}