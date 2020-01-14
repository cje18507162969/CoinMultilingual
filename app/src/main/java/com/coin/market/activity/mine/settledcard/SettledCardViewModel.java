package com.coin.market.activity.mine.settledcard;

import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coin.market.activity.mine.settleddata.SettledDataActivity;
import com.coin.market.databinding.ActivitySettledCardLayoutBinding;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import teng.wang.comment.api.FaceApiMbr;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.SettledEditModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;

/**
 * @author: Lenovo
 * @date: 2019/9/3
 * @info: 商家申请 ViewModel
 */
public class SettledCardViewModel extends BaseActivityViewModel<SettledCardActivity, ActivitySettledCardLayoutBinding> {


    public SettledCardViewModel(SettledCardActivity activity, ActivitySettledCardLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {

    }

    /**
     * 商家申请 申请资料 提交资料
     */
    public void bindShop(String token, final SettledEditModel mdoel) {
        String json = new Gson().toJson(mdoel);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        FaceApiMbr.getV1ApiServiceMbr().addMerchants(token, body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(String data) {
                        try {
                            Toast.makeText(getActivity(), "您已经成功申请商家！", Toast.LENGTH_SHORT).show();
                            SettledDataActivity.JUMP(getActivity(), "1");
                            EventBus.getDefault().post("ApplyShop"); // 申请商家成功  做相关操作
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
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

}
