package com.coin.market.activity.password;

import android.widget.Toast;

import com.coin.market.databinding.ActivityResetPasswordLayoutBinding;

import org.greenrobot.eventbus.EventBus;

import okhttp3.RequestBody;
import teng.wang.comment.api.FaceApi;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.api.RequestBodyUtils;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;

/**
 * @author Ycc 忘记密码 model
 * @version v1.0
 * @Time 2018-9-5
 */

public class ResetPasswordModel extends BaseActivityViewModel<ResetPasswordActivity, ActivityResetPasswordLayoutBinding> {

    public ResetPasswordModel(ResetPasswordActivity activity, ActivityResetPasswordLayoutBinding binding){
        super(activity,binding);
    }

    @Override
    protected void initView() {

    }

    /** 重置密码 */
    public void forget(String password, String key){
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("forget_password_key", key)
                .addParam("new_password", password)
                .addParam("new_password_confirmation",password)
                .builder();
        FaceApiTest.getV1ApiServiceTest().forgotPasswordTwo(body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag,true) {
                    @Override
                    public void onSuccess(final String model) {
                        getActivity().finish();
                        Toast.makeText(getActivity(), "修改成功！", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post("eventPass");
                        // getActivity().startActivity(new Intent(getActivity(), MoneyActivity.class));
                    }
                });
    }

    /**忘记密码接口*/
    public void forget(String phone, String verify_code, String password){
        FaceApi.getV1ApiService().forget(phone,verify_code,password)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag,true) {
                    @Override
                    public void onSuccess(final String model) {
                        EventBus.getDefault().post("eventPass");
                        getActivity().finish();
                    }
                });
    }
}
