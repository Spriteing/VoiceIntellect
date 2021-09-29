package com.fengdi.voiceintellect.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.fengdi.voiceintellect.R
import com.fengdi.voiceintellect.app.annotation.VMScope
import com.fengdi.voiceintellect.app.base.BaseFragment
import com.fengdi.voiceintellect.app.ext.*
import com.fengdi.voiceintellect.data.model.bean.DeviceBean
import com.fengdi.voiceintellect.databinding.FragmentMainBinding
import com.fengdi.voiceintellect.ui.adapter.SceneAdapter
import com.fengdi.voiceintellect.viewmodel.MainViewModel
import com.fengdi.voiceintellect.viewmodel.request.RequestDeviceViewModel
import com.kingja.loadsir.core.LoadService
import kotlinx.android.synthetic.main.fragment_scene.*
import kotlinx.android.synthetic.main.include_recyclerview.*
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
class SceneDetailFragment : BaseFragment<MainViewModel, FragmentMainBinding>() {

    //设备适配器
    private val sceneAdapter: SceneAdapter by lazy { SceneAdapter(arrayListOf()) }

    //界面状态管理者
    private lateinit var loadsir: LoadService<Any>

    @VMScope("device")
    lateinit var requestDeviceViewModel: RequestDeviceViewModel

    override fun layoutId() = R.layout.fragment_scene

    //当前场景设备
    var sceneDeviceId = -1


    override fun initView(savedInstanceState: Bundle?) {
        injectFragmentVM()

        arguments?.getParcelable<DeviceBean>("scene")?.let {
            myToolBar.title = it.sceneSwitchName
            sceneDeviceId = it.sceneSwitchId
        }

        //状态页配置
        loadsir = loadServiceInit(swipeRefresh) {
            //点击重试时触发
            loadsir.showLoading()
            requestDeviceViewModel.getSceneSwitchs(sceneDeviceId)
        }

        //初始化recyclerView
        recyclerView.init(LinearLayoutManager(context), sceneAdapter)

        //初始化 SwipeRefreshLayout
        swipeRefresh.init {
            //触发刷新监听时请求数据
            requestDeviceViewModel.getSceneSwitchs(sceneDeviceId)
        }

        loadsir.showLoading()
        requestDeviceViewModel.getSceneSwitchs(sceneDeviceId)


        sceneAdapter.setOnItemClickListener { adapter, view, position ->
            //打开场景
            requestDeviceViewModel.openScene(sceneAdapter.data[position].sceneSwitchAttributeId)
        }
    }

    override fun createObserver() {
        requestDeviceViewModel.scenesState.observe(viewLifecycleOwner) { result ->
            loadListData(result, sceneAdapter, loadsir, recyclerView, swipeRefresh)
        }

        requestDeviceViewModel.sceneSwitchResult.observe(viewLifecycleOwner) {
            parseState(it, {
                showMessage("操作成功")
                requestDeviceViewModel.getSceneSwitchs(sceneDeviceId)
            }, {
                showMessage(it.errorMsg)
            })
        }
    }

}