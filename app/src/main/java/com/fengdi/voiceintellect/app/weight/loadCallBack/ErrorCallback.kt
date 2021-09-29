package com.fengdi.voiceintellect.app.weight.loadCallBack

import com.fengdi.voiceintellect.R
import com.kingja.loadsir.callback.Callback


class ErrorCallback : Callback() {

    override fun onCreateView(): Int {
        return R.layout.layout_error
    }

}