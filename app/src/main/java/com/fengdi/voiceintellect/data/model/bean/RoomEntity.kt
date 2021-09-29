package com.fengdi.voiceintellect.data.model.bean

import java.io.Serializable

data class RoomEntity(
        val gatewayId: Int,
        val roomId: Int,
        val roomName: String,
        var isCheck: Boolean = false
) : Serializable