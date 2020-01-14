package com.coin.market.activity.mine.googleqr;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.databinding.ActivityGoogleqrConfirmLayoutBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;

/**
 * @author: Lenovo
 * @date: 2019/8/13
 * @info: 绑定谷歌验证器 确认
 */
public class GoogleQrConfirmActivity extends BaseActivity implements View.OnClickListener {

    private ActivityGoogleqrConfirmLayoutBinding binding;
    private GoogleQrConfirmViewModel model;
    private String secret;

    public static void JUMP(Context context, String secret) {
        context.startActivity(new Intent(context, GoogleQrConfirmActivity.class).putExtra("secret", secret));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        EventBus.getDefault().register(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_googleqr_confirm_layout);
        model = new GoogleQrConfirmViewModel(this, binding);
        if (!TextUtils.isEmpty(getIntent().getStringExtra("secret"))){
            secret = getIntent().getStringExtra("secret");
        }
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.googleTimerButton.setOnClickListener(this);
        binding.googleSaveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_Return:
                finish();
                break;
            case R.id.google_timer_button:
                if (!TextUtils.isEmpty(FaceApplication.getMobile())){
                    model.sendCode(FaceApplication.getToken(), FaceApplication.getMobile());
                }
                break;
            case R.id.google_save_button:
                if (TextUtils.isEmpty(binding.bindGooglePhoneEdit.getText().toString())){
                    Toast.makeText(mContext, "请输入手机验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(binding.bindGoogleVerifyEdit.getText().toString())){
                    Toast.makeText(mContext, "请输入谷歌验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                model.createGoogleCaptcha(FaceApplication.getToken(), secret, binding.bindGooglePhoneEdit.getText().toString(), binding.bindGoogleVerifyEdit.getText().toString());
                break;
            default:
                break;
        }
    }

    /**
     *   谷歌验证器绑定成功 刷新安全中心数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bindGoogle(String model) {
        try {
            if (null != model && model.equals("BindGoogle")) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
