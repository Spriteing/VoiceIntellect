package com.fengdi.voiceintellect.app.utils

import android.os.CountDownTimer
import android.widget.TextView
import com.fengdi.voiceintellect.R

class CountDownTimerUtils
/**
 * @param textView          The TextView
 * @param millisInFuture     millisInFuture  从开始调用start()到倒计时完成
 * 并onFinish()方法被调用的毫秒数。（译者注：倒计时时间，单位毫秒）
 * @param countDownInterval 接收onTick(long)回调的间隔时间。（译者注：单位毫秒）
 */(//显示倒计时的文字
        private val mTextView: TextView, millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {
    override fun onTick(millisUntilFinished: Long) {
        //设置不可点击
        mTextView.isClickable = false
        //设置倒计时时间
        mTextView.text = mTextView.resources.getString(R.string.str_resend_sms_s, (millisUntilFinished / 1000).toString())
        mTextView.setTextColor(mTextView.resources.getColor(R.color.c_666666))
    }

    override fun onFinish() {
        mTextView.text = "重新发送"
        mTextView.isClickable = true //重新获得点击
        mTextView.setTextColor(mTextView.resources.getColor(R.color.white))
    }

}