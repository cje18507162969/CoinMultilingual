package com.coin.market.activity.mine.publish;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.coin.market.R;
import com.coin.market.activity.transaction.launch.LaunchPosterActivity;
import com.coin.market.databinding.ActivityMyPublishLayoutBinding;

import teng.wang.comment.base.BaseActivity;

/**
 * @author: Lenovo
 * @date: 2019/8/5
 * @info: 我发布的广告 列表
 */
public class MyPublishActivity extends BaseActivity implements View.OnClickListener {

    private ActivityMyPublishLayoutBinding binding;
    private MyPublishViewModel model;

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, MyPublishActivity.class));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_publish_layout);
        model = new MyPublishViewModel(this, binding);
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.publishButtonLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_Return:
                finish();
                break;
            case R.id.publish_button_layout:
                // 发布广告
                LaunchPosterActivity.JUMP(this);
                break;
            default:
                break;
        }
    }
}
