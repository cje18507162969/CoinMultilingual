package com.coin.market.activity.mine.coinorderinfo;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.coin.market.R;
import com.coin.market.databinding.ActivityCoinOrderInfoLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.model.EntrustInfo;

/**
 * @author: Lenovo
 * @date: 2019/7/23
 * @info: 成交明细
 */
public class CoinOrderInfoActivity extends BaseActivity implements View.OnClickListener {


    private ActivityCoinOrderInfoLayoutBinding binding;
    private CoinOrderInfoModel model;
    public String orderId, type;
    public String payName, coinName;
    private EntrustInfo entrustInfo;
    private String buy = "";
    private String sell = "";

    public static void JUMP(Context context, EntrustInfo entrustInfo) {
        Intent intent = new Intent(context, CoinOrderInfoActivity.class);
        intent.putExtra("EntrustInfo", entrustInfo);
        context.startActivity(intent);
    }

    @Override
    protected void initIntentData() {
        entrustInfo = (EntrustInfo) getIntent().getSerializableExtra("EntrustInfo");
        orderId = entrustInfo.getOrder_id();
        type = entrustInfo.getType()+"";
        payName = entrustInfo.getPay_name()+"";
        coinName = entrustInfo.getCoin_name()+"";
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_coin_order_info_layout);
        model = new CoinOrderInfoModel(this, binding);
        if (!EmptyUtil.isEmpty(entrustInfo)){
            setTopUi(entrustInfo);
        }
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
    }

    private void setTopUi(EntrustInfo entity){
        binding.coinOrderInfoCoinName.setText(entity.getCoin_name() + "/" + entity.getPay_name());
        binding.orderInfoTitle1.setText("总成交额(" + entity.getPay_name()+")"); //总成交额
        binding.orderInfoTitle2.setText("成交均价(" + entity.getPay_name()+")"); //成交均价

        if (type.equals("1")){
            binding.orderInfoTitle3.setText("成交量(" + entity.getCoin_name()+")"); //成交量
            binding.orderInfoTitle4.setText("手续费(" + entity.getPay_name()+")");// 手续费
        }else if (type.equals("0")){
            binding.orderInfoTitle3.setText("成交量(" + entity.getCoin_name()+")"); //成交量
            binding.orderInfoTitle4.setText("手续费(" + entity.getCoin_name()+")");// 手续费
        }

        binding.orderInfoValue1.setText(entity.getVolume()+""); //总成交额
        binding.orderInfoValue2.setText(entity.getAveragePrice()+""); //成交均价
        binding.orderInfoValue3.setText(entity.getDeal()+""); //成交量
        binding.orderInfoValue4.setText(entity.getFee()+""); //手续费

        if (entity.getType()==0){
            binding.buySellText.setText("买入");
            binding.buySellText.setTextColor(getResources().getColor(R.color.app_home_coin_text_color));
        }else {
            binding.buySellText.setText("卖出");
            binding.buySellText.setTextColor(getResources().getColor(R.color.app_home_coin_text_color_red));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_Return:
                finish();
                break;
            default:
                break;
        }
    }

}
