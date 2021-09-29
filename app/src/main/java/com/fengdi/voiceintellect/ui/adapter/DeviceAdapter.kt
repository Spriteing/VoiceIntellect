package com.fengdi.voiceintellect.ui.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.fengdi.voiceintellect.R
import com.fengdi.voiceintellect.data.model.bean.DeviceBean
import kotlinx.android.synthetic.main.item_device.view.*

class DeviceAdapter(data: ArrayList<DeviceBean>) : BaseQuickAdapter<DeviceBean, BaseViewHolder>(R.layout.item_device, data) {

    override fun convert(holder: BaseViewHolder, item: DeviceBean) {
        when{
            !item.deviceName.isNullOrEmpty()->{
                holder.itemView.tvName.text = item.deviceName
            }
            !item.sensorName.isNullOrEmpty()->{
                holder.itemView.tvName.text = item.sensorName
            }
            !item.sceneSwitchName.isNullOrEmpty()->{
                holder.itemView.tvName.text = item.sceneSwitchName
            }
        }

//        Glide.with(context).load(item.logo).into(holder.itemView.ivLogo)

    }
}