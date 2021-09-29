package com.fengdi.voiceintellect.data.model.bean

import java.io.Serializable

class GatewayEntity(
        //网关id
        val gatewayId: Int,
        //网关名称
        val gatewayName: String,
        //mac地址
        val mac: String,
        //用户id
        val userId: Int,
        val status: String?,
        var onlineState: String?,
        //设备集合
        var devices: ArrayList<DeviceEntity>
) : Serializable