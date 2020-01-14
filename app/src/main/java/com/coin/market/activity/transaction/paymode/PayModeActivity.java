package com.coin.market.activity.transaction.paymode;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.coin.market.R;
import com.coin.market.databinding.ActivityPayModeLayoutBinding;
import com.coin.market.activity.transaction.addpaymode.AddPayModeActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;

/**
 * @author: Lenovo
 * @date: 2019/8/2
 * @info: 我的收款方式
 */
public class PayModeActivity extends BaseActivity implements View.OnClickListener {

    private ActivityPayModeLayoutBinding binding;
    private PayModeViewModel model;

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, PayModeActivity.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        EventBus.getDefault().register(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pay_mode_layout);
        model = new PayModeViewModel(this, binding);
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.titleBar.mImgScan.setOnClickListener(this);
        binding.payModeAddButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_Return:
                finish();
                break;
            case R.id.pay_mode_add_button: // 添加支付方式
                AddPayModeActivity.JUMP(this);
                break;
            default:
                break;
        }
    }

    /**
     *  添加成功刷新数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addPay(String str) {
        try {
            if (null != str && str.equals("finishAddPay")) {
                model.getPayMentMethod(FaceApplication.getToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
