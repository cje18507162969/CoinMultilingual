package com.coin.market.activity.mine.bindemail;


import android.widget.Toast;

import com.coin.market.activity.mine.bindemailconfirm.BindEmailConfirmActivity;
import com.coin.market.databinding.ActivityBindPhoneLayoutBinding;

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
 * @info: 安全中心 绑定手机 ViewModel     address/emailAuthenticationSend
 */
public class BindEmailViewModel extends BaseActivityViewModel <BindEmailActivity, ActivityBindPhoneLayoutBinding>{

    public BindEmailViewModel(BindEmailActivity activity, ActivityBindPhoneLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {

    }

    /**
     *   发送邮箱验证码
     */
    public void sendEmail(final String email) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("email", email)
                .builder();
        FaceApiTest.getV1ApiServiceTest().emailAuthenticationSend(body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(String data) {
                        try {
                            Toast.makeText(getActivity(), "已发送至您的邮箱", Toast.LENGTH_SHORT).show();
                            BindEmailConfirmActivity.JUMP(getActivity(), email);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
