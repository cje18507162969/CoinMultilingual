package com.coin.market.fragment.homesquare;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.coin.market.R;
import com.coin.market.activity.home.feedback.PublishFeedbackActivity;
import com.coin.market.activity.home.news.HomeNewsActivity;
import com.coin.market.activity.home.share.ShareActivity;
import com.coin.market.activity.mine.identity.IdentityActivity;
import com.coin.market.adapter.HomeCoinAdapter;
import com.coin.market.adapter.HomeMarketsAdapter;
import com.coin.market.adapter.PointAdapter;
import com.coin.market.databinding.FragmentHomesqureLayoutBinding;
import com.coin.market.fragment.hot.HotFragment;
import com.coin.market.util.EmptyUtil;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.paradoxie.autoscrolltextview.VerticalTextview;

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

public class HomeViewModel extends BaseFragmentViewModel<HomeFragment, FragmentHomesqureLayoutBinding> {

    private String Type = "rate";  // rate：涨幅榜   amount：成交榜   new：新币榜
    private HomeCoinAdapter coinAdapter;  // 上方货币行情
    private PointAdapter pointAdapter; // 上方货币行情 指示器
    private ViewPageAdapter mAdapter; // hot
    HomeMarketsAdapter homeMarketsAdapter;
    private List<PointsModel> points = new ArrayList<PointsModel>();
    private int Position = 0;

