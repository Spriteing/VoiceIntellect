package com.fengdi.voiceintellect.ui.fragment

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.fengdi.voiceintellect.R
import com.fengdi.voiceintellect.app.annotation.VMScope
import com.fengdi.voiceintellect.app.base.BaseFragment
import com.fengdi.voiceintellect.app.ext.*
import com.fengdi.voiceintellect.app.utils.CacheUtil
import com.fengdi.voiceintellect.data.model.bean.RoomGatewayResult
import com.fengdi.voiceintellect.databinding.FragmentMainBinding
import com.fengdi.voiceintellect.ui.adapter.DeviceAdapter
import com.fengdi.voiceintellect.viewmodel.MainViewModel
import com.fengdi.voiceintellect.viewmodel.request.RequestDeviceViewModel
import com.kingja.loadsir.core.LoadService
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.include_recyclerview.*
import me.hgj.jetpackmvvm.ext.nav

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
class MainFragment : BaseFragment<MainViewModel, FragmentMainBinding>() {

    //设备适配器
    private val deviceAdapter: DeviceAdapter by lazy { DeviceAdapter(arrayListOf()) }

    //界面状态管理者
    private lateinit var loadsir: LoadService<Any>

    @VMScope("device")
    lateinit var requestDeviceViewModel: RequestDeviceViewModel

    override fun layoutId() = R.layout.fragment_main

    override fun initView(savedInstanceState: Bundle?) {
        injectFragmentVM()

        //状态页配置
        loadsir = loadServiceInit(swipeRefresh) {
            //点击重试时触发
            loadsir.showLoading()
            requestDeviceViewModel.getDeivces()
            requestDeviceViewModel.getRooms()
        }

        //初始化recyclerView
        recyclerView.init(LinearLayoutManager(context), deviceAdapter)

        //初始化 SwipeRefreshLayout
        swipeRefresh.init {
            //触发刷新监听时请求数据
            requestDeviceViewModel.getDeivces()
            requestDeviceViewModel.getRooms()
        }

        loadsir.showLoading()
        requestDeviceViewModel.getDeivces()
        requestDeviceViewModel.getRooms()


        //初始化Databind
        mDatabind.click = ProxyClick()

        deviceAdapter.setOnItemClickListener { adapter, view, position ->
            deviceAdapter.data[position].let {
                when{
                    //场景开关
                    it.mac.startsWith("YINKE:SCENESWITCH")->{
                        nav().navigate(R.id.action_mainFragment_to_sceneFragment,Bundle().apply {
                            putParcelable("scene",it)
                        })
                    }
                }
            }
        }

    }

    override fun createObserver() {
        requestDeviceViewModel.devicesState.observe(viewLifecycleOwner) { result ->
            loadListData(result, deviceAdapter, loadsir, recyclerView, swipeRefresh)
            CacheUtil.getCurRoom()?.let {
                tvName.text = it.roomName
            }
        }
    }

    inner class ProxyClick {
        /**
         * 选择网关
         */
        fun selectRoom() {
            requestDeviceViewModel.gatewayList.let {
                val gateways = arrayListOf<String>()

                val gatewayList = arrayListOf<RoomGatewayResult>()

                //按照网关id，添加到对应网关
                it.forEach { device ->
                    gateways.add(device.zigbeeGateway.gatewayName)
                    gatewayList.add(device)
                }

                AlertDialog.Builder(requireContext())
                    .setTitle("选择网关")
                    .setSingleChoiceItems(
                        gateways.toTypedArray(),
                        -1
                    ) { dialog: DialogInterface, which: Int ->

//                        launchActivity(
//                            Intent(this, AddInfraRedSendActivity::class.java)
//                                .putExtra("rooms", gatewayList[which])
//                        )
//
                        dialog.dismiss()
                        nav().navigate(R.id.action_mainFragment_to_selectRoomFragment, Bundle().apply {
                            putInt("position", which)
                        })
                    }
                    .show()
            }

        }

    }


}