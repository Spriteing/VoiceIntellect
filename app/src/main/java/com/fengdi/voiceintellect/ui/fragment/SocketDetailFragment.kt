package com.fengdi.voiceintellect.ui.fragment

import android.os.Bundle
import android.view.MotionEvent
import com.fengdi.voiceintellect.R
import com.fengdi.voiceintellect.app.annotation.VMScope
import com.fengdi.voiceintellect.app.base.BaseFragment
import com.fengdi.voiceintellect.app.ext.injectFragmentVM
import com.fengdi.voiceintellect.app.ext.showMessage
import com.fengdi.voiceintellect.app.weight.MyToolBar
import com.fengdi.voiceintellect.data.model.bean.DeviceBean
import com.fengdi.voiceintellect.databinding.FragmentMainBinding
import com.fengdi.voiceintellect.databinding.FragmentSocketBinding
import com.fengdi.voiceintellect.viewmodel.MainViewModel
import com.fengdi.voiceintellect.viewmodel.request.RequestDeviceViewModel
import kotlinx.android.synthetic.main.fragment_socket.*
import me.hgj.jetpackmvvm.ext.nav
import me.hgj.jetpackmvvm.ext.parseState

/**
 *
 *
 *@author: YangJie
 *@email: 2295360491@qq.com
 *@time: 2021/9/25 下午 1:42
 *@descripton: 插座详情
 *
 *
 */
class SocketDetailFragment : BaseFragment<MainViewModel, FragmentSocketBinding>() {


    @VMScope("device")
    lateinit var requestDeviceViewModel: RequestDeviceViewModel

    override fun layoutId() = R.layout.fragment_socket

    //当前场景设备
    var socketDeviceId = -1


    override fun initView(savedInstanceState: Bundle?) {
        injectFragmentVM()

        arguments?.getParcelable<DeviceBean>("socket")?.let {
            myToolBar.title = it.deviceName
            socketDeviceId = it.deviceId

            swStatus.isChecked = it.status == "ON"
        }

        myToolBar.setOnToolBarClickListener(object : MyToolBar.OnToolBarClick() {
            override fun onLeftClick() {
                nav().navigateUp()
            }
        })

        tvGoBack.setOnClickListener {
            nav().navigateUp()
        }


        swStatus.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                requestDeviceViewModel.operateDevice(socketDeviceId, if (swStatus.isChecked) "ON" else "OFF")
            }
            return@setOnTouchListener false
        }

    }

    override fun createObserver() {

        requestDeviceViewModel.sceneSwitchResult.observe(viewLifecycleOwner) {
            parseState(it, {
                showMessage("操作成功")
            }, {
                showMessage(it.errorMsg)
            })
        }
    }

}