package com.coin.market.activity.mine.paypass;

import android.text.TextUtils;
import android.widget.Toast;

import com.coin.market.databinding.ActivityPayPassLayoutBinding;

import okhttp3.RequestBody;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.api.RequestBodyUtils;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;

/**
 * @author: Lenovo
 * @date: 2019/7/29
 * @info: 填写身份认证 ViewModel
 */
public class PayPassViewModel extends BaseActivityViewModel <PayPassActivity, ActivityPayPassLayoutBinding>{

    public PayPassViewModel(PayPassActivity activity, ActivityPayPassLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {

    }

    /**
     *   上传身份证
     */
    public void updatePay(String token, String payPass, String code) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("password", payPass)
                .addParam("code", code)
                .builder();
        FaceApiTest.getV1ApiServiceTest().updatePayPassword(token, body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(String str) {
                        try {
                            if (!TextUtils.isEmpty(str))
                                Toast.makeText(getActivity(), str+"", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     *   发送邮箱验证码
     */
    public void getCode(String mobile) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("mobile", mobile)
                .addParam("type", "paypwd")
                .builder();
        FaceApiTest.getV1ApiServiceTest().sendMobileCode(body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(String data) {
                        try {
                           getBinding().mTimeBtn.startTimer();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
