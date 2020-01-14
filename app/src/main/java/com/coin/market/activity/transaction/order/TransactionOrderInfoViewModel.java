package com.coin.market.activity.transaction.order;

import android.view.View;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.databinding.ActivityTransactionOrderInfoLayoutBinding;
import com.coin.market.util.DateUtils;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.PhoneUtils;
import com.coin.market.util.StringUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.RequestBody;
import teng.wang.comment.api.FaceApiMbr;
import teng.wang.comment.api.RequestBodyUtils;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.OTCOrderInfoEntity;
import teng.wang.comment.model.PayMethodsBean;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.log.Log;

/**
 * @author: Lenovo
 * @date: 2019/8/1
 * @info: 交易订单详情 ViewModel
 */
public class TransactionOrderInfoViewModel extends BaseActivityViewModel<TransactionOrderInfoActivity, ActivityTransactionOrderInfoLayoutBinding> {

    // 总共6种状态  买家3种  卖家3种   买家： 1、买家下单付款  2、待放行  3、已完成
    // 卖家： 1、待付款  2、买家已打款 放行  3、出售完成 订单完成
    public int status;
    private long endTime;
    private Timer timer;
    private String time1, time2;
    public OTCOrderInfoEntity infoEntity;
    public PayMethodsBean payMethod;  // 支付方式

    public TransactionOrderInfoViewModel(TransactionOrderInfoActivity activity, ActivityTransactionOrderInfoLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        getOTCDataList(FaceApplication.getToken(), getActivity().orderId, 1);
    }

