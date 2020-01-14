package com.coin.market.activity.mine.about;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.coin.market.R;
import com.coin.market.databinding.ActivityAboutLayoutBinding;

import teng.wang.comment.base.BaseActivity;

/**
 * @author: Lenovo
 * @date: 2019/7/29
 * @info: 关于我们
 */
public class AboutActivity extends BaseActivity implements View.OnClickListener {


    private ActivityAboutLayoutBinding binding;
    private AboutViewModel model;

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, AboutActivity.class));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about_layout);
        model = new AboutViewModel(this, binding);
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.aboutVersionButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_Return:
                finish();
                break;
            case R.id.about_version_button:
                //model.getVersion(1);
                break;
            default:
                break;
        }
    }

}
