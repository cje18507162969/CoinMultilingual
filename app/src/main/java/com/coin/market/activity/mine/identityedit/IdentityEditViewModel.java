package com.coin.market.activity.mine.identityedit;

import android.text.TextUtils;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coin.market.R;
import com.coin.market.activity.mine.identitysuccess.IdentitySuccessActivity;
import com.coin.market.databinding.ActivityIdentityeditLayoutBinding;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import teng.wang.comment.SPConstants;
import teng.wang.comment.api.FaceApiMbr;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.api.RequestBodyUtils;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.UsersEntity;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.DataKeeper;

import static org.litepal.LitePalApplication.getContext;

/**
 * @author: Lenovo
 * @date: 2019/7/29
 * @info: 填写身份认证 ViewModel
 */
public class IdentityEditViewModel extends BaseActivityViewModel <IdentityEditActivity, ActivityIdentityeditLayoutBinding>{

    public IdentityEditViewModel(IdentityEditActivity activity, ActivityIdentityeditLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {

    }

    /**
     *   上传身份证
     */
    public void updateImage(File file, final int type) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        FaceApiMbr.getV1ApiServiceMbr().uploadQr(body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(String str) {
                        try {
                            switch (type) {
                                case 1:
                                    Glide.with(getActivity()).load(str).into(getBinding().settledCardImg1);
                                    getActivity().url1 = str;
                                    break;
                                case 2:
                                    Glide.with(getActivity()).load(str).into(getBinding().settledCardImg2);
                                    getActivity().url2 = str;
                                    break;
                                case 3:
                                    Glide.with(getActivity()).load(str).into(getBinding().settledCardImg3);
                                    getActivity().url3 = str;
                                    break;
                                default:
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     *   实名认证
     */
    public void Identity(String token, String name, String CardId) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("firstName", name)
                .addParam("passportId", CardId)
                .builder();
        FaceApiTest.getV1ApiServiceTest().authentication(token, body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(String data) {
                        try {
                            if (!TextUtils.isEmpty(data))
                               Toast.makeText(getActivity(), data, Toast.LENGTH_SHORT).show();
                            IdentitySuccessActivity.JUMP(getActivity());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
