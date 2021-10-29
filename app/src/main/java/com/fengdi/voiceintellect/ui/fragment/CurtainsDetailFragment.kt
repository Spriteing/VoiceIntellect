package com.fengdi.voiceintellect.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import com.fengdi.voiceintellect.R
import com.fengdi.voiceintellect.app.annotation.VMScope
import com.fengdi.voiceintellect.app.base.BaseFragment
import com.fengdi.voiceintellect.app.ext.injectFragmentVM
import com.fengdi.voiceintellect.app.ext.showMessage
import com.fengdi.voiceintellect.app.weight.MyToolBar
import com.fengdi.voiceintellect.data.model.bean.DeviceBean
import com.fengdi.voiceintellect.databinding.FragmentCurtainsBinding
import com.fengdi.voiceintellect.viewmodel.MainViewModel
import com.fengdi.voiceintellect.viewmodel.request.RequestDeviceViewModel
import kotlinx.android.synthetic.main.fragment_curtains.*
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
class CurtainsDetailFragment : BaseFragment<MainViewModel, FragmentCurtainsBinding>() {


    @VMScope("curtains")
    lateinit var requestDeviceViewModel: RequestDeviceViewModel

    override fun layoutId() = R.layout.fragment_curtains

    //当前场景设备
    var curtainDeviceId = -1


    @RequiresApi(Build.VERSION_CODES.N)
    override fun initView(savedInstanceState: Bundle?) {
        injectFragmentVM()

        arguments?.getParcelable<DeviceBean>("curtain")?.let {
            myToolBar.title = it.deviceName
            curtainDeviceId = it.deviceId

        }

        myToolBar.setOnToolBarClickListener(object : MyToolBar.OnToolBarClick() {
            override fun onLeftClick() {
                nav().navigateUp()
            }
        })

        tvGoBack.setOnClickListener {
            nav().navigateUp()
        }

        ivClose.setOnClickListener {
            //关闭窗帘
            sbProgress.setProgress(0, true)
            requestDeviceViewModel.operateCurtail(curtainDeviceId, 0)
        }

        ivOpen.setOnClickListener {
            //打开窗帘
            sbProgress.setProgress(100, true)
            requestDeviceViewModel.operateCurtail(curtainDeviceId, 100)
        }


        sbProgress.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                requestDeviceViewModel.operateCurtail(curtainDeviceId, sbProgress.progress)
            }

        })


    }

    override fun createObserver() {

        requestDeviceViewModel.sceneSwitchResult.observe(viewLifecycleOwner) {
            parseState(it, {
                showMessage("操作成功")
                requestDeviceViewModel.getSceneSwitchs(curtainDeviceId)
            }, {
                showMessage(it.errorMsg)
            })
        }
    }

}