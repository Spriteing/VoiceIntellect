package com.fengdi.voiceintellect.app.utils

import com.fengdi.voiceintellect.R


object DeviceIconUtil {

    /**
     * 获取设备的图标
     */
    fun getDeviceIcon(deviceType: String?, channelNums: Int? = 1): Int {
        var channel=1
        if (channelNums!=null){
            channel=channelNums
        }

        return when (deviceType) {
            "SOCKET" -> {
                //插座
                R.mipmap.ic_home_image_cz
            }
            "SWITCH" -> {
                //开关
                when (channel) {
                    1 -> {
                        R.mipmap.ic_home_image_kg1
                    }
                    2 -> {
                        R.mipmap.ic_home_image_kg2
                    }
                    3 -> {
                        R.mipmap.ic_home_image_kg3
                    }
                    else -> {
                        R.mipmap.ic_home_image_kg1
                    }
                }
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
            "TEMPERATUER" -> {
                //温度传感器
                R.mipmap.ic_home_image_wsd
            }
            "INFRARED" -> {
                //红外终端
                R.mipmap.ic_intellect_black
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


    //设备
    const val TYPE_DEVICE = 1

    //传感器
    const val TYPE_SENSOR = 2

    //场景开关
    const val TYPE_SCENESWITCH = 3

    const val TYPE_INFRARED = 4

    /**
     * 获取设备的图标
     */
    fun getDeviceType(typeName: String?): Int {

        return when (typeName) {
            "SOCKET" -> {
                //插座
                TYPE_DEVICE
            }
            "SWITCH" -> {
                //开关
                TYPE_DEVICE
            }
            "CURTAIN" -> {
                //电动窗帘
                TYPE_DEVICE
            }
            "LIGHT" -> {
                TYPE_SENSOR
            }
            "MAGNETIC" -> {
                //门磁
                TYPE_SENSOR
            }
            "PIR" -> {
                //人体红外
                TYPE_SENSOR
            }
            "HUMIDITY" -> {
                //湿度传感器
                TYPE_SENSOR
            }
            "TEMPER" -> {
                //温度传感器
                TYPE_SENSOR
            }
            "TEMPERATURE" -> {
                //温度传感器
                TYPE_SENSOR
            }
            "TEMPERATUER" -> {
                //温度传感器
                TYPE_SENSOR
            }
            "INFRARED" -> {
                //红外终端
                TYPE_INFRARED
            }
            "SCENESWITCH" -> {
                //场景开关
                TYPE_SCENESWITCH
            }
            else -> {
                TYPE_DEVICE
            }
        }

    }


}