package com.fengdi.voiceintellect.ui.fragment

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.fengdi.voiceintellect.R
import com.fengdi.voiceintellect.app.annotation.VMScope
import com.fengdi.voiceintellect.app.base.BaseFragment
import com.fengdi.voiceintellect.app.ext.*
import com.fengdi.voiceintellect.app.utils.CacheUtil
import com.fengdi.voiceintellect.data.model.bean.RoomGatewayResult
import com.fengdi.voiceintellect.databinding.FragmentMainBinding
import com.fengdi.voiceintellect.ui.activity.LoginActivity
import com.fengdi.voiceintellect.ui.adapter.DeviceAdapter
import com.fengdi.voiceintellect.ui.dialog.DeleteOrModifyPopup
import com.fengdi.voiceintellect.ui.dialog.HintDialog
import com.fengdi.voiceintellect.ui.dialog.SelectGatewayDialog
import com.fengdi.voiceintellect.viewmodel.MainViewModel
import com.fengdi.voiceintellect.viewmodel.request.RequestDeviceViewModel
import com.kingja.loadsir.core.LoadService
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.include_recyclerview.*
import kotlinx.android.synthetic.main.popup_setting.view.*
import me.hgj.jetpackmvvm.ext.nav
import org.heiyiren.app.app.callback.MyOnItemClickListener

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
        recyclerView.init(GridLayoutManager(context, 3), deviceAdapter)

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
                if (it.mac.isNullOrEmpty()) {
                    return@setOnItemClickListener
                }
                //场景开关
                when (it.deviceType) {
                    "SCENESWITCH" -> {
                        nav().navigate(R.id.action_mainFragment_to_sceneFragment, Bundle().apply {
                            putParcelable("scene", it)
                        })
                    }
                    //开关详情
                    "SWITCH" -> {
                        nav().navigate(R.id.action_mainFragment_to_switchDetailFragment, Bundle().apply {
                            putParcelable("switch", it)
                        })
                    }
                    //红外
                    "INFRARED" -> {
                        nav().navigate(R.id.action_mainFragment_to_terminalDetailFragment, Bundle().apply {
                            putParcelable("terminal", it)
                        })
                    }
                    //插座
                    "SOCKET" -> {
                        nav().navigate(R.id.action_mainFragment_to_socketDetailFragment, Bundle().apply {
                            putParcelable("socket", it)
                        })
                    }
                    //窗帘
                    "CURTAIN" -> {
                        nav().navigate(R.id.action_mainFragment_to_curtainsDetailFragment, Bundle().apply {
                            putParcelable("curtain", it)
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
         *
         * 设置
         */
        fun setting() {

            val popupView = layoutInflater.inflate(R.layout.popup_setting, null)

            val popupWindow = PopupWindow(
                popupView, resources.getDimensionPixelOffset(R.dimen.dp_200),
                LinearLayout.LayoutParams.WRAP_CONTENT, true
            )


            // 设置点击返回键使其消失，且不影响背景，此时setOutsideTouchable函数即使设置为false
            // 点击PopupWindow 外的屏幕，PopupWindow依然会消失；相反，如果不设置BackgroundDrawable
            // 则点击返回键PopupWindow不会消失，同时，即时setOutsideTouchable设置为true
            // 点击PopupWindow 外的屏幕，PopupWindow依然不会消失
            popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // 设置是否允许在外点击使其消失，到底有用没？
            popupWindow.isOutsideTouchable = true


            popupWindow.showAsDropDown(tvSetting, 0, -resources.getDimensionPixelOffset(R.dimen.dp_10))

            popupView?.apply {
                tvSelectRoom.setOnClickListener {
                    //选择房间
                    popupWindow.dismiss()
                    selectRoom()
                }
                tvLogout.setOnClickListener {
                    //退出登录
                    HintDialog(context, "确定要退出登录？").let {
                        it.onOkClickListener = View.OnClickListener { view ->
                            popupWindow.dismiss()
                            it.dismiss()
                            startActivity(Intent(requireContext(), LoginActivity::class.java))
                            requireActivity().finish()

                            CacheUtil.setIsLogin(false)
                            CacheUtil.setUser(null)

                        }
                        it.show()
                    }
                }
            }


//            QMUIBottomSheet(requireContext()).run {
//                setRadius(resources.getDimensionPixelOffset(R.dimen.dp_10))
//                val dialogView = layoutInflater.inflate(R.layout.popup_setting, null)
//                dialogView.tvSelectRoom.setOnClickListener { view ->
//                    //选择房间
//
//
//                }
//
//                dialogView.tvLogout.setOnClickListener {
//                    //退出登录
//                    HintDialog(context, "确定要退出登录？").let {
//                        it.onOkClickListener = View.OnClickListener { view ->
//                            dismiss()
//                            it.dismiss()
//                            startActivity(Intent(requireContext(), LoginActivity::class.java))
//                            requireActivity().finish()
//                        }
//                        it.show()
//                    }
//                }
//
//                addContentView(dialogView)
//
//                show()
//            }


        }

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
                SelectGatewayDialog(requireContext(), gatewayList).apply {
                    onItemClickListener = object : MyOnItemClickListener<RoomGatewayResult> {
                        override fun onItemClick(view: View, data: RoomGatewayResult, position: Int) {

                            dismiss()
                            nav().navigate(R.id.action_mainFragment_to_selectRoomFragment, Bundle().apply {
                                putInt("position", position)
                            })
                        }
                    }
                    show()
                }
            }

        }

    }


}