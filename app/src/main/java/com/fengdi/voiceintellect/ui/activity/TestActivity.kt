package com.fengdi.voiceintellect.ui.activity

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.alibaba.fastjson.JSONException
import com.alibaba.fastjson.JSONObject
import com.alibaba.idst.nui.*
import com.alibaba.idst.nui.Constants.AudioState
import com.alibaba.idst.nui.Constants.NuiVprEvent
import com.blankj.utilcode.util.LogUtils
import com.fengdi.voiceintellect.R
import com.fengdi.voiceintellect.app.base.BaseActivity
import com.fengdi.voiceintellect.app.utils.Auth
import com.fengdi.voiceintellect.app.utils.Utils
import com.fengdi.voiceintellect.databinding.ActivityMainBinding
import com.fengdi.voiceintellect.viewmodel.MainViewModel
import com.github.lilei.coroutinepermissions.requestPermissionsForResult
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TestActivity : BaseActivity<MainViewModel, ActivityMainBinding>(), INativeNuiCallback {

    override fun layoutId() = R.layout.activity_main

    val WAVE_FRAM_SIZE = 20 * 2 * 1 * 16000 / 1000 //20ms audio for 16k/16bit/mono


    val SAMPLE_RATE = 16000


    lateinit var mAudioRecorder: AudioRecord

    var nui_instance = NativeNui()

    private var mInit = false

    lateinit var mHandler: Handler

    lateinit var mHanderThread: HandlerThread


    private val permsSd =
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)


    override fun initView(savedInstanceState: Bundle?) {

        imageView.setOnClickListener {
            startDialog()
        }
        CoroutineScope(Dispatchers.Main).launch {
            try {
                requestPermissionsForResult(*permsSd, rationale = "为了更好的提供服务，需要获取存储空间，音频权限权限")

                initNui()
            } catch (e: Exception) {
            }
        }
    }

    private fun initNui() {
        mHanderThread = HandlerThread("process_thread")
        mHanderThread.start()
        mHandler = Handler(mHanderThread.looper)


        //拷贝资源
        //这里主动调用完成SDK配置文件的拷贝
        if (CommonUtils.copyAssetsData(this)) {
            LogUtils.i("copy assets data done");
        } else {
            LogUtils.i("copy assets failed");
            return
        }
        //录音初始化，录音参数中格式只支持16bit/单通道，采样率支持8K/16K
        mAudioRecorder = AudioRecord(
            MediaRecorder.AudioSource.DEFAULT, SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, WAVE_FRAM_SIZE * 4
        )

        //获取工作路径

        val asset_path = CommonUtils.getModelPath(this)


        val debug_path = externalCacheDir!!.absolutePath + "/debug_" + System.currentTimeMillis()
        Utils.createDir(debug_path)
        //初始化SDK，注意用户需要在Auth.getAliYunTicket中填入相关ID信息才可以使用。


        val ret = nui_instance.initialize(this, genInitParams(asset_path, debug_path), Constants.LogLevel.LOG_LEVEL_VERBOSE, true)
        LogUtils.i("result = $ret")
        if (ret == Constants.NuiResultCode.SUCCESS) {
            mInit = true
        }

        //设置相关识别参数，具体参考API文档

        nui_instance.setParams(genParams())

    }

    private fun startDialog() {
        mHandler.post {
            val ret = nui_instance.startDialog(
                Constants.VadMode.TYPE_P2T,
                genDialogParams()
            )
            LogUtils.i("start done with $ret")
        }
    }

    private fun genDialogParams(): String? {
        var params = ""
        try {
            val dialog_param = JSONObject()
            params = dialog_param.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return params
    }


    private fun genParams(): String {
        var params = ""
        try {
            val nls_config = JSONObject()
            nls_config["enable_intermediate_result"] = true
            //            参数可根据实际业务进行配置
            nls_config["enable_inverse_text_normalization"] = true
            nls_config["enable_voice_detection"] = true

            val parameters = JSONObject()
            parameters["nls_config"] = nls_config
            parameters["service_type"] = Constants.kServiceTypeASR
            //            如果有HttpDns则可进行设置
            params = parameters.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return params
    }


    private fun genInitParams(workpath: String, debugpath: String): String? {
        var str = ""
        try {
            //获取token方式一般有两种：

            //方法1：
            //参考Auth类的实现在端上访问阿里云Token服务获取SDK进行获取
            val `object`: JSONObject = Auth.getAliYunTicket()
            Log.e("EdisonTicket", `object`.toString())
            `object`["url"] = "wss://nls-gateway.cn-shanghai.aliyuncs.com:443/ws/v1"
            `object`["device_id"] = Utils.getDeviceId()
            `object`["workspace"] = workpath
            `object`["debug_path"] = debugpath
            `object`["sample_rate"] = "16000"
            `object`["format"] = "opus"
            Log.e("EdisonTicket", `object`.toString())
            //            object.put("save_wav", "true");
            str = `object`.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return str
    }


    //当回调事件发生时调用
    override fun onNuiEventCallback(
        event: Constants.NuiEvent?, resultCode: Int, arg2: Int, kwsResult: KwsResult?,
        result: AsrResult?
    ) {
        LogUtils.i("onNuiEventCallbackEvent=$event")
        LogUtils.i("onNuiEventCallbackEvent result=${result?.asrResult}")
//        when (event) {
//            Constants.NuiEvent.EVENT_ASR_RESULT -> {
//                val jsonStr = result?.asrResult
//                val jsonObject = JSONObject.parseObject(jsonStr)
//                val payLoad = jsonObject.getJSONObject("payload")
//                val command = payLoad.getString("result")
//
//
//
//                dialogues[dialogues.size - 1].let {
//                    if (it.type == 1) {
//                        it.message = command
//                    }
//                }
//                runOnUiThread {
//                    setCurStatus(false)
//                    dialogueAdapter.notifyDataSetChanged()
//                }
//            }
//            Constants.NuiEvent.EVENT_ASR_PARTIAL_RESULT -> {
//                val jsonStr = result?.asrResult
//                val jsonObject = JSONObject.parseObject(jsonStr)
//                val payLoad = jsonObject.getJSONObject("payload")
//                val command = payLoad.getString("result")
//
//                asrResult = command
//
//                dialogues[dialogues.size - 1].let {
//                    if (it.type == 1) {
//                        it.message = command
//                    }
//                }
//
//                updataRecyclerView()
//
//
//            }
//            Constants.NuiEvent.EVENT_ASR_ERROR -> {
//                LogUtils.v("ERROR with $resultCode")
//                runOnUiThread {
//                    setCurStatus(false)
//                }
//
//            }
////            NuiEvent.EVENT_VAD_END -> {
////
////                voiceActiviteFlag = true
////            }
//        }
    }

    override fun onNuiNeedAudioData(buffer: ByteArray, len: Int): Int {
        if (mAudioRecorder.state != AudioRecord.STATE_INITIALIZED) {
            LogUtils.e("audio recorder not init")
            return -1
        }
        return mAudioRecorder.read(buffer, 0, len)
    }

    //当录音状态发送变化的时候调用
    override fun onNuiAudioStateChanged(state: AudioState) {
        LogUtils.i("onNuiAudioStateChanged")
        when (state) {
            AudioState.STATE_OPEN -> {
                LogUtils.i("audio recorder start")
                mAudioRecorder.startRecording()
                LogUtils.i("audio recorder start done")
            }
            AudioState.STATE_CLOSE -> {
                LogUtils.i("audio recorder close")
                mAudioRecorder.release()
            }
            AudioState.STATE_PAUSE -> {
                LogUtils.i("audio recorder pause")
                mAudioRecorder.stop()
            }
        }
    }

    override fun onNuiAudioRMSChanged(vol: Float) {
        //音频音量检测
        LogUtils.i("onNuiAudioRMSChanged vol $vol")

    }

    override fun onNuiVprEventCallback(event: NuiVprEvent) {
        LogUtils.i("onNuiVprEventCallback event $event")
    }

}


