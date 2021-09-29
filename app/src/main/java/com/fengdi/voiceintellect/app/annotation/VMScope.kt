package com.fengdi.voiceintellect.app.annotation


/**
 * @package    com.cj.customwidget.page.viewmodel
 * @author     luan
 * @date       2020/11/2
 * @des         用于标记vm的作用域
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class VMScope(val scopeName: String) {
}