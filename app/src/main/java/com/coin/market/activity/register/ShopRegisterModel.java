package com.coin.market.activity.register;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import com.coin.market.databinding.ActivityShopRegisterLayoutBinding;

import okhttp3.RequestBody;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.api.RequestBodyUtils;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;

/**
 * @author 注册入口model
 * @version v1.0
 * @Time 2018-9-3
 */

public class ShopRegisterModel extends BaseActivityViewModel<ShopRegisterActivity, ActivityShopRegisterLayoutBinding> {

    public ShopRegisterModel(ShopRegisterActivity activity, ActivityShopRegisterLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        onEdtText();
    }

    private void onEdtText() {
        getBinding().mEdtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 1 && getBinding().mEdtCode.getText().toString().trim().length() >= 1 && getBinding().mEdtPassword.getText().toString().trim().length() >= 1 && getBinding().mEdtTwoPassword.getText().toString().trim().length() >= 1) {
                    getBinding().mBtnRegister.setEnabled(true);
                    getBinding().mBtnRegister.setAlpha(1.0f);
                } else {
                    getBinding().mBtnRegister.setEnabled(false);
                    getBinding().mBtnRegister.setAlpha(0.5f);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getBinding().mEdtPhone.setSelection(getBinding().mEdtPhone.getText().toString().length());
            }
        });
        getBinding().mEdtCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 1 && getBinding().mEdtPhone.getText().toString().trim().length() >= 1 && getBinding().mEdtPassword.getText().toString().trim().length() >= 1 && getBinding().mEdtTwoPassword.getText().toString().trim().length() >= 1) {
                    getBinding().mBtnRegister.setEnabled(true);
                    getBinding().mBtnRegister.setAlpha(1.0f);
                } else {
                    getBinding().mBtnRegister.setEnabled(false);
                    getBinding().mBtnRegister.setAlpha(0.5f);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                getBinding().mEdtCode.setSelection(getBinding().mEdtCode.getText().toString().length());
            }
        });
        getBinding().mEdtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 1 && getBinding().mEdtPhone.getText().toString().trim().length() >= 1 && getBinding().mEdtCode.getText().toString().trim().length() >= 1 && getBinding().mEdtTwoPassword.getText().toString().trim().length() >= 1) {
                    getBinding().mBtnRegister.setEnabled(true);
                    getBinding().mBtnRegister.setAlpha(1.0f);
                } else {
                    getBinding().mBtnRegister.setEnabled(false);
                    getBinding().mBtnRegister.setAlpha(0.5f);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                getBinding().mEdtPassword.setSelection(getBinding().mEdtPassword.getText().toString().length());
            }
        });
        getBinding().mEdtTwoPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 1 && getBinding().mEdtPhone.getText().toString().trim().length() >= 1 && getBinding().mEdtCode.getText().toString().trim().length() >= 1 && getBinding().mEdtPassword.getText().toString().trim().length() >= 1) {
                    getBinding().mBtnRegister.setEnabled(true);
                    getBinding().mBtnRegister.setAlpha(1.0f);
                } else {
                    getBinding().mBtnRegister.setEnabled(false);
                    getBinding().mBtnRegister.setAlpha(0.5f);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                getBinding().mEdtTwoPassword.setSelection(getBinding().mEdtTwoPassword.getText().toString().length());
            }
        });
    }

    /**
     * 发送短信验证码  type：1 是注册  2是找回密码    当前为1
     */
    public void sendCode() {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("mobile", getBinding().mEdtPhone.getText().toString().trim())
                .addParam("type", "register")
                .builder();
        FaceApiTest.getV1ApiServiceTest().sendMobileCode(body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(String s) {
                        /**发送验证码接口成功倒计时开始*/
                        getBinding().mTimeBtn.startTimer();
                    }
                });
    }

    /**
     * 提交注册
     */
    public void Register(String mobile, String password, String code, String pid) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("mobile", mobile)
                .addParam("password", password)
                .addParam("code", code)
                .addParam("inviteCode", pid)
                .builder();
        FaceApiTest.getV1ApiServiceTest().mbrRegister(body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(String model) {
                        getActivity().finish();
                        Toast.makeText(getActivity(), "注册成功", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
