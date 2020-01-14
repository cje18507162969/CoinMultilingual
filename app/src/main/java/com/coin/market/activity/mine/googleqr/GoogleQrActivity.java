package com.coin.market.activity.mine.googleqr;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.coin.market.R;
import com.coin.market.databinding.ActivityGoogleLayoutBinding;
import com.coin.market.util.androidUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import teng.wang.comment.base.BaseActivity;

/**
 * @author: Lenovo
 * @date: 2019/8/13
 * @info: 谷歌身份验证器
 */
public class GoogleQrActivity extends BaseActivity implements View.OnClickListener {

    private ActivityGoogleLayoutBinding binding;
    private GoogleQrViewModel model;


    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, GoogleQrActivity.class));
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_google_layout);
        model = new GoogleQrViewModel(this, binding);
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.googleQrCopy.setOnClickListener(this);
        binding.bindPhoneEditButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_Return:
                finish();
                break;
            case R.id.google_qr_copy:
                androidUtils.getCopyText(this, binding.googleSecretKey.getText().toString().trim());
                break;
            case R.id.bind_phone_edit_button:
                GoogleQrConfirmActivity.JUMP(this, binding.googleSecretKey.getText().toString().trim());
                break;
            default:
                break;
        }
    }

    /**
     *  绑定邮箱成功 刷新安全中心数据
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
