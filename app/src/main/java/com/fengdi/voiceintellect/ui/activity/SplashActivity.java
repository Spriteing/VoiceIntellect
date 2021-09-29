package com.fengdi.voiceintellect.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.fengdi.voiceintellect.app.utils.CacheUtil;
import com.fengdi.voiceintellect.R;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;


public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        //设置透明状态栏
        QMUIStatusBarHelper.translucent(this);

        boolean mIsNotFirst = CacheUtil.INSTANCE.isFirst();
//        if (mIsNotFirst) {//首次
//            BGALocalImageSize localImageSize = new BGALocalImageSize(1080, 1920, 320, 640);
//            // 设置数据源
//            bannerGuide.setData(localImageSize, ImageView.ScaleType.FIT_XY,
//                    R.mipmap.gui1,
//                    R.mipmap.gui2,
//                    R.mipmap.gui3,
//                    R.mipmap.gui4,
//                    R.mipmap.gui5,
//                    R.mipmap.gui6
//            );
//        } else {//非首次
        new Handler().postDelayed(() -> {
            if (CacheUtil.INSTANCE.isLogin() && CacheUtil.INSTANCE.getUser() != null) {
                startActivity(new Intent(this, MainActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, 2000);

//        }

//        initEvent();
    }


}