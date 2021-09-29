package com.fengdi.voiceintellect.data.model.bean

data class SwitchBean(
    val attributeName: String,
    val sceneSwitchAttributeId: Int,
    val switchId: Int,
    val onOffStatus: String,
    val sceneId: Int
)