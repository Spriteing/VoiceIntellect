package com.fengdi.voiceintellect.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.blankj.utilcode.util.ToastUtils
import com.fengdi.voiceintellect.app.utils.CacheUtil
import com.fengdi.voiceintellect.R
import com.fengdi.voiceintellect.app.base.BaseActivity
import com.fengdi.voiceintellect.app.ext.showMessage
import com.fengdi.voiceintellect.app.utils.CountDownTimerUtils
import com.fengdi.voiceintellect.databinding.ActivityLoginBinding
import com.fengdi.voiceintellect.viewmodel.LoginViewModel
import com.fengdi.voiceintellect.viewmodel.request.RequestLoginViewModel
import kotlinx.android.synthetic.main.activity_login.*
import me.hgj.jetpackmvvm.ext.parseState

class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {

    val requestLoginViewModel: RequestLoginViewModel by viewModels()


    override fun layoutId() = R.layout.activity_login


    override fun initView(savedInstanceState: Bundle?) {

        requestLoginViewModel.verifyResult.observe(this) { result ->
            parseState(result, {
                //设置验证码倒计时
                CountDownTimerUtils(tvSendNumber, 60000, 1000).start()
                showMessage("发送成功")
            }, {
                showMessage(it.errorMsg)
            })
        }

        requestLoginViewModel.loginResult.observe(this) { result ->
            parseState(result, {
                showMessage("登录成功")
                CacheUtil.setIsLogin(true)
                CacheUtil.setUser(it[0])

                startActivity(Intent(this, MainActivity::class.java))

            }, {
                showMessage(it.errorMsg)
            })
        }

        mDatabind.click = PorxyClick()
        mDatabind.viewModel = mViewModel
    }


    inner class PorxyClick {
        /**
         * 登录
         */
        fun login() {
            when {
                mViewModel.userName.get().isEmpty() -> ToastUtils.showShort("请输入手机号")
                mViewModel.verifyCode.get().isEmpty() -> ToastUtils.showShort("请输入验证码")
                else -> requestLoginViewModel.loginReq(mViewModel.userName.get(), mViewModel.verifyCode.get())
            }

        }

        //清除用户名
        fun clearUserName() {
            mViewModel.userName.set("")
        }


        /**
         * 获取验证码
         */
        fun getVerifyCode() {
            when {
                mViewModel.userName.get().isEmpty() -> ToastUtils.showShort("请输入手机号")
                else -> {
                    requestLoginViewModel.verifyCodeReq(mViewModel.userName.get())
                }
            }

        }
    }
}