package com.coin.market.activity.transaction.mytransact;

import android.view.View;
import android.widget.Toast;

import com.coin.market.databinding.ActivityMyTransactLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import teng.wang.comment.api.FaceApiMbr;
import teng.wang.comment.api.RequestBodyUtils;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.ParamsEntry;
import teng.wang.comment.model.PayMethodsBean;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.log.Log;

/**
 * @author: Lenovo
 * @date: 2019/8/5
 * @info: 我要买或卖币  发布
 */
public class MyTransactViewModel extends BaseActivityViewModel <MyTransactActivity, ActivityMyTransactLayoutBinding>{

    public List<PayMethodsBean> payList = new ArrayList<PayMethodsBean>();

    public MyTransactViewModel(MyTransactActivity activity, ActivityMyTransactLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        getPayMentMethod(FaceApplication.getToken(), 0); //获取用户支付方式
    }

    /**
     *    获取收款方式
     */
    public void getPayMentMethod(String token, int isShow) {
        FaceApiMbr.getV1ApiServiceMbr().getPayMentMethod(token)
                .compose(RxSchedulers.<ApiModel<List<PayMethodsBean>>>io_main())
                .subscribe(new RxObserver<List<PayMethodsBean>>(getActivity(), getActivity().Tag, isShow==1? true:false) {
                    @Override
                    public void onSuccess(List<PayMethodsBean> list) {
                        try {
                            if (list.size()>0){
                                Log.e("cjh>>>", "支付方式：PayMethodsBean" + new Gson().toJson(list));
                                setPayMethod(list);
                                payList = list;
                                getBinding().noPayLayout.setVisibility(View.GONE);
                                getBinding().yesPayLayout.setVisibility(View.VISIBLE);
                            }else {
                                getBinding().noPayLayout.setVisibility(View.VISIBLE);
                                getBinding().yesPayLayout.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     *    发布
     */
    public void publishSubmit(String token, ParamsEntry entry, int isShow) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("coinId", entry.getId())  //币id
                .addParam("price", entry.getPrice())  //单价
                .addParam("number", entry.getNumber())  //数量
                .addParam("remark", entry.getRemark())  //备注
                .addParam("currencyName", "CNY")  // 单位
                .addParam("otcTradeType", entry.getType())  //类型（买/卖）
                .addParam("userPay", entry.getUserPay())  //支付方式 数组格式
                .addParam("minTradeMoney", entry.getMinMoney())  //最小
                .addParam("maxTradeMoney", entry.getMaxMoney())  //最大
                .builder();
        FaceApiMbr.getV1ApiServiceMbr().publishOrder(token, body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, isShow==1? true:false) {
                    @Override
                    public void onSuccess(String str) {
                        try {
                            Toast.makeText(getActivity(), "发布成功", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post("publish");
                            getActivity().finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    // 设置支付方式 或没有支付方式的处理
    private void setPayMethod(List<PayMethodsBean> list){
        if (!EmptyUtil.isEmpty(list)) {
            // 如果有支付方式 显示设置
            getBinding().noPayLayout.setVisibility(View.GONE);
            getBinding().myTransactAlipayLayout.setVisibility(View.GONE);
            getBinding().myTransactWeixLayout.setVisibility(View.GONE);
            getBinding().myTransactBankLayout.setVisibility(View.GONE);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPaymentMethod().equals("alipay")){
                    //支付宝
                    getBinding().myTransactAlipayLayout.setVisibility(View.VISIBLE);
                    getActivity().alipay = 1;
                }
                if (list.get(i).getPaymentMethod().equals("wxpay")){
                    //微信
                    getBinding().myTransactWeixLayout.setVisibility(View.VISIBLE);
                    getActivity().weix = 1;
                }
                if (list.get(i).getPaymentMethod().equals("unionpay")){
                    //银行卡
                    getBinding().myTransactBankLayout.setVisibility(View.VISIBLE);
                    getActivity().bank = 1;
                }
            }
        }else {
            // 没有支付方式  去添加支付方式
            getBinding().noPayLayout.setVisibility(View.VISIBLE);
            getBinding().myTransactWeixLayout.setVisibility(View.GONE);
        }
    }

}
