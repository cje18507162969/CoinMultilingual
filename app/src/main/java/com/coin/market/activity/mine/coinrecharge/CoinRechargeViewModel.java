package com.coin.market.activity.mine.coinrecharge;

import com.coin.market.R;
import com.coin.market.databinding.ActivityCoinRechargeLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.google.gson.Gson;

import java.util.List;

import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.CoinItemAddressModel;
import teng.wang.comment.model.CoinListEntity;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.ZXingUtils;
import teng.wang.comment.utils.log.Log;

/**
 * @author: Lenovo
 * @date: 2019/7/15
 * @info:  币种充值  显示二维码 密钥地址  ViewModel
 */
public class CoinRechargeViewModel extends BaseActivityViewModel<CoinRechargeActivity, ActivityCoinRechargeLayoutBinding> {


    public CoinRechargeViewModel(CoinRechargeActivity activity, ActivityCoinRechargeLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        if (!EmptyUtil.isEmpty(getActivity().getIntent().getStringExtra("coinId"))){
            getCoinAddress(FaceApplication.getToken(), getActivity().getIntent().getStringExtra("coinId"));
        }else {
            getCoinList();
        }
    }


    /**
     *   获取充值币种二维码 详情
     */
    public void getCoinAddress(String token, final String id) {
        FaceApiTest.getV1ApiServiceTest().getCoinItemAddress(token, id)
                .compose(RxSchedulers.<ApiModel<CoinItemAddressModel>>io_main())
                .subscribe(new RxObserver<CoinItemAddressModel>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(CoinItemAddressModel entity) {
                        try {
                            Log.e("cjh>>>", "获取充币地址:" + new Gson().toJson(entity));
                            Log.e("cjh>>>", "importMin:" + entity.importMin);
                            getActivity().imgurl = entity.getAddress();
                            getBinding().coinRechargeQr.setImageBitmap(ZXingUtils.createQRImage(entity.getAddress(), 800, 800));
                            getBinding().coinRechargeAddress.setText(entity.getAddress());
                            getBinding().coinRechargeContent.setText(getActivity().getResources().getString(R.string.mine_coin_recharge_text_content)
                                     + "1USDT");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     *    获取充币  币种类型
     */
    public void getCoinList() {
//        FaceApiTest.getV1ApiServiceTest().getCionList()
//                .compose(RxSchedulers.<ApiModel<List<CoinListEntity>>>io_main())
//                .subscribe(new RxObserver<List<CoinListEntity>>(getActivity(), getActivity().Tag, false) {
//                    @Override
//                    public void onSuccess(List<CoinListEntity> list) {
//                        try {
//                            Log.e("cjh>>>", "获取充币 币种类型List<CoinListEntity>:" + new Gson().toJson(list));
//                            getCoinAddress(FaceApplication.getToken(), list.get(0).getCoin_id()+"");
//                            getActivity().coinName = list.get(0).getName()+"";
//                            getBinding().rechargeCoinName.setText(getActivity().coinName);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
    }

}
