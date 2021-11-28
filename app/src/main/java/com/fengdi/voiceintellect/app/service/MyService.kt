package com.fengdi.voiceintellect.app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.fengdi.voiceintellect.app.App
import com.fengdi.voiceintellect.app.event.EventViewModel
import com.kongqw.serialportlibrary.SerialPortFinder
import com.kongqw.serialportlibrary.SerialPortManager
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener
import java.io.File
import java.math.BigInteger

class MyService : Service(), OnOpenSerialPortListener {

    val eventViewModel: EventViewModel by lazy { App.eventViewModelInstance }

    val mSerialPortManager: SerialPortManager by lazy {
        SerialPortManager()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        initSerialPort()
    }

    /***
     * 初始化串口
     */
    private fun initSerialPort() {
        LogUtils.v("initSerialPort")
        val serialPortFinder = SerialPortFinder()
        val devices = serialPortFinder.devices
        for (device in devices) {
            val name = device.name
            if (name != null && name == "ttyS4") {
                // 打开串口
                mSerialPortManager.setOnOpenSerialPortListener(this)
                    .setOnSerialPortDataListener(object : OnSerialPortDataListener {
                        override fun onDataReceived(bytes: ByteArray) {
                            val hex = BigInteger(1, bytes).toString(16)
                            // f40601ff
                            if (hex == "f40601ff") {
                                eventViewModel.startNuiEvent.value = true
                            }
                        }

                        override fun onDataSent(bytes: ByteArray) {

                        }
                    })
                    .openSerialPort(device.file, 9600)
                break
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mSerialPortManager.closeSerialPort()

    }

    override fun onSuccess(device: File) {
        LogUtils.v(String.format("串口 [%s] 打开成功", device.getPath()))
    }

    override fun onFail(device: File, p1: OnOpenSerialPortListener.Status?) {
        LogUtils.v("onFail$p1")
        when (p1) {
            OnOpenSerialPortListener.Status.NO_READ_WRITE_PERMISSION -> ToastUtils.showShort(device.path, "没有读写权限")
            OnOpenSerialPortListener.Status.OPEN_FAIL -> ToastUtils.showShort(device.path, "串口打开失败")
            else -> ToastUtils.showShort(device.path, "串口打开失败")
        }
    }
}