package com.coin.market.activity.mine.coinselect;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.coin.market.R;
import com.coin.market.databinding.ActivityCoinSelectLayoutBinding;

import teng.wang.comment.base.BaseActivity;

/**
 * @author  充币选择  列表
 * @version v1.0
 * @Time
 */

public class CoinSelectActivity extends BaseActivity implements View.OnClickListener {

    private ActivityCoinSelectLayoutBinding binding;
    private CoinSelectViewModel model;
    private String Jump;

    /** 这里根据业务不同  判断Jump 分别跳到不同的 界面去    充币提币 公用一个币种选择列表   根据Jump判断是充币CB 还是提币 TB*/
    public static void JUMP(Context context, String Jump) {
        context.startActivity(new Intent(context, CoinSelectActivity.class).putExtra("Jump", Jump));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_coin_select_layout);
        model = new CoinSelectViewModel(this, binding);
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setText("币种选择");
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
