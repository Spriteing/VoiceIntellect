package com.fengdi.voiceintellect.ui.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.fengdi.voiceintellect.R
import com.fengdi.voiceintellect.data.model.bean.DeviceBean
import com.fengdi.voiceintellect.data.model.bean.SceneBean
import kotlinx.android.synthetic.main.item_device.view.*
import kotlinx.android.synthetic.main.item_device.view.tvName
import kotlinx.android.synthetic.main.item_scene.view.*

class SceneAdapter(data: ArrayList<SceneBean>) : BaseQuickAdapter<SceneBean, BaseViewHolder>(R.layout.item_scene, data) {

    override fun convert(holder: BaseViewHolder, item: SceneBean) {
        holder.itemView.tvName.text = item.attributeName
        holder.itemView.tvState.text=item.onOffStatus
    }
}