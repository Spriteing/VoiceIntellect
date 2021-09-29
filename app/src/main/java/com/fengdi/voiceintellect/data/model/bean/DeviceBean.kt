package com.fengdi.voiceintellect.data.model.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class DeviceBean(
    val deviceName: String,
    val sensorName: String,
    val sceneSwitchName: String,
    val logo: String,
    val id: Int,
    val mac: String,
    val sceneSwitchId: Int
) : Parcelable {
}