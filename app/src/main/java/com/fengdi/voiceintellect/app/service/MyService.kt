package com.fengdi.voiceintellect.app.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.fengdi.voiceintellect.R
import com.fengdi.voiceintellect.app.App
import com.fengdi.voiceintellect.app.event.EventViewModel
import com.kongqw.serialportlibrary.SerialPortFinder
import com.kongqw.serialportlibrary.SerialPortManager
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.hgj.jetpackmvvm.ext.launch
import me.hgj.jetpackmvvm.ext.util.keyguardManager
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
        eventViewModel.viewModelScope.launch {
            while (true){
                delay(1000)
                mSerialPortManager.sendBytes(ByteArray(2))
            }
        }
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
                        @SuppressLint("InvalidWakeLockTag")
                        override fun onDataReceived(bytes: ByteArray) {
                            val hex = BigInteger(1, bytes).toString(16)
                            // f40601ff
                            if (hex == "f40601ff") {
                                LogUtils.v("--------1-------串口收到消息")


                                //唤醒屏幕代码
                                val mPowerManager = getSystemService(POWER_SERVICE) as PowerManager
                                val mWakeLock =
                                    mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag")
                                mWakeLock.acquire(60 * 1000L /*1 minutes*/)

                                //解锁屏幕代码

                                val mUnLock: KeyguardManager.KeyguardLock
                                val keyguardManager = (getSystemService(KEYGUARD_SERVICE) as KeyguardManager)
                                mUnLock = keyguardManager.newKeyguardLock("unLock")
                                mUnLock.disableKeyguard()

                                eventViewModel.viewModelScope.launch{
                                    eventViewModel.startNuiEvent.value = true
                                }
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

    private var mNotificationManager: NotificationManager? = null
    private val NOTIFICATION_ID = 123452544



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android O requires a Notification Channel.

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.app_name)

            // Create the channel for the notification
            val mChannel =
                NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager!!.createNotificationChannel(mChannel)
        }

        startForeground(NOTIFICATION_ID, getNotification())

        return START_STICKY

    }

    private val CHANNEL_ID = "channel_02"

    private fun getNotification(): Notification? {

        // Get the application name from the Settings

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this)
            .setContentTitle("银科智能")
            .setContentText("正在运行中")
            .setOngoing(true)
            .setPriority(Notification.PRIORITY_HIGH)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setWhen(System.currentTimeMillis())

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID) // Channel ID
        }
        return builder.build()
    }


    override fun onDestroy() {
        super.onDestroy()
        LogUtils.v(String.format("closeSerialPort()"))
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