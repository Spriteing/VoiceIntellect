package com.fengdi.voiceintellect.app.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.fengdi.voiceintellect.R;
import com.qmuiteam.qmui.util.QMUIDeviceHelper;

import org.heiyiren.app.mvp.ui.widget.StatusBarPlaceHolder;


/**
 * Author : huiGer
 * Time   : 2018/8/10 0010 下午 12:03.
 * Desc   : 标题栏
 */
public class MyToolBar extends Toolbar implements View.OnClickListener {

    private OnToolBarClick clickListener;
    private ImageView leftIcon, rightIcon;
    private TextView tvTitle;
    private StatusBarPlaceHolder statusBar;
    private TextView tvRight;
    private ConstraintLayout linTitleBar;

    public MyToolBar(Context context) {
        this(context, null);
    }

    public MyToolBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyToolBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.toolbar_layout, this);

        leftIcon = findViewById(R.id.toolBar_icon);
        leftIcon.setOnClickListener(this);
        tvRight = findViewById(R.id.tv_right);
        tvTitle = findViewById(R.id.toolBar_title);
        linTitleBar = findViewById(R.id.layout_content);
        statusBar = findViewById(R.id.statusBar);
        findViewById(R.id.right_layout).setOnClickListener(this);
        rightIcon = findViewById(R.id.right_icon);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyToolBar);
        setTitle(typedArray.getString(R.styleable.MyToolBar_title));
        tvTitle.setTextColor(typedArray.getColor(R.styleable.MyToolBar_titleTextColor, getResources().getColor(R.color.textColorLight)));
        setRightContent(typedArray.getString(R.styleable.MyToolBar_rightContent));
        float textSize = typedArray.getDimension(R.styleable.MyToolBar_rightTextSize,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, context.getResources().getDisplayMetrics()));
        tvRight.getPaint().setTextSize(textSize);
        tvRight.setTextColor(typedArray.getColor(R.styleable.MyToolBar_rightTextColor, getResources().getColor(R.color.textColorLight)));

        Drawable bgDrawable = typedArray.getDrawable(R.styleable.MyToolBar_android_background);
        if (bgDrawable != null) {
            linTitleBar.setBackground(bgDrawable);
        }
        //是否需要考虑状态栏
        boolean isNeedStatusbarPadding = typedArray.getBoolean(R.styleable.MyToolBar_isNeedStatusbarPadding, true);

        if (isNeedStatusbarPadding && supportTranslucent()) {
            statusBar.setVisibility(View.VISIBLE);
        }


        setLeftIcon(typedArray.getResourceId(R.styleable.MyToolBar_leftIcon, R.drawable.ic_goback));
        setLeftIconVisible((typedArray.getInt(R.styleable.MyToolBar_leftIconVisible, 1) == 1) ? VISIBLE : INVISIBLE);
        int rightIconResourceId = typedArray.getResourceId(R.styleable.MyToolBar_rightIcon, -1);
        if (rightIconResourceId == -1) {
            rightIcon.setVisibility(GONE);
        } else {
            rightIcon.setVisibility(VISIBLE);
            rightIcon.setImageResource(rightIconResourceId);
        }

        setStatusBarVisible(typedArray.getInt(R.styleable.MyToolBar_statusBarVisible, 1) == 1 ? VISIBLE : GONE);

        typedArray.recycle();

        setOnToolBarClickListener(new OnToolBarClick());
    }

    private static boolean supportTranslucent() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                // Essential Phone 在 Android 8 之前沉浸式做得不全，系统不从状态栏顶部开始布局却会下发 WindowInsets
                && !(QMUIDeviceHelper.isEssentialPhone() && Build.VERSION.SDK_INT < 26);
    }


    /**
     * 设置左边按钮图标
     *
     * @param idRes
     */
    public void setLeftIcon(@DrawableRes int idRes) {
        leftIcon.setImageResource(idRes);
        setLeftIconVisible(VISIBLE);
    }

    /**
     * 左边按钮显示状态
     *
     * @param visibility
     */
    public void setLeftIconVisible(int visibility) {
        leftIcon.setVisibility(visibility);
    }

    /**
     * 设置标题
     *
     * @param str
     */
    @Override
    public void setTitle(CharSequence str) {
        if (tvTitle == null) {
            return;
        }
        tvTitle.setText(str);
        tvTitle.setVisibility(VISIBLE);
    }

    /**
     * 设置右边文字
     *
     * @param str
     */
    public void setRightContent(CharSequence str) {
        if (tvRight == null || TextUtils.isEmpty(str)) {
            return;
        }
        tvRight.setText(str);
        tvRight.setVisibility(VISIBLE);
    }

    public void setRightIcon(@DrawableRes int idRes) {
        this.rightIcon.setImageResource(idRes);
        this.rightIcon.setVisibility(VISIBLE);
    }

    /**
     * 设置右边文字显示状态
     *
     * @param visible
     */
    public void setRightTextVisible(int visible) {
        tvRight.setVisibility(visible);

    }

    public void setOnToolBarClickListener(OnToolBarClick clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolBar_icon:
                if (clickListener != null) {
                    clickListener.onLeftClick();
                }
                break;
            case R.id.right_layout:
                if (clickListener != null) {
                    clickListener.onRightClick();
                }
                break;
            default:
        }
    }

    /**
     * 显示状态
     *
     * @param visible
     */
    public void setStatusBarVisible(int visible) {
        findViewById(R.id.statusBar).setVisibility(visible);
    }

    public static class OnToolBarClick {
        public void onLeftClick() {

        }

        public void onRightClick() {
        }
    }

}
