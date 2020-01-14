package com.coin.market.fragment.assets;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;

import com.coin.market.R;
import com.coin.market.activity.assets.records.AssetRecordsActivity;
import com.coin.market.adapter.AssetsAccountAdapter;
import com.coin.market.adapter.AssetsAccountsAdapter;
import com.coin.market.adapter.HomeMarketsAdapter;
import com.coin.market.databinding.FragmentAssetsLayoutBinding;
import com.coin.market.fragment.assetsaccount.AssetsAccountFragment;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import teng.wang.comment.api.FaceApiMbr;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseFragmentViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.AccountsDTO;
import teng.wang.comment.model.AccountsModel;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.CoinTotalModel;
import teng.wang.comment.model.CoinWorthModel;
import teng.wang.comment.model.FB_BBEntity;
import teng.wang.comment.model.MarketsModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.DataKeeper;
import teng.wang.comment.utils.log.Log;
import teng.wang.comment.widget.ViewPageAdapter;

/**
 * @author 资产Fragment ViewModel
 * @version v1.0
 * @Time 2018-9-3
 */

public class AssetsViewModel extends BaseFragmentViewModel<AssetsFragment, FragmentAssetsLayoutBinding> {

    private String type = "";
    private int itemType;
    private ViewPageAdapter mAdapter;
    private AssetsAccountFragment fragment_bb, fragment_fb;

