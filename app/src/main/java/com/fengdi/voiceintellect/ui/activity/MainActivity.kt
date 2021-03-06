package com.fengdi.voiceintellect.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSONException
import com.alibaba.fastjson.JSONObject
import com.alibaba.idst.nui.*
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.fengdi.voiceintellect.R
import com.fengdi.voiceintellect.app.App
import com.fengdi.voiceintellect.app.base.BaseActivity
import com.fengdi.voiceintellect.app.event.EventViewModel
import com.fengdi.voiceintellect.app.network.apiService
import com.fengdi.voiceintellect.app.nui.AudioPlayer
import com.fengdi.voiceintellect.app.nui.AudioPlayerCallback
import com.fengdi.voiceintellect.app.service.MyService
import com.fengdi.voiceintellect.app.utils.Auth
import com.fengdi.voiceintellect.app.utils.CacheUtil
import com.fengdi.voiceintellect.app.utils.Utils
import com.fengdi.voiceintellect.data.model.bean.DialogueBean
import com.fengdi.voiceintellect.databinding.ActivityMainBinding
import com.fengdi.voiceintellect.ui.adapter.VoiceHelperAdapter
import com.fengdi.voiceintellect.ui.dialog.MyBottomPopup
import com.fengdi.voiceintellect.viewmodel.MainViewModel
import com.github.lilei.coroutinepermissions.requestPermissionsForResult
import com.kongqw.serialportlibrary.SerialPortFinder
import com.kongqw.serialportlibrary.SerialPortManager
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener
import kotlinx.android.synthetic.main.popup_voice_helper.*
import kotlinx.android.synthetic.main.popup_voice_helper.view.*
import kotlinx.coroutines.*
import me.hgj.jetpackmvvm.ext.parseState
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.Runnable
import java.math.BigInteger
import java.util.*


class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(), INativeNuiCallback {

    //????????????
    var exitTime = 0L

