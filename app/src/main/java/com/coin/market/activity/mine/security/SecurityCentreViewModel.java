package com.coin.market.activity.mine.security;

import android.view.View;

import com.coin.market.R;
import com.coin.market.databinding.ActivitySecurityCentreLayoutBinding;
import com.coin.market.util.EmptyUtil;

import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.UserInfosDTO;
import teng.wang.comment.model.UserModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;

/**
 * @author: Lenovo
 * @date: 2019/7/29
 * @info:
 */
public class SecurityCentreViewModel extends BaseActivityViewModel<SecurityCentreActivity, ActivitySecurityCentreLayoutBinding> {

    public SecurityCentreViewModel(SecurityCentreActivity activity, ActivitySecurityCentreLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        getUserInfo(FaceApplication.getToken());
    }

    /**
     *   获取用户信息
     *   这里要根据字段 判断账号信息完整度
     */
    public void getUserInfo(String token) {
        FaceApiTest.getV1ApiServiceTest().userInfo(token)
                .compose(RxSchedulers.<ApiModel<UserInfosDTO>>io_main())
                .subscribe(new RxObserver<UserInfosDTO>(getActivity(), getActivity().Tag, false) {
                    @Override
                    public void onSuccess(UserInfosDTO data) {
                        try {
                            setUi(data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void setUi(UserInfosDTO data){
        if (!EmptyUtil.isEmpty(data.getMobile())){  // 手机已绑定
            getActivity().complete+=20;
            getBinding().securityCentreGoogleText.setTextColor(getActivity().getResources().getColor(R.color.color_999999));
            getBinding().securityCentreGoogleImg.setVisibility(View.GONE);
        }
//        if (!EmptyUtil.isEmpty(data.getEmail())){  // Email已绑定
//            getActivity().complete+=20;
//            getBinding().securityCentreEmailText.setTextColor(getActivity().getResources().getColor(R.color.color_999999));
//            getBinding().securityCentreEmailImg.setVisibility(View.GONE);
//            getBinding().securityCentreEmailText.setText(getActivity().getResources().getString(R.string.mine_security_centre_off_bind));
//        }
//        if (!EmptyUtil.isEmpty(data.getGoogle())){  // 谷歌已绑定
//            getActivity().complete+=20;
//            getBinding().securityCentreGoogleText.setTextColor(getActivity().getResources().getColor(R.color.color_999999));
//            getBinding().securityCentreGoogleImg.setVisibility(View.GONE);
//            getBinding().securityCentreGoogleText.setText(getActivity().getResources().getString(R.string.mine_security_centre_off_bind));
//        }
        getActivity().setSecurityLength(getActivity().complete);
    }
}
