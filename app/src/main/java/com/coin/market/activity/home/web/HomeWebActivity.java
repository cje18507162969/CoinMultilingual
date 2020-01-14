package com.coin.market.activity.home.web;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.coin.market.R;
import com.coin.market.databinding.ActivityHelpCenterLayoutBinding;
import com.coin.market.util.EmptyUtil;

import teng.wang.comment.base.BaseActivity;

/**
 * @author: Lenovo
 * @date: 2019/8/7
 * @info: 帮助中心
 */
public class HomeWebActivity extends BaseActivity implements View.OnClickListener {

    private ActivityHelpCenterLayoutBinding binding;
    private HomeWebViewModel model;

    public static void JUMP(Context context, String title, String url){
        context.startActivity(new Intent(context, HomeWebActivity.class).putExtra("title", title).putExtra("url", url));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_help_center_layout);
        model = new HomeWebViewModel(this, binding);
    }

    @Override
    protected void initTitleData() {
        if (!EmptyUtil.isEmpty(getIntent().getStringExtra("title"))){
            binding.titleBar.txtTitle.setText(getIntent().getStringExtra("title"));
        }
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
    }

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
