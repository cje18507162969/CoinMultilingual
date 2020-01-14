package com.coin.market.fragment.transaction;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.adapter.TransactionSelectCoinsAdapter;
import com.coin.market.databinding.FragmentTransactionLayoutBinding;
import com.coin.market.fragment.transaction.transaction_bb.TransactionBBFragment;
import com.coin.market.fragment.transaction.transaction_fb.TransactionFBFragment;
import com.coin.market.util.EmptyUtil;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseFragmentViewModel;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.CoinEntity;
import teng.wang.comment.model.ScreenBean;
import teng.wang.comment.model.TabCoinsEntity;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.log.Log;
import teng.wang.comment.widget.ViewPageAdapter;

/**
 * @author 交易Fragment ViewModel
 * @version v1.0
 * @Time 2018-9-3
 */

public class TransactionViewModel extends BaseFragmentViewModel<TransactionFragment, FragmentTransactionLayoutBinding> {

    private ViewPageAdapter mAdapter;
    private TabLayout tabLayout;
    private EasyRecyclerView easyRecyclerView;
    public String type = "";
    private EditText editScreen;
    private TextView wx, card, alipay, close, screen;
    private TransactionSelectCoinsAdapter adapter;
    public String payMode;

    public TransactionViewModel(TransactionFragment fragment, FragmentTransactionLayoutBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initView() {
        initFragment();
        initNaviSelectCoin();
        initNaviSelectPay();
        initQuotationAdapter(); // 初始化 行情adapter
        getTab();
        getBinding().fragmentTransactionLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); //关闭手势滑动
    }

    //Navigation  抽屉布局 币币选择币种
    private void initNaviSelectCoin() {
        View handerView = LayoutInflater.from(getContexts()).inflate(R.layout.transaction_left_layout, null);
        getBinding().navView.addHeaderView(handerView);
        tabLayout = handerView.findViewById(R.id.transaction_tab_layout);
        easyRecyclerView = handerView.findViewById(R.id.transaction_recycler);
        //initTabLayout(); // 初始化 tab
    }

