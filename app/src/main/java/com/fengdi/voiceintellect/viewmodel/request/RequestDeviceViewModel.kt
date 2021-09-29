package com.fengdi.voiceintellect.viewmodel.request

import androidx.lifecycle.MutableLiveData
import com.fengdi.voiceintellect.app.ext.isNotNull
import com.fengdi.voiceintellect.app.network.apiService
import com.fengdi.voiceintellect.app.network.stateCallback.ListDataUiState
import com.fengdi.voiceintellect.app.utils.CacheUtil
import com.fengdi.voiceintellect.data.model.bean.DeviceBean
import com.fengdi.voiceintellect.data.model.bean.RoomGatewayResult
import com.fengdi.voiceintellect.data.model.bean.SceneBean
import com.fengdi.voiceintellect.data.model.bean.SwitchBean
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.state.ResultState

/**
 * 请求设备信息的Viewmodel
 */
class RequestDeviceViewModel : BaseViewModel() {

    //设备列表
    var devicesState = MutableLiveData<ListDataUiState<DeviceBean>>()

    //场景列表
    var scenesState = MutableLiveData<ListDataUiState<SceneBean>>()

    //开关列表
    var switchState = MutableLiveData<ListDataUiState<SwitchBean>>()

    var gatewayList: List<RoomGatewayResult> = arrayListOf()

    //选择场景的请求结果
    val sceneSwitchResult = MutableLiveData<ResultState<Any>>()


    //操作设备的请求结果
    val switchDeviceResult = MutableLiveData<ResultState<Any>>()

    /**
     * 获取设备
     */
    fun getRooms(isReqRoom: Boolean = false) {
        CacheUtil.getUser()?.let {
            request({ apiService.getRoomList(it.userId) }, { datas ->
                if (datas.isNotEmpty()) {
                    gatewayList = datas
                }
                if (datas.isNotEmpty() && datas[0].roomList.isNotNull() && datas[0].roomList.isNotEmpty()) {
                    val roomEntity = datas[0].roomList[0]

                    if (isReqRoom) {
                        CacheUtil.setCurRoom(roomEntity)

                        getDeivces()
                    }
                } else {
                    //请求成功
                    val listDataUiState = ListDataUiState(
                        isSuccess = true,
                        isRefresh = true,
                        isEmpty = true,
                        hasMore = false,
                        isFirstEmpty = true,
                        listData = arrayListOf<DeviceBean>()
                    )
                    devicesState.value = listDataUiState
                }


            }, {
                //请求失败
                val listDataUiState = ListDataUiState(
                    isSuccess = false,
                    errMessage = it.errorMsg,
                    isRefresh = true,
                    listData = arrayListOf<DeviceBean>()
                )
                devicesState.value = listDataUiState
            })
        }

    }

    /**
     * 获取设备
     */
    fun getDeivces() {
        var roomId = -1
        CacheUtil.getCurRoom()?.let {
            roomId = it.roomId
        }
        //如果之前没有获取过房间
        if (roomId == -1) {
            getRooms(true)
            return
        }


        CacheUtil.getUser()?.let {
            request({ apiService.getDeviceFormRoom(roomId) }, { datas ->
                //请求成功
                val listDataUiState = ListDataUiState(
                    isSuccess = true,
                    isRefresh = true,
                    isEmpty = datas.isEmpty(),
                    hasMore = false,
                    isFirstEmpty = datas.isEmpty(),
                    listData = datas
                )
                devicesState.value = listDataUiState
            }, {
                //请求失败
                val listDataUiState = ListDataUiState(
                    isSuccess = false,
                    errMessage = it.errorMsg,
                    isRefresh = true,
                    listData = arrayListOf<DeviceBean>()
                )
                devicesState.value = listDataUiState
            })
        }

    }


    /**
     * 获取场景列表
     */
    fun getSceneSwitchs(sceneSwitchId: Int) {

        request({ apiService.getSceneSwitchs(sceneSwitchId) }, { data ->
            //请求成功
            val listDataUiState = ListDataUiState(
                isSuccess = true,
                isRefresh = true,
                isEmpty = data.sceneSwitchAttributeList.isEmpty(),
                hasMore = false,
                isFirstEmpty = data.sceneSwitchAttributeList.isEmpty(),
                listData = data.sceneSwitchAttributeList
            )
            scenesState.value = listDataUiState
        }, {
            //请求失败
            val listDataUiState = ListDataUiState(
                isSuccess = false,
                errMessage = it.errorMsg,
                isRefresh = true,
                listData = arrayListOf<SceneBean>()
            )
            scenesState.value = listDataUiState
        })
    }


    /**
     * 获取开关详情
     */
    fun getSwitchDevice(switchId: Int) {

        request({ apiService.getSwitcDevices(switchId) }, { data ->
            //请求成功
            val listDataUiState = ListDataUiState(
                isSuccess = true,
                isRefresh = true,
                isEmpty = data.isEmpty(),
                hasMore = false,
                isFirstEmpty = data.isEmpty(),
                listData = data
            )
            switchState.value = listDataUiState
        }, {
            //请求失败
            val listDataUiState = ListDataUiState(
                isSuccess = false,
                errMessage = it.errorMsg,
                isRefresh = true,
                listData = arrayListOf<SwitchBean>()
            )
            switchState.value = listDataUiState
        })
    }

    /**
     * 打开或关闭设备
     */
    fun operateDevice(deviceId: Int,status:String) {
        request(
            { apiService.operateDevice(deviceId, if (status=="ON") "OFF" else "ON") }, sceneSwitchResult,
            true,
            "请求网络中..."
        )
    }

    /**
     * 打开或关闭场景开关
     */
    fun openScene(sceneSwitchId: Int) {
        request(
            { apiService.openScene(sceneSwitchId) }, sceneSwitchResult,
            true,
            "请求网络中..."
        )
    }
}