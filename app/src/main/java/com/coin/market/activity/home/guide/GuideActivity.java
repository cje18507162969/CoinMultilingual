package com.coin.market.activity.home.guide;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.coin.market.R;
import com.coin.market.databinding.ActivityGuideLayoutBinding;
import com.coin.market.util.EmptyUtil;

import teng.wang.comment.base.BaseActivity;

/**
 * @author: Lenovo
 * @date: 2019/8/7
 * @info:
 */
public class GuideActivity extends BaseActivity implements View.OnClickListener {

    private ActivityGuideLayoutBinding binding;
    private GuideViewModel model;

    public static void JUMP(Context context, String title, String url){
        context.startActivity(new Intent(context, GuideActivity.class).putExtra("title", title).putExtra("url", url));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_guide_layout);
        model = new GuideViewModel(this, binding);
        if (!EmptyUtil.isEmpty(getIntent().getStringExtra("url"))){
            Glide.with(this).load(getIntent().getStringExtra("url")).into(binding.guideImg);
        }
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setText(getIntent().getStringExtra("title"));
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
