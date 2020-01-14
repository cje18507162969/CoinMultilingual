package com.coin.market.activity.mine.identity;

import android.view.View;

import com.coin.market.R;
import com.coin.market.databinding.ActivityIdentityLayoutBinding;

import teng.wang.comment.SPConstants;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.UserInfosDTO;
import teng.wang.comment.model.UserModel;
import teng.wang.comment.model.UsersEntity;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.DataKeeper;

import static org.litepal.LitePalApplication.getContext;

/**
 * @author: Lenovo
 * @date: 2019/7/29
 * @info: 身份认证 ViewModel
 */
public class IdentityViewModel extends BaseActivityViewModel <IdentityActivity, ActivityIdentityLayoutBinding>{

    public IdentityViewModel(IdentityActivity activity, ActivityIdentityLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        switch (FaceApplication.getAuthentication()){//1-未实名认证；2-实名认证审核中 3-实名未通过 4-已实名 ,
            case 1:
                getActivity().setIsIdentity(1);
                getUserInfo(FaceApplication.getToken());
                break;
            case 2:

            case 3:

            case 4:
                getUserInfo(FaceApplication.getToken());
                break;
        }

    }

    /**
     *   获取用户信息
     *   判断是否已经身份认证
     */
    public void getUserInfo(String token) {
        FaceApiTest.getV1ApiServiceTest().userInfo(token)
                .compose(RxSchedulers.<ApiModel<UserInfosDTO>>io_main())
                .subscribe(new RxObserver<UserInfosDTO>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(UserInfosDTO data) {
                        try {
                            getActivity().setIsIdentity(0);
                            getActivity().isIdentity = data.getAuthentication();
                            if (getActivity().isIdentity==4){
                                // 4为 已认证
                                getBinding().identityNoLayout.setVisibility(View.GONE);
                                getBinding().identityText.setText(getContext().getResources().getString(R.string.mine_identity_verified));
                                getBinding().identityText.setTextColor(getActivity().getResources().getColor(R.color.color_999999));
                                getBinding().identityImg.setVisibility(View.GONE);
                                getBinding().identityName.setText(data.getFirstName());
                                getBinding().identityPhone.setText(data.getMobile());
                                getBinding().identityCity.setText("中国");
                                getBinding().identityCardid.setText(data.getPassportId());
                                getBinding().identityNoLayout.setVisibility(View.GONE);
                                getBinding().identityButtonLayout.setClickable(false);
                                UsersEntity model = FaceApplication.getUserInfoModel();
                                model.setAuthentication(4);
                                DataKeeper.put(getContext(), SPConstants.USERINFOMODEL,model);
                            }else{
                                getBinding().identityNoLayout.setVisibility(View.VISIBLE);
                                getBinding().identityYesLayout.setVisibility(View.GONE);
                                getActivity().setIsIdentity(1);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
