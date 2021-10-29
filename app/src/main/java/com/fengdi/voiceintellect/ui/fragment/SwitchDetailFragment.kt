package com.fengdi.voiceintellect.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.fengdi.voiceintellect.R
import com.fengdi.voiceintellect.app.annotation.VMScope
import com.fengdi.voiceintellect.app.base.BaseFragment
import com.fengdi.voiceintellect.app.ext.*
import com.fengdi.voiceintellect.app.weight.MyToolBar
import com.fengdi.voiceintellect.data.model.bean.DeviceBean
import com.fengdi.voiceintellect.databinding.FragmentMainBinding
import com.fengdi.voiceintellect.ui.adapter.SwitchAdapter
import com.fengdi.voiceintellect.viewmodel.MainViewModel
import com.fengdi.voiceintellect.viewmodel.request.RequestDeviceViewModel
import com.kingja.loadsir.core.LoadService
import kotlinx.android.synthetic.main.fragment_switch.*
import me.hgj.jetpackmvvm.ext.nav
import me.hgj.jetpackmvvm.ext.parseState

/**
 *
 *
 *@author: YangJie
 *@email: 2295360491@qq.com
 *@time: 2021/9/25 下午 1:42
 *@descripton: MainFragment
 *
 *
 */
class SwitchDetailFragment : BaseFragment<MainViewModel, FragmentMainBinding>() {

    //设备适配器
    private val switchAdapter: SwitchAdapter by lazy { SwitchAdapter(arrayListOf()) }

    //界面状态管理者
    private lateinit var loadsir: LoadService<Any>

    @VMScope("device")
    lateinit var requestDeviceViewModel: RequestDeviceViewModel

    override fun layoutId() = R.layout.fragment_switch

    //当前场景设备
    var switchId = -1


    override fun initView(savedInstanceState: Bundle?) {

        injectFragmentVM()


        arguments?.getParcelable<DeviceBean>("switch")?.let {
            myToolBar.title = it.deviceName
            switchId = it.deviceId
        }

        myToolBar.setOnToolBarClickListener(object : MyToolBar.OnToolBarClick() {
            override fun onLeftClick() {
                nav().navigateUp()
            }
        })

        tvGoBack.setOnClickListener {
            nav().navigateUp()
        }

        //状态页配置
        loadsir = loadServiceInit(swipeRefresh) {
            //点击重试时触发
            loadsir.showLoading()
            requestDeviceViewModel.getSwitchDevice(switchId)
        }

        loadsir.showLoading()

        //初始化recyclerView
        recyclerView.init(GridLayoutManager(context, 3), switchAdapter)

        //初始化 SwipeRefreshLayout
        swipeRefresh.init {
            //触发刷新监听时请求数据
            requestDeviceViewModel.getSwitchDevice(switchId)
        }

        requestDeviceViewModel.getSwitchDevice(switchId)


        //开关的点击事件
//        switchAdapter.addChildClickViewIds(R.id.swStatus)
        switchAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.swStatus) {
                //打开场景
                switchAdapter.data[position].run {
                    requestDeviceViewModel.operateDevice(deviceId, deviceAttributeValue)
                }
            }
        }
    }

    override fun createObserver() {
        requestDeviceViewModel.switchState.observe(viewLifecycleOwner) { result ->
            loadListData(result, switchAdapter, loadsir, recyclerView, swipeRefresh)
            if (result.listData.size == 2) {
                recyclerView.layoutManager?.let {
                    it as GridLayoutManager
                    it.spanCount = 2
                }
            }
        }

        requestDeviceViewModel.switchDeviceResult.observe(viewLifecycleOwner) {
            parseState(it, {
                showMessage("操作成功")
                requestDeviceViewModel.getSwitchDevice(switchId)
            }, {
                showMessage(it.errorMsg)
            })
        }
    }

}