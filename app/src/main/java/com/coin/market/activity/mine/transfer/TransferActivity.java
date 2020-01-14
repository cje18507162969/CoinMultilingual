package com.coin.market.activity.mine.transfer;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.activity.mine.transferrecord.TransferRecordActivity;
import com.coin.market.databinding.ActivityTransferLayoutBinding;
import com.coin.market.fragment.coinsselect.CoinsSelectFragment;
import com.coin.market.util.AnimationUtils;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.FB_BBEntity;

/**
 * @author: Lenovo
 * @date: 2019/7/17
 * @info: 划转
 */
public class TransferActivity extends BaseActivity implements View.OnClickListener {

    private ActivityTransferLayoutBinding binding;
    private TransferViewModel model;
    private CoinsSelectFragment fragment;
    private FragmentTransaction transaction;
    public String coinId = "";
    public String accountType = "fb_type";  // fb_type = 法币  bb_type = 币币
    public FB_BBEntity.ListdetailBean fbBbEntity; // 选择币种 返回的实体类

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, TransferActivity.class));
    }

    public static void JUMP(Context context, String accountType) { //资产上方 没选择币种的划转
        context.startActivity(new Intent(context, TransferActivity.class).putExtra("accountType", accountType));
    }

    public static void JUMP(Context context, String accountType, String coinId) { //资产下方 选择了币种的划转
        context.startActivity(new Intent(context, TransferActivity.class).putExtra("accountType", accountType).putExtra("coinId", coinId));
    }

    @Override
    protected void initIntentData() {
        coinId = getIntent().getStringExtra("coinId");
        if (!EmptyUtil.isEmpty(getIntent().getStringExtra("accountType"))){
            accountType = getIntent().getStringExtra("accountType");
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transfer_layout);
        model = new TransferViewModel(this, binding);
        if (!EmptyUtil.isEmpty(accountType) && !EmptyUtil.isEmpty(coinId)){ // 带币种跳转过来
            model.getOtcCoins(FaceApplication.getToken(), accountType);
        }else if (!EmptyUtil.isEmpty(accountType) && EmptyUtil.isEmpty(coinId)){ // 没币种 资产跳转过来 设置法币 币币
            //initFragment();
        }else {
            //initFragment();
        }
        if (!EmptyUtil.isEmpty(accountType)){
            if (accountType.equals("fb_type")) {
                binding.transferAccount1.setText(this.getResources().getString(R.string.mine_transfer_fb_account));
                binding.transferAccount2.setText(this.getResources().getString(R.string.mine_transfer_bb_account));
            } else if (accountType.equals("bb_type")) {
                binding.transferAccount1.setText(this.getResources().getString(R.string.mine_transfer_bb_account));
                binding.transferAccount2.setText(this.getResources().getString(R.string.mine_transfer_fb_account));
            }
        }

    }

    @Override
    protected void initTitleData() {
        //binding.titleBar.txtTitle.setText("币种选择");
        binding.titleBar.txtTitle.setVisibility(View.GONE);
        binding.titleBar.mImgScan.setVisibility(View.VISIBLE);
        binding.titleBar.mImgScan.setImageResource(R.drawable.mine_history);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.titleBar.mImgScan.setOnClickListener(this);
        binding.transferExchangeButton.setOnClickListener(this);
        binding.transferCoinsButton.setOnClickListener(this);
        binding.transferAll.setOnClickListener(this);
        binding.transferConfirmButton.setOnClickListener(this);
        binding.transferNumbEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence)) {
                    binding.transferConfirmButton.setEnabled(true);
                    binding.transferConfirmButton.setAlpha(1.0f);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initFragment() {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_slide_in, R.anim.anim_slide_out);
        if (null == fragment) {
            fragment = CoinsSelectFragment.getCoinsSelectFragment(accountType);
            transaction.add(R.id.coins_fr_layout, fragment, fragment.getTag());
            fragment.fragmentisHidden(transaction);
        } else {
            if (fragment.isVisible()) {
                transaction.hide(fragment);
            } else {
                transaction.show(fragment);
            }
        }
        transaction.commit();

        // 选择币种的回调
        fragment.setListener(new CoinsSelectFragment.mOnClickCallback() {
            @Override
            public void selectItem(FB_BBEntity.ListdetailBean entity) {
                coinId = entity.getCoin_id() + "";
                fbBbEntity = entity;
                binding.transferSelectCoin.setText(entity.getCoin_name());
                binding.transferSelectCoin2.setText(entity.getCoin_name());
                binding.transferNumbEdit.setText("");
                if (accountType.equals("fb_type")){
                    binding.transferAvailableNumb.setText(StringUtils.double2String(Double.parseDouble(entity.getOtcAavailable()), 8)+ " " +entity.getCoin_name());
                }else {
                    binding.transferAvailableNumb.setText(StringUtils.double2String(Double.parseDouble(entity.getAvailable()), 8)+ " " +entity.getCoin_name());
                }
            }
        });
    }

    /**
     * 交换
     */
    private void setExchange() {
        AnimationUtils.rotationAnimation(binding.transferExchangeButton, "rotation", 0f, 360f);
        if (accountType.equals("fb_type")) {
            binding.transferAccount1.setText(this.getResources().getString(R.string.mine_transfer_bb_account));
            binding.transferAccount2.setText(this.getResources().getString(R.string.mine_transfer_fb_account));
            accountType = "bb_type";
            if (!EmptyUtil.isEmpty(fbBbEntity)){
                binding.transferAvailableNumb.setText(Double.parseDouble(fbBbEntity.getAvailable())+ " " +fbBbEntity.getCoin_name());
                if (!TextUtils.isEmpty(binding.transferNumbEdit.getText().toString())){
                    binding.transferNumbEdit.setText(StringUtils.double2String(Double.parseDouble(fbBbEntity.getAvailable()), 8));
                }
            }
        } else if (accountType.equals("bb_type")) {
            binding.transferAccount1.setText(this.getResources().getString(R.string.mine_transfer_fb_account));
            binding.transferAccount2.setText(this.getResources().getString(R.string.mine_transfer_bb_account));
            accountType = "fb_type";
            if (!EmptyUtil.isEmpty(fbBbEntity)){
                binding.transferAvailableNumb.setText(Double.parseDouble(fbBbEntity.getOtcAavailable())+ " " +fbBbEntity.getCoin_name());
                if (!TextUtils.isEmpty(binding.transferNumbEdit.getText().toString())){
                    binding.transferNumbEdit.setText(StringUtils.double2String(Double.parseDouble(fbBbEntity.getOtcAavailable()), 8));
                }
            }
        }
        if (!EmptyUtil.isEmpty(fragment)){
            fragment.getData(accountType);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_Return:
                finish();
                break;
            case R.id.transfer_exchange_button: // 法币 币币交换
                setExchange();
                break;
            case R.id.transfer_coins_button: // 换币种
                initFragment();
                //fragment.getData(accountType);
                break;
            case R.id.mImg_Scan: // 跳转到 历史
                if (TextUtils.isEmpty(coinId)){
                    Toast.makeText(mContext, "请选择币种", Toast.LENGTH_SHORT).show();
                    initFragment();
                    return;
                }
                TransferRecordActivity.JUMP(this, coinId, "");
                break;
            case R.id.transfer_all: // 全部
                if (!EmptyUtil.isEmpty(fbBbEntity)){
                    if (accountType.equals("fb_type")) {
                        binding.transferNumbEdit.setText(StringUtils.double2String(Double.parseDouble(fbBbEntity.getOtcAavailable()), 8) + "");
                    } else if (accountType.equals("bb_type")) {
                        binding.transferNumbEdit.setText(StringUtils.double2String(Double.parseDouble(fbBbEntity.getAvailable()), 8) + "");
                    }
                }else {
                    Toast.makeText(mContext, "请选择币种", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.transfer_confirm_button: // 划转提交
                if (TextUtils.isEmpty(coinId)) {
                    Toast.makeText(mContext, "请选择币种！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(binding.transferNumbEdit.getText().toString().trim())) {
                    Toast.makeText(mContext, "请输入划转数量！", Toast.LENGTH_SHORT).show();
                    return;
                }
                model.transfer(FaceApplication.getToken(), coinId, accountType, binding.transferNumbEdit.getText().toString().trim());
                break;
            default:
                break;
        }
    }

}
