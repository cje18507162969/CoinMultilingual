package com.coin.market.activity.mine.bindemail;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.coin.market.R;
import com.coin.market.databinding.ActivityBindPhoneLayoutBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import teng.wang.comment.base.BaseActivity;

/**
 * @author: Lenovo
 * @date: 2019/7/29
 * @info: 安全中心 绑定手机 绑定邮箱
 */
public class BindEmailActivity extends BaseActivity implements View.OnClickListener {

    private ActivityBindPhoneLayoutBinding binding;
    private BindEmailViewModel model;
    private String status; // phone是手机绑定   email是邮箱绑定

    public static void JUMP(Context context, String status) {
        context.startActivity(new Intent(context, BindEmailActivity.class).putExtra("status", status));
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bind_phone_layout);
        model = new BindEmailViewModel(this, binding);
        if (!TextUtils.isEmpty(status)){
            setStatusUi(status);
        }
    }

    @Override
    protected void initIntentData() {
        status = getIntent().getStringExtra("status");
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.bindPhoneEditButton.setOnClickListener(this);
        binding.bindPhoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence+"")){
                    binding.bindPhoneEditButton.setAlpha(1.0f);
                }else {
                    binding.bindPhoneEditButton.setAlpha(0.5f);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setStatusUi(String status){
        if (status.equals("phone")){
            // 绑定手机号
            binding.bindTitle.setText("绑定手机");
            binding.bindText.setText("手机号码");
            binding.bindPhoneEdit.setHint(getResources().getString(R.string.mine_security_edit_phone_hide));
        }else {
            // 绑定邮箱
            binding.bindTitle.setText("绑定邮箱");
            binding.bindText.setText("邮箱地址");
            binding.bindPhoneEdit.setHint(getResources().getString(R.string.mine_security_edit_email_hide));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_Return:
                finish();
                break;
            case R.id.bind_phone_edit_button:
                model.sendEmail(binding.bindPhoneEdit.getText().toString().trim());
                break;
            default:
                break;
        }
    }

    /**
     *  绑定邮箱成功 退出此页面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void number(String model) {
        try {
            if (null != model && model.equals("BindEmail")) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
