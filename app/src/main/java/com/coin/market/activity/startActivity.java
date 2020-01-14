package com.coin.market.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.coin.market.R;
import com.coin.market.activity.main.MainActivity;
import com.coin.market.activity.mine.settings.SettingsActivity;
import com.coin.market.databinding.ActivityStartLayoutBinding;

import java.util.Locale;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.utils.StatusBarUtil;

public class startActivity extends BaseActivity {

    private Context context;
    private ActivityStartLayoutBinding binding;

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTransparentForImageViewInFragment(this, null);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        context = this;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_start_layout);
        setSteepStatusBar(true);
        AlphaAnimation anima = new AlphaAnimation(0.3f, 1.0f);
        anima.setDuration(3000);// 设置动画显示时间
        binding.startImg.startAnimation(anima);
        anima.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if ("en-US".equals(FaceApplication.getLanguage())){
                    setLanguage(1);
                }else if ("ko-KR".equals(FaceApplication.getLanguage())){
                    setLanguage(2);
                }else if ("ja-JP".equals(FaceApplication.getLanguage())){
                    setLanguage(3);
                }else if ("zh-CN".equals(FaceApplication.getLanguage())){
                    setLanguage(0);
                }
//                MainActivity.JUMP(context);
//                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }



    /**
     * 切换语言
     * @param type
     */
    private void setLanguage(int type){
        switch (type) {
            case 0:
                Locale.setDefault(Locale.CHINA);
                Configuration config = getBaseContext().getResources().getConfiguration();
                config.locale = Locale.CHINA;
                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                Intent intent = new Intent(startActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            case 1:
                Locale.setDefault(Locale.ENGLISH);
                Configuration config1 = getBaseContext().getResources().getConfiguration();
                config1.locale = Locale.ENGLISH;
                getBaseContext().getResources().updateConfiguration(config1, getBaseContext().getResources().getDisplayMetrics());
                Intent intent1 = new Intent(startActivity.this, MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
                finish();
                break;
            case 2:
                Locale.setDefault(Locale.KOREA);
                Configuration config2 = getBaseContext().getResources().getConfiguration();
                config2.locale = Locale.KOREA;
                getBaseContext().getResources().updateConfiguration(config2, getBaseContext().getResources().getDisplayMetrics());
                Intent intent2 = new Intent(startActivity.this, MainActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent2);
                finish();
                break;
            case 3:
                Locale.setDefault(Locale.JAPAN);
                Configuration config3 = getBaseContext().getResources().getConfiguration();
                config3.locale = Locale.JAPAN;
                getBaseContext().getResources().updateConfiguration(config3, getBaseContext().getResources().getDisplayMetrics());
                Intent intent3 = new Intent(startActivity.this, MainActivity.class);
                intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent3);
                finish();
                break;

        }
    }

    private void setStatuBar() {
        Window win = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//透明状态栏
            // 状态栏字体设置为深色，SYSTEM_UI_FLAG_LIGHT_STATUS_BAR 为SDK23增加
            win.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // 部分机型的statusbar会有半透明的黑色背景
            win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            win.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            win.setStatusBarColor(Color.TRANSPARENT);// SDK21
        }
    }

}
