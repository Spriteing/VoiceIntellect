package com.fengdi.voiceintellect.ui.adapter

import android.graphics.Color
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.fengdi.voiceintellect.R
import com.fengdi.voiceintellect.data.model.bean.DeviceBean
import com.fengdi.voiceintellect.data.model.bean.InfraredDeviceEntity
import com.fengdi.voiceintellect.data.model.bean.InfraredKeyBean
import com.fengdi.voiceintellect.data.model.bean.SceneBean
import kotlinx.android.synthetic.main.item_device.view.*
import kotlinx.android.synthetic.main.item_device.view.tvName
import kotlinx.android.synthetic.main.item_scene.view.*

class RemoteControlAdapter(data: ArrayList<InfraredKeyBean>) : BaseQuickAdapter<InfraredKeyBean, BaseViewHolder>(R.layout.item_remote_control, data) {

    override fun convert(holder: BaseViewHolder, item: InfraredKeyBean) {
        holder.itemView.tvName.text = item.keyName
        if (!item.keyColour.isNullOrEmpty()){
            holder.itemView.tvName.setBackgroundColor(Color.parseColor(item.keyColour))
        }
    }
}