package org.heiyiren.app.mvp.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import me.hgj.jetpackmvvm.ext.util.dp2px

/**
 * @author yangjie
 * 用于StatusBar的占位符
 */
class StatusBarPlaceHolder : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun onMeasure(widthMeasureSpec: Int, height: Int) {
        var heightMeasureSpec = QMUIStatusBarHelper.getStatusbarHeight(context)
        if (heightMeasureSpec==0){
            heightMeasureSpec=dp2px(33)
        }

//        Timber.v("heightMeasureSpec:%s", heightMeasureSpec)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec) //如果是继承的viewgroup比如linearlayout时，可以先计算

        var widthResult = 0
        //view根据xml中layout_width和layout_height测量出对应的宽度和高度值，
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        when (widthSpecMode) {
            MeasureSpec.UNSPECIFIED -> widthResult = widthSpecSize
            MeasureSpec.AT_MOST -> widthResult = 10
            MeasureSpec.EXACTLY ->                 //当xml布局中是准确的值，比如200dp是，判断一下当前view的宽度和准确值,取两个中大的，这样的好处是当view的宽度本事超过准确值不会出界
                //其实可以直接使用准确值
                widthResult = Math.max(10, widthSpecSize)
            else -> {
            }
        }
        var heightResult = 0
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        when (heightSpecMode) {
            MeasureSpec.UNSPECIFIED -> heightResult = heightSpecSize
            MeasureSpec.AT_MOST -> heightResult = heightMeasureSpec
            MeasureSpec.EXACTLY -> heightResult = Math.max(heightMeasureSpec, heightSpecSize)
            else -> {
            }
        }
        setMeasuredDimension(widthResult, heightResult)
    }

}