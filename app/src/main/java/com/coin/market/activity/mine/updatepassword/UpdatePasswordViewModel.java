package com.coin.market.activity.mine.updatepassword;

import android.text.TextUtils;
import android.widget.Toast;

import com.coin.market.databinding.ActivityUpdatePasswordLayoutBinding;

import okhttp3.RequestBody;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.api.RequestBodyUtils;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;

/**
 * @author: Lenovo
 * @date: 2019/9/10
 * @info: 修改密码 ViewModel
 */
public class UpdatePasswordViewModel extends BaseActivityViewModel <UpdatePasswordActivity, ActivityUpdatePasswordLayoutBinding>{

    public UpdatePasswordViewModel(UpdatePasswordActivity activity, ActivityUpdatePasswordLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {

    }

    public void updatePassword(String token, String pass, String password) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("password", password)
                .builder();
        FaceApiTest.getV1ApiServiceTest().updatePassword(token, body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(String s) {
                        if (!TextUtils.isEmpty(s))
                            Toast.makeText(getActivity(), ""+s, Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                });
    }

}
