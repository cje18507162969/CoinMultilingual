package com.coin.market.activity.transaction.paysave;

import android.widget.Toast;

import com.coin.market.databinding.ActivityPayModeSaveLayoutBinding;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import teng.wang.comment.api.FaceApiMbr;
import teng.wang.comment.api.RequestBodyUtils;
import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;

/**
 * @author: Lenovo
 * @date: 2019/8/2
 * @info: 保存添加的银行卡信息 ViewModel
 */
public class PayModeSaveViewModel extends BaseActivityViewModel<PayModeSaveActivity, ActivityPayModeSaveLayoutBinding> {

    public PayModeSaveViewModel(BaseActivity activity, ActivityPayModeSaveLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {

    }

    /**
     * 添加支付方式   paymentMethod 收款方式   payName 名称    payAccount 账号   payBank 开户行   payBankBranch 开户行支行   qrCode 图片地址
     */
    public void addPayMode(String token, String paymentMethod, String payName, String payAccount, String payBank, String payBankBranch, String qrCode) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("paymentMethod", paymentMethod)
                .addParam("payName", payName)
                .addParam("payAccount", payAccount)
                .addParam("payBank", payBank)
                .addParam("payBankBranch", payBankBranch)
                .addParam("qrCode", qrCode)
                .builder();
        FaceApiMbr.getV1ApiServiceMbr().addPayMentMethod(token, body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(String str) {
                        try {
                            Toast.makeText(getActivity(), "添加地址成功！", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post("finishAddPay");
                            getActivity().finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     *   上传QR 支付、收款方式二维码
     */
    public void updateImage(File file) {
        RequestBody  requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        FaceApiMbr.getV1ApiServiceMbr().uploadQr(body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, false) {
                    @Override
                    public void onSuccess(String str) {
                        try {
                            // Toast.makeText(getActivity(), "二维码上传成功！", Toast.LENGTH_SHORT).show();
                            addPayMode(FaceApplication.getToken(), getActivity().PayMode,   // 收款方式
                                    getBinding().payModeNameEdit.getText().toString().trim(),// 名称
                                    getBinding().payModeCodeEdit.getText().toString(),// 账号
                                    getBinding().payBankCodeEdit.getText().toString(),// 开户行
                                    getBinding().payAddressCodeEdit.getText().toString(), // 开户行支行
                                    str); // 图片地址
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


}
