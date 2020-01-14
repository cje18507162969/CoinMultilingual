package com.coin.market.activity.transaction.order;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.coin.market.R;
import com.coin.market.activity.home.feedback.FeedbackActivity;
import com.coin.market.activity.home.feedback.PublishFeedbackActivity;
import com.coin.market.activity.showpicture.ShowPictureActivity;
import com.coin.market.databinding.ActivityTransactionOrderInfoLayoutBinding;
import com.coin.market.util.androidUtils;
import com.coin.market.wight.dialog.PayMethodDialog;
import com.coin.market.wight.dialog.TransactionConfirmDialog;
import com.coin.market.wight.dialog.TransactionContentDialog;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.PayMethodsBean;
import teng.wang.comment.utils.StatusBarUtil;
import teng.wang.comment.widget.ActionSheetDialog;
import teng.wang.comment.widget.MessageWindow;

/**
 * @author: Lenovo
 * @date: 2019/8/1
 * @info: 交易订单详情
 */
public class TransactionOrderInfoActivity extends BaseActivity implements View.OnClickListener {

    private ActivityTransactionOrderInfoLayoutBinding binding;
    private TransactionOrderInfoViewModel model;
    public String orderId = "";
    public String type;
    private TransactionConfirmDialog dialog;

    public static void JUMP(Context context, String orderId, String type) {
        context.startActivity(new Intent(context, TransactionOrderInfoActivity.class).putExtra("orderId", orderId).putExtra("type", type));
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.app_style_blue), 0);
    }

    @Override
    protected void initIntentData() {
        orderId = getIntent().getStringExtra("orderId");
        type = getIntent().getStringExtra("type");
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(true);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transaction_order_info_layout);
        model = new TransactionOrderInfoViewModel(this, binding);
        binding.titleBar.titleLayout.setBackgroundResource(R.color.app_style_blue);
        binding.titleBar.txtTitle.setVisibility(View.GONE);
        binding.titleBar.titleImg.setImageResource(R.drawable.transaction_return);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.orderInfoConfirmButton.setOnClickListener(this);
        binding.orderInfoPhoneLayout.setOnClickListener(this);
        binding.orderInfoPayLayout.setOnClickListener(this);
        binding.orderInfoItem1Img2.setOnClickListener(this);
        binding.orderInfoItem2Img1.setOnClickListener(this);
        binding.orderInfoItem3Img.setOnClickListener(this);
        binding.orderInfoItem4Img1.setOnClickListener(this);
        binding.orderInfoBottomItem1Img.setOnClickListener(this);
        binding.orderInfoBottomItem2Img.setOnClickListener(this);
        binding.orderInfoCancelButton.setOnClickListener(this);
        binding.orderInfoAllPriceButton.setOnClickListener(this);
    }

    /**
     *   选择支付方式
     */
    /**
     * 筛选记录的 Dialog
     */
    private void showDiaLog() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_Return:
                finish();
                break;
            case R.id.order_info_pay_layout:
                // 更换支付方式
                if (model.infoEntity.getPayMethods().size() > 0) {
                    PayMethodDialog dialog = new PayMethodDialog(this, model.infoEntity.getPayMethods());
                    dialog.builder()
                            .setCancelable(true)
                            .setCanceledOnTouchOutside(true)
                            .setOnClickListener(new PayMethodDialog.OnItemClickListener() {
                                @Override
                                public void onClick(PayMethodsBean bean) {
                                    model.setPay(bean);
                                }
                            });
                    dialog.show();
                }
                break;
            case R.id.order_info_cancel_button: //取消订单
                if (model.status == 4) {
                    //卖家待确认收款  可以申诉  不能取消
                    new MessageWindow(this, "当前状态不能取消订单！").showAtBottom(binding.orderInfoAllPrice);
                } else {// 取消订单
                    TransactionContentDialog cancelDialog = new TransactionContentDialog(this, "取消订单", "是否需要取消订单", "选择确认订单将取消");
                    cancelDialog.show();
                    cancelDialog.setListener(new TransactionContentDialog.mOnClickCallback() {
                        @Override
                        public void confirmOnClick() {
                            model.cancelOrder(FaceApplication.getToken(), orderId, 1);
                        }
                    });
                }
                break;
            case R.id.order_info_confirm_button: // 买家已成功付款  卖家已收款
                if (model.status == 1) { //买家待放行的时候 可以申诉
                    TransactionContentDialog cancelDialog = new TransactionContentDialog(this, "提交申诉", "是否需要申诉订单", "选择确认将申诉订单");
                    cancelDialog.show();
                    cancelDialog.setListener(new TransactionContentDialog.mOnClickCallback() {
                        @Override
                        public void confirmOnClick() {
                            model.appeal(FaceApplication.getToken(), orderId, 1);
                        }
                    });
                } else if (model.status == 8){
                    new ActionSheetDialog(this)
                            .builder()
                            .setCancelable(true)
                            .setCanceledOnTouchOutside(true)
                            .setTitle("是否去问题反馈？")
                            .setOnDismissListener(new ActionSheetDialog.DialogOnDismissListener() {
                                @Override
                                public void onDialogDismiss(ActionSheetDialog dialog) {

                                }
                            })
                            .addSheetItem("确定", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    PublishFeedbackActivity.JUMP(TransactionOrderInfoActivity.this);
                                }
                            }).show();
                } else {
                    dialog = new TransactionConfirmDialog(this, orderId, type);
                    dialog.show();
                    dialog.setListener(new TransactionConfirmDialog.mOnClickCallback() {
                        @Override
                        public void confirmOnClick(String id) {
                            if (type.equals("buy")) {
                                // 作为买家 我已付款成功
                                model.verifyTrade(FaceApplication.getToken(), orderId, 1);
                            } else {
                                // 作为卖家 已收款 放行
                                model.otcSellVerify(FaceApplication.getToken(), orderId, 1);
                            }
                        }
                    });
                }
                break;
            case R.id.order_info_phone_layout:
                model.setCallPhone();
                break;

            case R.id.order_info_all_price_button:
                androidUtils.getCopyText(this, binding.orderInfoAllPrice.getText().toString());
                break;
            case R.id.order_info_item1_img2: // 通常代表收款人
                androidUtils.getCopyText(this, binding.orderInfoItem1Text.getText().toString());
                break;
            case R.id.order_info_item2_img1: // 通常代表收款二维码
                ShowPictureActivity.JUMP(this, model.payMethod.getQrCode());
                break;
            case R.id.order_info_item3_img: // 通常代表收款账号
                androidUtils.getCopyText(this, binding.orderInfoItem3Text.getText().toString());
                break;
            case R.id.order_info_item4_img1: // 通常代表收款账号
                androidUtils.getCopyText(this, binding.orderInfoItem4Text.getText().toString());
                break;
            case R.id.order_info_bottom_item1_img: // 通常代表订单号
                androidUtils.getCopyText(this, binding.orderInfoBottomItem1Text.getText().toString());
                break;
            case R.id.order_info_bottom_item2_img: // 通常代表付款参考号
                androidUtils.getCopyText(this, binding.orderInfoBottomItem2Text.getText().toString());
                break;
            default:
                break;
        }

    }
}
