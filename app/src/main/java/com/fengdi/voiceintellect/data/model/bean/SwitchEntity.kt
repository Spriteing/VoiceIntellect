package com.fengdi.voiceintellect.data.model.bean

data class SwitchEntity(
    val deviceAttributeId: Int,
    val deviceAttributeMac: String,
    val deviceAttributeName: String,
    val deviceAttributeValue: String,
    val deviceId: Int,
    val updateTime: String
)