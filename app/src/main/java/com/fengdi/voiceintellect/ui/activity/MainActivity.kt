package com.fengdi.voiceintellect.ui.activity

import android.Manifest
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.fengdi.voiceintellect.R
import com.fengdi.voiceintellect.app.base.BaseActivity
import com.fengdi.voiceintellect.data.model.bean.DialogueBean
import com.fengdi.voiceintellect.databinding.ActivityMainBinding
import com.fengdi.voiceintellect.ui.adapter.VoiceHelperAdapter
import com.fengdi.voiceintellect.ui.dialog.MyBottomPopup
import com.fengdi.voiceintellect.viewmodel.MainViewModel
import com.github.lilei.coroutinepermissions.requestPermissionsForResult
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import kotlinx.android.synthetic.main.popup_voice_helper.*
import kotlinx.android.synthetic.main.popup_voice_helper.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.zip.Inflater

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    //退出时间
    var exitTime = 0L

    private val permsSd =
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)

    val dialogueAdapter: VoiceHelperAdapter by lazy {
        VoiceHelperAdapter(arrayListOf())
    }

    override fun layoutId() = R.layout.activity_main

    lateinit var voiceHelperDialog: MyBottomPopup

    lateinit var dialogView: View


    override fun onResume() {
        /**  * 设置为横屏   */
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
                    //如果当前界面不是主页，那么直接调用返回即可
                    nav.navigateUp()
                } else {
                    //是主页
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        ToastUtils.showShort("再按一次退出程序")
                        exitTime = System.currentTimeMillis()
                    } else {
                        finish()
                    }
                }
            }
        })


        CoroutineScope(Dispatchers.Main).launch {
            try {
                requestPermissionsForResult(*permsSd, rationale = "为了更好的提供服务，需要获取存储空间，音频权限权限")

                mViewModel.initNui(this@MainActivity)
            } catch (e: Exception) {
            }
        }

        initVoiceHelper()

        mViewModel.isShowVoiceDialog.observe(this) {
            if (it) {
                voiceHelperDialog.show()
            } else {
                voiceHelperDialog.dismiss()
            }
        }

        mViewModel.dialogues.observe(this) {
            dialogueAdapter.setList(it)
            if (it.size > 3) {
                rcyDialogue.scrollToPosition(it.size - 1)
            }
        }

        mViewModel.isAwaken.observe(this) {
            if (voiceHelperDialog.isShowing) {
                if (it) {
                    dialogView.tvStatus.setText("倾听中...")
                } else {
                    dialogView.tvStatus.setText("倾听停止")
                }
            }
        }


        mDatabind.click = PorxyClick()
        mDatabind.viewmodel = mViewModel

    }


    /**
     * 显示语音助手
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
                mViewModel.stopNui()
            }
        }
    }


    inner class PorxyClick {
        fun startNui() = mViewModel.startNui()
    }


    override fun onStop() {
        super.onStop()
        mViewModel.onStop()
    }


}