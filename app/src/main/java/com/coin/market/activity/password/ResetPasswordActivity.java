package com.coin.market.activity.password;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.databinding.ActivityResetPasswordLayoutBinding;

import teng.wang.comment.base.BaseActivity;

/**
 * @author 重置密码
 * @version v1.0
 * @Time 2018-9-5
 */

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private ResetPasswordModel mPasswordModel;
    private ActivityResetPasswordLayoutBinding binding;
    private String phone, verify, key; //电话号码  短信验证码  忘记密码的key

    public static void JUMP(Context context, String phone, String verify, String key) {
        context.startActivity(new Intent(context, ResetPasswordActivity.class).putExtra("phone", phone).putExtra("verify", verify).putExtra("key", key));
    }

//    @Override
//    protected void setStatusBar() {
//        StatusBarUtil.setTransparentForImageViewInFragment(ResetPasswordActivity.this, null);
//    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reset_password_layout);
        mPasswordModel = new ResetPasswordModel(this, binding);
    }

    @Override
    protected void initIntentData() {
        phone = getIntent().getStringExtra("phone");
        verify = getIntent().getStringExtra("verify");
        key = getIntent().getStringExtra("key");
    }

    @Override
    protected void setListener() {
        binding.mBtnNext.setOnClickListener(this);
        binding.resetPassFinish.setOnClickListener(this);
        binding.mEdtPassword.addTextChangedListener(this);
        binding.mEdtTwoPassword.addTextChangedListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mBtn_next:
                if (TextUtils.isEmpty(binding.mEdtPassword.getText().toString().trim())) {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(binding.mEdtTwoPassword.getText().toString().trim())) {
                    Toast.makeText(this, "请输入确认密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!binding.mEdtPassword.getText().toString().trim().equals(binding.mEdtTwoPassword.getText().toString().trim())) {
                    Toast.makeText(this, "您两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                mPasswordModel.forget(binding.mEdtPassword.getText().toString().trim(), key);
                break;
            case R.id.reset_pass_finish:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() >= 1 && binding.mEdtPassword.getText().toString().trim().length() >= 1 && binding.mEdtTwoPassword.getText().toString().trim().length() >= 1) {
            binding.mBtnNext.setEnabled(true);
            binding.mBtnNext.setAlpha(1.0f);
        } else {
            binding.mBtnNext.setEnabled(false);
            binding.mBtnNext.setAlpha(0.5f);
        }
    }
}
