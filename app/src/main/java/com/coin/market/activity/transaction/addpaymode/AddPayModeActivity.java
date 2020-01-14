package com.coin.market.activity.transaction.addpaymode;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.coin.market.R;
import com.coin.market.databinding.ActivityAddPayModeLayoutBinding;
import com.coin.market.activity.transaction.paysave.PayModeSaveActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import teng.wang.comment.base.BaseActivity;

/**
 * @author: Lenovo
 * @date: 2019/8/2
 * @info: 添加支付方式
 */
public class AddPayModeActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAddPayModeLayoutBinding binding;
    private AddPayModeViewModel model;

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, AddPayModeActivity.class));
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_pay_mode_layout);
        model = new AddPayModeViewModel(this, binding);
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.addPayModeAlipay.setOnClickListener(this);
        binding.addPayModeWeix.setOnClickListener(this);
        binding.addPayModeBank.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_Return:
                finish();
                break;
            case R.id.add_pay_mode_bank: // 银行卡
                PayModeSaveActivity.JUMP(this, "unionpay");
                break;
            case R.id.add_pay_mode_alipay: // 支付宝
                PayModeSaveActivity.JUMP(this, "alipay");
                break;
            case R.id.add_pay_mode_weix: // 微信
                PayModeSaveActivity.JUMP(this, "wxpay");
                break;
            default:
                break;
        }
    }

    /**
     *   提交成功取消选择支付方式 页面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void finish(String str) {
        try {
            if (null != str && str.equals("finishAddPay")) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
