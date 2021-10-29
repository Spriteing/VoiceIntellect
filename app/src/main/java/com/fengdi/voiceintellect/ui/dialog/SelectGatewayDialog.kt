package com.fengdi.voiceintellect.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.fengdi.voiceintellect.R
import com.fengdi.voiceintellect.data.model.bean.RoomGatewayResult
import com.fengdi.voiceintellect.ui.adapter.GatewayAdapter
import com.yanzhenjie.recyclerview.OnItemClickListener
import kotlinx.android.synthetic.main.dialog_hint.view.*
import kotlinx.android.synthetic.main.dialog_select_gateway.view.*
import org.heiyiren.app.app.callback.MyOnItemClickListener

class SelectGatewayDialog(context: Context, val list: ArrayList<RoomGatewayResult>) : MyBaseDialog(context) {

    var onItemClickListener: MyOnItemClickListener<RoomGatewayResult>? = null


    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_select_gateway, null)
        view.rcyGateway?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = GatewayAdapter(list).apply {
                setOnItemClickListener { _, view, position ->
                    onItemClickListener?.onItemClick(view, data[position], position)
                }
            }
        }

        addContentView(view)
    }


}