package com.fengdi.voiceintellect.ui.adapter

import android.view.MotionEvent
import android.view.View
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.fengdi.voiceintellect.R
import com.fengdi.voiceintellect.app.utils.DeviceIconUtil
import com.fengdi.voiceintellect.data.model.bean.DeviceBean
import kotlinx.android.synthetic.main.item_device.view.*

class DeviceAdapter(data: ArrayList<DeviceBean>) : BaseQuickAdapter<DeviceBean, BaseViewHolder>(R.layout.item_device, data) {

    override fun convert(holder: BaseViewHolder, data: DeviceBean) {
        holder.itemView.run {
            if (data.infraredDeviceMac != null) {
                data.mac = data.infraredDeviceMac
                data.deviceName = data.infraredDeviceName
            }
            if (data.sceneSwitchName != null) {
                data.deviceName = data.sceneSwitchName
            }

            if (data.deviceName == null || data.deviceName!!.isEmpty()) {
                swStatus.visibility = View.GONE
                tvValue.visibility = View.VISIBLE


                data.mac?.let {
                    when {
                        it.startsWith("YINKE:HUMIDITY") -> {
                            tvValue.text = "湿度：" + data.sensorValue + "%"
                        }
                        it.startsWith("YINKE:TEMPER") -> {
                            tvValue.text = "温度：" + data.sensorValue + "°"
                        }
                        it.startsWith("YINKE:PIR") -> {
                            //传感器 当status ==OFF 显示没有人，，当status ==ON 显示 有人
                            tvValue.text = if (data.sensorValue == "ON") "没人" else "有人"
                        }
                        it.startsWith("YINKE:MAGNETIC") -> {
                            //传感器  status==ON 显示 门开   status==OFF 显示门关
                            tvValue.text = if (data.sensorValue == "ON") "门关" else "门开"
                        }
                        else -> {
                            //传感器数值
                            tvValue.text = data.sensorValue
                        }
                    }
                }


                tvName.text = data.sensorName


            } else {
//                swStatus.visibility = View.VISIBLE
                tvValue.visibility = View.GONE


                swStatus.isChecked = data.status == "ON"

                tvName.text = data.deviceName

                tvTime.visibility = View.GONE

            }

            ivOnLineState.isSelected = data.onlineState == "online"

            ivDevice.setImageResource(DeviceIconUtil.getDeviceIcon(data.mac))
            if (data.infraredDeviceMac.isNullOrBlank()){
                ivDevice.setImageResource(DeviceIconUtil.getDeviceIcon(data.mac))
            }else{
                ivDevice.setImageResource(R.mipmap.ic_intellect_black)
            }

        }

//        Glide.with(context).load(item.logo).into(holder.itemView.ivLogo)

    }
}