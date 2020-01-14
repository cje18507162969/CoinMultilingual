package com.coin.market.activity.mine.coinaddress;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.coin.market.R;
import com.coin.market.activity.mine.coinaddressdda.CoinAddAddressActivity;
import com.coin.market.databinding.ActivityCoinAddressLayoutBinding;
import com.coin.market.util.EmptyUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;

/**
 * @author: Lenovo
 * @date: 2019/7/17
 * @info: 提币地址 列表
 */
public class CoinAddressActivity extends BaseActivity implements View.OnClickListener {

    private ActivityCoinAddressLayoutBinding binding;
    private CoinAddressViewModel model;
    public String CoinId, coinName;

    public static void JUMP(Context context, String CoinId, String coinName) {
        context.startActivity(new Intent(context, CoinAddressActivity.class).putExtra("coinId", CoinId).putExtra("coinName", coinName));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        EventBus.getDefault().register(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_coin_address_layout);
        model = new CoinAddressViewModel(this, binding);
    }

    @Override
    protected void initIntentData() {
        if (!EmptyUtil.isEmpty(getIntent().getStringExtra("coinId"))) {
            CoinId = getIntent().getStringExtra("coinId");
        }
        coinName = getIntent().getStringExtra("coinName");
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
        binding.cionAddressTitle.setText(coinName+"地址");
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.coinAddAddressButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_Return:
                finish();
                break;
            case R.id.coin_add_address_button: // 添加地址
                CoinAddAddressActivity.JUMP(this, CoinId);
                break;
            default:
                break;
        }
    }

    /**
     *  添加的广播接收
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void login(String str) {
        try {
            if (null != str && str.equals("addAddress")) {
                model.getCoinList(FaceApplication.getToken(), CoinId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
