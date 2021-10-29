package com.fengdi.voiceintellect.ui.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.fengdi.voiceintellect.R
import com.fengdi.voiceintellect.data.model.bean.DeviceBean
import com.fengdi.voiceintellect.data.model.bean.RoomGatewayResult
import com.fengdi.voiceintellect.data.model.bean.SceneBean
import kotlinx.android.synthetic.main.item_device.view.*
import kotlinx.android.synthetic.main.item_device.view.tvName
import kotlinx.android.synthetic.main.item_scene.view.*

class GatewayAdapter(data: ArrayList<RoomGatewayResult>) : BaseQuickAdapter<RoomGatewayResult, BaseViewHolder>(R.layout.item_gateway, data) {

    override fun convert(holder: BaseViewHolder, item: RoomGatewayResult) {
        holder.itemView.tvName.text = item.zigbeeGateway.gatewayName
    }
}