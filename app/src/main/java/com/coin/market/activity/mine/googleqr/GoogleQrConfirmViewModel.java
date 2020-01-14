package com.coin.market.activity.mine.googleqr;

import android.widget.Toast;

import com.coin.market.databinding.ActivityGoogleqrConfirmLayoutBinding;

import org.greenrobot.eventbus.EventBus;

import okhttp3.RequestBody;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.api.RequestBodyUtils;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;

/**
 * @author: Lenovo
 * @date: 2019/8/13
 * @info: 谷歌身份验证器 ViewModel
 */
public class GoogleQrConfirmViewModel extends BaseActivityViewModel <GoogleQrConfirmActivity, ActivityGoogleqrConfirmLayoutBinding>{

    public GoogleQrConfirmViewModel(GoogleQrConfirmActivity activity, ActivityGoogleqrConfirmLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {

    }

    /**
     * 发送短信验证码  type：1 是注册  2是找回密码   3 绑定谷歌验证器 当前是 3  要传token
     */
    public void sendCode(String token, String mobile) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("type", "3")
                .addParam("mobile", mobile)
                .addParam("validate", "google")
                .builder();
        FaceApiTest.getV1ApiServiceTest().sendMobileCode(token, body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(String s) {
                        /**发送验证码接口成功倒计时开始*/
                        getBinding().googleTimerButton.startTimer();
                    }
                });
    }

    /**
     *   谷歌验证器绑定
     */
    public void createGoogleCaptcha(String token, String secret, String code, String google_code) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("code", code)
                .addParam("google_code", google_code)
                .addParam("secret", secret)
                .addParam("type", "1")
                .builder();
        FaceApiTest.getV1ApiServiceTest().bindGoogle(token, body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(String data) {
                        try {
                            Toast.makeText(getActivity(), "谷歌绑定成功！", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post("BindGoogle");
                            getActivity().finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
