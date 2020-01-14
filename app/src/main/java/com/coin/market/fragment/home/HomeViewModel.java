package com.coin.market.fragment.home;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.coin.market.R;
import com.coin.market.activity.home.news.HomeNewsActivity;
import com.coin.market.activity.quotation.QuotationActivity;
import com.coin.market.adapter.HomeCoinAdapter;
import com.coin.market.adapter.HomeMarketAdapter;
import com.coin.market.adapter.HomeMarketsAdapter;
import com.coin.market.adapter.PointAdapter;
import com.coin.market.databinding.FragmentHomeLayoutBinding;
import com.coin.market.fragment.hot.HotFragment;
import com.coin.market.util.EmptyUtil;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.paradoxie.autoscrolltextview.VerticalTextview;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import teng.wang.comment.SPConstants;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseFragmentViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.AccountsDTO;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.ArticlesDTO;
import teng.wang.comment.model.MarketsDTO;
import teng.wang.comment.model.MarketsEntity;
import teng.wang.comment.model.MarketsModel;
import teng.wang.comment.model.PointsModel;
import teng.wang.comment.model.UserInfosDTO;
import teng.wang.comment.model.UsersEntity;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.DataKeeper;
import teng.wang.comment.utils.log.Log;
import teng.wang.comment.widget.AlphaAndScalePageTransformer;
import teng.wang.comment.widget.BannerViewPager;
import teng.wang.comment.widget.ViewPageAdapter;

public class HomeViewModel extends BaseFragmentViewModel<HomeFragment, FragmentHomeLayoutBinding> {

    private String Type = "rate";  // rate：涨幅榜   amount：成交榜   new：新币榜
    private HomeCoinAdapter coinAdapter;  // 上方货币行情
    private HomeMarketAdapter marketAdapter;  // 下方货币行情分类 榜单
    private PointAdapter pointAdapter; // 上方货币行情 指示器
    private ViewPageAdapter mAdapter; // hot
    private List<PointsModel> points = new ArrayList<PointsModel>();
    private int Position = 0;
    private HomeMarketsAdapter homeMarketsAdapter;

