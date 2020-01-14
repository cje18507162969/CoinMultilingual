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
import com.coin.market.activity.main.MainActivity;
import com.coin.market.databinding.ActivityForgetPasswordLayoutBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import teng.wang.comment.base.BaseActivity;

/**
 * @author 忘记密码  找回密码
 * @version v1.0
 * @Time 2018-9-5
 */

public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private int verifyType = 1;
    private ForgetPasswordModel mPasswordModel;
    private ActivityForgetPasswordLayoutBinding binding;

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, ForgetPasswordActivity.class));
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forget_password_layout);
        mPasswordModel = new ForgetPasswordModel(this, binding);
    }

    @Override
    protected void setListener() {
        binding.mTimeBtn.setOnClickListener(this);       // 验证码button
        binding.mBtnNext.setOnClickListener(this);       // 下一步
        binding.forgetPassClose.setOnClickListener(this);  // 退出当前界面
        binding.switchVerifyMode.setOnClickListener(this); // 切换验证方式
        binding.mEdtPasswordPhone.addTextChangedListener(this);
        binding.mEdtPasswordCode.addTextChangedListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mBtn_next:
                if (TextUtils.isEmpty(binding.mEdtPasswordPhone.getText().toString().trim())) {
                    Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.mine_security_edit_phone_hide), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(binding.mEdtPasswordCode.getText().toString().trim())) {
                    Toast.makeText(this, getResources().getString(R.string.tv_code_input), Toast.LENGTH_SHORT).show();
                    return;
                }
                String password = binding.EdtPassword.getText().toString().toString();
                String passwordenter = binding.EdtPasswordEnter.getText().toString().toString();

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(this,  getResources().getString(R.string.forget_pass_edit_pass), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(passwordenter)) {
                    Toast.makeText(this,  getResources().getString(R.string.forget_pass_edit_pass_confirm), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(passwordenter)){
                    Toast.makeText(this,  getResources().getString(R.string.forget_two_new_passwords_entered_inconsistently), Toast.LENGTH_SHORT).show();
                    return;
                }
                mPasswordModel.forgotPassword(
                        binding.mEdtPasswordPhone.getText().toString().trim(),
                        binding.mEdtPasswordCode.getText().toString().trim(),
                        password+"");
                break;
            case R.id.mTimeBtn:
                if (TextUtils.isEmpty(binding.mEdtPasswordPhone.getText().toString().trim())) {
                    Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.mine_security_edit_phone_hide), Toast.LENGTH_SHORT).show();
                    return;
                }
                mPasswordModel.sendCode();
//                if (verifyType==1){
//                    mPasswordModel.sendCode();
//                }else {
//                    mPasswordModel.sendEmail();
//                }
                break;
            case R.id.switch_verify_mode: // 切换验证方式
                binding.mEdtPasswordPhone.setText("");
                if (verifyType == 1) {
                    verifyType = 2;
                    switchVerifyMode(verifyType);
                } else {
                    verifyType = 1;
                    switchVerifyMode(verifyType);
                }
                break;
            case R.id.forget_pass_close:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * type:0  手机号码验证   type:1 邮箱验证
     */
    private void switchVerifyMode(int type) {
        switch (type) {
            case 1:
                binding.switchVerifyMode.setText(this.getResources().getString(R.string.forget_pass_email));
                binding.mEdtPasswordPhone.setHint(this.getResources().getString(R.string.forget_pass_edit_phone));
                binding.mEdtPasswordCode.setHint(this.getResources().getString(R.string.register_edit_verify));
                break;
            case 2:
                binding.switchVerifyMode.setText(this.getResources().getString(R.string.forget_pass_phone));
                binding.mEdtPasswordPhone.setHint(this.getResources().getString(R.string.forget_pass_edit_email));
                binding.mEdtPasswordCode.setHint(this.getResources().getString(R.string.tv_code_input));
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
        if (s.length() >= 1 && binding.mEdtPasswordPhone.getText().toString().trim().length() >= 1 && binding.mEdtPasswordCode.getText().toString().trim().length() >= 1) {
            binding.mBtnNext.setEnabled(true);
            binding.mBtnNext.setAlpha(1.0f);
        } else {
            binding.mBtnNext.setEnabled(false);
            binding.mBtnNext.setAlpha(0.5f);
        }
    }

    /**
     *  修改密码成功的广播 关闭当前ac 让用户去登录
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Success(String model) {
        try {
            if (null != model && model.equals("eventPass")) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
