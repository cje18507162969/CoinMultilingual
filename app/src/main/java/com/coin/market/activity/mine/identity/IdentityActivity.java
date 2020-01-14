package com.coin.market.activity.mine.identity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.coin.market.R;
import com.coin.market.activity.mine.identityedit.IdentityEditActivity;
import com.coin.market.databinding.ActivityIdentityLayoutBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;

/**
 * @author: Lenovo
 * @date: 2019/7/29
 * @info: 身份认证
 */
public class IdentityActivity extends BaseActivity implements View.OnClickListener {

    private ActivityIdentityLayoutBinding binding;
    private IdentityViewModel model;
    public int isIdentity = 0; // 是否身份认证  认证状态  1-未认证 2-待审核 3-未通过 4-已实名

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, IdentityActivity.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_identity_layout);
        model = new IdentityViewModel(this, binding);
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);

    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.identityButtonLayout.setOnClickListener(this);
    }

    public void setIsIdentity(int type) {
        if (type == 0) {
            binding.identityImg.setImageResource(R.drawable.mine_verified);
            binding.identityText.setText(getResources().getString(R.string.mine_identity_verified));
            binding.identityText.setTextColor(getResources().getColor(R.color.app_style_blue));
            binding.identityArrow.setVisibility(View.GONE);
            binding.identityNoLayout.setVisibility(View.GONE);
            binding.identityYesLayout.setVisibility(View.VISIBLE);
        } else {
            binding.identityImg.setImageResource(R.drawable.mine_prompt);
            binding.identityText.setText(getResources().getString(R.string.mine_identity_go_verify_text));
            binding.identityText.setTextColor(getResources().getColor(R.color.app_home_coin_text_color_red));
            binding.identityArrow.setVisibility(View.VISIBLE);
            binding.identityNoLayout.setVisibility(View.VISIBLE);
            binding.identityYesLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_Return:
                finish();
                break;
            case R.id.identity_button_layout:
                if (isIdentity != 4) {
                    IdentityEditActivity.JUMP(this);
                }
                break;
            default:
                break;
        }
    }


    /**
     *  身份认证成功  finish掉前面的所有页面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void finish(String str) {
        try {
            if (!TextUtils.isEmpty(str) && str.equals("IdentitySuccess")) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null!=model){
            model.getUserInfo(FaceApplication.getToken());
        }

    }
}
