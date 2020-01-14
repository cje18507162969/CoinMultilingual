package com.coin.market.fragment.coinorder;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;

import com.coin.market.R;
import com.coin.market.activity.mine.coinorderinfo.CoinOrderInfoActivity;
import com.coin.market.adapter.CoinOrderMoreAdapter;
import com.coin.market.databinding.FragmentCoinOrderLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseFragmentViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.EntrustInfo;
import teng.wang.comment.model.EntrustMoreModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.log.Log;

/**
 * @author: Lenovo
 * @date: 2019/7/19
 * @info: 个人中心 订单管理 历史记录 ViewModel
 */
public class CoinOrderMoreFragmentViewModel extends BaseFragmentViewModel<CoinOrderMoreFragment, FragmentCoinOrderLayoutBinding> {

    private CoinOrderMoreAdapter adapter;
    private EntrustMoreModel entity;

    public CoinOrderMoreFragmentViewModel(CoinOrderMoreFragment fragment, FragmentCoinOrderLayoutBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initView() {
        setCoinAdapter();
        getHistoryCoinList(FaceApplication.getToken(), getFragments().page,  "2");
    }

    /**
     * 获取历史委托记录  初次进来 查所有 无筛选参数的
     */
    public void getHistoryCoinList(String token, final int page, String type) {
        FaceApiTest.getV1ApiServiceTest().getAllHistory(token, page, type)
                .compose(RxSchedulers.<ApiModel<EntrustMoreModel>>io_main())
                .subscribe(new RxObserver<EntrustMoreModel>(getContexts(), getFragments().getTags(), true) {
                    @Override
                    public void onSuccess(EntrustMoreModel model) {
                        try {
                            Log.e("cjh>>>id:", "获取历史委托记录  初次进来 查所有 无筛选参数的  EntrustModel:" + new Gson().toJson(model));
                            if (page == 1) {
                                if (null != model && model.getData().size() > 0) {
                                    adapter.clear();
                                    adapter.addAll(model.getData());
                                } else {
                                    //占位图
                                    getBinding().coinOrderRecycler.setEmptyView(R.layout.comment_view_seat2_layout);
                                    getBinding().coinOrderRecycler.showEmpty();
                                }
                            } else {
                                if (model.getData().size() <= 0) {
                                    adapter.stopMore();
                                } else {
                                    adapter.addAll(model.getData());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 获取历史委托记录 有删选条件的
     */
    public void getTradeCoinList(String token, final int page, final String pay_id, String type, String coinName) {
        FaceApiTest.getV1ApiServiceTest().getAllHistory2(token, page, pay_id, type, coinName)
                .compose(RxSchedulers.<ApiModel<EntrustMoreModel>>io_main())
                .subscribe(new RxObserver<EntrustMoreModel>(getContexts(), getFragments().getTags(), true) {
                    @Override
                    public void onSuccess(EntrustMoreModel model) {
                        try {
                            Log.e("cjh>>>id:" + pay_id, " 获取历史委托记录 有筛选条件的 EntrustModel:" + new Gson().toJson(model));
                            if (page == 1) {
                                if (null != model && model.getData().size() > 0) {
                                    adapter.clear();
                                    adapter.addAll(model.getData());
                                } else {
                                    //占位图
                                    getBinding().coinOrderRecycler.setEmptyView(R.layout.comment_view_seat_layout);
                                    getBinding().coinOrderRecycler.showEmpty();
                                }
                            } else {
                                if (model.getData().size() <= 0) {
                                    adapter.stopMore();
                                } else {
                                    adapter.addAll(model.getData());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 初始化Adapter
     */
    public void setCoinAdapter() {
        adapter = new CoinOrderMoreAdapter(getContexts());
        adapter.setListener(new CoinOrderMoreAdapter.itemClick() {
            @Override
            public void Click(EntrustInfo entity) {
                CoinOrderInfoActivity.JUMP(getContexts(), entity);
            }
        });
        getBinding().coinOrderRecycler.setLayoutManager(new LinearLayoutManager(getContexts()));
        getBinding().coinOrderRecycler.setAdapter(adapter);
        getBinding().coinOrderRecycler.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBinding().coinOrderRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getFragments().page = 1;
                        if (!EmptyUtil.isEmpty(getFragments().entity)){
                            getTradeCoinList(FaceApplication.getToken(), getFragments().page, getFragments().entity.getCoin_id() + "", getFragments().entity.getType() + "", getFragments().entity.getCoin_nme());
                        }else {
                            getHistoryCoinList(FaceApplication.getToken(), getFragments().page,  "2");
                        }
                    }
                }, 1000);
            }
        });
        adapter.setMore(R.layout.view_more, new RecyclerArrayAdapter.OnMoreListener() {
            @Override
            public void onMoreShow() {
                getBinding().coinOrderRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getFragments().page++;
                        if (!EmptyUtil.isEmpty(getFragments().entity)){
                            getTradeCoinList(FaceApplication.getToken(), getFragments().page, getFragments().entity.getCoin_id() + "", getFragments().entity.getType() + "", getFragments().entity.getCoin_nme());
                        }else {
                            getHistoryCoinList(FaceApplication.getToken(), getFragments().page, "2");
                        }
                    }
                }, 1000);
            }

            @Override
            public void onMoreClick() {

            }
        });
        adapter.setError(R.layout.view_error_foot, new RecyclerArrayAdapter.OnErrorListener() {
            @Override
            public void onErrorShow() {

            }

            @Override
            public void onErrorClick() {
                adapter.resumeMore();
            }
        });
        adapter.setNoMore(R.layout.view_nomore);
    }

}
