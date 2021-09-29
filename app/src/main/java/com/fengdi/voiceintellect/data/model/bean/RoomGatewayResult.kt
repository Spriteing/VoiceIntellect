package com.fengdi.voiceintellect.data.model.bean

import java.io.Serializable

data class RoomGatewayResult(
        val roomList: List<RoomEntity>,
        val zigbeeGateway: GatewayEntity
) : Serializable
