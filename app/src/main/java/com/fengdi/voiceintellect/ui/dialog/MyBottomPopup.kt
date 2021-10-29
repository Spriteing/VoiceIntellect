package com.fengdi.voiceintellect.ui.dialog


import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.fengdi.voiceintellect.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.qmuiteam.qmui.layout.IQMUILayout
import com.qmuiteam.qmui.layout.QMUIPriorityLinearLayout
import com.qmuiteam.qmui.widget.dialog.QMUIBaseDialog
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheetBehavior
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheetRootLayout

open class MyBottomPopup constructor(private val mContext: Context, styleRes: Int = R.style.QMUI_Dialog) : QMUIBaseDialog(mContext, styleRes) {

    private var mRootView: QMUIBottomSheetRootLayout
    private val mBehavior: QMUIBottomSheetBehavior<QMUIBottomSheetRootLayout>
    private var mAnimateToCancel = false
    private var mAnimateToDismiss = false


    init {
        val container = layoutInflater.inflate(R.layout.qmui_bottom_sheet_dialog, null) as ViewGroup
        mRootView = container.findViewById(R.id.bottom_sheet)
        mBehavior = QMUIBottomSheetBehavior()
        mBehavior.isHideable = true

        mBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    if (mAnimateToCancel) {
                        // cancel() invoked
                        cancel()
                    } else if (mAnimateToDismiss) {
                        // dismiss() invoked but it it not triggered by cancel()
                        dismiss()
                    } else {
                        // drag to cancel
                        cancel()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        mBehavior.peekHeight = 0
        mBehavior.setAllowDrag(false)
        mBehavior.skipCollapsed = true
        val rootViewLp = mRootView.layoutParams as CoordinatorLayout.LayoutParams
        rootViewLp.behavior = mBehavior

        // We treat the CoordinatorLayout as outside the dialog though it is technically inside

        // We treat the CoordinatorLayout as outside the dialog though it is technically inside
        container.findViewById<View>(R.id.touch_outside)
                .setOnClickListener(
                        View.OnClickListener {
                            if (mBehavior.state == BottomSheetBehavior.STATE_SETTLING) {
                                return@OnClickListener
                            }
                            if (isShowing && shouldWindowCloseOnTouchOutside()) {
                                cancel()
                            }
                        })
//        mRootView?.setOnTouchListener { view, event -> // Consume the event and prevent it from falling through
//           return@setOnTouchListener true
//        }

        setContentView(container, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window
        if (window != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }


        mRootView.setRadius(context.resources.getDimensionPixelOffset(R.dimen.dp_10), IQMUILayout.HIDE_RADIUS_SIDE_BOTTOM)

        ViewCompat.requestApplyInsets(mRootView)
    }


    fun setRadius(radius: Int) {
        mRootView.setRadius(radius, IQMUILayout.HIDE_RADIUS_SIDE_BOTTOM)
    }


    override fun cancel() {
        if (mBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
            mAnimateToCancel = false
            super.cancel()
        } else {
            mAnimateToCancel = true
            mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN)
        }
    }

    override fun dismiss() {
        if (mBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
            mAnimateToDismiss = false
            super.dismiss()
        } else {
            mAnimateToDismiss = true
            mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN)
        }
    }

    override fun show() {
        super.show()

        if (mBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            mRootView.postOnAnimation { mBehavior.state = BottomSheetBehavior.STATE_EXPANDED }
        }
        mAnimateToCancel = false
        mAnimateToDismiss = false

    }


    fun addContentView(view: View, layoutParams: QMUIPriorityLinearLayout.LayoutParams?) {
        mRootView.addView(view, layoutParams)
    }

    fun addContentView(view: View) {
        val lp = QMUIPriorityLinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        lp.setPriority(QMUIPriorityLinearLayout.LayoutParams.PRIORITY_DISPOSABLE)

        mRootView.addView(view, lp)
    }


}



