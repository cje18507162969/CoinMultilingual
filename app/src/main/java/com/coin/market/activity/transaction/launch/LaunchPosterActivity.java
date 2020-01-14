package com.coin.market.activity.transaction.launch;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.databinding.ActivityLaunchPosterLayoutBinding;
import com.coin.market.activity.transaction.mytransact.MyTransactActivity;
import com.coin.market.fragment.coinsselect.CoinsSelectFragment;
import com.coin.market.util.ArithTulis;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.model.FB_BBEntity;
import teng.wang.comment.model.ParamsEntry;

/**
 * @author: Lenovo
 * @date: 2019/8/2
 * @info: 发布广告
 */
public class LaunchPosterActivity extends BaseActivity implements View.OnClickListener {

    private ActivityLaunchPosterLayoutBinding binding;
    private LaunchPosterViewModel model;
    private CoinsSelectFragment fragment;
    private FragmentTransaction transaction;
    private int type;
    private int coinId;

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, LaunchPosterActivity.class));
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_launch_poster_layout);
        model = new LaunchPosterViewModel(this, binding);
        //initFragment();
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.launchPosterBuyCoin.setOnClickListener(this);
        binding.launchPosterSellCoin.setOnClickListener(this);
        binding.launchPosterCoinSelect.setOnClickListener(this);
        binding.launchPosterNextButton.setOnClickListener(this);
    }

    private void setTypeUi(int type) {
        this.type = type;
        binding.launchPosterBuyCoin.setTextSize(14);
        binding.launchPosterBuyCoin.setTextColor(getResources().getColor(R.color.color_666666));
        binding.launchPosterSellCoin.setTextSize(14);
        binding.launchPosterSellCoin.setTextColor(getResources().getColor(R.color.color_666666));
        if (type == 0) {
            binding.launchPosterBuyCoin.setTextSize(18);
            binding.launchPosterBuyCoin.setTextColor(getResources().getColor(R.color.app_style_blue));
        } else {
            binding.launchPosterSellCoin.setTextSize(18);
            binding.launchPosterSellCoin.setTextColor(getResources().getColor(R.color.app_style_blue));
        }

    }

    private void initFragment() {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_slide_in, R.anim.anim_slide_out);
        if (null == fragment) {
            fragment = CoinsSelectFragment.getCoinsSelectFragment("fb_type");
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
                binding.launchPosterCoinText.setText(entity.getCoin_name());
                binding.launchPosterAvailable.setText(getResources().getString(R.string.transaction_fb_transact_assets_account_available_text)
                        + ArithTulis.add(Double.parseDouble(entity.getOtcAavailable()), 0) + " " + entity.getCoin_name());
                binding.launchPosterNumbText.setText(entity.getCoin_name());
                coinId = Integer.parseInt(entity.getCoin_id());
            }
        });
    }

    private void jumpPublish(){
        if (TextUtils.isEmpty(binding.launchPosterNumb.getText().toString().trim())){
            Toast.makeText(mContext, "请输入交易数量", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(binding.launchPosterPrice.getText().toString().trim())){
            Toast.makeText(mContext, "请输入交易价格", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(binding.launchPosterMinPrice.getText().toString().trim())){
            Toast.makeText(mContext, "请输入最小成交金额", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(binding.launchPosterMaxPrice.getText().toString().trim())){
            Toast.makeText(mContext, "请输入最大成交金额", Toast.LENGTH_SHORT).show();
            return;
        }
        ParamsEntry paramsEntry = new ParamsEntry();
        paramsEntry.setId(coinId);
        paramsEntry.setPrice(binding.launchPosterPrice.getText().toString().trim());
        paramsEntry.setNumber(binding.launchPosterNumb.getText().toString().trim());
        paramsEntry.setMinMoney(binding.launchPosterMinPrice.getText().toString().trim());
        paramsEntry.setMaxMoney(binding.launchPosterMaxPrice.getText().toString().trim());
        if (type==0){
            paramsEntry.setType("buy");
        }else {
            paramsEntry.setType("sell");
        }
        MyTransactActivity.JUMP(this, paramsEntry);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_Return:
                finish();
                break;
            case R.id.launch_poster_buy_coin:
                setTypeUi(0);
                break;
            case R.id.launch_poster_sell_coin:
                setTypeUi(1);
                break;
            case R.id.launch_poster_coin_select:
                initFragment();
                break;
            case R.id.launch_poster_next_button: // 去填写备注 选择支付方式 发布
                jumpPublish();
                break;
            default:
                break;
        }
    }

    /**
     * 发布成功 关闭此界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void publish(String model) {
        try {
            if (null != model && model.equals("publish")) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