    //Navigation  抽屉布局 选择支付方式 筛选
    private void initNaviSelectPay() {
        View handerView = LayoutInflater.from(getContexts()).inflate(R.layout.transaction_right_layout, null);
        getBinding().navViewPay.addHeaderView(handerView);
        RelativeLayout ll = handerView.findViewById(R.id.transaction_right_layout);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) ll.getLayoutParams();
        lp.height = getFragments().getView().getMeasuredHeight();
        ll.setLayoutParams(lp);
        editScreen = handerView.findViewById(R.id.transaction_right_screen);
        wx = handerView.findViewById(R.id.transaction_right_wx);
        card = handerView.findViewById(R.id.transaction_right_card);
        alipay = handerView.findViewById(R.id.transaction_right_alipay);
        close = handerView.findViewById(R.id.transaction_right_button_close);
        screen = handerView.findViewById(R.id.transaction_right_button_screen);
        wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 微信
                setScreenButton(1);
            }
        });
        alipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 支付吧
                setScreenButton(2);
            }
        });
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 银行卡
                setScreenButton(3);
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 重置
                setScreenButton(0);
                editScreen.setText("");
                payMode = "";
            }
        });
        screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 确认筛选
                ScreenBean bean = new ScreenBean();
                bean.setMoney(editScreen.getText().toString().trim());
                bean.setPay(payMode);
                EventBus.getDefault().post(bean);
                DrawerLayout drawer = getBinding().fragmentTransactionLayout;
                drawer.closeDrawer(GravityCompat.END);
            }
        });
    }

    public void setScreenButton(int click) {
        wx.setTextColor(getFragments().getResources().getColor(R.color.color_666666));
        alipay.setTextColor(getFragments().getResources().getColor(R.color.color_666666));
        card.setTextColor(getFragments().getResources().getColor(R.color.color_666666));
        wx.setBackground(getFragments().getResources().getDrawable(R.drawable.shape_gray_button_layout));
        alipay.setBackground(getFragments().getResources().getDrawable(R.drawable.shape_gray_button_layout));
        card.setBackground(getFragments().getResources().getDrawable(R.drawable.shape_gray_button_layout));
        switch (click) {
            case 1:
                wx.setTextColor(getFragments().getResources().getColor(R.color.app_style_blue));
                wx.setBackground(getFragments().getResources().getDrawable(R.drawable.shape_blue_line_button_layout));
                payMode = "wxpay";
                break;
            case 2:
                alipay.setTextColor(getFragments().getResources().getColor(R.color.app_style_blue));
                alipay.setBackground(getFragments().getResources().getDrawable(R.drawable.shape_blue_line_button_layout));
                payMode = "alipay";
                break;
            case 3:
                card.setTextColor(getFragments().getResources().getColor(R.color.app_style_blue));
                card.setBackground(getFragments().getResources().getDrawable(R.drawable.shape_blue_line_button_layout));
                payMode = "unionpay";
                break;
            default:
                break;
        }
    }

    /**
     * 初始化ViewPager Adapter
     */
    private void initFragment() {
        mAdapter = new ViewPageAdapter(getFragments().getChildFragmentManager(), new ArrayList<Fragment>(), new ArrayList<String>());
        mAdapter.fragmentsList.add(TransactionBBFragment.getTransactionBBFragment());//币币
        mAdapter.fragmentsList.add(TransactionFBFragment.getTransactionFBFragment());//法币

        getBinding().transactionBarMain.transactionViewPager.setAdapter(mAdapter);
        getBinding().transactionBarMain.transactionViewPager.setCurrentItem(0);
        getBinding().transactionBarMain.transactionViewPager.setOffscreenPageLimit(mAdapter.getCount());
        getBinding().transactionBarMain.transactionViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setButton(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void CurrentItem(int item) {
        getBinding().transactionBarMain.transactionViewPager.setCurrentItem(item);
    }

    public void setButton(int item) {
        getBinding().transactionBarMain.transactionBb.setBackground(getFragments().getResources().getDrawable(R.drawable.shape_gray_button_layout));
        getBinding().transactionBarMain.transactionFb.setBackground(getFragments().getResources().getDrawable(R.drawable.shape_gray_button_layout));
        getBinding().transactionBarMain.transactionBb.setTextColor(getFragments().getResources().getColor(R.color.color_666666));
        getBinding().transactionBarMain.transactionFb.setTextColor(getFragments().getResources().getColor(R.color.color_666666));
        switch (item) {
            case 0:
                getBinding().transactionBarMain.transactionBb.setBackground(getFragments().getResources().getDrawable(R.drawable.shape_blue_line_button_layout));
                getBinding().transactionBarMain.transactionBb.setTextColor(getFragments().getResources().getColor(R.color.app_style_blue));
                break;
            case 1:
                getBinding().transactionBarMain.transactionFb.setBackground(getFragments().getResources().getDrawable(R.drawable.shape_blue_line_button_layout));
                getBinding().transactionBarMain.transactionFb.setTextColor(getFragments().getResources().getColor(R.color.app_style_blue));
                break;
            default:
                break;
        }
    }

    /**
     * 获取 Tab 数据
     */
    public void getTab() {
        FaceApiTest.getV1ApiServiceTest().getTabCoins()
                .compose(RxSchedulers.<ApiModel<List<TabCoinsEntity>>>io_main())
                .subscribe(new RxObserver<List<TabCoinsEntity>>(getContexts(), getFragments().getTags(), false) {
                    @Override
                    public void onSuccess(List<TabCoinsEntity> list) {
                        try {
                            Log.e("cjh>>>", "getTab:" + new Gson().toJson(list));
                            if (!EmptyUtil.isEmpty(list)) {
                                List<String> strings = new ArrayList<String>();
                                for (int i = 0; i < list.size(); i++) {
                                    strings.add(list.get(i).getName());
                                }
                                initTabLayout(strings); // 初始化 tab
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 获得Tab下面各种币
     */
    public void getCoins(String title) {
        FaceApiTest.getV1ApiServiceTest().getCoins(title)
                .compose(RxSchedulers.<ApiModel<List<CoinEntity>>>io_main())
                .subscribe(new RxObserver<List<CoinEntity>>(getContexts(), getFragments().getTags(), false) {
                    @Override
                    public void onSuccess(List<CoinEntity> list) {
                        try {
                            Log.e("cjh>>>", "获得Tab下面各种币：" + new Gson().toJson(list));
                            if (!EmptyUtil.isEmpty(adapter)) {
                                if (!EmptyUtil.isEmpty(list)) {
                                    adapter.clear();
                                    adapter.addAll(list);
                                } else {
                                    //占位图
                                    easyRecyclerView.setEmptyView(R.layout.comment_view_seat_layout);
                                    easyRecyclerView.showEmpty();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void initTabLayout(final List<String> list) {
        if (EmptyUtil.isEmpty(list)) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(list.get(i)));
        }
        //  初始化换币种  请求数据
        type = list.get(0);
        getCoins(type);
        tabLayout.setTabMode(0);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                type = list.get(tab.getPosition());
                getCoins(type);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    // 初始化 币币 选择币种 列表
    private void initQuotationAdapter() {
        adapter = new TransactionSelectCoinsAdapter(getContexts());
        easyRecyclerView.setLayoutManager(new LinearLayoutManager(getContexts()));
        easyRecyclerView.setAdapter(adapter);
        easyRecyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                easyRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getCoins(type);
                    }
                }, 1000);
            }
        });

        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // 选择其他币种 回来刷新交易页面数据
//                UpdateCoinModel model = new UpdateCoinModel();
//                model.setCoinId(adapter.getItem(position).getMarket_id()+"");
//                model.setName(adapter.getItem(position).getName()+"/"+adapter.getItem(position).getPay_name());
                EventBus.getDefault().post(adapter.getItem(position));
                DrawerLayout drawer = getBinding().fragmentTransactionLayout;
                drawer.closeDrawer(GravityCompat.START);
//                QuotationActivity.JUMP(getContexts(), adapter.getItem(position).getCoin_id() + "");
            }
        });
    }

}
