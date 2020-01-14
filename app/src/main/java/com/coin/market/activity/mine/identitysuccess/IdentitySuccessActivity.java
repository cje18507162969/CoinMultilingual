package com.coin.market.activity.mine.identitysuccess;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.coin.market.R;
import com.coin.market.databinding.ActivityIdentitySuccessLayoutBinding;

import org.greenrobot.eventbus.EventBus;

import teng.wang.comment.SPConstants;
import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.UsersEntity;
import teng.wang.comment.utils.DataKeeper;

import static org.litepal.LitePalApplication.getContext;

/**
 * @author: Lenovo
 * @date: 2019/7/29
 * @info: 认证成功 跳转去交易 并关闭掉前面的页面
 */
public class IdentitySuccessActivity extends BaseActivity implements View.OnClickListener {

    private ActivityIdentitySuccessLayoutBinding binding;
    private IdentitySuccessViewModel model;

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, IdentitySuccessActivity.class));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_identity_success_layout);
        model = new IdentitySuccessViewModel(this, binding);
        binding.identityeditTransactionButton.setAlpha(1.0f);
        UsersEntity model = FaceApplication.getUserInfoModel();
        model.setAuthentication(4);
        DataKeeper.put(IdentitySuccessActivity.this, SPConstants.USERINFOMODEL,model);
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.identityeditTransactionButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_Return:
                finish();
                break;
            case R.id.identityedit_transaction_button:
                // 认证成功  发送广播 关闭页面
                EventBus.getDefault().post("IdentitySuccess");
                finish();
                break;
            default:
                break;
        }
    }
}
