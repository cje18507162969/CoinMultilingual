package com.coin.market.activity.mine.bindemailconfirm;

import android.databinding.ViewDataBinding;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import okhttp3.RequestBody;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.api.RequestBodyUtils;
import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;

/**
 * @author: Lenovo
 * @date: 2019/8/12
 * @info: 绑定邮箱 确认 ViewModel
 */
public class BindEmailConfirmViewModel extends BaseActivityViewModel {

    public BindEmailConfirmViewModel(BaseActivity activity, ViewDataBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {

    }

    /**
     *   发送邮箱验证码
     */
    public void bindEmail(String token, String email, String code) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("email", email)
                .addParam("code", code)
                .builder();
        FaceApiTest.getV1ApiServiceTest().emailAuthenticationMerge(token, body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(String data) {
                        try {
                            Toast.makeText(getActivity(), "绑定邮箱成功！", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post("BindEmail");
                            getActivity().finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
