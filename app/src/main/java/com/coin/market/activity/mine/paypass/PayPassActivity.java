package com.coin.market.activity.mine.paypass;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.databinding.ActivityPayPassLayoutBinding;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;

/**
 * @author: Lenovo
 * @date: 2019/7/29
 * @info: 设置支付密码
 */
public class PayPassActivity extends BaseActivity implements View.OnClickListener {

    private ActivityPayPassLayoutBinding binding;
    private PayPassViewModel model;

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, PayPassActivity.class));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pay_pass_layout);
        model = new PayPassViewModel(this, binding);
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.mTimeBtn.setOnClickListener(this);
        binding.payButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_Return:
                finish();
                break;
            case R.id.mTimeBtn:
                model.getCode(FaceApplication.getMobile());
                break;
            case R.id.pay_button:
                if (TextUtils.isEmpty(binding.payPassword.getText().toString().trim())){
                    Toast.makeText(mContext, getResources().getString(R.string.mine_set_up_payment_password), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(binding.payTwoPassword.getText().toString().trim())){
                    Toast.makeText(mContext, getResources().getString(R.string.mine_set_up_payment_password_again), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!binding.payPassword.getText().toString().trim().equals(binding.payTwoPassword.getText().toString().trim())){
                    Toast.makeText(mContext,  getResources().getString(R.string.mine_passwords_are_inconsistent_twice), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(binding.payCode.getText().toString().trim())){
                    Toast.makeText(mContext,  getResources().getString(R.string.tv_code_input), Toast.LENGTH_SHORT).show();
                    return;
                }
                model.updatePay(FaceApplication.getToken(), binding.payPassword.getText().toString().trim(), binding.payCode.getText().toString().trim());
                break;
            default:
                break;
        }
    }

}
