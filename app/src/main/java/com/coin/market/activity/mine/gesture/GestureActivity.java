package com.coin.market.activity.mine.gesture;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.coin.market.R;
import com.coin.market.databinding.ActivityGestureLayoutBinding;

import teng.wang.comment.SPConstants;
import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.utils.DataKeeper;

/**
 * @author: Lenovo
 * @date: 2019/8/6
 * @info: 手势解锁
 */
public class GestureActivity extends BaseActivity implements View.OnClickListener {

    private ActivityGestureLayoutBinding binding;
    private GestureViewModel model;
    public String verify; // 是否是验证 如果传过来是verify 说明是验证

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, GestureActivity.class));
    }

    public static void JUMP(Context context, String verify) {
        context.startActivity(new Intent(context, GestureActivity.class).putExtra("verify", verify));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gesture_layout);
        model = new GestureViewModel(this, binding);
    }

    @Override
    protected void initIntentData() {
        verify = getIntent().getStringExtra("verify");
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_Return:
                DataKeeper.remove(this, SPConstants.MBR_GESTURRE);
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 监听手机返回按钮
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!TextUtils.isEmpty(verify)){

            }else {
                finish();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
