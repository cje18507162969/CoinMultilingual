package com.coin.market.activity.register;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.databinding.ActivityShopRegisterLayoutBinding;

import teng.wang.comment.base.BaseActivity;

/**
 * @author 注册入口
 * @version v1.0
 * @Time 2018-9-3
 */

public class ShopRegisterActivity extends BaseActivity implements View.OnClickListener {

    private ShopRegisterModel mRegisterModel;
    private ActivityShopRegisterLayoutBinding mRegisterLayoutBinding;

    public static void JUMP(Context context){
        context.startActivity(new Intent(context, ShopRegisterActivity.class));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        mRegisterLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_shop_register_layout);
        mRegisterModel = new ShopRegisterModel(this, mRegisterLayoutBinding);
    }

    @Override
    protected void setListener() {
        mRegisterLayoutBinding.mTimeBtn.setOnClickListener(this);
        mRegisterLayoutBinding.mBtnRegister.setOnClickListener(this);
        mRegisterLayoutBinding.mBtnRegisterClose.setOnClickListener(this);
        mRegisterLayoutBinding.mBtnRegisterLogin.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mBtn_register_close:
                finish();
                break;
            case R.id.mBtn_register_login:
                finish();
                break;
            case R.id.mBtn_register:
                try {

                    String Phone = mRegisterLayoutBinding.mEdtPhone.getText().toString().trim();//手机号
                    String Code = mRegisterLayoutBinding.mEdtCode.getText().toString().trim();//验证码
                    String Password = mRegisterLayoutBinding.mEdtPassword.getText().toString().trim();//密码
                    String twoPassword = mRegisterLayoutBinding.mEdtTwoPassword.getText().toString().trim(); //确认密码
                    String pid = mRegisterLayoutBinding.shareCode.getText().toString().trim();//邀请码

                    if (TextUtils.isEmpty(mRegisterLayoutBinding.mEdtPhone.getText().toString().trim())){
                        Toast.makeText(this, getResources().getString(R.string.mine_security_edit_phone_hide), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(mRegisterLayoutBinding.mEdtCode.getText().toString().trim())){
                        Toast.makeText(this, getResources().getString(R.string.tv_code_input), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(mRegisterLayoutBinding.mEdtPassword.getText().toString().trim())){
                        Toast.makeText(this, getResources().getString(R.string.mine_input_pwd), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(mRegisterLayoutBinding.mEdtTwoPassword.getText().toString().trim())){
                        Toast.makeText(this, getResources().getString(R.string.mine_input_pwd_confir), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!Password.equals(twoPassword)){
                        Toast.makeText(this, getResources().getString(R.string.mine_passwords_are_inconsistent_twice), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mRegisterModel.Register(Phone, Password, Code, pid);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.mTimeBtn:
                mRegisterModel.sendCode();
                break;
            default:
                break;
        }
    }
//    @Override
//    protected boolean isImmersionBarEnabled() {
//        return true;
//    }
//    /**
//     * 初始化沉浸式
//     */
//    @Override
//    protected void initImmersionBar() {
//        super.initImmersionBar();
//        mImmersionBar.statusBarDarkFont(true, 0.2f).init();
//    }
}