    public HomeViewModel(HomeFragment fragment, FragmentHomesqureLayoutBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initView() {
        initCoinAdapter(); // 初始化 货币行情adapter
        initPointAdapter();
        initMarketAdapter(); // 初始化 货币行情分类adapter
        getCoin(); //货币行情
        getRanking(1); // 币种榜单
        getRefreshData(); // 刷新数据

        if (!EmptyUtil.isEmpty(points)){
            points.get(Position).setSelect(true);
        }
    }

    /**
     * 刷新数据
     */
    private void getRefreshData() {
//        getBinding().appBarMain.homeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        getNews(); //公告 喇叭
//                        getCoin(); //货币行情
//                        getRanking(1);//货币行情分类
//                        if (FaceApplication.isLogin()) {// 下拉刷新个人信息
//                            getUserInfo(FaceApplication.getToken());
//                        }
//                        // 加载完数据设置为不刷新状态，将下拉进度收起来
//                        getBinding().appBarMain.homeRefreshLayout.setRefreshing(false);
//                    }
//                }, 1200);
//            }
//        });
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
                            model.setMobile(data.getMobile()+"");
                            model.setAuthentication(data.getAuthentication());
                            DataKeeper.put(getContexts(), SPConstants.USERINFOMODEL,model);
                            if (!TextUtils.isEmpty(data.getMobile())){

                            }
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
     *    获取充币  币种类型
     */
    public void getCoinList() {
        FaceApiTest.getV1ApiServiceTest().getTbCionList(FaceApplication.getToken())
                .compose(RxSchedulers.<ApiModel<List<AccountsDTO>>>io_main())
                .subscribe(new RxObserver<List<AccountsDTO>>(getContexts(), getFragments().getTag(), false) {
                    @Override
                    public void onSuccess(List<AccountsDTO> list) {
                        try {
                            Log.e("cjh>>>", "提币选择币种：" + new Gson().toJson(list));
                            if (list!=null&&list.size()>0){
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
    public void getBanner(final BannerViewPager bvp) {
        FaceApiTest.getV1ApiServiceTest().getBanner(FaceApplication.getToken(),FaceApplication.getTenantCode())
                .compose(RxSchedulers.<ApiModel<List<BannerViewPager.BannerItemBean>>>io_main())
                .subscribe(new RxObserver<List<BannerViewPager.BannerItemBean>>(getContexts(), getFragments().getTags(), false) {
                    @Override
                    public void onSuccess(List<BannerViewPager.BannerItemBean> list) {
                        try {
                            initBanner(list,bvp);
//                            initBanner(list);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 公告
     */
    public void getNews(final VerticalTextview mVerticalTextview) {
        FaceApiTest.getV1ApiServiceTest().getNotice(FaceApplication.getToken(),FaceApplication.getTenantCode())
                .compose(RxSchedulers.<ApiModel<List<ArticlesDTO>>>io_main())
                .subscribe(new RxObserver<List<ArticlesDTO>>(getContexts(), getFragments().getTags(), false) {
                    @Override
                    public void onSuccess(final List<ArticlesDTO> entity) {
                        try {
                            Log.e("cjh>>>","公告BulletinEntity:" + new Gson().toJson(entity));
                            ArrayList<String> titleList = new ArrayList<String>();
                            for (int i = 0; i < entity.size(); i++) {
                                titleList.add(entity.get(i).getTitle());
                            }
                            mVerticalTextview.setTextList(titleList);
                            mVerticalTextview.setText(16, 8, getContexts().getResources().getColor(R.color.app_home_text));
                            mVerticalTextview.setTextStillTime(2000);//设置停留时长间隔
                            mVerticalTextview.setAnimTime(300);//设置进入和退出的时间间隔
                            mVerticalTextview.setOnItemClickListener(new VerticalTextview.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    HomeNewsActivity.JUMP(getContexts(), entity.get(position), position + "");
                                }
                            });
                            mVerticalTextview.startAutoScroll();
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
    public void getCoin() {
        page = 1;
        limit = 3;
        FaceApiTest.getV1ApiServiceTest().getCoin(page,limit)
                .compose(RxSchedulers.<ApiModel<MarketsModel>>io_main())
                .subscribe(new RxObserver<MarketsModel>(getContexts(), getFragments().getTags(), false) {
                    @Override
                    public void onSuccess(MarketsModel model) {
                        try {
//                            Log.e("cjh>>>", "货币行情,上方货币行情maketsEntity:" + new Gson().toJson(model.getList()));
                            if (!EmptyUtil.isEmpty(mAdapter) && !EmptyUtil.isEmpty(model.getList())) {
                                getHotData(model.getList()); // 初始化货币行情 hot
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
     * 首页行情分类  涨幅榜 成交榜 新币榜
     */
    public int mpage = 1;
    public int mlimit = 10;
    public void getRanking(int page) {
        mpage = page;
        FaceApiTest.getV1ApiServiceTest().getCoin(page,10)
                .compose(RxSchedulers.<ApiModel<MarketsModel>>io_main())
                .subscribe(new RxObserver<MarketsModel>(getContexts(), getFragments().getTags(), false) {
                    @Override
                    public void onSuccess(MarketsModel model) {
                        try {
//                            Log.e("cjh>>>", "实时行情maketsEntity:" + new Gson().toJson(model.getList()));
                            if (!EmptyUtil.isEmpty(homeMarketsAdapter) && !EmptyUtil.isEmpty(model.getList())) {
                                try {
                                    if (mpage == 1 || mpage == 0) {
                                        if (null != model && model.getList().size() > 0) {
                                            homeMarketsAdapter.clear();
                                            homeMarketsAdapter.addAll(model.getList());
                                        } else {
                                            //占位图
                                            getBinding().appBarMain.homeFlauntRecycler.setEmptyView(R.layout.comment_view_seat_layout);
                                            getBinding().appBarMain.homeFlauntRecycler.showEmpty();
                                        }
                                    } else {
                                        if (model.getList().size() <= 0) {
                                            mpage = 1;
                                            homeMarketsAdapter.stopMore();
                                        } else {
                                            homeMarketsAdapter.addAll(model.getList());
                                        }
                                    }
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
                                    List<MarketsDTO> allData = homeMarketsAdapter.getAllData();
                                    List<MarketsDTO> list = model.getList();
                                    for (int i = 0; i < list.size(); i++) {
                                        for (int j = 0; j < allData.size(); j++) {
                                            if (i==j){
                                                allData.get(j).setName(list.get(i).getName());
                                                allData.get(j).setPrice(list.get(i).getPrice());
                                                allData.get(j).setPriceScale(list.get(i).getPriceScale());
                                                allData.get(j).setRate(list.get(i).getRate());
                                                homeMarketsAdapter.notifyItemChanged(j);
                                            }
                                        }
                                    }

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






    // 初始化 货币行情adapter
    private void initCoinAdapter() {
        coinAdapter = new HomeCoinAdapter(getContexts());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContexts());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        getBinding().appBarMain.coinRecycler.setLayoutManager(layoutManager);
//        getBinding().appBarMain.coinRecycler.setAdapter(coinAdapter);
//        coinAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
////                QuotationActivity.JUMP(getContexts(), coinAdapter.getItem(position));
//            }
//        });
    }

    // 初始化 热门货币 Hot
    private void initHotAdapter(ViewPager vpHot) {
        mAdapter = new ViewPageAdapter(getFragments().getChildFragmentManager(), new ArrayList<Fragment>(), new ArrayList<String>());
        vpHot.removeAllViews();
        vpHot.setAdapter(mAdapter);
        vpHot.setCurrentItem(0);
        vpHot.setOffscreenPageLimit(mAdapter.getCount());
        vpHot.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        if (null==mAdapter)
            return;
        points.clear();
        pointAdapter.clear();
        mAdapter.fragmentsList.clear();
        MarketsEntity marketsEntity = new MarketsEntity();
        marketsEntity.setList(list);
        if (marketsEntity.getList().size() <= 3) {
            mAdapter.fragmentsList.add(HotFragment.getHotFragment(marketsEntity));//
            PointsModel point = new PointsModel();
            points.add(point);
        } else {
            for (int i = 0; i < marketsEntity.getList().size(); i++) {
                if ((i + 1) % 3 == 0) {
                    MarketsEntity item = new MarketsEntity();
                    List<MarketsDTO> lista = new ArrayList<>();
                    lista.add(list.get(i - 2));
                    lista.add(list.get(i - 1));
                    lista.add(list.get(i));
                    item.setList(lista);
                    mAdapter.fragmentsList.add(HotFragment.getHotFragment(item));//
                    PointsModel point = new PointsModel();
                    points.add(point);
                } else {
                    if ((i + 1) == marketsEntity.getList().size() && (i + 1) % 3 != 0) {
                        MarketsEntity item = new MarketsEntity();
                        List<MarketsDTO> lista = new ArrayList<>();
                        if ((i+1 + 1) % 3 == 0){
                            lista.add(marketsEntity.getList().get(i - 1));
                            lista.add(marketsEntity.getList().get(i));
                        }else if ((i+1 + 2) % 3 == 0){
                            lista.add(marketsEntity.getList().get(i));
                        }
                        item.setList(lista);
                        mAdapter.fragmentsList.add(HotFragment.getHotFragment(item));//
                        PointsModel point = new PointsModel();
                        points.add(point);
                    }
                }
            }
        }
        mAdapter.notifyDataSetChanged();
        points.get(Position).setSelect(true);
        pointAdapter.addAll(points);
    }

    //  初始化指示器 小点
    private void initPointAdapter() {
        pointAdapter = new PointAdapter(getContexts());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContexts());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        getBinding().appBarMain.hotRecycler.setLayoutManager(layoutManager);
//        getBinding().appBarMain.hotRecycler.setAdapter(pointAdapter);
    }



    /**
     *  初始化 货币行情分类 adapter
     */

    VerticalTextview mVerticalTextview = null;
    ViewPager vpHot = null;

    public void initMarketAdapter() {
        homeMarketsAdapter = new HomeMarketsAdapter(getContexts());
        getBinding().appBarMain.homeFlauntRecycler.setLayoutManager(new LinearLayoutManager(getContexts()));
        getBinding().appBarMain.homeFlauntRecycler.setAdapter(homeMarketsAdapter);
        getBinding().appBarMain.homeFlauntRecycler.setVerticalScrollBarEnabled(false);

        homeMarketsAdapter.addHeader(new RecyclerArrayAdapter.ItemView() {
            @Override
            public View onCreateView(ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) getContexts().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View contextView = inflater.inflate(R.layout.layout_home_header,null);
                BannerViewPager bannerViewPager =  contextView.findViewById(R.id.banner_view_pager);
                getBanner(bannerViewPager);

                mVerticalTextview = contextView.findViewById(R.id.mVerticalTextview);
                getNews(mVerticalTextview);

                vpHot = contextView.findViewById(R.id.hot_viewpager);
                initHotAdapter(vpHot);
                getCoin();

                contextView.findViewById(R.id.home_realnameauthentication).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IdentityActivity.JUMP(getContexts());
                    }
                });


                contextView.findViewById(R.id.home_help_center_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PublishFeedbackActivity.JUMP(getContexts());
                    }
                });


                contextView.findViewById(R.id.home_share_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShareActivity.JUMP(getContexts());
                    }
                });


                return contextView;
            }

            @Override
            public void onBindView(View headerView) {
                ((ViewGroup)headerView).requestDisallowInterceptTouchEvent(true);
            }
        });
        getBinding().appBarMain.homeFlauntRecycler.setScrollbarFadingEnabled(false);
        getBinding().appBarMain.homeFlauntRecycler.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBinding().appBarMain.homeFlauntRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mpage = 1;
                        getRanking(mpage);
                        if (null!=mVerticalTextview){
                            getNews(mVerticalTextview);
                        }

                        if (null!=vpHot){
                            initHotAdapter(vpHot);
                            getCoin(); //货币行情
                        }

                    }
                }, 1000);
            }
        });
        homeMarketsAdapter.setMore(R.layout.view_more, new RecyclerArrayAdapter.OnMoreListener() {
            @Override
            public void onMoreShow() {
                getBinding().appBarMain.homeFlauntRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mpage++;
                        getRanking(mpage);
                    }
                }, 1000);
            }

            @Override
            public void onMoreClick() {

            }
        });
        homeMarketsAdapter.setError(R.layout.view_error_foot, new RecyclerArrayAdapter.OnErrorListener() {
            @Override
            public void onErrorShow() {

            }

            @Override
            public void onErrorClick() {
                homeMarketsAdapter.resumeMore();
            }
        });
        homeMarketsAdapter.setNoMore(R.layout.view_nomore);
    }

    // 初始化 banner
    private void initBanner(List<BannerViewPager.BannerItemBean> banners) {
//        getBinding().appBarMain.bannerViewPager.setData(banners, new BannerViewPager.BannerImageLoader() {
//            @Override
//            public void displayImg(Context context, Object imgPath, ImageView img) {
//                Glide.with(context)
//                        .load(imgPath)
//                        .apply(new RequestOptions())
//                        .into(img);
//            }
//        });
//        getBinding().appBarMain.bannerViewPager.setPageTransformer(new AlphaAndScalePageTransformer())
//                .setPageMargin(30)
//                .setAutoPlay(true)
//                .setOnBannerItemClickListener(new BannerViewPager.OnBannerItemClickListener() {
//                    @Override
//                    public void onClickListener(BannerViewPager.BannerItemBean itemBean) {
//                        // HomeWebActivity.JUMP(getContexts(), "公告详情", "https://www.baidu.com/");
//                    }
//                });
    }

    // 初始化 banner
    private void initBanner(List<BannerViewPager.BannerItemBean> banners,BannerViewPager bvp) {
        bvp.setData(banners, new BannerViewPager.BannerImageLoader() {
            @Override
            public void displayImg(Context context, Object imgPath, ImageView img) {
                Glide.with(context)
                        .load(imgPath)
                        .apply(new RequestOptions())
                        .into(img);
            }
        });
        bvp.setPageTransformer(new AlphaAndScalePageTransformer())
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
