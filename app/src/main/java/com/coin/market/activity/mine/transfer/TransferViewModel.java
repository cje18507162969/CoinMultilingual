package com.coin.market.activity.mine.transfer;

import android.widget.Toast;

import com.coin.market.databinding.ActivityTransferLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import teng.wang.comment.api.FaceApiMbr;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.FB_BBEntity;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.log.Log;
import teng.wang.comment.widget.MessageWindow;

/**
 * @author: Lenovo
 * @date: 2019/7/17
 * @info: 划转 ViewModel
 */
public class TransferViewModel extends BaseActivityViewModel<TransferActivity, ActivityTransferLayoutBinding> {


    public TransferViewModel(TransferActivity activity, ActivityTransferLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {

    }

    /**
     *   划转请求
     */
    public void transfer(String token, String coinId, String accountType, String number) {
        FaceApiMbr.getV1ApiServiceMbr().transfer(token, coinId, accountType, number)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(String str) {
                        try {
                            Toast.makeText(getActivity(), "划转成功", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post("Transfer");
                            getActivity().finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     *    划转 获取币种类型
     */
    public void getOtcCoins(String token, final String accountType) {
        FaceApiMbr.getV1ApiServiceMbr().getOTCCoins(token)
                .compose(RxSchedulers.<ApiModel<FB_BBEntity>>io_main())
                .subscribe(new RxObserver<FB_BBEntity>(getActivity(), getActivity().Tag, false) {
                    @Override
                    public void onSuccess(FB_BBEntity entity) {
                        try {
                            Log.e("cjh>>>", "划转获取法币 币种选择" + new Gson().toJson(entity));
                            for (int i = 0; i < entity.getListdetail().size(); i++) {
                                if (entity.getListdetail().get(i).getCoin_id().equals(getActivity().coinId)){
                                    getActivity().fbBbEntity = entity.getListdetail().get(i);
                                    getBinding().transferSelectCoin.setText(entity.getListdetail().get(i).getCoin_name());
                                    getBinding().transferSelectCoin2.setText(entity.getListdetail().get(i).getCoin_name());
                                    getBinding().transferNumbEdit.setText("");
                                    if (accountType.equals("fb_type")){
                                        getBinding().transferAvailableNumb.setText(Double.parseDouble(entity.getListdetail().get(i).getOtcAavailable())+ " " +entity.getListdetail().get(i).getCoin_name());
                                    }else {
                                        getBinding().transferAvailableNumb.setText(Double.parseDouble(entity.getListdetail().get(i).getAvailable())+ " " +entity.getListdetail().get(i).getCoin_name());
                                    }
                                }
                            }
                            if (EmptyUtil.isEmpty(getActivity().fbBbEntity)){
                                new MessageWindow(getActivity(), "该币种无法划转!").showAtBottom(getBinding().transferAvailableNumb);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

//    /**
//     *    OTC 获取账户币种余额 法币
//     */
//    public void getAvailable(String token, String orderId) {
//        FaceApiMbr.getV1ApiServiceMbr().getAvailable(token, orderId)
//                .compose(RxSchedulers.<ApiModel<String>>io_main())
//                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, true) {
//                    @Override
//                    public void onSuccess(String str) {
//                        try {
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//    }
//
//    /**
//     *    OTC 获取账户币种余额 币币
//     */
//    public void getBalance(String token, String orderId) {
//        FaceApiTest.getV1ApiServiceTest().getBalance(token, orderId)
//                .compose(RxSchedulers.<ApiModel<String>>io_main())
//                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, true) {
//                    @Override
//                    public void onSuccess(String str) {
//                        try {
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//    }

}
