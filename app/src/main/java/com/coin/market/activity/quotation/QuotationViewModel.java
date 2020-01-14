package com.coin.market.activity.quotation;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.coin.market.R;
import com.coin.market.adapter.QuotationDealAdapter;
import com.coin.market.adapter.QuotationDepthAdapter;
import com.coin.market.adapter.QuotationDepthSellAdapter;
import com.coin.market.adapter.QuotationNavigationAdapter;
import com.coin.market.databinding.ActivityQuotationLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import org.litepal.util.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import teng.wang.comment.api.FaceApiMbr;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.CoinEntity;
import teng.wang.comment.model.DepthModel;
import teng.wang.comment.model.MarketsDTO;
import teng.wang.comment.model.MarketsModel;
import teng.wang.comment.model.QuotationContentModel;
import teng.wang.comment.model.QuotationDealModel;
import teng.wang.comment.model.TabCoinsEntity;
import teng.wang.comment.model.ZbjkExchangeModel;
import teng.wang.comment.retrofit.FaceBaseUrl;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.log.Log;

import static org.litepal.LitePalApplication.getContext;

public class QuotationViewModel extends BaseActivityViewModel<QuotationActivity, ActivityQuotationLayoutBinding> {

    private QuotationNavigationAdapter adapter;
    private EasyRecyclerView recycler;
    private TabLayout naviTabLayout;
    private TabLayout bottomTabLayout;// 选择币种
    public String coinName = "";
    private int itemType;
    private String loadUrl;
    private QuotationDepthAdapter depthAdapter;
    private QuotationDepthSellAdapter depthAdapter2;
    private QuotationDealAdapter dealAdapter;
    private int priceScale;
    private int amountScale;

    public QuotationViewModel(QuotationActivity activity, ActivityQuotationLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        initHeaderLayout(); // 初始化  NavigationView内布局 选择币种
        initQuotationAdapter(); // 初始化 行情adapter
        initBottomTabLayout(); // 初始化下方webView Tab
        initDepthAdapter(); // 初始化下方深度数据
        bindValue(getActivity().coinEntity);
        setWebView(FaceBaseUrl.V1_KURL+"#/kline/"+getActivity().coinEntity.getId());
//        setWebView("http://159.138.49.66:82/#/kline/1");
//        getDepthH5(getActivity().MarketId); // 根据 币种Id 加载WebView h5页面
//        getMarketDetail(getActivity().MarketId); // // 行情 深度数据
//        getTab();
//        getQuotationInfo(getActivity().coinId);
    }

    private void bindValue(MarketsDTO model){

        getBinding().appBarQuotation.tvQuotationName.setText(model.getName());
        getBinding().appBarQuotation.highView.setText(StringUtils.double2String(model.getHigh().doubleValue(), model.getPriceScale()) + "");
        getBinding().appBarQuotation.lowView.setText(StringUtils.double2String(model.getLow().doubleValue(), model.getPriceScale()) + "");
        getBinding().appBarQuotation.priceView.setText(StringUtils.double2String(model.getPrice().doubleValue(), model.getPriceScale()) + "");
        getBinding().appBarQuotation.priceViewCny.setText("≈" + StringUtils.double2String(model.getPriceCny().doubleValue(), model.getPriceCnyScale()));
        getBinding().appBarQuotation.rateView.setText(model.getRate() + "%");
        getBinding().appBarQuotation.l24hView.setText(model.getNumber() + "");

        getBinding().appBarQuotation.text1.setText("买盘 数量(" + model.getName() + ")");
        getBinding().appBarQuotation.text2.setText("价格(" + model.getPrice() + ")");
        getBinding().appBarQuotation.text3.setText("数量(" + model.getName() + ")" + " 卖盘");
    }

    private void initHeaderLayout() {
        View headerLayout = getBinding().quotationNavView.inflateHeaderView(R.layout.view_navigation_quotation_layout);
        recycler = headerLayout.findViewById(R.id.quotation_navigation_recycler);
        naviTabLayout = headerLayout.findViewById(R.id.quotation_navigation_tab_layout);
    }

