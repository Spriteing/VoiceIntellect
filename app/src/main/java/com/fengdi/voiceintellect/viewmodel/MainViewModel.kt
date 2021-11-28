package com.fengdi.voiceintellect.viewmodel

import androidx.lifecycle.MutableLiveData
import com.fengdi.voiceintellect.app.network.apiService
import com.fengdi.voiceintellect.app.utils.CacheUtil
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.state.ResultState


class MainViewModel : BaseViewModel() {


    //语音控制的请求结果
    val discernResult = MutableLiveData<ResultState<String>>()

//    /**
//     * 语音控制
//     */
//    fun sendVoiceCommand() {
//        var roomId: Int
//        CacheUtil.getCurRoom()?.let {
//            roomId = it.roomId
//            request(
//                { apiService.voiceCommand(roomId, asrResult.value!!) },
//                { result ->
//                    dialogues1.add(DialogueBean(0, result))
//                    dialogues.value = dialogues1
//
//                    nui_tts_instance.startTts("1", "", result)
//
//                }, {
//                    dialogues1.add(DialogueBean(0, it.errorMsg))
//                    dialogues.value = dialogues1
//
//                    nui_tts_instance.startTts("1", "", it.errorMsg)
//
//                }
//            )
//
//            asrResult.value = ""
////                isShowVoiceDialog.value = false
//        }
//
//
//    }


    /**
     * 获取验证码
     */
    fun sendVoiceCommand(asrResult: String) {
        var roomId: Int
        CacheUtil.getCurRoom()?.let {
            roomId = it.roomId
            request(
                { apiService.voiceCommand(roomId,asrResult) },
                discernResult,
                false,
                "正在获取..."
            )
        }
    }

}