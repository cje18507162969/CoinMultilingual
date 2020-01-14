package com.coin.market.activity.home.news;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.coin.market.R;
import com.coin.market.databinding.ActivityHomeNewsLayoutBinding;
import com.coin.market.jpush.JPushModel;

import java.io.Serializable;
import java.util.List;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.model.ArticlesDTO;
import teng.wang.comment.model.BulletinEntity;

/**
 * @author: Lenovo
 * @date: 2019/8/8
 * @info: 首页新闻 列表
 */
public class HomeNewsActivity extends BaseActivity implements View.OnClickListener {

    private HomeNewsViewModel model;
    private ActivityHomeNewsLayoutBinding binding;
    public int position = 0;

    public static void JUMP(Context context, ArticlesDTO entity, String position) {
        Intent intent = new Intent(context, HomeNewsActivity.class);
        intent.putExtra("BulletinEntity",(Serializable) entity);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    @Override
    protected void initIntentData() {
        try {
            position = Integer.parseInt(getIntent().getStringExtra("position"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_news_layout);
        model = new HomeNewsViewModel(this, binding);
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setText("公告详情");
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_Return:
                finish();
                break;
            default:
                break;
        }
    }

}