    // 初始化 交易价格 实时买卖adapter
    private void initDepthAdapter() {
        depthAdapter = new QuotationDepthAdapter(getActivity()); // 深度1
        depthAdapter2 = new QuotationDepthSellAdapter(getActivity()); // 深度2
        dealAdapter = new QuotationDealAdapter(getActivity()); // 成交

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        getBinding().appBarQuotation.quotationRecycler1.setLayoutManager(layoutManager);
        getBinding().appBarQuotation.quotationRecycler1.setAdapter(depthAdapter);
        getBinding().appBarQuotation.quotationRecycler1.setNestedScrollingEnabled(false);//禁止滑动

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
        getBinding().appBarQuotation.quotationRecycler2.setLayoutManager(layoutManager1);
        getBinding().appBarQuotation.quotationRecycler2.setAdapter(depthAdapter2);
        getBinding().appBarQuotation.quotationRecycler2.setNestedScrollingEnabled(false);//禁止滑动

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity());
        getBinding().appBarQuotation.quotationRecyclerCj.setLayoutManager(layoutManager2);
        getBinding().appBarQuotation.quotationRecyclerCj.setAdapter(dealAdapter);
        getBinding().appBarQuotation.quotationRecyclerCj.setNestedScrollingEnabled(false);//禁止滑动
    }


    /**
     * 获取 Tab 数据
     */
    public void getTab() {
        FaceApiTest.getV1ApiServiceTest().getTabCoins()
                .compose(RxSchedulers.<ApiModel<List<TabCoinsEntity>>>io_main())
                .subscribe(new RxObserver<List<TabCoinsEntity>>(getActivity(), getActivity().Tag, false) {
                    @Override
                    public void onSuccess(List<TabCoinsEntity> list) {
                        try {
                            android.util.Log.e("cjh>>>", "getTab:" + new Gson().toJson(list));
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
                .subscribe(new RxObserver<List<CoinEntity>>(getActivity(), getActivity().Tag, true) {
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
                                    recycler.setEmptyView(R.layout.comment_view_seat_layout);
                                    recycler.showEmpty();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 行情 WebView h5
     */
    public void getDepthH5(final String id) {
        FaceApiMbr.getV1ApiServiceMbr().getDepthH5(FaceApplication.getToken())
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, false) {
                    @Override
                    public void onSuccess(String str) {
                        try {
                            Log.e("cjh>>>行情 WebView h5:","" + str);
                            loadUrl = str + "/#/chart/" + id;
                            String kine = str + "/#/kline/" + id;
                            setWebView(kine); // k先图
                            setWebViewBottom(loadUrl);     // 热力图
                            Log.e("cjh>>>", "chart:" + loadUrl);
                            Log.e("cjh>>>", "kline:" + kine);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 获取交易对详情  行情深度数据
     */
    public void getMarketDetail(final String market_id) {
        FaceApiTest.getV1ApiServiceTest().getCoinDepth(FaceApplication.getToken(),"BTC_USDT")
                .compose(RxSchedulers.<ApiModel<DepthModel>>io_main())
                .subscribe(new RxObserver<DepthModel>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(final DepthModel model) {
                        Log.e("cjh>>>","行情深度数据:" + new Gson().toJson(model));
//                        getBinding().appBarQuotation.tvQuotationName.setText("BTC/USDT");
//                        getBinding().appBarQuotation.highView.setText(StringUtils.double2String(model.getHigh(), model.getPriceScale()) + "");
//                        getBinding().appBarQuotation.lowView.setText(StringUtils.double2String(model.getLow(), model.getPriceScale()) + "");
//                        getBinding().appBarQuotation.priceView.setText(StringUtils.double2String(model.getPrice(), model.getPriceScale()) + "");
//                        getBinding().appBarQuotation.priceViewCny.setText("≈" + StringUtils.double2String(model.getPrice_cny(), 2));
//                        getBinding().appBarQuotation.rateView.setText(model.getRate() + "%");
//                        getBinding().appBarQuotation.l24hView.setText(model.getNumber() + "");
//
//                        getBinding().appBarQuotation.text1.setText("买盘 数量(" + model.getName() + ")");
//                        getBinding().appBarQuotation.text2.setText("价格(" + model.getPay_name() + ")");
//                        getBinding().appBarQuotation.text3.setText("数量(" + model.getName() + ")" + " 卖盘");

                        getDepth(market_id); // 刷新深度数据
                        getDeal(market_id); // 刷新成交数据
                    }
                });
    }

    /**
     * 币种 深度数据 H5下方 深度
     */
    public void getDepth(final String id) {
        FaceApiTest.getV1ApiServiceTest().getCoinDepth(FaceApplication.getToken(),id)
                .compose(RxSchedulers.<ApiModel<DepthModel>>io_main())
                .subscribe(new RxObserver<DepthModel>(getActivity(), getActivity().Tag, false) {
                    @Override
                    public void onSuccess(DepthModel model) {
                        try {
                            priceScale = model.getPriceScale();
                            amountScale = model.getAmountScale();
                            Log.e("cjh>>>id:" + id, "  深度图>>DepthModel:" + new Gson().toJson(model));
                            depthAdapter.getScale(priceScale, amountScale);
                            depthAdapter.clear();
                            depthAdapter.addAll(model.getBuys());
                            Collections.reverse(model.getSells());
                            depthAdapter2.getScale(priceScale, amountScale);
                            depthAdapter2.clear();
                            depthAdapter2.addAll(model.getSells());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 行情 成交数据
     */
    public void getDeal(final String id) {
        FaceApiTest.getV1ApiServiceTest().getDealData(id)
                .compose(RxSchedulers.<ApiModel<List<QuotationDealModel>>>io_main())
                .subscribe(new RxObserver<List<QuotationDealModel>>(getActivity(), getActivity().Tag, false) {
                    @Override
                    public void onSuccess(List<QuotationDealModel> list) {
                        try {
                            Log.e("行情成交：",""+ new Gson().toJson(list));
                            if (!EmptyUtil.isEmpty(list)) {
                                dealAdapter.clear();
                                dealAdapter.addAll(list);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 行情 简介
     */
    public void getQuotationInfo(String MarketId) {
        FaceApiTest.getV1ApiServiceTest().getQuotationInfo(MarketId)
                .compose(RxSchedulers.<ApiModel<QuotationContentModel>>io_main())
                .subscribe(new RxObserver<QuotationContentModel>(getActivity(), getActivity().Tag, false) {
                    @Override
                    public void onSuccess(QuotationContentModel model) {
                        try {
                            if (!EmptyUtil.isEmpty(model)) {
                                getBinding().appBarQuotation.jjTime.setText(model.getIssue_date()); // 发行时间
                                getBinding().appBarQuotation.jjFxzl.setText(model.getIssue_gross()); // 发行总量
                                getBinding().appBarQuotation.jjLtzl.setText(model.getCirculate_gross()); // 流通总量
                                getBinding().appBarQuotation.jjZcjg.setText(model.getCrowd_price()+""); // 众筹价格
                                getBinding().appBarQuotation.jjBps.setText(model.getWhite_book_url()); //白皮书
                                getBinding().appBarQuotation.jjGw.setText(model.getOfficial_website_url()); // 官网
                                getBinding().appBarQuotation.jjQkcx.setText(model.getBlock_the_query_url()); // 区块查询
                                getBinding().appBarQuotation.jjText.setText(model.getBrief_introduction()); // 简介
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 货币行情  上方货币行情
     */
    public void getCoin() {
        FaceApiTest.getV1ApiServiceTest().getCoin(1,1)
                .compose(RxSchedulers.<ApiModel<MarketsModel>>io_main())
                .subscribe(new RxObserver<MarketsModel>(getActivity(), getActivity().Tag, false) {
                    @Override
                    public void onSuccess(MarketsModel model) {
                        try {
                            if (null==model)
                                return;
                            Log.e("cjh>>>", "实时行情 K线图" + new Gson().toJson(model.getList()));
                            List<MarketsDTO> list = model.getList();
                            if (null!=list&&list.size()>0){
                                MarketsDTO marketsDTO = list.get(0);
                                bindValue(marketsDTO);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrors(int eCode) {
                        //getFragments().timerCancel();
                    }
                });
    }
    /**
     * 初始化 货币行情分类  Navigation adapter
     */
    private void initQuotationAdapter() {
        if (EmptyUtil.isEmpty(recycler)) {
            return;
        }
        adapter = new QuotationNavigationAdapter(getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // item 跳转去此币种的行情
                // QuotationActivity.JUMP(getContexts());
                getActivity().MarketId = adapter.getItem(position).getMarket_id() + "";
                getMarketDetail(getActivity().MarketId); // // 行情 深度数据
                getDepthH5(getActivity().MarketId); // 行情 H5
                getQuotationInfo(getActivity().MarketId); // 行情 简介
                DrawerLayout drawer = getBinding().quotationDrawerLayout;
                drawer.closeDrawer(GravityCompat.START);
//                getActivity().coinEntity = adapter.getItem(position);
            }
        });
    }

    private void initTabLayout(final List<String> list) {
        if (EmptyUtil.isEmpty(list)) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            naviTabLayout.addTab(naviTabLayout.newTab().setText(list.get(i)));
        }
        naviTabLayout.setTabMode(0);
        getCoins(list.get(0));
        naviTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                coinName = list.get(tab.getPosition());
                getCoins(coinName);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * 本页面 Tab   深度、成交、简介
     */
    private void initBottomTabLayout() {
        bottomTabLayout = getBinding().appBarQuotation.tabLayout;
        bottomTabLayout.addTab(bottomTabLayout.newTab().setText(R.string.quotation_tab_name_depth));
        bottomTabLayout.addTab(bottomTabLayout.newTab().setText(R.string.quotation_tab_name_amount));
        bottomTabLayout.addTab(bottomTabLayout.newTab().setText(R.string.quotation_tab_name_info));
        bottomTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        //深度图
                        getBinding().appBarQuotation.quotationSd.setVisibility(View.VISIBLE);
                        getBinding().appBarQuotation.quotationCj.setVisibility(View.GONE);
                        getBinding().appBarQuotation.quotationJj.setVisibility(View.GONE);
                        break;
                    case 1:
                        //成交
                        getBinding().appBarQuotation.quotationSd.setVisibility(View.GONE);
                        getBinding().appBarQuotation.quotationCj.setVisibility(View.VISIBLE);
                        getBinding().appBarQuotation.quotationJj.setVisibility(View.GONE);
                        break;
                    case 2:
                        //简介
                        getBinding().appBarQuotation.quotationSd.setVisibility(View.GONE);
                        getBinding().appBarQuotation.quotationCj.setVisibility(View.GONE);
                        getBinding().appBarQuotation.quotationJj.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }



    private void setWebView(String url) {
        WebSettings webSettings = getBinding().appBarQuotation.webViewChart.getSettings();
//        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheMaxSize(1024 * 1024 * 8);
        webSettings.setAllowFileAccess(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webSettings.setDisplayZoomControls(false);
            webSettings.setSupportZoom(true);
            webSettings.setBuiltInZoomControls(true);
        }
        getBinding().appBarQuotation.webViewChart.loadUrl(url);
        getBinding().appBarQuotation.webViewChart.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http") || url.startsWith("https")) {
                    view.loadUrl(url);
                    return true;
                } else {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        view.getContext().startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });


    }

    private void setWebViewBottom(String url) {
        WebSettings webSettings = getBinding().appBarQuotation.webViewDepth.getSettings();
//        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        //加载需要显示的网页
        getBinding().appBarQuotation.webViewDepth.loadUrl(url);
    }




}
