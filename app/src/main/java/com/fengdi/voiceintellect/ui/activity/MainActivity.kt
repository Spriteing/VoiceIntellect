package com.fengdi.voiceintellect.ui.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import com.alibaba.idst.nui.INativeNuiCallback
import com.blankj.utilcode.util.ToastUtils
import com.fengdi.voiceintellect.R
import com.fengdi.voiceintellect.app.base.BaseActivity
import com.fengdi.voiceintellect.databinding.ActivityMainBinding
import com.fengdi.voiceintellect.viewmodel.MainViewModel
import com.github.lilei.coroutinepermissions.requestPermissionsForResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    //退出时间
    var exitTime = 0L

    private val permsSd =
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)

    override fun layoutId() = R.layout.activity_main

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

        mDatabind.click = PorxyClick()
        mDatabind.viewmodel = mViewModel
    }

    inner class PorxyClick {
        fun startNui() = mViewModel.startNui()
    }

    override fun onStop() {
        super.onStop()
        mViewModel.onStop()
    }

}