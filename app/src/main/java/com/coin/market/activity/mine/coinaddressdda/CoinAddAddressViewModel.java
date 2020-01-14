package com.coin.market.activity.mine.coinaddressdda;

import android.widget.Toast;

import com.coin.market.databinding.ActivityCoinAddAddressLayoutBinding;

import org.greenrobot.eventbus.EventBus;

import okhttp3.RequestBody;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.api.RequestBodyUtils;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;

/**
 * @author: Lenovo
 * @date: 2019/7/18
 * @info:  CoinAddAddressActivity ViewModel
 */
public class CoinAddAddressViewModel extends BaseActivityViewModel <CoinAddAddressActivity, ActivityCoinAddAddressLayoutBinding> {

    public CoinAddAddressViewModel(CoinAddAddressActivity activity, ActivityCoinAddAddressLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {

    }

    /**
     *   添加币种地址
     */
    public void addCoinAddress(String token, String coin_id, String address, String tag) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("address", address)
                .addParam("coinId", coin_id)
                .addParam("tag", tag)
                .builder();
        FaceApiTest.getV1ApiServiceTest().addCoinAddress(token, body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(String data) {
                        try {
                            Toast.makeText(getActivity(), "添加地址成功！", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post("addAddress");
                            getActivity().finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
