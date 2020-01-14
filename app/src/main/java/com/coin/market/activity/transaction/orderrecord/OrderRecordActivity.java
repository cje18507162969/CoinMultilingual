package com.coin.market.activity.transaction.orderrecord;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.coin.market.R;
import com.coin.market.databinding.ActivityOrderRecordLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.coin.market.wight.dialog.OrderRecordScreenDialog;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;

/**
 * @author: Lenovo
 * @date: 2019/8/2
 * @info: 交易法币订单记录
 */
public class OrderRecordActivity extends BaseActivity implements View.OnClickListener {

    private ActivityOrderRecordLayoutBinding binding;
    private OrderRecordViewModel model;
    private OrderRecordScreenDialog dialog;
    public String otcTradeType = "";  // otcTradeType：买或卖   buy sell
    public String otcStatus = "order_all";  // otcStatus：状态

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, OrderRecordActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            model.getOrderList(FaceApplication.getToken(), otcTradeType, otcStatus, model.page, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_record_layout);
        model = new OrderRecordViewModel(this, binding);
        initDialog();
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
        binding.titleBar.mImgScan.setVisibility(View.VISIBLE);
        binding.titleBar.mImgScan.setImageResource(R.drawable.mine_filter);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.titleBar.mImgScan.setOnClickListener(this);
    }

    private void initDialog() {
        dialog = new OrderRecordScreenDialog(this);
        dialog.setListener(new OrderRecordScreenDialog.itemClick() {
            @Override
            public void selectClick(int type, int status) {
                otcTradeType = "";
                switch (type) {
                    case 1:
                        otcTradeType = "buy";
                        break;
                    case 2:
                        otcTradeType = "sell";
                        break;
                    default:
                        break;
                }

                switch (status) {
                    case 1:
                        otcStatus = "noalreadypay";
                        break;
                    case 2:
                        otcStatus = "waitverify";
                        break;
                    case 3:
                        otcStatus = "alreadysuccess";
                        break;
                    case 4:
                        otcStatus = "alreadycancel";
                        break;
                    case 5:
                        otcStatus = "order_inthecomplaint";
                        break;
                    default:
                        break;
                }
                model.page = 1;
                if (type == 0 && status == 0) {
                    // 重置
                    otcStatus = "order_all";
                    otcTradeType = "";
                }
                model.getOrderList(FaceApplication.getToken(), otcTradeType, otcStatus, model.page, 0);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_Return:
                finish();
                break;
            case R.id.mImg_Scan:
                if (EmptyUtil.isEmpty(dialog)) {
                    dialog.show();
                } else {
                    dialog.show();
                }
                break;
            default:
                break;
        }
    }
}
