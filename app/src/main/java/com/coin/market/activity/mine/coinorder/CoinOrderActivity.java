package com.coin.market.activity.mine.coinorder;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.coin.market.R;
import com.coin.market.databinding.ActivityCoinOrderLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.coin.market.wight.dialog.OrderScreenDialog;

import org.greenrobot.eventbus.EventBus;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.model.CoinListEntity;

/**
 * @author: Lenovo
 * @date: 2019/7/19
 * @info: 订单管理
 */
public class CoinOrderActivity extends BaseActivity implements View.OnClickListener {

    private int tabType = 1;
    private ActivityCoinOrderLayoutBinding binding;
    private CoinOrderViewModel model;
    public OrderScreenDialog dialog;

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, CoinOrderActivity.class));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_coin_order_layout);
        model = new CoinOrderViewModel(this, binding);
        dialog = new OrderScreenDialog(this, model.coins);
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
        binding.coinOrderAll.setOnClickListener(this);
        binding.coinOrderRecords.setOnClickListener(this);
        dialog.setListener(new OrderScreenDialog.itemClick() {
            @Override
            public void Click(CoinListEntity entity) {
                EventBus.getDefault().post(entity);
            }
        });
    }

    public void setTabButton(int type){
        binding.coinOrderAll.setTextColor(this.getResources().getColor(R.color.color_999999));
        binding.coinOrderRecords.setTextColor(this.getResources().getColor(R.color.color_999999));
        binding.coinOrderAll.setTextSize(14);
        binding.coinOrderRecords.setTextSize(14);
        if (type==0){
            binding.coinOrderAll.setTextColor(this.getResources().getColor(R.color.app_home_text));
            binding.coinOrderAll.setTextSize(18);

        }else {
            binding.coinOrderRecords.setTextColor(this.getResources().getColor(R.color.app_home_text));
            binding.coinOrderRecords.setTextSize(18);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_Return:
                finish();
                break;
            case R.id.mImg_Scan: // 筛选的弹窗
                if (EmptyUtil.isEmpty(dialog)){
                    dialog.show();
                }else {
                    dialog.show();
                }
                break;
            case R.id.coin_order_all:
                model.setAdapterItem(0);
                break;
            case R.id.coin_order_records:
                model.setAdapterItem(1);
                break;
            default:
                break;
        }
    }
}
