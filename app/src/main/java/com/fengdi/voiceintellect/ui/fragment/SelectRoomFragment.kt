package com.fengdi.voiceintellect.ui.fragment

import android.os.Bundle
import android.view.View
import com.fengdi.voiceintellect.R
import com.fengdi.voiceintellect.app.annotation.VMScope
import com.fengdi.voiceintellect.app.base.BaseFragment
import com.fengdi.voiceintellect.app.ext.injectFragmentVM
import com.fengdi.voiceintellect.app.ext.showMessage
import com.fengdi.voiceintellect.app.utils.CacheUtil
import com.fengdi.voiceintellect.data.model.bean.RoomEntity
import com.fengdi.voiceintellect.databinding.FragmentSelectRoomBinding
import com.fengdi.voiceintellect.viewmodel.MainViewModel
import com.fengdi.voiceintellect.viewmodel.request.RequestDeviceViewModel
import kotlinx.android.synthetic.main.fragment_select_room.*
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
class SelectRoomFragment : BaseFragment<MainViewModel, FragmentSelectRoomBinding>() {


    @VMScope("device")
    lateinit var requestDeviceViewModel: RequestDeviceViewModel


    /**
     * 房间id
     */
    var roomEntiry: RoomEntity? = null

    val roomList = arrayListOf<RoomEntity>()


    override fun layoutId() = R.layout.fragment_select_room

    override fun initView(savedInstanceState: Bundle?) {
        injectFragmentVM()


        val rooms = arrayListOf<String>()

        arguments?.getInt("position")?.let {
            if (requestDeviceViewModel.gatewayList != null && requestDeviceViewModel.gatewayList[0].roomList != null) {
                roomList.addAll(requestDeviceViewModel.gatewayList[it].roomList)

                roomList.forEach { room ->
                    rooms.add(room.roomName)
                }
            }
        }

        if (roomList.isEmpty()) {
            return
        }


        roomEntiry = roomList[0]

        mySpinner.setItemsData(rooms)
        mySpinner.setOnItemClickListener(object : MyOnItemClickListener<String> {
            override fun onItemClick(view: View, data: String, position: Int) {
                roomEntiry = roomList[position]

            }
        })

        tvConfirm.setOnClickListener {
            if (roomEntiry == null) {
                showMessage("你还没有房间")
            } else {
                //保存选择的房间
                CacheUtil.setCurRoom(roomEntiry!!)
                requestDeviceViewModel.getDeivces()
                showMessage("选择成功")
                nav().navigateUp()
            }
        }

    }

    override fun createObserver() {

    }


}