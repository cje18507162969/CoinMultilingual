package com.coin.market.activity.password;

import android.widget.Toast;

import com.coin.market.databinding.ActivityForgetPasswordLayoutBinding;

import org.greenrobot.eventbus.EventBus;

import okhttp3.RequestBody;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.api.RequestBodyUtils;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.ForgotPasswordEntity;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;

/**
 * @author Ycc 忘记密码 model
 * @version v1.0
 * @Time 2018-9-5
 */

public class ForgetPasswordModel extends BaseActivityViewModel<ForgetPasswordActivity,ActivityForgetPasswordLayoutBinding> {

    public ForgetPasswordModel(ForgetPasswordActivity activity, ActivityForgetPasswordLayoutBinding binding){
        super(activity,binding);
    }

    @Override
    protected void initView() {

    }


    /**
     * 发送短信验证码    找回密码    restpwd
     */
    public void sendCode() {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("mobile", getBinding().mEdtPasswordPhone.getText().toString().trim())
                .addParam("type", "restpwd")
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
     * 发送短信验证码  type：1 是注册  2是找回密码    当前为2找回密码
     */
    public void sendEmail() {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("email",getBinding().mEdtPasswordPhone.getText().toString().trim())
                .builder();
        FaceApiTest.getV1ApiServiceTest().sendEmailCode(body)
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
     *  忘记密码 第1步  mobile：手机号   code：验证码   password：新密码
     */
    public void forgotPassword(final String mobile , final String code, String password) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("mobile", mobile )
                .addParam("code",code)
                .addParam("password", password)
                .builder();
        FaceApiTest.getV1ApiServiceTest().forgotPasswordOne(body)
                .compose(RxSchedulers.<ApiModel<ForgotPasswordEntity>>io_main())
                .subscribe(new RxObserver<ForgotPasswordEntity>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(ForgotPasswordEntity entity) {
                        getActivity().finish();
                        EventBus.getDefault().post("eventPass");
                    }
                });
    }

}
