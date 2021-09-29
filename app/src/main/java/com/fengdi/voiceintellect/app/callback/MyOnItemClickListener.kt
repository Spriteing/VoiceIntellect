package org.heiyiren.app.app.callback

import android.view.View

interface MyOnItemClickListener<T> {
    /**
     * item 被点击
     *
     * @param view     被点击的 [View]
     * @param data     数据
     * @param position 在 RecyclerView 中的位置
     */
    fun onItemClick(view: View,  data: T, position: Int)
}