    private val permsSd =
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)

    val dialogueAdapter: VoiceHelperAdapter by lazy {
        VoiceHelperAdapter(dialogues)
    }

    override fun layoutId() = R.layout.activity_main

    lateinit var voiceHelperDialog: MyBottomPopup

    lateinit var dialogView: View


    val eventViewModel: EventViewModel by lazy { App.eventViewModelInstance }


    /**
     * ??????????????????
     */
    var isAwaken = false

    val SAMPLE_RATE = 16000


    //?????????????????????
    var asrResult = ""

    val WAVE_FRAM_SIZE = 20 * 2 * 1 * 16000 / 1000 //20ms audio for 16k/16bit/mono


    //???????????????
    val dialogues = ArrayList<DialogueBean>()

    var voiceHighCount = 0 //???????????????

    var voiceLowCount = 0 //???????????????

    var voiceActiviteFlag = false //????????????????????????

    private var mInit = false

    var assetPath: String = ""

    var initialized = false

    //  AudioPlayer??????????????????16000
    lateinit var mAudioTrack: AudioPlayer

    private var output_file: OutputStream? = null
    private val b_savewav = false


    //////////////////////////////////////
    val nui_tts_instance by lazy {
        NativeNui(Constants.ModeType.MODE_TTS)
    }

    val stopRunable = Runnable { stopNui() }


    lateinit var mHandler: Handler

    //??????????????????
    val nuiInstance by lazy {
        NativeNui()
    }


    var mSoundPool: SoundPool? = null


    private val soundIdMap: HashMap<Int, Int> = HashMap()


    override fun onResume() {
        /**  * ???????????????   */
        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        super.onResume()
    }

    override fun initView(savedInstanceState: Bundle?) {


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val nav = Navigation.findNavController(this@MainActivity, R.id.host_fragment)
                if (nav.currentDestination != null && nav.currentDestination!!.id != R.id.mainFragment) {
                    //???????????????????????????????????????????????????????????????
                    nav.navigateUp()
                } else {
                    //?????????
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        ToastUtils.showShort("????????????????????????")
                        exitTime = System.currentTimeMillis()
                    } else {
                        finish()
                    }
                }
            }
        })


        CoroutineScope(Dispatchers.Main).launch {
            try {
                requestPermissionsForResult(*permsSd, rationale = "???????????????????????????????????????????????????????????????????????????")

                startService(Intent(this@MainActivity, MyService::class.java))
                initNui()
            } catch (e: Exception) {
            }
        }

        initVoiceHelper()



        mDatabind.click = PorxyClick()
        mDatabind.viewmodel = mViewModel

        mViewModel.discernResult.observe(this) {
            parseState(it, { result ->
                dialogues.add(DialogueBean(0, result))
                nui_tts_instance.startTts("1", "", result)
                dialogueAdapter.notifyDataSetChanged()
            }, { e ->
                dialogues.add(DialogueBean(0, e.errorMsg))
                dialogueAdapter.notifyDataSetChanged()

                nui_tts_instance.startTts("1", "", e.errorMsg)
            })
        }

        eventViewModel.startNuiEvent.observeInActivity(this) {
            if (it) {
                showNuiDialog()
            }
        }

    }

    fun setCurStatus(isDiscern: Boolean) {
        LogUtils.i("--------1-------setCurStatus")
        isAwaken = isDiscern
        if (voiceHelperDialog.isShowing) {
            if (isDiscern) {
                dialogView.tvStatus.setText("?????????...")
            } else {
                dialogView.tvStatus.setText("????????????")
            }
        }
    }


    /**
     * ??????????????????
     */
    fun initVoiceHelper() {
        voiceHelperDialog = MyBottomPopup(this).apply {
            dialogView = LayoutInflater.from(this@MainActivity).inflate(R.layout.popup_voice_helper, null)
            dialogView.rcyDialogue.let {
                it.layoutManager = LinearLayoutManager(this@MainActivity)
                it.adapter = dialogueAdapter
            }

            addContentView(dialogView)
            setOnCancelListener {
                stopNui()
            }
        }
    }


    inner class PorxyClick {
        fun startNui() {
            dialogues.add(DialogueBean(0, "?????????????????????"))

            voiceHelperDialog.show()
            this@MainActivity.startNui()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        nuiInstance.release()
    }


    fun initNui() {

        mHandler = Handler()

        mAudioTrack = AudioPlayer(object : AudioPlayerCallback {
            override fun playStart() {
                LogUtils.i("start play")
            }

            override fun playOver() {

            }
        })

        val path = CommonUtils.getModelPath(this)
        LogUtils.i("workpath = $path")
        assetPath = path


        //????????????????????????SDK?????????????????????
        if (CommonUtils.copyAssetsData(this)) {
            LogUtils.i("copy assets data done")
        } else {
            LogUtils.i("copy assets failed")
            return
        }
        if (Constants.NuiResultCode.SUCCESS == Initialize(path)) {
            initialized = true
        } else {
            LogUtils.e("init failed")
        }

        //??????????????????
        val assetPath = CommonUtils.getModelPath(this)

        val debugPath: String = this.externalCacheDir?.absolutePath + "/debug_" + System.currentTimeMillis()
        Utils.createDir(debugPath)


        //?????????SDK????????????????????????Auth.getAliYunTicket???????????????ID????????????????????????
        val ret: Int = nuiInstance.initialize(this, genInitParams(assetPath, debugPath), Constants.LogLevel.LOG_LEVEL_VERBOSE, true)
        LogUtils.i("result = $ret")
        if (ret == Constants.NuiResultCode.SUCCESS) {
            mInit = true
        }


        //???????????????????????????????????????API??????
        nuiInstance.setParams(genParams())

        initSound()

    }

    /**
     * ???????????????
     */
    private fun initSound() {
        if (mSoundPool == null) {
            // 5.0 ??? ??????
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                var audioAttributes: AudioAttributes?
                audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
                mSoundPool = SoundPool.Builder()
                    .setMaxStreams(16)
                    .setAudioAttributes(audioAttributes)
                    .build()
            } else { // 5.0 ??????
                mSoundPool = SoundPool(16, AudioManager.STREAM_MUSIC, 0) // ??????SoundPool
            }
        }
        //????????????ID????????????????????????

        //????????????ID????????????????????????
        val mp30: Array<Int> = loadRaw(App.instance, R.raw.help)
        val mp31: Array<Int> = loadRaw(App.instance, R.raw.here)
        soundIdMap.put(mp30[0], mp30[1])
        soundIdMap.put(mp31[0], mp31[1])
    }


    /**
     * ??????????????????
     */
    private fun loadRaw(context: Context, raw: Int): Array<Int> {
        val soundId = mSoundPool!!.load(context, raw, 1)
        val duration = getMp3Duration(context, raw)
        return arrayOf(soundId, duration)
    }

    /**
     * ???????????????????????????
     */
    private fun getMp3Duration(context: Context, rawId: Int): Int {
        try {
            val uri = Uri.parse("android.resource://" + context.packageName + "/" + rawId)
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(context, uri)
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer.prepare()
            val time = mediaPlayer.duration
            //Toast.makeText(this,"EdisonTime:"+String.valueOf(time),Toast.LENGTH_SHORT).show();
            Log.e("EdisonTime34", time.toString())
            return time
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return 0
    }

    fun updataRecyclerView() {
        runOnUiThread {
            dialogueAdapter.notifyDataSetChanged()
        }
    }

    private val mPlayThread = object : Thread() {
        override fun run() {
            val randIndex = (1 + Math.random() * (2 - 1 + 1)).toInt()
            val duration: Int? = soundIdMap[randIndex]
            Log.e("EdisonSongIndex", randIndex.toString())
            Log.e("EdisonTime", duration.toString())

            mSoundPool?.play(randIndex, 1f, 1f, 0, 0, 1f)

            dialogues.clear()
            if (randIndex == 1) {
                dialogues.add(DialogueBean(0, "?????????????????????"))
            } else {
                dialogues.add(DialogueBean(0, "??????"))
            }

            dialogues.add(DialogueBean(1, ""))

            updataRecyclerView()

            try {
                //???????????????????????????
                if (duration != null) {
                    sleep((duration / 10).toLong())
                    startNui()
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    private fun showNuiDialog() {
        runOnUiThread {
            LogUtils.v("--------1-------showNuiDialog")
            voiceHelperDialog.show()
        }

        mPlayThread.run()
        //????????????
    }


    private val mAudioRecorder by lazy {
        AudioRecord(
            MediaRecorder.AudioSource.DEFAULT, SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, WAVE_FRAM_SIZE * 4
        );
    }


    //??????????????????????????????
    override fun onNuiEventCallback(
        event: Constants.NuiEvent?, resultCode: Int, arg2: Int, kwsResult: KwsResult?,
        result: AsrResult?
    ) {
        LogUtils.i("onNuiEventCallbackEvent=$event")
        LogUtils.i("onNuiEventCallbackEvent result=${result?.asrResult}")
        when (event) {
            Constants.NuiEvent.EVENT_ASR_RESULT -> {


                val jsonStr = result?.asrResult
                val jsonObject = JSONObject.parseObject(jsonStr)
                val payLoad = jsonObject.getJSONObject("payload")
                val command = payLoad.getString("result")



                dialogues[dialogues.size - 1].let {
                    if (it.type == 1) {
                        it.message = command
                    }
                }
                runOnUiThread {
                    setCurStatus(false)
                    dialogueAdapter.notifyDataSetChanged()
                }
            }
            Constants.NuiEvent.EVENT_ASR_PARTIAL_RESULT -> {


                val jsonStr = result?.asrResult
                val jsonObject = JSONObject.parseObject(jsonStr)
                val payLoad = jsonObject.getJSONObject("payload")
                val command = payLoad.getString("result")

                asrResult = command

                dialogues[dialogues.size - 1].let {
                    if (it.type == 1) {
                        it.message = command
                    }
                }

                updataRecyclerView()


            }
            Constants.NuiEvent.EVENT_ASR_ERROR -> {
                LogUtils.v("ERROR with $resultCode")
                runOnUiThread {
                    setCurStatus(false)
                }

            }
//            NuiEvent.EVENT_VAD_END -> {
//
//                voiceActiviteFlag = true
//            }
        }


    }

    /**
     *?????????NativeNui???start????????????????????????????????????????????????????????????buffer????????????????????????????????????
     *????????????????????????????????????????????????????????????return?????????????????????????????????????????????<=0??????????????????
     */
    override fun onNuiNeedAudioData(buffer: ByteArray, len: Int): Int {
        if (mAudioRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
            LogUtils.e("audio recorder not init")
            return -1
        }
        return mAudioRecorder.read(buffer, 0, len)
    }

    /**
     * ??????????????????????????????????????????
     */
    override fun onNuiAudioStateChanged(state: Constants.AudioState?) {
        LogUtils.i("onNuiAudioStateChanged")
        if (state == Constants.AudioState.STATE_OPEN) {
            LogUtils.i("audio recorder start")
            mAudioRecorder.startRecording()
            LogUtils.i("audio recorder start done")
        } else if (state == Constants.AudioState.STATE_CLOSE) {
            LogUtils.i("audio recorder close")
            mAudioRecorder.release()
        } else if (state == Constants.AudioState.STATE_PAUSE) {
            LogUtils.i("audio recorder pause")
            mAudioRecorder.stop()
        }
    }


    /**
     * ??????????????????
     */
    override fun onNuiAudioRMSChanged(vol: Float) {
        //??????????????????
        LogUtils.i("onNuiAudioRMSChanged vol $vol")
        //  Log.e("EdisonVoiceValue","onNuiAudioRMSChanged vol " + val);
        //  Log.e("EdisonVoiceValue","onNuiAudioRMSChanged vol " + val);
        //  Log.e("EdisonVoiceValue","onNuiAudioRMSChanged vol " + val);
        if (vol > -40) { //????????????????????????
            if (voiceHighCount < 300) voiceHighCount++
            if (voiceHighCount > 20) voiceActiviteFlag = true
        }
        LogUtils.i("voiceHighCount $voiceHighCount")

        //??????????????????
        if (voiceActiviteFlag) {
            if (vol < -50) {
                LogUtils.i("voiceLowCount $voiceLowCount")
                voiceLowCount++
                if (voiceLowCount > 30) {

//                    val ret: Long = nuiInstance.stopDialog().toLong()
//                    LogUtils.i("cancel dialog $ret end")

//                    stopNui()
                    voiceActiviteFlag = false
                    voiceLowCount = 0
                    voiceHighCount = 0
                    mHandler.postDelayed({
                        stopNui()
                        mViewModel.sendVoiceCommand(asrResult)
                    }, 2000)


                }
            } else voiceLowCount = 0
        }
    }

    override fun onNuiVprEventCallback(event: Constants.NuiVprEvent?) {
        LogUtils.i("onNuiVprEventCallback event $event");
    }


    /**
     * ??????????????????
     */
    fun startNui() {

        if (isAwaken) {
            //?????? ????????????
            stopNui()
        }
        val ret: Int = nuiInstance.startDialog(
            Constants.VadMode.TYPE_P2T,
            "{}"
        )
        LogUtils.i("--------1-------start done with $ret")


        runOnUiThread {
            setCurStatus(true)
        }

        mHandler.postDelayed(stopRunable, 10000)

        voiceLowCount = 0
        voiceActiviteFlag = false
        voiceHighCount = 0
    }


    /**
     * ????????????
     */
    fun stopNui() {
        mHandler.removeCallbacks(stopRunable)
        nuiInstance.stopDialog()
        setCurStatus(false)
    }


    private fun genInitParams(workpath: String, debugpath: String): String? {
        var str = ""
        try {
            val jsonObj: JSONObject = Auth.getAliYunTicket()
            LogUtils.e("EdisonTicket", jsonObj.toString())

            jsonObj["url"] = "wss://nls-gateway.cn-shanghai.aliyuncs.com:443/ws/v1"
            jsonObj["device_id"] = Utils.getDeviceId()
            jsonObj["workspace"] = workpath
            jsonObj["debug_path"] = debugpath
            jsonObj["sample_rate"] = "16000"
            jsonObj["format"] = "opus"
            Log.e("EdisonTicket", jsonObj.toString())
            str = jsonObj.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        LogUtils.i("InsideUserContext:$str")
        return str
    }

    private fun genParams(): String {
        var params = ""
        try {
            val nls_config = JSONObject()
            nls_config["enable_intermediate_result"] = true
            //            ???????????????????????????????????????
            nls_config["enable_inverse_text_normalization"] = true
            nls_config["enable_voice_detection"] = true
            val parameters = JSONObject()
            parameters["nls_config"] = nls_config
            parameters["service_type"] = Constants.kServiceTypeASR
            //            ?????????HttpDns??????????????????
//            parameters.put("direct_ip", Utils.getDirectIp());
            params = parameters.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return params
    }


    /////////////////////////////// TTs Edison

    /////////////////////////////// TTs Edison
    private fun Initialize(path: String): Int {
        val ret: Int = nui_tts_instance.tts_initialize(object : INativeTtsCallback {
            override fun onTtsEventCallback(event: INativeTtsCallback.TtsEvent, task_id: String, ret_code: Int) {
                LogUtils.i("tts event:$event task id $task_id ret $ret_code")
                if (event == INativeTtsCallback.TtsEvent.TTS_EVENT_START) {
                    mAudioTrack.play()
                    LogUtils.i("start play")
                } else if (event == INativeTtsCallback.TtsEvent.TTS_EVENT_END) {
                    LogUtils.i("play end")
                    if (b_savewav) {
                        try {
                            output_file?.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                } else if (event == INativeTtsCallback.TtsEvent.TTS_EVENT_PAUSE) {
                    mAudioTrack.pause()
                    LogUtils.i("play pause")
                } else if (event == INativeTtsCallback.TtsEvent.TTS_EVENT_RESUME) {
                    mAudioTrack.play()
                } else if (event == INativeTtsCallback.TtsEvent.TTS_EVENT_ERROR) {
                }
            }

            override fun onTtsDataCallback(info: String, info_len: Int, data: ByteArray) {
                if (info.length > 0) {
                    LogUtils.i("info: $info")
                }
                if (data.isNotEmpty()) {
                    mAudioTrack.setAudioData(data)
                    LogUtils.i("write:" + data.size)
                    if (b_savewav) {
                        try {
                            output_file?.write(data)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            override fun onTtsVolCallback(vol: Int) {
                LogUtils.i("tts vol $vol")
            }
        }, genTicket(path), Constants.LogLevel.LOG_LEVEL_VERBOSE, true)
        if (Constants.NuiResultCode.SUCCESS != ret) {
            LogUtils.i("create failed")
        }
        nui_tts_instance.setparamTts("sample_rate", "16000")
        // ??????????????????????????????????????????????????????
        nui_tts_instance.setparamTts("font_name", "siqi")
        nui_tts_instance.setparamTts("enable_subtitle", "1")
        //        nui_tts_instance.setparamTts("speed_level", "1");
//        nui_tts_instance.setparamTts("pitch_level", "0");
//        nui_tts_instance.setparamTts("volume", "1.0");
        if (b_savewav) {
            try {
                output_file = FileOutputStream("/sdcard/mit/tmp/test.pcm")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return ret
    }


    private fun genTicket(workpath: String): String? {
        var str = ""
        try {
            //??????token????????????????????????

            //??????1???
            //??????Auth????????????????????????????????????Token????????????SDK????????????
            val obj = Auth.getAliYunTicket()

            //??????2?????????????????????
            //????????????????????????token?????????????????????????????????token????????????????????????


            //?????????????????????id???token?????????????????????????????????????????????????????????????????????????????????
            //https://help.aliyun.com/document_detail/72153.html?spm=a2c4g.11186623.6.555.59bd69bb6tkTSc
            // JSONObject object = new JSONObject();
            //  object.put("app_key", "n4apKnXZwL1Q7P2C");
            //   object.put("token", "");
            obj["device_id"] = Utils.getDeviceId()
            obj["url"] = "wss://nls-gateway.cn-shanghai.aliyuncs.com:443/ws/v1"
            obj["workspace"] = workpath
            // ?????????????????????
            obj["mode_type"] = "2"
            str = obj.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        LogUtils.i("UserContext:$str")
        return str
    }


}