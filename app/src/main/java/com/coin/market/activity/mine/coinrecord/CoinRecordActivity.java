package com.coin.market.activity.mine.coinrecord;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.coin.market.R;
import com.coin.market.databinding.ActivityCoinRecordLayoutBinding;
import com.coin.market.util.EmptyUtil;

import teng.wang.comment.base.BaseActivity;

/**
 * @author: Lenovo
 * @date: 2019/7/16
 * @info:  充币记录
 */
public class CoinRecordActivity extends BaseActivity implements View.OnClickListener {

    private ActivityCoinRecordLayoutBinding binding;
    private CoinRecordViewModel model;
    public String CoinId, Jump,coinName; // Jump跳转 CB=充币  TB=提币
    public int type = 0;

    public static void JUMP(Context context, String Jump, String coinId,String coinName) {
        context.startActivity(new Intent(context,
                CoinRecordActivity.class).putExtra("Jump", Jump)
                .putExtra("coinId", coinId).putExtra("coinName",coinName));
    }

    @Override
    protected void initIntentData() {
        CoinId = getIntent().getStringExtra("coinId");
        Jump = getIntent().getStringExtra("Jump");
        Bundle extras = getIntent().getExtras();
        coinName = extras.getString("coinName", "");
        if (Jump.equals("TB")){
            type = 0;
        }else {
            type = 1;
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_coin_record_layout);
        model = new CoinRecordViewModel(this, binding);
        if (!EmptyUtil.isEmpty(Jump)){
            if (Jump.equals("TB")){
                binding.coinRecordTitle.setText(getResources().getString(R.string.mine_coin_outhis));
            }else {
                binding.coinRecordTitle.setText(getResources().getString(R.string.mine_coin_recharge_title_record));
            }
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