    /**
     *   取消订单
     */
    public void cancelOrder(String token, String orderId, int showType) {
        FaceApiMbr.getV1ApiServiceMbr().cancelOrder(token, orderId)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, showType == 1 ? true : false) {
                    @Override
                    public void onSuccess(String str) {
                        try {
                            Toast.makeText(getActivity(), "订单已取消！", Toast.LENGTH_SHORT).show();
                            getActivity().recreate();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     *   申诉
     */
    public void appeal(String token, String orderId, int showType) {
        FaceApiMbr.getV1ApiServiceMbr().appeal(token, orderId)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, showType == 1 ? true : false) {
                    @Override
                    public void onSuccess(String str) {
                        try {
                            Toast.makeText(getActivity(), "已申诉！", Toast.LENGTH_SHORT).show();
                            getOTCDataList(FaceApplication.getToken(), getActivity().orderId, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     *   我作为买家 确认付款  我已付款成功
     */
    public void verifyTrade(String token, String orderId, int showType) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("Id", orderId)
                .builder();
        FaceApiMbr.getV1ApiServiceMbr().verifyTrade(token, body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, showType == 1 ? true : false) {
                    @Override
                    public void onSuccess(String str) {
                        try {
                            Toast.makeText(getActivity(), "已付款成功！", Toast.LENGTH_SHORT).show();
                            getOTCDataList(FaceApplication.getToken(), getActivity().orderId, 0);
                            //getActivity().recreate();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     *    作为卖家 已收款 放行
     */
    public void otcSellVerify(String token, String orderId, int showType) {
        FaceApiMbr.getV1ApiServiceMbr().otcSellVerify(token, orderId)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, showType == 1 ? true : false) {
                    @Override
                    public void onSuccess(String str) {
                        try {
                            Toast.makeText(getActivity(), "已放行！", Toast.LENGTH_SHORT).show();
                            getOTCDataList(FaceApplication.getToken(), getActivity().orderId, 0);
                            //getActivity().recreate();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 详情Info
     */
    public void getOTCDataList(String token, String orderId, int showType) {
        FaceApiMbr.getV1ApiServiceMbr().getOrderInfo(token, orderId)
                .compose(RxSchedulers.<ApiModel<OTCOrderInfoEntity>>io_main())
                .subscribe(new RxObserver<OTCOrderInfoEntity>(getActivity(), getActivity().Tag, showType == 1 ? true : false) {
                    @Override
                    public void onSuccess(OTCOrderInfoEntity entity) {
                        try {
                            infoEntity = entity;
                            getActivity().type = entity.getOtcTradeType();
                            Log.e("cjh>>>", "OTCOrderInfoEntity:" + new Gson().toJson(entity));
                            int position = 0;
                            if (entity.getOtcTradeType().equals("buy")) {
                                switch (entity.getOtcStatus()) {
                                    case "noalreadypay":
                                        position = 0;
                                        break;
                                    case "alreadysuccess":
                                        position = 2;
                                        break;
                                    case "alreadycancel":
                                        position = 6;
                                        break;
                                    case "waitverify":
                                        position = 1;
                                        break;
                                    case "order_inthecomplaint": //卖家申诉中
                                        position = 8;
                                        break;
                                    default:
                                        break;
                                }
                            } else if (entity.getOtcTradeType().equals("sell")) {
                                switch (entity.getOtcStatus()) {
                                    case "noalreadypay":
                                        position = 3;
                                        break;
                                    case "alreadysuccess":
                                        position = 5;
                                        break;
                                    case "alreadycancel":
                                        position = 7;
                                        break;
                                    case "waitverify":
                                        position = 4;
                                        break;
                                    case "order_inthecomplaint": //卖家申诉中
                                        position = 9;
                                        break;
                                    default:
                                        break;
                                }
                            }
                            setInfoUi(entity);
                            setTypeUi(position, entity);
                            getBinding().orderInfoTopLayout.setVisibility(View.VISIBLE);
                            getBinding().orderInfoBottomLayout.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    // 详情UI  交易方式信息
    private void setInfoUi(OTCOrderInfoEntity entity) {
        endTime = DateUtils.dayDiff3(entity.getExpiredAt(), "yyyy-MM-dd HH:mm:ss");
        getBinding().orderInfoCoinName.setText(entity.getCoinName());//币名字
        getBinding().orderInfoPrice.setText("¥ " + StringUtils.double2String(entity.getPrice(), 2));// 单价
        getBinding().orderInfoNumb.setText(StringUtils.double2String(entity.getNumber(), 8) + " " + entity.getCoinName());// 数量 + 单位
        getBinding().orderInfoAllPrice.setText(StringUtils.double2String(entity.getAmount(), 2) + "");
        if (entity.getPayMethods().size() > 0) {
            setPay(entity.getPayMethods().get(0));
        }
    }

    // 显示界面支付方式
    public void setPay(PayMethodsBean bean) {
        this.payMethod = bean;
        switch (bean.getPaymentMethod()) {
            case "unionpay":
                getBinding().orderInfoItem3Text.setText(bean.getPayAccount());
                getBinding().orderInfoPayText.setText("银行卡");
                getBinding().orderInfoPayImg.setImageResource(R.drawable.transaction_buy_bankcard);
                getBinding().orderInfoItem2Layout.setVisibility(View.GONE);
                getBinding().orderInfoItem3Title.setText("银行卡号");
                getBinding().orderInfoItem1Text.setText(bean.getPayName());
                break;
            case "alipay":
                getBinding().orderInfoItem3Text.setText(bean.getPayAccount());
                getBinding().orderInfoPayText.setText("支付宝");
                getBinding().orderInfoPayImg.setImageResource(R.drawable.transaction_buy_alipay);
                getBinding().orderInfoItem2Layout.setVisibility(View.VISIBLE);
                getBinding().orderInfoItem3Title.setText("支付宝账号");
                getBinding().orderInfoItem1Text.setText(bean.getPayName());
                break;
            case "wxpay":
                getBinding().orderInfoItem3Text.setText(bean.getPayAccount());
                getBinding().orderInfoPayText.setText("微信");
                getBinding().orderInfoPayImg.setImageResource(R.drawable.transaction_buy_wechat);
                getBinding().orderInfoItem2Layout.setVisibility(View.VISIBLE);
                getBinding().orderInfoItem3Title.setText("微信号");
                getBinding().orderInfoItem1Text.setText(bean.getPayName());
                break;
            default:
                break;
        }
    }

    private void setTypeUi(int status, OTCOrderInfoEntity entity) {
        this.status = status;
        switch (status) {
            case 0:
                // 0、买家下单付款
                time1 = "请在";
                time2 = "内付款给卖家";
                getBinding().orderInfoTypeText.setText("请付款");
                getBinding().orderInfoItem4Layout.setVisibility(View.GONE);
                getBinding().orderInfoBottomItem1Title.setText("订单号");
                getBinding().orderInfoBottomItem2Title.setText("付款参考号");
                getBinding().orderInfoTipsText.setText("在转账过程中请勿备注BTC、USDT、火信等信息，防止汇款被拦截、银行卡被冻结等问题");
                getBinding().orderInfoCancelButton.setText("取消订单");
                getBinding().orderInfoConfirmButton.setText("我已付款成功");
                getBinding().orderInfoItem1Title.setText("收款人");
                getBinding().orderInfoItem1Text.setText(entity.getSellerName()); //收款人姓名
                setPay(entity.getPayMethods().get(0));
                getBinding().orderInfoBottomItem1Text.setText(entity.getOrderNumber());
                getBinding().orderInfoBottomItem2Text.setText(entity.getOtcTradeId() + "");
                startTime(entity);
                break;
            case 1:
                // 1、待放行
                time1 = "预计在";
                time2 = "内收到资产";
                getBinding().orderInfoTypeText.setText("待放行");
                getBinding().orderInfoPayLayout.setVisibility(View.GONE);
                getBinding().orderInfoPhoneLayout.setVisibility(View.VISIBLE);
                getBinding().orderInfoItem1Title.setText("卖家昵称");
                getBinding().orderInfoItem1Text.setText(entity.getSellerName());
                getBinding().orderInfoItem2Layout.setVisibility(View.GONE);
                getBinding().orderInfoItem3Layout.setVisibility(View.GONE);
                getBinding().orderInfoItem4Layout.setVisibility(View.GONE);
                getBinding().orderInfoItem5Layout.setVisibility(View.GONE);
                getBinding().orderInfoLine.setVisibility(View.GONE);
                getBinding().orderInfoContentLayout.setVisibility(View.GONE);
                getBinding().orderInfoLine.setVisibility(View.GONE);
                getBinding().orderInfoTipsText.setText("93.8%的用户会在5分钟内收到资产");
                getBinding().orderInfoConfirmButton.setText("申诉");
                getBinding().orderInfoBottomItem1Title.setText("订单号");
                getBinding().orderInfoBottomItem2Title.setText("付款参考号");
                getBinding().orderInfoBottomItem1Text.setText(entity.getOrderNumber());
                getBinding().orderInfoBottomItem2Text.setText(entity.getOtcTradeId() + "");
                getBinding().orderInfoBottomItem3Layout.setVisibility(View.VISIBLE);
                getBinding().orderInfoBottomItem3Text.setText(entity.getCreatedAt());
                getBinding().orderInfoCancelButton.setEnabled(false);
                getBinding().orderInfoCancelButton.setTextColor(getActivity().getResources().getColor(R.color.white));
                startTime(entity);
                break;
            case 2:
                // 2、已完成
                getBinding().orderInfoTypeImg.setImageResource(R.drawable.transaction_complete);
                getBinding().orderInfoTimeText.setText("该订单已完成");
                getBinding().orderInfoTypeText.setText("已完成");
                getBinding().orderInfoPayLayout.setVisibility(View.GONE);
                getBinding().orderInfoPhoneLayout.setVisibility(View.GONE);
                getBinding().orderInfoItem1Title.setText("卖家昵称");
                getBinding().orderInfoItem1Text.setText(entity.getSellerName());
                getBinding().orderInfoItem1Img2.setVisibility(View.GONE);
                getBinding().orderInfoItem2Layout.setVisibility(View.GONE);
                getBinding().orderInfoItem2Img1.setImageResource(R.drawable.transaction_copy);
                getBinding().orderInfoItem3Text.setVisibility(View.VISIBLE);
                getBinding().orderInfoItem3Title.setText("订单号");
                getBinding().orderInfoItem3Text.setText(entity.getOrderNumber());
                getBinding().orderInfoItem4Title.setText("付款参考号");
                getBinding().orderInfoItem4Text.setText(entity.getOtcTradeId());
                getBinding().orderInfoItem4Img1.setVisibility(View.VISIBLE);
                getBinding().orderInfoItem5Layout.setVisibility(View.VISIBLE);
                getBinding().orderInfoItem5Title.setText("下单时间");
                getBinding().orderInfoItem5Text.setText(entity.getCreatedAt());
                getBinding().orderInfoContentLayout.setVisibility(View.GONE);
                getBinding().orderInfoBottomLayout.setVisibility(View.GONE);
                getBinding().orderInfoLine.setVisibility(View.GONE);
                getBinding().orderInfoButtonLayout.setVisibility(View.GONE);
                getBinding().orderInfoBottomItem1Layout.setVisibility(View.GONE);
                getBinding().orderInfoBottomItem2Layout.setVisibility(View.GONE);
                getBinding().orderInfoTipsText.setText("Topcoin法币账户与交易账户之间划转手续费全免");
                break;
            case 3:
                // 3、  卖家： 1、待付款
                time1 = "预计在";
                time2 = "内收到买家付款";
                getBinding().orderInfoTypeText.setText("待付款");
                getBinding().orderInfoPhoneLayout.setVisibility(View.VISIBLE);
                getBinding().orderInfoItem1Title.setText("买家昵称"); //
                getBinding().orderInfoItem1Text.setText(entity.getBuyerName()); // 秦时明月
                getBinding().orderInfoTipsText.setVisibility(View.GONE);
                getBinding().orderInfoCancelButton.setText("申诉");
                getBinding().orderInfoConfirmButton.setText("放行" + entity.getCoinName()); // 放行+ CoinName
                getBinding().orderInfoConfirmButton.setEnabled(false);
                getBinding().orderInfoCancelButton.setEnabled(false);
                getBinding().orderInfoCancelButton.setTextColor(getActivity().getResources().getColor(R.color.white));
                getBinding().orderInfoConfirmButton.setBackgroundResource(R.drawable.shape_gray_button_layout);

                getBinding().orderInfoBottomLayout.setVisibility(View.VISIBLE);
                getBinding().orderInfoPayLayout.setVisibility(View.GONE);
                getBinding().orderInfoContentLayout.setVisibility(View.GONE);
                getBinding().orderInfoItem2Layout.setVisibility(View.GONE);
                getBinding().orderInfoItem3Layout.setVisibility(View.GONE);
                getBinding().orderInfoItem4Layout.setVisibility(View.GONE);
                getBinding().orderInfoItem5Layout.setVisibility(View.GONE);
                getBinding().orderInfoLine.setVisibility(View.GONE);
                getBinding().orderInfoBottomItem1Title.setText("订单号");
                getBinding().orderInfoBottomItem2Title.setText("付款参考号");
                getBinding().orderInfoBottomItem1Text.setText(entity.getOrderNumber());
                getBinding().orderInfoBottomItem2Text.setText(entity.getOtcTradeId() + "");
                getBinding().orderInfoBottomItem3Layout.setVisibility(View.VISIBLE);
                getBinding().orderInfoBottomItem3Text.setText(entity.getCreatedAt());
                startTime(entity);
                break;
            case 4:
                // 4、  卖家： 1、请放行
                getBinding().orderInfoTypeText.setText("请放行");
                getBinding().orderInfoTimeText.setText("请查收对方付款");
                getBinding().orderInfoPayLayout.setVisibility(View.GONE);
                getBinding().orderInfoPhoneLayout.setVisibility(View.VISIBLE);
                getBinding().orderInfoTipsText.setVisibility(View.GONE);
                getBinding().orderInfoCancelButton.setVisibility(View.GONE);
                getBinding().orderInfoItem1Title.setText("买家昵称");
                getBinding().orderInfoItem1Text.setText(entity.getBuyerName()); //收款人姓名
                getBinding().orderInfoPayLayout.setVisibility(View.GONE);
                getBinding().orderInfoContentLayout.setVisibility(View.GONE);
                getBinding().orderInfoItem2Layout.setVisibility(View.GONE);
                getBinding().orderInfoItem3Layout.setVisibility(View.GONE);
                getBinding().orderInfoItem4Layout.setVisibility(View.GONE);
                getBinding().orderInfoItem5Layout.setVisibility(View.GONE);
                getBinding().orderInfoLine.setVisibility(View.GONE);
                getBinding().orderInfoBottomItem1Title.setText("订单号");
                getBinding().orderInfoBottomItem2Title.setText("付款参考号");
                getBinding().orderInfoBottomItem1Text.setText(entity.getOrderNumber());
                getBinding().orderInfoBottomItem2Text.setText(entity.getOtcTradeId() + "");
                getBinding().orderInfoBottomItem3Layout.setVisibility(View.VISIBLE);
                getBinding().orderInfoBottomItem3Text.setText(entity.getCreatedAt());
                getBinding().orderInfoBottomLayout.setVisibility(View.VISIBLE);
                getBinding().orderInfoConfirmButton.setText("放行" + entity.getCoinName());
                getBinding().orderInfoCancelButton.setEnabled(false);
                getBinding().orderInfoCancelButton.setTextColor(getActivity().getResources().getColor(R.color.white));
                break;
            case 5:
                // 5、  卖家： 1、已完成
                getBinding().orderInfoTypeImg.setImageResource(R.drawable.transaction_complete);
                getBinding().orderInfoTimeText.setText("该订单已完成");
                getBinding().orderInfoTypeText.setText("已完成");
                getBinding().orderInfoPayLayout.setVisibility(View.GONE);
                getBinding().orderInfoPhoneLayout.setVisibility(View.GONE);
                getBinding().orderInfoItem1Title.setText("买家昵称");
                getBinding().orderInfoItem1Text.setText(entity.getBuyerName());
                getBinding().orderInfoItem1Img2.setVisibility(View.GONE);
                getBinding().orderInfoItem2Layout.setVisibility(View.GONE);
                getBinding().orderInfoItem2Img1.setImageResource(R.drawable.transaction_copy);
                getBinding().orderInfoItem3Text.setVisibility(View.VISIBLE);
                getBinding().orderInfoItem3Title.setText("订单号");
                getBinding().orderInfoItem3Text.setText(entity.getOrderNumber());
                getBinding().orderInfoItem4Title.setText("付款参考号");
                getBinding().orderInfoItem4Text.setText(entity.getOtcTradeId());
                getBinding().orderInfoItem4Img1.setVisibility(View.VISIBLE);
                getBinding().orderInfoItem5Layout.setVisibility(View.VISIBLE);
                getBinding().orderInfoItem5Title.setText("下单时间");
                getBinding().orderInfoItem5Text.setText(entity.getCreatedAt());
                getBinding().orderInfoContentLayout.setVisibility(View.GONE);
                getBinding().orderInfoBottomLayout.setVisibility(View.GONE);
                getBinding().orderInfoLine.setVisibility(View.GONE);
                getBinding().orderInfoButtonLayout.setVisibility(View.GONE);
                getBinding().orderInfoBottomItem1Layout.setVisibility(View.GONE);
                getBinding().orderInfoBottomItem2Layout.setVisibility(View.GONE);
                getBinding().orderInfoTipsText.setText("Topcoin法币账户与交易账户之间划转手续费全免");
                break;
            case 6:
                // 6、  买家： 已取消
                getBinding().orderInfoTypeText.setText("已取消");
                getBinding().orderInfoTimeText.setText("该订单已取消");
                getBinding().orderInfoItem4Layout.setVisibility(View.GONE);
                getBinding().orderInfoBottomItem1Title.setText("订单号");
                getBinding().orderInfoBottomItem2Title.setText("付款参考号");
                getBinding().orderInfoBottomItem1Text.setText(entity.getOrderNumber());
                getBinding().orderInfoBottomItem2Text.setText(entity.getOtcTradeId() + "");
                getBinding().orderInfoTipsText.setVisibility(View.GONE);
                getBinding().orderInfoCancelButton.setText("取消订单");
                getBinding().orderInfoConfirmButton.setText("我已付款成功");
                getBinding().orderInfoItem1Title.setText("卖家昵称");
                getBinding().orderInfoItem1Text.setText(entity.getSellerName()); //收款人姓名
                getBinding().orderInfoPayLayout.setVisibility(View.GONE);
                getBinding().orderInfoContentLayout.setVisibility(View.GONE);
                getBinding().orderInfoItem2Layout.setVisibility(View.GONE);
                getBinding().orderInfoItem3Layout.setVisibility(View.GONE);
                getBinding().orderInfoItem4Layout.setVisibility(View.GONE);
                getBinding().orderInfoItem5Layout.setVisibility(View.GONE);
                getBinding().orderInfoLine.setVisibility(View.GONE);
                getBinding().orderInfoBottomItem3Layout.setVisibility(View.VISIBLE);
                getBinding().orderInfoBottomItem3Text.setText(entity.getCreatedAt());
                getBinding().orderInfoButtonLayout.setVisibility(View.GONE);
                break;
            case 7:
                // 7、  卖家： 已取消
                getBinding().orderInfoTypeText.setText("已取消");
                getBinding().orderInfoTimeText.setText("该订单已取消");
                getBinding().orderInfoBottomItem1Title.setText("订单号");
                getBinding().orderInfoBottomItem2Title.setText("付款参考号");
                getBinding().orderInfoBottomItem1Text.setText(entity.getOrderNumber());
                getBinding().orderInfoBottomItem2Text.setText(entity.getOtcTradeId() + "");
                getBinding().orderInfoTipsText.setVisibility(View.GONE);
                getBinding().orderInfoItem1Title.setText("买家昵称");
                getBinding().orderInfoItem1Text.setText(entity.getBuyerName()); //收款人姓名
                getBinding().orderInfoPayLayout.setVisibility(View.GONE);
                getBinding().orderInfoContentLayout.setVisibility(View.GONE);
                getBinding().orderInfoItem2Layout.setVisibility(View.GONE);
                getBinding().orderInfoItem3Layout.setVisibility(View.GONE);
                getBinding().orderInfoItem4Layout.setVisibility(View.GONE);
                getBinding().orderInfoItem5Layout.setVisibility(View.GONE);
                getBinding().orderInfoLine.setVisibility(View.GONE);
                getBinding().orderInfoBottomItem3Layout.setVisibility(View.VISIBLE);
                getBinding().orderInfoBottomItem3Text.setText(entity.getCreatedAt());
                getBinding().orderInfoBottomLayout.setVisibility(View.GONE);
                getBinding().orderInfoButtonLayout.setVisibility(View.GONE);
                break;
            case 8:
                // 8、  买家： 申诉中
                getBinding().orderInfoTypeText.setText("申诉中");
                getBinding().orderInfoTimeText.setText("该订单已申诉");
                getBinding().orderInfoItem4Layout.setVisibility(View.GONE);
                getBinding().orderInfoBottomItem1Title.setText("订单号");
                getBinding().orderInfoBottomItem2Title.setText("付款参考号");
                getBinding().orderInfoBottomItem1Text.setText(entity.getOrderNumber());
                getBinding().orderInfoBottomItem2Text.setText(entity.getOtcTradeId() + "");
                getBinding().orderInfoTipsText.setVisibility(View.GONE);
                getBinding().orderInfoCancelButton.setText("取消订单");
                getBinding().orderInfoConfirmButton.setText("我已付款成功");
                getBinding().orderInfoItem1Title.setText("卖家昵称");
                getBinding().orderInfoItem1Text.setText(entity.getSellerName()); //收款人姓名
                getBinding().orderInfoPayLayout.setVisibility(View.GONE);
                getBinding().orderInfoContentLayout.setVisibility(View.GONE);
                getBinding().orderInfoItem2Layout.setVisibility(View.GONE);
                getBinding().orderInfoItem3Layout.setVisibility(View.GONE);
                getBinding().orderInfoItem4Layout.setVisibility(View.GONE);
                getBinding().orderInfoItem5Layout.setVisibility(View.GONE);
                getBinding().orderInfoLine.setVisibility(View.GONE);
                getBinding().orderInfoBottomItem3Layout.setVisibility(View.VISIBLE);
                getBinding().orderInfoBottomItem3Text.setText(entity.getCreatedAt());
                getBinding().orderInfoBottomLayout.setVisibility(View.VISIBLE);
                getBinding().orderInfoButtonLayout.setVisibility(View.GONE);

                getBinding().orderInfoButtonLayout.setVisibility(View.VISIBLE);
                getBinding().orderInfoCancelButton.setVisibility(View.GONE);
                getBinding().orderInfoTipsText.setVisibility(View.VISIBLE);
                getBinding().orderInfoTipsText.setText("Topcoin法币账户与交易账户之间划转手续费全免");
                getBinding().orderInfoConfirmButton.setText("去反馈");

                break;
            case 9:
                // 9、  卖家： 申诉中
//                getBinding().orderInfoTypeText.setText("申诉中");
//                getBinding().orderInfoTimeText.setText("该订单已申诉");
//                getBinding().orderInfoItem4Layout.setVisibility(View.GONE);
//                getBinding().orderInfoBottomItem1Title.setText("订单号");
//                getBinding().orderInfoBottomItem2Title.setText("付款参考号");
//                getBinding().orderInfoBottomItem1Text.setText(entity.getOrderNumber());
//                getBinding().orderInfoBottomItem2Text.setText(entity.getOtcTradeId() + "");
//                getBinding().orderInfoTipsText.setVisibility(View.GONE);
//                getBinding().orderInfoCancelButton.setText("取消订单");
//                getBinding().orderInfoConfirmButton.setText("我已付款成功");
//                getBinding().orderInfoItem1Title.setText("买家昵称");
//                getBinding().orderInfoItem1Text.setText(entity.getBuyerName()); //打款人姓名
//                getBinding().orderInfoPayLayout.setVisibility(View.GONE);
//                getBinding().orderInfoContentLayout.setVisibility(View.GONE);
//                getBinding().orderInfoItem2Layout.setVisibility(View.GONE);
//                getBinding().orderInfoItem3Layout.setVisibility(View.GONE);
//                getBinding().orderInfoItem4Layout.setVisibility(View.GONE);
//                getBinding().orderInfoItem5Layout.setVisibility(View.GONE);
//                getBinding().orderInfoLine.setVisibility(View.GONE);
//                getBinding().orderInfoBottomItem3Layout.setVisibility(View.VISIBLE);
//                getBinding().orderInfoBottomItem3Text.setText(entity.getCreatedAt());
//                getBinding().orderInfoBottomLayout.setVisibility(View.VISIBLE);
//                getBinding().orderInfoButtonLayout.setVisibility(View.GONE);

                getBinding().orderInfoTypeText.setText("申诉中");
                getBinding().orderInfoTimeText.setText("订单申诉中");
                getBinding().orderInfoPayLayout.setVisibility(View.GONE);
                getBinding().orderInfoPhoneLayout.setVisibility(View.VISIBLE);
                getBinding().orderInfoCancelButton.setVisibility(View.GONE);
                getBinding().orderInfoItem1Title.setText("买家昵称");
                getBinding().orderInfoItem1Text.setText(entity.getBuyerName()); //收款人姓名
                getBinding().orderInfoPayLayout.setVisibility(View.GONE);
                getBinding().orderInfoContentLayout.setVisibility(View.GONE);
                getBinding().orderInfoItem2Layout.setVisibility(View.GONE);
                getBinding().orderInfoItem3Layout.setVisibility(View.GONE);
                getBinding().orderInfoItem4Layout.setVisibility(View.GONE);
                getBinding().orderInfoItem5Layout.setVisibility(View.GONE);
                getBinding().orderInfoLine.setVisibility(View.GONE);
                getBinding().orderInfoBottomItem1Title.setText("订单号");
                getBinding().orderInfoBottomItem2Title.setText("付款参考号");
                getBinding().orderInfoBottomItem1Text.setText(entity.getOrderNumber());
                getBinding().orderInfoBottomItem2Text.setText(entity.getOtcTradeId() + "");
                getBinding().orderInfoBottomItem3Layout.setVisibility(View.VISIBLE);
                getBinding().orderInfoBottomItem3Text.setText(entity.getCreatedAt());
                getBinding().orderInfoBottomLayout.setVisibility(View.VISIBLE);
                getBinding().orderInfoConfirmButton.setText("放行" + entity.getCoinName());
                getBinding().orderInfoCancelButton.setEnabled(false);
                getBinding().orderInfoCancelButton.setTextColor(getActivity().getResources().getColor(R.color.white));
                getBinding().orderInfoTipsText.setVisibility(View.VISIBLE);
                getBinding().orderInfoTipsText.setText("在转账过程中请勿备注BTC、USDT、火信等信息，防止汇款被拦截、银行卡被冻结等问题");
                break;
            default:
                break;
        }
    }

    private void setTimeText(final String text) {
        getBinding().orderInfoTimeText.post(new Runnable() {
            @Override
            public void run() {
                getBinding().orderInfoTimeText.setText(text);
            }
        });
    }

    public void setCallPhone() {
        if (!EmptyUtil.isEmpty(infoEntity)) {
            if (infoEntity.getOtcTradeType().equals("buy")) { //买
                PhoneUtils.callPhone(getActivity(), infoEntity.getSellerMobile());
            } else if (infoEntity.getOtcTradeType().equals("sell")) { //卖
                PhoneUtils.callPhone(getActivity(), infoEntity.getBuyerMobile());
            }
        }
    }

    private void startTime(OTCOrderInfoEntity entity) {
        if (0 < DateUtils.dayDiff2(entity.getExpiredAt(), DateUtils.getTime(), "yyyy-MM-dd HH:mm:ss")) {
            endTime = DateUtils.dayDiff3(entity.getExpiredAt(), "yyyy-MM-dd HH:mm:ss");
        } else {
            getBinding().orderInfoTimeText.setText("已超时");
            return;
        }
        if (endTime > 0L) {
            Log.e("cjh>>>", "哈哈");
            long aa = System.currentTimeMillis();
            if (System.currentTimeMillis() < endTime) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        long currTime = System.currentTimeMillis();
                        if (currTime < endTime) {
                            long l = (endTime - currTime) / 1000L;
                            long s = l % 60;//秒
                            long m = l / 60;//分
                            String strTime = (m > 0 ? (m + "分") : "") + s + "秒";
                            String text = time1 + strTime + time2; // 请在19:44内付款给卖家
                            setTimeText(text);
                        } else {
                            // TODO: 2018/9/13 取消
//                            closeTimer();
//                            btnVerify.setEnabled(false);
//                            btnReset.setEnabled(false);
//                            titleRightText.setText("申诉中");
                        }
                    }
                }, 0, 500L);
            }
        } else {
            getBinding().orderInfoTimeText.setText("已超时");
            Log.e("cjh>>>", "嘻嘻");
        }
    }

}
