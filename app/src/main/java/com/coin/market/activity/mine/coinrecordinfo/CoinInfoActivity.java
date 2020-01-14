package com.coin.market.activity.mine.coinrecordinfo;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.coin.market.R;
import com.coin.market.databinding.ActivityCoinInfoLayoutBinding;
import com.coin.market.util.EmptyUtil;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.model.ImportsDTO;

/**
 * @author: Lenovo
 * @date: 2019/7/17
 * @info: 充币历史记录详情
 */
public class CoinInfoActivity extends BaseActivity implements View.OnClickListener {

    private ActivityCoinInfoLayoutBinding binding;
    private CoinInfoViewModel model;
    private String id;  // 历史记录的 id 用于接口拉数据
    public String Jump ,coinName;  // 历史记录的 id 用于判断 是哪里跳过来的   TB=提币  CB=充币
    private ImportsDTO infoModel;

    public static void JUMP(Context context, String Jump, ImportsDTO infoModel,String coinName) {
        context.startActivity(new Intent(context,CoinInfoActivity.class)
                .putExtra("Jump", Jump)
                .putExtra("infoModel", infoModel)
                .putExtra("coinName",coinName));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_coin_info_layout);
        model = new CoinInfoViewModel(this, binding);
        if (!EmptyUtil.isEmpty(infoModel)){
            model.setCoinInfo(infoModel);
        }
    }

    @Override
    protected void initIntentData() {
        id = getIntent().getStringExtra("id");
        Jump = getIntent().getStringExtra("Jump");
        Bundle extras = getIntent().getExtras();
        coinName = extras.getString("coinName","");
        infoModel = (ImportsDTO) getIntent().getSerializableExtra("infoModel");

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
