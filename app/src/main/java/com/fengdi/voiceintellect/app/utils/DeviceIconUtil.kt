package com.fengdi.voiceintellect.app.utils

import com.fengdi.voiceintellect.R


object DeviceIconUtil {

    /**
     * 获取设备的图标
     */
    fun getDeviceIcon(deviceMac: String?): Int {
        if (deviceMac.isNullOrEmpty()) {
            return R.mipmap.ic_home_image_kg1
        }
        val macs = deviceMac.split(":")
        if (macs.size<2){
            return R.mipmap.ic_intellect_black
        }
        return when (macs[1]) {
            "SOCKET" -> {
                //插座
                R.mipmap.ic_home_image_cz
            }
            "SWITCH" -> {
                //开关
                R.mipmap.ic_home_image_kg2
            }
            "CURTAIN" -> {
                //电动窗帘
                R.mipmap.ic_home_image_cl
            }
            "LIGHT" -> {
                R.mipmap.ic_home_image_cgq
            }
            "MAGNETIC" -> {
                //门磁
                R.mipmap.ic_home_image_cgq
            }
            "PIR" -> {
                //人体红外
                R.mipmap.ic_home_image_hw
            }
            "HUMIDITY" -> {
                //湿度传感器
                R.mipmap.ic_home_image_wsd
            }
            "TEMPER" -> {
                //温度传感器
                R.mipmap.ic_home_image_wsd
            }
            "TEMPERATURE" -> {
                //温度传感器
                R.mipmap.ic_home_image_wsd
            }
            "SCENESWITCH" -> {
                //场景开关
               R.mipmap.ic_home_image_cjkg
            }
            else -> {
                R.mipmap.ic_home_image_kg1
            }
        }

    }
}