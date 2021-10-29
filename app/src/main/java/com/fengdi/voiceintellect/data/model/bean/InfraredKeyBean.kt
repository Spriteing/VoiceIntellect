package com.fengdi.voiceintellect.data.model.bean

import java.io.Serializable

data class InfraredKeyBean(
    val infraredRemoteControllerId: Int,
    val infraredRemoteControllerKeyId: Int,
    val keyCodeValue: String,
    var keyColour: String,
    var orderNumber: Int,
    var keyName: String
):Serializable