    public HomeViewModel(HomeFragment fragment, FragmentHomeLayoutBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initView() {
        initHotAdapter();
        initCoinAdapter(); // 初始化 货币行情adapter
        initPointAdapter();
        initMarketAdapter(); // 初始化 货币行情分类adapter
//        initTabLayout(); // 初始化 tab
        getBanner();    //轮播图
        getNews(); //公告 喇叭
        getCoin(false); //货币行情
        getRanking(1); // 币种榜单
        getRefreshData(); // 刷新数据

        if (!EmptyUtil.isEmpty(points)) {
            points.get(Position).setSelect(true);
        }

        switch (FaceApplication.getLevel()){
            case 1:
                getBinding().appBarMain.homeShareButton.setVisibility(View.VISIBLE);
                break;
            case  2:  //个人代理
                getBinding().appBarMain.homeShareButton.setVisibility(View.VISIBLE);
                break;
            case  3:  //业务员
                getBinding().appBarMain.homeShareButton.setVisibility(View.VISIBLE);

                break;

        }


    }

    /**
     * 刷新数据
     */
    private void getRefreshData() {
        getBinding().appBarMain.homeRefreshLayout.setEnableRefresh(true);//是否启用下拉刷新功能
        getBinding().appBarMain.homeRefreshLayout.setEnableLoadmore(true);//是否启用上拉加载功能
        getBinding().appBarMain.homeRefreshLayout.setEnableAutoLoadmore(false);//是否启用自动加载功能
        getBinding().appBarMain.homeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                getBanner();    //轮播图
                getNews(); //公告 喇叭
                getCoin(false); //货币行情
                getRanking(1);//货币行情分类
                if (FaceApplication.isLogin()) {// 下拉刷新个人信息
                    getUserInfo(FaceApplication.getToken());
                }
            }
        });
        getBinding().appBarMain.homeRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(final RefreshLayout refreshLayout) {

                if (page == 1)
                    page = 2;
                getRanking(mpage);
            }
        });
    }

    /**
     * 获取用户信息
     * 判断是否已经身份认证
     */
    public void getUserInfo(final String token) {
        FaceApiTest.getV1ApiServiceTest().userInfo(token)
                .compose(RxSchedulers.<ApiModel<UserInfosDTO>>io_main())
                .subscribe(new RxObserver<UserInfosDTO>(getContexts(), getFragments().getTag(), true) {
                    @Override
                    public void onSuccess(UserInfosDTO data) {
                        try {
                            Log.e("cjh>>> token:" + token, "userInfo:" + new Gson().toJson(data));
                            UsersEntity model = FaceApplication.getUserInfoModel();
                            model.setMobile(data.getMobile() + "");
                            model.setAuthentication(data.getAuthentication());
                            DataKeeper.put(getContexts(), SPConstants.USERINFOMODEL, model);
                            getFragments().setUserData();
//                            getFragments().id_flag = data.getId_flag();
//                            if (data.getId_flag().equals("1")) {
//                                getBinding().navView.getMenu().findItem(R.id.nav_publish).setVisible(true);
//                            } else {
//                                getBinding().navView.getMenu().findItem(R.id.nav_publish).setVisible(false);
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrors(int eCode) {
                        // 如果token过期 让用户重新登录  重新获取token
                        if (eCode == 9001) {
                            loginOut();
                        }
                    }
                });
    }


    /**
     * 获取充币  币种类型
     */
    public void getCoinList() {
        FaceApiTest.getV1ApiServiceTest().getTbCionList(FaceApplication.getToken())
                .compose(RxSchedulers.<ApiModel<List<AccountsDTO>>>io_main())
                .subscribe(new RxObserver<List<AccountsDTO>>(getContexts(), getFragments().getTag(), false) {
                    @Override
                    public void onSuccess(List<AccountsDTO> list) {
                        try {
                            Log.e("cjh>>>", "提币选择币种：" + new Gson().toJson(list));
                            if (list != null && list.size() > 0) {
                                getFragments().CoinId = list.get(0).getCoinId();
                                getFragments().CoinName = list.get(0).getCoinName();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void loginOut() {
        //TODO 接受退出登录事件,删除本地用户信息数据库信息，数据库数据等
        DataKeeper.removeAll(getFragments().getActivity());
        EventBus.getDefault().post("exitLogin");
    }

    /**
     * Banner
     */
    public void getBanner() {
        FaceApiTest.getV1ApiServiceTest().getBanner(FaceApplication.getToken(), FaceApplication.getTenantCode())
                .compose(RxSchedulers.<ApiModel<List<BannerViewPager.BannerItemBean>>>io_main())
                .subscribe(new RxObserver<List<BannerViewPager.BannerItemBean>>(getContexts(), getFragments().getTags(), false) {
                    @Override
                    public void onSuccess(List<BannerViewPager.BannerItemBean> list) {
                        try {
                            initBanner(list);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 公告
     */
    public void getNews() {
        FaceApiTest.getV1ApiServiceTest().getNotice(FaceApplication.getToken(), FaceApplication.getTenantCode())
                .compose(RxSchedulers.<ApiModel<List<ArticlesDTO>>>io_main())
                .subscribe(new RxObserver<List<ArticlesDTO>>(getContexts(), getFragments().getTags(), false) {
                    @Override
                    public void onSuccess(final List<ArticlesDTO> entity) {
                        try {
                            Log.e("cjh>>>", "公告BulletinEntity:" + new Gson().toJson(entity));
                            if (null!=entity&&entity.size()>0){
                                ArrayList<String> titleList = new ArrayList<String>();
                                for (int i = 0; i < entity.size(); i++) {
                                    titleList.add(entity.get(i).getTitle());
                                }
                                getBinding().appBarMain.mVerticalTextview.setTextList(titleList);
                                getBinding().appBarMain.mVerticalTextview.setText(16, 8, getContexts().getResources().getColor(R.color.app_home_text));
                                getBinding().appBarMain.mVerticalTextview.setTextStillTime(2000);//设置停留时长间隔
                                getBinding().appBarMain.mVerticalTextview.setAnimTime(300);//设置进入和退出的时间间隔
                                getBinding().appBarMain.mVerticalTextview.setOnItemClickListener(new VerticalTextview.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {
                                        HomeNewsActivity.JUMP(getContexts(), entity.get(position), position + "");
                                    }
                                });
                                getBinding().appBarMain.mVerticalTextview.startAutoScroll();
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
    public int page = 1;
    public int limit = 3;

    public void getCoin(final boolean isrefresh) {
        page = 1;
        limit = 3;
        FaceApiTest.getV1ApiServiceTest().getCoin(page, limit)
                .compose(RxSchedulers.<ApiModel<MarketsModel>>io_main())
                .subscribe(new RxObserver<MarketsModel>(getContexts(), getFragments().getTags(), false) {
                    @Override
                    public void onSuccess(MarketsModel model) {
                        try {
//                            Log.e("cjh>>>", "货币行情,上方货币行情maketsEntity:" + new Gson().toJson(model.getList()));
                            if (!EmptyUtil.isEmpty(coinAdapter) && !EmptyUtil.isEmpty(model.getList())) {
//                                initHotAdapter();
//                                getHotData(model.getList()); // 初始化货币行情 hot
                                setCoinAdapter(model.getList(),isrefresh);
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


    public void setCoinAdapter(List<MarketsDTO> list,boolean isrefresh){
        if(mpage == 1){
            if (null!=list&&list.size()>0){
//                ll_has.setVisibility(View.GONE);
                if (null!=coinAdapter){
                    if (isrefresh){
                        coinAdapter.clear();
                        coinAdapter.addAll(list);
//                        List<MarketsDTO> allData = coinAdapter.getAllData();
//                        for (int i = 0; i < list.size(); i++) {
//                            for (int j = 0; j < allData.size(); j++) {
//                                if (i==j){
//                                    allData.get(j).setName(list.get(i).getName());
//                                    allData.get(j).setPrice(list.get(i).getPrice());
//                                    allData.get(j).setPriceScale(list.get(i).getPriceScale());
//                                    allData.get(j).setRate(list.get(i).getRate());
//                                    coinAdapter.notifyItemChanged(j);
//                                }
//                            }
//                        }
                    }else{
                        coinAdapter.clear();
                        coinAdapter.addAll(list);
                        coinAdapter.notifyDataSetChanged();
                    }
                    coinAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            if (0==position){
                                QuotationActivity.JUMP(getContexts(), coinAdapter.getItem(position));
                            }else {
                                Toast.makeText(getContexts(),"暂未开放",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }else {
//                ll_has.setVisibility(View.VISIBLE);
                return;
            }

        }

    }

    /**
     * 首页行情分类  涨幅榜 成交榜 新币榜
     */
    public int mpage = 1;
    public int mlimit = 10;

    public void getRanking(int page) {
        mpage = page;
        FaceApiTest.getV1ApiServiceTest().getCoin(page, 10)
                .compose(RxSchedulers.<ApiModel<MarketsModel>>io_main())
                .subscribe(new RxObserver<MarketsModel>(getContexts(), getFragments().getTags(), false) {
                    @Override
                    public void onSuccess(MarketsModel model) {
                        try {
                            getBinding().appBarMain.homeRefreshLayout.finishRefresh();
                            getBinding().appBarMain.homeRefreshLayout.finishLoadmore();
//                            Log.e("cjh>>>", "实时行情maketsEntity:" + new Gson().toJson(model.getList()));
                            if (!EmptyUtil.isEmpty(homeMarketsAdapter)) {
                                try {
                                    setAdapter(model.getList());
                                    //占位图
//                                            getBinding().appBarMain.homeFlauntRecycler.setEmptyView(R.layout.comment_view_seat_layout);
//                                            getBinding().appBarMain.homeFlauntRecycler.showEmpty();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrors(int eCode) {
                        //getFragments().timerCancel();
                        getBinding().appBarMain.homeRefreshLayout.finishRefresh();
                        getBinding().appBarMain.homeRefreshLayout.finishLoadmore();
                    }
                });

    }


    /**
     * 定时刷新
     */
    public void getRankingTimer() {
        FaceApiTest.getV1ApiServiceTest().getCoin(page,mpage*10)
                .compose(RxSchedulers.<ApiModel<MarketsModel>>io_main())
                .subscribe(new RxObserver<MarketsModel>(getContexts(), getFragments().getTags(), false) {
                    @Override
                    public void onSuccess(MarketsModel model) {
                        try {
//                            Log.e("cjh>>>", "实时行情maketsEntity:" + new Gson().toJson(model.getList()));
                            if (!EmptyUtil.isEmpty(homeMarketsAdapter) && !EmptyUtil.isEmpty(model.getList())) {
                                try {
                                    homeMarketsAdapter.clear();
                                    homeMarketsAdapter.addAll( model.getList());
//                                    List<MarketsDTO> allData = homeMarketsAdapter.getAllData();
//                                    List<MarketsDTO> list = model.getList();
//                                    for (int i = 0; i < list.size(); i++) {
//                                        for (int j = 0; j < allData.size(); j++) {
//                                            if (i==j){
//                                                allData.get(j).setName(list.get(i).getName());
//                                                allData.get(j).setPrice(list.get(i).getPrice());
//                                                allData.get(j).setPriceScale(list.get(i).getPriceScale());
//                                                allData.get(j).setRate(list.get(i).getRate());
//                                                homeMarketsAdapter.notifyItemChanged(j);
//                                            }
//                                        }
//                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else{
                                if (null!=homeMarketsAdapter)
                                    homeMarketsAdapter.stopMore();
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
     *  设置adapter
     * @param lsJson
     */
    public void setAdapter(List<MarketsDTO> lsJson){

        if(mpage == 1){
            if (null!=lsJson&&lsJson.size()>0){
//                ll_has.setVisibility(View.GONE);
                getFragments().list.clear();
                homeMarketsAdapter.clear();
                getFragments().list.addAll(lsJson);
                homeMarketsAdapter.addAll(lsJson);
//                homeMarketsAdapter.notifyDataSetChanged();
            }else {
//                ll_has.setVisibility(View.VISIBLE);
                return;
            }

        }
        if(homeMarketsAdapter != null){
            if(mpage > 1){
                mpage ++;
                getFragments().list.addAll(lsJson);
                homeMarketsAdapter.addAll(lsJson);
                homeMarketsAdapter.notifyItemRangeInserted(getFragments().list.size()-lsJson.size(),lsJson.size());
                getBinding().appBarMain.homeFlauntRecycler.smoothScrollToPosition(getFragments().list.size()-lsJson.size()-1);
            }else {
                homeMarketsAdapter.notifyDataSetChanged();
            }
        }
    }



    // 初始化 货币行情adapter
    private void initCoinAdapter() {
        coinAdapter = new HomeCoinAdapter(getContexts());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContexts());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        getBinding().appBarMain.coinRecycler.setLayoutManager(layoutManager);
        getBinding().appBarMain.coinRecycler.setAdapter(coinAdapter);
        coinAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                QuotationActivity.JUMP(getContexts(), coinAdapter.getItem(position));
            }
        });
    }

    // 初始化 热门货币 Hot
    private void initHotAdapter() {
//        FragmentTransaction transaction = getFragments().getChildFragmentManager().beginTransaction();
//        List<Fragment> fragments = getFragments().getChildFragmentManager().getFragments();
//        if (fragments != null) {
//
//            for (Fragment childFragment :fragments) {
//                transaction.remove(childFragment);
//
//            }
//            transaction.commit();
//        }
        mAdapter = new ViewPageAdapter(getFragments().getChildFragmentManager(), new ArrayList<Fragment>(), new ArrayList<String>());
        getBinding().appBarMain.hotViewpager.setAdapter(mAdapter);
        getBinding().appBarMain.hotViewpager.setCurrentItem(0);
        getBinding().appBarMain.hotViewpager.setOffscreenPageLimit(mAdapter.getCount());
        getBinding().appBarMain.hotViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                try {
                    Position = position;
                    for (int i = 0; i < points.size(); i++) {
                        points.get(i).setSelect(false);
                    }
                    points.get(position).setSelect(true);
                    pointAdapter.clear();
                    pointAdapter.addAll(points);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void getHotData(List<MarketsDTO> list) {
        MarketsEntity marketsEntity = new MarketsEntity();
        marketsEntity.setList(list);

//        getFragments().getChildFragmentManager().beginTransaction()
//                .add(R.id.fl_coin, HotFragment.getHotFragment(marketsEntity), "f1")        //.addToBackStack("fname")
//                .commit();
//        points.clear();
//        pointAdapter.clear();
//        mAdapter.fragmentsList.clear();
//        viewPager删除缓存fragment
//        FragmentTransaction transaction = getFragments().getChildFragmentManager().beginTransaction();
//        List<Fragment> fragments = getFragments().getChildFragmentManager().getFragments();
//        if (fragments != null) {
//
//            for (Fragment childFragment :fragments) {
//                transaction.remove(childFragment);
//
//            }
//            transaction.commit();
//        }
//        MarketsEntity marketsEntity = new MarketsEntity();
//        marketsEntity.setList(list);
//        if (marketsEntity.getList().size() <= 3) {
//            if (mAdapter.fragmentsList.size()>0){
//                mAdapter.fragmentsList.add(HotFragment.getHotFragment(marketsEntity));//
//                PointsModel point = new PointsModel();
//                points.add(point);
//            }else{
//                mAdapter.fragmentsList.add(HotFragment.getHotFragment(marketsEntity));//
//                PointsModel point = new PointsModel();
//                points.add(point);
//            }
//
//        } else {
//            for (int i = 0; i < marketsEntity.getList().size(); i++) {
//                if ((i + 1) % 3 == 0) {
//                    MarketsEntity item = new MarketsEntity();
//                    List<MarketsDTO> lista = new ArrayList<>();
//                    lista.add(list.get(i - 2));
//                    lista.add(list.get(i - 1));
//                    lista.add(list.get(i));
//                    item.setList(lista);
//                    mAdapter.fragmentsList.add(HotFragment.getHotFragment(item));//
//                    PointsModel point = new PointsModel();
//                    points.add(point);
//                } else {
//                    if ((i + 1) == marketsEntity.getList().size() && (i + 1) % 3 != 0) {
//                        MarketsEntity item = new MarketsEntity();
//                        List<MarketsDTO> lista = new ArrayList<>();
//                        if ((i + 1 + 1) % 3 == 0) {
//                            lista.add(marketsEntity.getList().get(i - 1));
//                            lista.add(marketsEntity.getList().get(i));
//                        } else if ((i + 1 + 2) % 3 == 0) {
//                            lista.add(marketsEntity.getList().get(i));
//                        }
//                        item.setList(lista);
//                        mAdapter.fragmentsList.add(HotFragment.getHotFragment(item));//
//                        PointsModel point = new PointsModel();
//                        points.add(point);
//                    }
//                }
//            }
//        }
//        mAdapter.notifyDataSetChanged();
//        points.get(Position).setSelect(true);
//        pointAdapter.addAll(points);
    }

    //  初始化指示器 小点
    private void initPointAdapter() {
        pointAdapter = new PointAdapter(getContexts());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContexts());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        getBinding().appBarMain.hotRecycler.setLayoutManager(layoutManager);
        getBinding().appBarMain.hotRecycler.setAdapter(pointAdapter);
    }


    /**
     * 初始化 货币行情分类 adapter
     */

    public void initMarketAdapter() {
        homeMarketsAdapter = new HomeMarketsAdapter(getContexts());
        getBinding().appBarMain.homeFlauntRecycler.setLayoutManager(new LinearLayoutManager(getContexts()));
        getBinding().appBarMain.homeFlauntRecycler.setAdapter(homeMarketsAdapter);
        getBinding().appBarMain.homeFlauntRecycler.setNestedScrollingEnabled(false);
        homeMarketsAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (0==position){
                    QuotationActivity.JUMP(getContexts(), coinAdapter.getItem(position));
                }else {
                    Toast.makeText(getContexts(),"暂未开放",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 初始化 banner
    private void initBanner(List<BannerViewPager.BannerItemBean> banners) {
        if (null!=banners&&banners.size()>0){
            getBinding().appBarMain.bannerViewPager.setData(banners, new BannerViewPager.BannerImageLoader() {
                @Override
                public void displayImg(Context context, Object imgPath, ImageView img) {
                    Glide.with(context)
                            .load(imgPath)
                            .apply(new RequestOptions())
                            .into(img);
                }
            });
            getBinding().appBarMain.bannerViewPager.setPageTransformer(new AlphaAndScalePageTransformer())
                    .setPageMargin(30)
                    .setAutoPlay(true)
                    .setOnBannerItemClickListener(new BannerViewPager.OnBannerItemClickListener() {
                        @Override
                        public void onClickListener(BannerViewPager.BannerItemBean itemBean) {
                            // HomeWebActivity.JUMP(getContexts(), "公告详情", "https://www.baidu.com/");
                        }
                    });
        }

    }

}
