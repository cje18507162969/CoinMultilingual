package com.coin.market.activity.mine.settleddata;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.coin.market.R;
import com.coin.market.databinding.ActivitySettledDataLayoutBinding;
import com.coin.market.util.EmptyUtil;

import teng.wang.comment.base.BaseActivity;

/**
 * @author: Lenovo
 * @date: 2019/9/3
 * @info:  商家 认证审核结果
 */
public class SettledDataActivity extends BaseActivity implements View.OnClickListener {

    private ActivitySettledDataLayoutBinding binding;
    private SettledDataViewModel model;
    public String coinId = "";
    public String type = "1";  // 1是审核中  2是审核通过

    public static void JUMP(Context context, String type) {
        context.startActivity(new Intent(context, SettledDataActivity.class).putExtra("type", type));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settled_data_layout);
        model = new SettledDataViewModel(this, binding);
        setType();
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
    }

    private void setType(){
        if (!EmptyUtil.isEmpty(getIntent().getStringExtra("type"))){
            type = getIntent().getStringExtra("type");
            if (!type.equals("1")){
                binding.settledDataImg.setImageResource(R.drawable.examinationpassed);
                binding.settledDataText1.setText("恭喜您，已成为交易所入驻商家！");
                binding.settledDataText2.setVisibility(View.GONE);
            }
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
