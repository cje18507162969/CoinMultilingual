package com.coin.market.activity.mine.updatepassword;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.databinding.ActivityUpdatePasswordLayoutBinding;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;

/**
 * @author: Lenovo
 * @date: 2019/9/10
 * @info: 修改密码
 */
public class UpdatePasswordActivity extends BaseActivity implements View.OnClickListener {

    private UpdatePasswordViewModel model;
    private ActivityUpdatePasswordLayoutBinding binding;

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, UpdatePasswordActivity.class));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_password_layout);
        model = new UpdatePasswordViewModel(this, binding);
    }

    @Override
    protected void setListener() {
        binding.mBtnUpdate.setOnClickListener(this);
        binding.updatePassFinish.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_pass_finish:
                finish();
                break;
            case R.id.mBtn_update:
                if (TextUtils.isEmpty(binding.mEdtFormerPassword.getText().toString().trim())){
                    Toast.makeText(mContext, getResources().getString(R.string.forget_pass_former_edit_pass), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(binding.mEdtNewPassword.getText().toString().trim())){
                    Toast.makeText(mContext, getResources().getString(R.string.forget_pass_edit_pass), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(binding.mEdtNewTwoPassword.getText().toString().trim())){
                    Toast.makeText(mContext, getResources().getString(R.string.forget_pass_edit_pass_confirm), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!binding.mEdtNewPassword.getText().toString().trim().equals(binding.mEdtNewTwoPassword.getText().toString().trim())){
                    Toast.makeText(mContext, getResources().getString(R.string.forget_two_new_passwords_entered_inconsistently), Toast.LENGTH_SHORT).show();
                    return;
                }
                model.updatePassword(FaceApplication.getToken(), binding.mEdtFormerPassword.getText().toString().trim(), binding.mEdtNewPassword.getText().toString().trim());
                break;
            default:
                break;
        }
    }
}
