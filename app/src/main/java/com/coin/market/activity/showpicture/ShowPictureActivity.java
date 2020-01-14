package com.coin.market.activity.showpicture;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.coin.market.R;
import com.coin.market.databinding.ActivityShowPictureBinding;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.utils.StatusBarUtil;
import teng.wang.comment.utils.glide.GlideUtil;

/**
 * @author  查看大图
 * @version v1.0
 * @Time 2018-9-5
 */

public class ShowPictureActivity extends BaseActivity{

    public static final String SHOWPICTUREURL = "SHOWPICTUREURL";
    private String url;
    private ActivityShowPictureBinding mBinding;

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.black), 0);
    }

    public static void JUMP(Context context, String url){
        context.startActivity(new Intent(context, ShowPictureActivity.class).putExtra("SHOWPICTUREURL", url));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(true);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_show_picture);
        initView();
    }

    private void setViewPager(){
    }

    @Override
    protected void setListener() {
        mBinding.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void initIntentData() {
        super.initIntentData();
        url = getIntent().getStringExtra(SHOWPICTUREURL);
    }

    private void initView(){
        GlideUtil.displayImage(this,mBinding.image,url);
    }
}