    public AssetsAccountsAdapter accountAdapter;
    public AssetsViewModel(AssetsFragment fragment, FragmentAssetsLayoutBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initView() {
        initAccountsAdapter();
        getBBAssetsData(FaceApplication.getToken());
        getAccounts(1);
    }

    private void initTabLayout(CoinWorthModel model) {
        List<String> mTitles = Arrays.asList("币币账户", "法币账户");
        getBinding().quotationTabLayout.addTab(getBinding().quotationTabLayout.newTab().setText(mTitles.get(0)));
        getBinding().quotationTabLayout.addTab(getBinding().quotationTabLayout.newTab().setText(mTitles.get(1)));
        mAdapter = new ViewPageAdapter(getFragments().getActivity().getSupportFragmentManager(), new ArrayList<Fragment>(), mTitles);

        mAdapter.fragmentsList.add(fragment_bb.getAssetsAccountFragment("bb", model));
        mAdapter.fragmentsList.add(fragment_fb.getAssetsAccountFragment("fb", model));
        getBinding().fragmentAssetsViewpager.setAdapter(mAdapter);
        getBinding().fragmentAssetsViewpager.setCurrentItem(0);
        getBinding().fragmentAssetsViewpager.setOffscreenPageLimit(mAdapter.getCount());
        getBinding().quotationTabLayout.setupWithViewPager(getBinding().fragmentAssetsViewpager);

        getBinding().fragmentAssetsViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    getFragments().accountType = "bb_type";
                } else if (position == 1) {
                    getFragments().accountType = "fb_type";
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void clearAdapter() {
//        clearAssetsData(FaceApplication.getToken());
    }

    /**
     * 获取总资产数据
     */
    public void getBBAssetsData(final String token) {
        FaceApiTest.getV1ApiServiceTest().getCoinWorth(token)
                .compose(RxSchedulers.<ApiModel<CoinTotalModel>>io_main())
                .subscribe(new RxObserver<CoinTotalModel>(getContexts(), getFragments().getTag(), true) {
                    @Override
                    public void onSuccess(CoinTotalModel model) { //CoinWorthModel
                        try {
                            Log.e("cjh>>>", "token:"+token+"获取总资产数据" + new Gson().toJson(model));
                            if ("0".equals(getFragments().type)){
                                model.setHide(false);
                            }else {
                                model.setHide(true);
                            }

                            getFragments().model = model;
                            getBinding().assetsAllMoney.setText(StringUtils.double2String(model.getTotalUSDT().doubleValue(), 8) + "");
                            getBinding().assetsAllMoneyCny.setText("≈" + (StringUtils.double2String(model.getTotalCny().doubleValue(), 2)) + "CNY");
                            EventBus.getDefault().post(model);
//                            if (EmptyUtil.isEmpty(mAdapter)){
////                                initTabLayout(model); // 初始化 tab
//                            }
//                            getFBAssetsData(FaceApplication.getToken(), model.getTotalPrice(), model.getPrice_cny());
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
     * 获取总资产数据列表
     */
    public int mpage = 1;
    public int mlimit = 10;
    public void getAccounts(int page) {
        FaceApiTest.getV1ApiServiceTest().getAccountsList(FaceApplication.getToken(),mpage,10)
                .compose(RxSchedulers.<ApiModel<AccountsModel>>io_main())
                .subscribe(new RxObserver<AccountsModel>(getContexts(), getFragments().getTags(), false) {
                    @Override
                    public void onSuccess(AccountsModel model) {
                        try {
                            Log.e("cjh>>>", "资产列表 AccountsList:" + new Gson().toJson(model.getList()));
                            if (!EmptyUtil.isEmpty(accountAdapter) && !EmptyUtil.isEmpty(model.getList())) {
                                try {
                                    if (mpage == 1 || mpage == 0) {
                                        if (null != model && model.getList().size() > 0) {
                                            accountAdapter.clear();
                                            accountAdapter.addAll(model.getList());
                                            getFragments().coinId = model.getList().get(0).getCoinId()+"";
                                            getFragments().coinName = model.getList().get(0).getCoinName()+"";
                                        } else {
                                            //占位图
                                            getBinding().ervAccounts.setEmptyView(R.layout.comment_view_seat_layout);
                                            getBinding().ervAccounts.showEmpty();
                                        }
                                    } else {
                                        if (model.getList().size() <= 0) {
                                            accountAdapter.stopMore();
                                        } else {
                                            accountAdapter.addAll(model.getList());
                                        }
                                    }
                                    setType(getFragments().type);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else{
                                if (null!=accountAdapter)
                                    accountAdapter.stopMore();
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


    public void setType(String type){
        List<AccountsDTO> allData = accountAdapter.getAllData();
        for (AccountsDTO ad:
                allData) {
            ad.setType(type);
        }
        accountAdapter.notifyDataSetChanged();
    }
    /**
     * 币种列表
     */
    public List<AccountsDTO> getList(){
        List<AccountsDTO> allData = new ArrayList<>();
        if (!EmptyUtil.isEmpty(accountAdapter)){
            allData = accountAdapter.getAllData();
        }
        return allData;
    }
    /**
     * 获取法币资产数据  计算得出币币 法币总资产
     */
    public void getFBAssetsData(String token, final BigDecimal available, final BigDecimal cny) {
        FaceApiMbr.getV1ApiServiceMbr().getOTCCoins(token)
                .compose(RxSchedulers.<ApiModel<FB_BBEntity>>io_main())
                .subscribe(new RxObserver<FB_BBEntity>(getContexts(), getFragments().getTag(), false) {
                    @Override
                    public void onSuccess(FB_BBEntity entity) {
                        try {
                            Log.e("cjh>>>", "获取法币资产数据" + new Gson().toJson(entity));
                            Log.e("cjh>>>", "法币总资产Assets" + entity.getAvailable().doubleValue());
                            if (!EmptyUtil.isEmpty(entity)) {
                                // 法币余额 + 币币余额 = 总资产
                                //BigDecimal f = entity.getAvailable();
                                BigDecimal f = new BigDecimal(Double.parseDouble(StringUtils.double2String(entity.getAvailable().doubleValue(), 8)));
                                BigDecimal b = new BigDecimal(Double.parseDouble(StringUtils.double2String(available.doubleValue(), 8)));
                                //BigDecimal b = available;
                                BigDecimal all = f.add(b);
                                BigDecimal cny1 = entity.getUsdtPrice().multiply(f).add(available.multiply(cny)).abs();
                                //BigDecimal cny = entity.getUsdtPrice().multiply(a) + available.multiply(cny).abs();
                                getBinding().assetsAllMoney.setText(StringUtils.double2String(all.doubleValue(), 8) + "");
                                getBinding().assetsAllMoneyCny.setText("≈" + (StringUtils.double2String(cny1.doubleValue(), 2)) + "CNY");

                                Log.e("cjh>>>", "币币法币总资产Assets   币币：" + b.doubleValue() + "  法币：" + f.doubleValue());
                                Log.e("cjh>>>", "币币：" + Double.parseDouble(b.doubleValue() + "") + "  法币：" + f.doubleValue() + "=" + (b.add(f)).abs().doubleValue());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 获取资产数据 刷新数据
     */
//    public void clearAssetsData(String token) {
//        FaceApiTest.getV1ApiServiceTest().getCoinWorth(token)
//                .compose(RxSchedulers.<ApiModel<CoinWorthModel>>io_main())
//                .subscribe(new RxObserver<CoinWorthModel>(getContexts(), getFragments().getTag(), true) {
//                    @Override
//                    public void onSuccess(CoinWorthModel model) {
//                        getFragments().model = model;
//                        getBinding().assetsAllMoney.setText(StringUtils.double2String(model.getTotalPrice().doubleValue(), 2));
//                        getBinding().assetsAllMoneyCny.setText("≈" + StringUtils.double2String(model.getTotalPrice().doubleValue() * model.getPrice_cny().doubleValue(), 2) + "CNY");
//                        EventBus.getDefault().post(model);
//                        getFBAssetsData(FaceApplication.getToken(), model.getTotalPrice(), model.getPrice_cny());
//                    }
//                    @Override
//                    public void onErrors(int eCode) {
//                        // 如果token过期 让用户重新登录  重新获取token
//                        if (eCode == 9001) {
//                            loginOut();
//                        }
//                    }
//                });
//    }


    /**
     * 初始化资产列表
     */
    public void initAccountsAdapter() {
        accountAdapter = new AssetsAccountsAdapter(getContexts());
        getBinding().ervAccounts.setLayoutManager(new LinearLayoutManager(getContexts()));
        getBinding().ervAccounts.setAdapter(accountAdapter);
        getBinding().ervAccounts.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBinding().ervAccounts.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getBBAssetsData(FaceApplication.getToken());
                        mpage = 1;
                        getAccounts(mpage);
                    }
                }, 1000);
            }
        });
        accountAdapter.setMore(R.layout.view_more, new RecyclerArrayAdapter.OnMoreListener() {
            @Override
            public void onMoreShow() {
                getBinding().ervAccounts.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mpage++;
                        getAccounts(mpage);
                    }
                }, 1000);
            }

            @Override
            public void onMoreClick() {

            }
        });
        accountAdapter.setError(R.layout.view_error_foot, new RecyclerArrayAdapter.OnErrorListener() {
            @Override
            public void onErrorShow() {

            }

            @Override
            public void onErrorClick() {
                accountAdapter.resumeMore();
            }
        });
//        accountAdapter.setNoMore(R.layout.view_nomore);
        accountAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                AssetRecordsActivity.JUMP(getContexts(), accountAdapter.getItem(position), "");
            }
        });
    }

    public void loginOut() {
        //TODO 接受退出登录事件,删除本地用户信息数据库信息，数据库数据等
        DataKeeper.removeAll(getFragments().getActivity());
        EventBus.getDefault().post("exitLogin");
    }


}
