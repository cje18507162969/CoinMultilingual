package com.coin.market.activity.mine.googleqr;

import com.coin.market.databinding.ActivityGoogleLayoutBinding;
import com.coin.market.util.Base64Utils;

import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.getGoogleModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;

/**
 * @author: Lenovo
 * @date: 2019/8/13
 * @info: 谷歌身份验证器 ViewModel
 */
public class GoogleQrViewModel extends BaseActivityViewModel <GoogleQrActivity, ActivityGoogleLayoutBinding>{

    public GoogleQrViewModel(GoogleQrActivity activity, ActivityGoogleLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        createGoogleCaptcha(FaceApplication.getToken());
    }

    /**
     *   获取用户 QR二维码信息 秘钥地址
     */
    public void createGoogleCaptcha(String token) {
        FaceApiTest.getV1ApiServiceTest().GoogleCaptcha(token)
                .compose(RxSchedulers.<ApiModel<getGoogleModel>>io_main())
                .subscribe(new RxObserver<getGoogleModel>(getActivity(), getActivity().Tag, false) {
                    @Override
                    public void onSuccess(getGoogleModel data) {
                        try {
                            getBinding().googleSecretKey.setText(data.getSecret());
                            getBinding().ivGoogleQrCode.setImageBitmap(Base64Utils.decodeBase64Str(data.getImage()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
