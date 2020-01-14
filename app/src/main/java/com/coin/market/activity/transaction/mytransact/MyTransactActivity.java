package com.coin.market.activity.transaction.mytransact;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.activity.transaction.addpaymode.AddPayModeActivity;
import com.coin.market.databinding.ActivityMyTransactLayoutBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ParamsEntry;

/**
 * @author: Lenovo
 * @date: 2019/8/5
 * @info: 我要 买或卖 币  发布
 */
public class MyTransactActivity extends BaseActivity implements View.OnClickListener {

    private ActivityMyTransactLayoutBinding binding;
    private MyTransactViewModel model;
    private String type;
    public int bank, alipay, weix;
    private ParamsEntry paramsEntry;

    public static void JUMP(Context context, ParamsEntry paramsEntry) {
        context.startActivity(new Intent(context, MyTransactActivity.class).putExtra("paramsEntry", paramsEntry));
    }

    @Override
    protected void initIntentData() {
        try {
            paramsEntry = (ParamsEntry) getIntent().getSerializableExtra("paramsEntry");
            type = paramsEntry.getType();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_transact_layout);
        model = new MyTransactViewModel(this, binding);
        if (type.equals("buy")){
            binding.myTransactTitleText.setText("我要买币");
        }else {
            binding.myTransactTitleText.setText("我要卖币");
        }
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.myTransactBankImg.setOnClickListener(this);
        binding.myTransactAlipayImg.setOnClickListener(this);
        binding.myTransactWeixImg.setOnClickListener(this);
        binding.btnPublishText.setOnClickListener(this);
        binding.noPayButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_Return:
                finish();
                break;
            case R.id.no_pay_button:
                AddPayModeActivity.JUMP(this);
                break;
            case R.id.btn_publish_text: // 发布
                List<String> strs = new ArrayList<>();
                paramsEntry.setUserPay(new ArrayList<String>());
                if (alipay==1){
                    strs.add("alipay");
                }
                if (weix==1){
                    strs.add("wxpay");
                }
                if (bank==1){
                    strs.add("unionpay");
                }
                if (strs.size()>0){
                    paramsEntry.setUserPay(strs);
                }
                if (model.payList.size()==0){
                    Toast.makeText(mContext, "请添加支付方式", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (paramsEntry.getUserPay().size()==0){
                    Toast.makeText(mContext, "请选择支付方式", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!TextUtils.isEmpty(binding.myTransactContentEdit.getText().toString().trim())){
                    paramsEntry.setRemark(binding.myTransactContentEdit.getText().toString().trim());
                }
                model.publishSubmit(FaceApplication.getToken(), paramsEntry, 1);
                break;
            case R.id.my_transact_bank_img:
                if (bank==1){
                    binding.myTransactBankImg.setImageResource(R.drawable.mine_switchoff);
                    bank = 2;
                }else {
                    binding.myTransactBankImg.setImageResource(R.drawable.mine_switchon);
                    bank = 1;
                }
                break;
            case R.id.my_transact_alipay_img:
                if (alipay==1){
                    binding.myTransactAlipayImg.setImageResource(R.drawable.mine_switchoff);
                    alipay = 2;
                }else {
                    binding.myTransactAlipayImg.setImageResource(R.drawable.mine_switchon);
                    alipay = 1;
                }
                break;
            case R.id.my_transact_weix_img:
                if (weix==1){
                    binding.myTransactWeixImg.setImageResource(R.drawable.mine_switchoff);
                    weix = 2;
                }else {
                    binding.myTransactWeixImg.setImageResource(R.drawable.mine_switchon);
                    weix = 1;
                }
                break;
            default:
                break;
        }
    }

    /**
     *   添加完收款方式 过来刷新
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getPayData(String str) {
        try {
            if (null != str && str.equals("finishAddPay")) {
                model.getPayMentMethod(FaceApplication.getToken(), 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
