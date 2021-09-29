package com.fengdi.voiceintellect.data.model.bean

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

/**
 * {"code":200,"message":"操作成功","data":[
 * {"gatewayId":10,"mac":"YINKE:GATEWAY:ZIGBEE:FF_54_35_21_00_4B_12_00:1:0:0","userId":1,"gatewayName":"牛顿网关"},
 * {"deviceId":14,"mac":"YINKE:DEVICE:ZIGBEE:00_12_4B_00_0A_DE_AB_5E:1:0:2","gatewayId":10,"deviceName":"开关","status":"ON"},
 * {"deviceId":38,"mac":"YINKE:SWITCH:ZIGBEE:00_12_4B_00_23_41_CC_DF:1:0:1","gatewayId":10,"deviceName":"墙壁开关","status":"ON"},
 * {"deviceId":46,"mac":"YINKE:SWITCH:ZIGBEE:00_12_4B_00_23_30_D0_41:1:0:2","gatewayId":10,"deviceName":"墙壁开关","status":"OFF"},
 * {"sensorId":1,"mac":"YINKE:SENSOR:ZIGBEE:FF_54_35_21_00_4B_12_03:1:0:0","gatewayId":10,"sensorName":"人体红外传感器","sensorValue":"有人"},
 * ,{"sensorId":9,"mac":"YINKE:SENSOR:ZIGBEE:FF_54_35_21_00_4B_12_01:1:0:0","gatewayId":10,"sensorName":"温度传感器","sensorValue":"35.6°"},
 * {"sensorId":10,"mac":"YINKE:SENSOR:ZIGBEE:FF_54_35_21_00_4B_12_00:1:0:0","gatewayId":10,"sensorName":"湿度传感器","sensorValue":"45%"},
 * {"sensorId":15,"mac":"YINKE:HUMIDITY:ZIGBEE:00_12_4B_00_23_41_CB_9D:1:0:1","gatewayId":10,"sensorName":"湿度传感器","sensorValue":"63.89�\b���"}]}
 */
data class DeviceEntity(
        //设备id
        val deviceId: Int,
        //设备名称
        val deviceName: String?,
        //网关id
        val gatewayId: Int,
        //mac地址
        val mac: String,
        //传感器id
        val sensorId: Int,
        //传感器名称
        var sensorName: String?,
        //传感器数值
        var sensorValue: String?,
        //开关状态
        var status: String?,
        var onlineState: String?,
        var updateTime: String?,
        //是否选择
        var isCheck: Boolean = false,
        //设备名称
        var relationship: String = "大于"

) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString().toString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.readString().toString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(deviceId)
        parcel.writeString(deviceName)
        parcel.writeInt(gatewayId)
        parcel.writeString(mac)
        parcel.writeInt(sensorId)
        parcel.writeString(sensorName)
        parcel.writeString(sensorValue)
        parcel.writeString(status)
        parcel.writeString(onlineState)
        parcel.writeString(updateTime)
        parcel.writeByte(if (isCheck) 1 else 0)
        parcel.writeString(relationship)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DeviceEntity> {
        override fun createFromParcel(parcel: Parcel): DeviceEntity {
            return DeviceEntity(parcel)
        }

        override fun newArray(size: Int): Array<DeviceEntity?> {
            return arrayOfNulls(size)
        }
    }


}