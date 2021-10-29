package com.fengdi.voiceintellect.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.fengdi.voiceintellect.R
import com.fengdi.voiceintellect.app.annotation.VMScope
import com.fengdi.voiceintellect.app.base.BaseFragment
import com.fengdi.voiceintellect.app.ext.*
import com.fengdi.voiceintellect.app.weight.MyToolBar
import com.fengdi.voiceintellect.data.model.bean.DeviceBean
import com.fengdi.voiceintellect.data.model.bean.InfraredDeviceEntity
import com.fengdi.voiceintellect.data.model.bean.InfraredKeyBean
import com.fengdi.voiceintellect.databinding.FragmentMainBinding
import com.fengdi.voiceintellect.ui.adapter.InfraredDeviceAdapter
import com.fengdi.voiceintellect.viewmodel.MainViewModel
import com.fengdi.voiceintellect.viewmodel.request.RequestDeviceViewModel
import com.kingja.loadsir.core.LoadService
import kotlinx.android.synthetic.main.fragment_scene.*
import kotlinx.android.synthetic.main.fragment_scene.myToolBar
import kotlinx.android.synthetic.main.fragment_scene.recyclerView
import kotlinx.android.synthetic.main.fragment_scene.swipeRefresh
import kotlinx.android.synthetic.main.fragment_scene.tvGoBack
import kotlinx.android.synthetic.main.fragment_switch.*
import me.hgj.jetpackmvvm.ext.nav
import me.hgj.jetpackmvvm.ext.parseState

/**
 *
 *
 *@author: YangJie
 *@email: 2295360491@qq.com
 *@time: 2021/9/25 下午 1:42
 *@descripton: 终端详情
 *
 *
 */
class TerminalDetailFragment : BaseFragment<MainViewModel, FragmentMainBinding>() {

    //设备适配器
    private val infraredDeviceAdapter: InfraredDeviceAdapter by lazy { InfraredDeviceAdapter(arrayListOf()) }

    //界面状态管理者
    private lateinit var loadsir: LoadService<Any>

    @VMScope("device")
    lateinit var requestDeviceViewModel: RequestDeviceViewModel

    override fun layoutId() = R.layout.fragment_scene

    //当前场景设备
    var infraredDeviceId = -1


    override fun initView(savedInstanceState: Bundle?) {
        injectFragmentVM()

        arguments?.getParcelable<DeviceBean>("terminal")?.let {
            myToolBar.title = it.infraredDeviceName
            infraredDeviceId = it.infraredDeviceId
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
            requestDeviceViewModel.getInfraredDevice(infraredDeviceId)
        }

        //初始化recyclerView
        recyclerView.init(GridLayoutManager(context, 4), infraredDeviceAdapter)

        //初始化 SwipeRefreshLayout
        swipeRefresh.init {
            //触发刷新监听时请求数据
            requestDeviceViewModel.getInfraredDevice(infraredDeviceId)
        }

        loadsir.showLoading()
        requestDeviceViewModel.getInfraredDevice(infraredDeviceId)


        infraredDeviceAdapter.setOnItemClickListener { adapter, view, position ->
            //跳转到遥控器界面
            nav().navigate(R.id.action_terminalDetailFragment_to_remoteControlFragment,Bundle().apply {
                putSerializable("remoteControl", adapter.data[position] as InfraredDeviceEntity)
            })
        }
    }

    override fun createObserver() {
        requestDeviceViewModel.infraredDeviceState.observe(viewLifecycleOwner) { result ->
            loadListData(result, infraredDeviceAdapter, loadsir, recyclerView, swipeRefresh)
        }

        requestDeviceViewModel.clickKeyResult.observe(viewLifecycleOwner) {
            parseState(it, {
                showMessage("操作成功")
            }, {
                showMessage(it.errorMsg)
            })
        }

    }

}