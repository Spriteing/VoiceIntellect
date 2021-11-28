package com.fengdi.voiceintellect.data.model.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class DeviceBean(
    var deviceName: String?,
    val sensorName: String,
    val sceneSwitchName: String?,
    val sensorValue: String?,
    val deviceType: String?,
    val onlineState: String,
    val infraredDeviceName: String?,
    val logo: String,
    val id: Int,
    var mac: String?,
    val infraredDeviceMac: String?,
    val status: String,
    val sceneSwitchId: Int,
    val channelNums: Int?,
    val infraredDeviceId: Int,
    val deviceId: Int
) : Parcelable {
}