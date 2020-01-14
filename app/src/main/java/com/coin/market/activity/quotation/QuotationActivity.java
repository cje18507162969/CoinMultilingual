package com.coin.market.activity.quotation;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.webkit.CookieSyncManager;

import com.coin.market.R;
import com.coin.market.databinding.ActivityQuotationLayoutBinding;
import com.coin.market.util.MyTimeTask;

import org.greenrobot.eventbus.EventBus;

import java.net.CookieManager;
import java.util.TimerTask;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.model.CoinEntity;
import teng.wang.comment.model.MarketsDTO;
import teng.wang.comment.utils.StatusBarUtil;

/**
 * @author 行情 WebView h5
 * @version v1.0
 * @Time
 */

public class QuotationActivity extends BaseActivity implements View.OnClickListener {

    private ActivityQuotationLayoutBinding binding;
    private QuotationViewModel model;
    public String MarketId = "";
    public String coinId = "";
    public MarketsDTO coinEntity;


    public static void JUMP(Context context, MarketsDTO coinEntity) {
        context.startActivity(new Intent(context, QuotationActivity.class).putExtra("coinEntity", coinEntity));
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.app_quotation_title_bg), 0);
    }

    @Override
    protected void initIntentData() {
        coinEntity = (MarketsDTO) getIntent().getSerializableExtra("coinEntity");
        MarketId = coinEntity.getId()+"";
        coinId = coinEntity.getId()+"";
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(true);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quotation_layout);
        model = new QuotationViewModel(this, binding);


    }

    @Override
    protected void setListener() {
        setTimer();
        binding.appBarQuotation.btnQuotationBack.setOnClickListener(this);
        binding.appBarQuotation.btnQuotationImg.setOnClickListener(this);
        binding.appBarQuotation.btnQuotationImg1.setOnClickListener(this);
        binding.appBarQuotation.btnQuotationImg2.setOnClickListener(this);
        binding.appBarQuotation.btnQuotationImg3.setOnClickListener(this);
        binding.appBarQuotation.btnQuotationBuy.setOnClickListener(this);
        binding.appBarQuotation.btnQuotationSell.setOnClickListener(this);
    }

    private static final int TIMER = 999;
    private MyTimeTask task;
    private void setTimer() {
        task = new MyTimeTask(1500, new TimerTask() {
            @Override
            public void run() {
                myHandler.sendEmptyMessage(TIMER);
                //或者发广播，启动服务都是可以的

            }
        });
        task.start();
    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIMER:
                    //在此执行定时操作
                    if (null!=model )
                        model.getCoin();
                    break;
                default:
                    break;
            }
        }
    };
    // Set上部分币的数据
    public void setUi() {
        binding.appBarQuotation.priceView.setText("");
        binding.appBarQuotation.priceViewCny.setText("");
        binding.appBarQuotation.rateView.setText("");
        binding.appBarQuotation.highView.setText("");
        binding.appBarQuotation.lowView.setText("");
        binding.appBarQuotation.l24hView.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_quotation_back:
                finish();
                break;
            case R.id.btn_quotation_img: // NavigationView 滑出来
                binding.quotationDrawerLayout.openDrawer(Gravity.LEFT); // 打开
                model.getCoins(model.coinName);
                break;
            case R.id.btn_quotation_img_1:
                break;
            case R.id.btn_quotation_img_2:
                break;
            case R.id.btn_quotation_img_3:
                break;
            case R.id.btn_quotation_buy:
                EventBus.getDefault().post("goTreaty");
                coinEntity.setSelect("buy");
                EventBus.getDefault().post(coinEntity);
                finish();
                break;
            case R.id.btn_quotation_sell:
                EventBus.getDefault().post("goTreaty");
                coinEntity.setSelect("sell");
                EventBus.getDefault().post(coinEntity);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CookieSyncManager.createInstance(this);
        binding.appBarQuotation.webViewChart.setWebChromeClient(null);
        binding.appBarQuotation.webViewChart.setWebViewClient(null);
        binding.appBarQuotation.webViewChart.getSettings().setJavaScriptEnabled(false);
        binding.appBarQuotation.webViewChart.clearCache(true);
    }

    
}
