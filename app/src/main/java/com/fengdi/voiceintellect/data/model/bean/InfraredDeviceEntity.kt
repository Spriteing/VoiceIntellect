package com.fengdi.voiceintellect.data.model.bean

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class InfraredDeviceEntity(
    val infraredDeviceId: Int,
    val infraredRemoteControllerId: Int,
    val infraredRemoteControllerName: String?
):Serializable