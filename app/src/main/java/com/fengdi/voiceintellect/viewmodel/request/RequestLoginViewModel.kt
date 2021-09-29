package com.fengdi.voiceintellect.viewmodel.request

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fengdi.voiceintellect.app.network.apiService
import com.fengdi.voiceintellect.data.model.bean.UserInfo
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.state.ResultState

class RequestLoginViewModel : BaseViewModel() {

    //login的返回接口
    var loginResult = MutableLiveData<ResultState<List<UserInfo>>>()

    //验证码的请求结果
    val verifyResult = MutableLiveData<ResultState<Any>>()

    /**
     * 登录到服务器
     */
    fun loginReq(mobile: String, code: String) {
        request(
            { apiService.login(mobile,code) },
            loginResult,
            true,
            "正在登录..."
        )
    }

    /**
     * 获取验证码
     */
    fun verifyCodeReq(mobile: String) {
        request(
            { apiService.getVerifyCode(mobile) },
            verifyResult,
            true,
            "正在获取..."
        )
    }

}