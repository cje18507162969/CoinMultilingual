package com.coin.market.activity.mine.service;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.coin.market.R;
import com.coin.market.databinding.ActivityServiceLayoutBinding;

import teng.wang.comment.base.BaseActivity;

/**
 * @author: Lenovo
 * @date: 2019/7/29
 * @info: 关于我们
 */
public class ServiceActivity extends BaseActivity implements View.OnClickListener {

    private ServiceViewModel model;
    private ActivityServiceLayoutBinding binding;

    public static void JUMP(Context context){
        context.startActivity(new Intent(context, ServiceActivity.class));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_service_layout);
        model = new ServiceViewModel(this, binding);
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setText("在线交谈");
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_Return:
                finish();
                break;
            default:
                break;
        }
    }
}
