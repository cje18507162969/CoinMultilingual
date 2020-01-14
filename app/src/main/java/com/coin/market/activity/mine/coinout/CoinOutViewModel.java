package com.coin.market.activity.mine.coinout;

import android.text.TextUtils;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.databinding.ActivityCoinOutLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import okhttp3.RequestBody;
import teng.wang.comment.SPConstants;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.api.RequestBodyUtils;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.AccountsDTO;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.CoinBalanceModel;
import teng.wang.comment.model.CoinItemAddressModel;
import teng.wang.comment.model.CoinListEntity;
import teng.wang.comment.model.UserInfosDTO;
import teng.wang.comment.model.UsersEntity;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.DataKeeper;
import teng.wang.comment.utils.ZXingUtils;
import teng.wang.comment.utils.log.Log;
import teng.wang.comment.widget.MessageWindow;

/**
 * @author: Lenovo
 * @date: 2019/7/17
 * @info: 提币 ViewModel
 */
public class CoinOutViewModel extends BaseActivityViewModel<CoinOutActivity, ActivityCoinOutLayoutBinding> {

    public CoinOutViewModel(CoinOutActivity activity, ActivityCoinOutLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
//        setCoinAdapter();
//        setData();
        if (!EmptyUtil.isEmpty(getActivity().CoinId)) {
            getCoinBalance(FaceApplication.getToken(), getActivity().CoinId);
            getUserInfo(FaceApplication.getToken());
        } else {
            getCoinList();
        }
    }

    /**
     * 获取提币  币种类型
     */
    public void getCoinList() {
//
    }

    /**
     * 获取用户信息
     * 判断是否已经身份认证
     */
    public void getUserInfo(final String token) {
        FaceApiTest.getV1ApiServiceTest().userInfo(token)
                .compose(RxSchedulers.<ApiModel<UserInfosDTO>>io_main())
                .subscribe(new RxObserver<UserInfosDTO>(getActivity(), getActivity().Tag, false) {
                    @Override
                    public void onSuccess(UserInfosDTO data) {
                        try {
                            Log.e("cjh>>> token:" + token, "userInfo:" + new Gson().toJson(data));
                            UsersEntity model = FaceApplication.getUserInfoModel();
                            model.setMobile(data.getMobile()+"");
                            model.setAuthentication(data.getAuthentication());
                            DataKeeper.put(getActivity(), SPConstants.USERINFOMODEL,model);
                            if (!TextUtils.isEmpty(data.getMobile())){

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrors(int eCode) {
                        // 如果token过期 让用户重新登录  重新获取token
                        if (eCode == 9001) {
                        }
                    }
                });
    }
    /**
     * 获取单个币种余额
     */
    public void getCoinBalance(String token, String coinId) {


        FaceApiTest.getV1ApiServiceTest().getCoinBalance(token, coinId)
                .compose(RxSchedulers.<ApiModel<AccountsDTO>>io_main())
                .subscribe(new RxObserver<AccountsDTO>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(AccountsDTO data) {
                        Log.e("cjh>>>", "CoinBalanceModel:" + new Gson().toJson(data));
                        try {
                            getActivity().coinAllMoney = data.getAvailable().doubleValue();
//                            getBinding().coinServiceCharge.setText(data.fee + "");
                            getBinding().allMoneyMinText.setText(
                                    getActivity().getResources().getString(R.string.transaction_fb_transact_assets_account_available_text)+"："
                                            + StringUtils.double2String(data.getAvailable().doubleValue(), 4) + " " + getActivity().coinName);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 获取单个币种信息
     */
    public void getCoinInfo(String token, String coinId) {
        FaceApiTest.getV1ApiServiceTest().getCoinInfo(token, coinId)
                .compose(RxSchedulers.<ApiModel<AccountsDTO>>io_main())
                .subscribe(new RxObserver<AccountsDTO>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(AccountsDTO data) {
                        Log.e("cjh>>>", "CoinInfo:" + new Gson().toJson(data));
                        try {
                            if (null!=data){
                                getBinding().coinServiceCharge.setText(data.fee + "");
                                getBinding().tvExportinfo.setText(getActivity().getResources().getString(R.string.mine_coin_out_text_content)
                                        + data.exportMin.intValue() + "USDT");
//                                if (data.exportMin.doubleValue()>0) {
//                                    getBinding().coinOutMin.setText(
//                                            getActivity().getResources().getString(R.string.mine_coin_minnum)+"："
//                                            + data.exportMin.intValue() + "");
//                                }

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 提币 不需要短信验证
     */
    public void getGetCoin(String token, String coinId, String address, String numb) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("address", address)
                .addParam("coinId", coinId)
                .addParam("number", numb)
                .builder();
        FaceApiTest.getV1ApiServiceTest().getGetCoin(token, body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(String data) {
                        try {
                            EventBus.getDefault().post("Get_Coin");
                            EventBus.getDefault().post("Transfer");
                            Toast.makeText(getActivity(), "提币申请成功！", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 提币 短信验证
     */
    public void getGetCoin(String token, String coinId, String address, String numb, String code) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("address", address)
                .addParam("coinId", coinId)
                .addParam("number", numb)
                .addParam("code", code)
                .builder();
        FaceApiTest.getV1ApiServiceTest().getGetCoin(token, body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(String data) {
                        try {
                            EventBus.getDefault().post("Get_Coin");
                            EventBus.getDefault().post("Transfer");
                            Toast.makeText(getActivity(), "提币申请成功！", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

//                    @Override
//                    public void onErrors(int eCode) {
//                        super.onErrors(eCode);
//                        Toast.makeText(getActivity(), "code:" + eCode, Toast.LENGTH_SHORT).show();
//                    }
                });
    }

}
