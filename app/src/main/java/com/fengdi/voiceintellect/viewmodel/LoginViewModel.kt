package com.fengdi.voiceintellect.viewmodel

import android.view.View
import androidx.databinding.ObservableInt
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.databind.StringObservableField

class LoginViewModel : BaseViewModel() {

    //用户名
    var userName = StringObservableField()


    //验证码
    var verifyCode = StringObservableField()


    //用户名清除按钮是否显示  不要在 xml 中写逻辑 所以逻辑判断放在这里
    var clearVisible = object : ObservableInt(userName) {
        override fun get(): Int {
            return if (userName.get().isEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
    }


}