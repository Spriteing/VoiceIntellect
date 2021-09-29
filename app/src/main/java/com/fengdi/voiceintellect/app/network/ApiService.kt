package com.fengdi.voiceintellect.app.network

import com.fengdi.voiceintellect.data.model.bean.*
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/23
 * 描述　: 网络API
 */
interface ApiService {

    companion object {
        const val SERVER_URL = "http://47.115.94.218:8080"
    }

    /**
     * 获取验证码
     */
    @GET("/user/validationCode")
    suspend fun getVerifyCode(@Query("mobile") mobile: String): ApiResponse<Any>

    /**
     * 登录
     */
    @GET("/user/login")
    suspend fun login(@Query("mobile") mobile: String, @Query("validationCode") code: String): ApiResponse<List<UserInfo>>

    /**
     * 获取设备列表
     */
    @GET("/room/getRoomsByUserId")
    suspend fun getRoomList(@Query("userId") userId: String): ApiResponse<ArrayList<RoomGatewayResult>>

    /**
     * 获得房间内设备
     */
    @GET("/device/getDevicesSensorsByRoomId")
    suspend fun getDeviceFormRoom(@Query("roomId") roomId: Int): ApiResponse<ArrayList<DeviceBean>>


    /**
     * 获得房间内设备
     */
    @GET("/sceneSwitch/getSceneSwitchBySceneSwitchId")
    suspend fun getSceneSwitchs(@Query("sceneSwitchId") sceneSwitchId: Int): ApiResponse<SceneSwitchResult>


    /**
     * 获取开关的设备
     */
    @GET("/device/getDeviceDetails")
    suspend fun getSwitcDevices(@Query("deviceId") deviceId: Int): ApiResponse<ArrayList<SwitchBean>>


    /**
     * 选择场景
     */
    @GET("/sceneSwitch/turnOnSceneSwitchAttribute")
    suspend fun openScene(@Query("sceneSwitchAttributeId") sceneSwitchAttributeId: Int): ApiResponse<Any>


    /**
     * 打开或关闭设备
     */
    @GET("/device/setDeviceSubChannel")
    suspend fun operateDevice(@Query("deviceAttributeId") id: Int, @Query("status") status: String): ApiResponse<Any>

}