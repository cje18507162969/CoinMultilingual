package com.coin.market.fragment.coinorder;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.adapter.CoinOrderAdapter;
import com.coin.market.databinding.FragmentCoinOrderLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import okhttp3.RequestBody;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.api.RequestBodyUtils;
import teng.wang.comment.base.BaseFragmentViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.CoinRecordEntity;
import teng.wang.comment.model.EntrustInfo;
import teng.wang.comment.model.EntrustModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.log.Log;

/**
 * @author: Lenovo
 * @date: 2019/7/19
 * @info: 个人中心 订单管理 全部委托 历史记录 ViewModel
 */
public class CoinOrderFragmentViewModel extends BaseFragmentViewModel<CoinOrderFragment, FragmentCoinOrderLayoutBinding> {

    private CoinOrderAdapter adapter;
    private List<CoinRecordEntity> entityList;

    public CoinOrderFragmentViewModel(CoinOrderFragment fragment, FragmentCoinOrderLayoutBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initView() {
        setCoinAdapter();
        getAllCoinList(FaceApplication.getToken(), getFragments().page, "2");
    }

    /**
     * 获取全部委托记录  初次进来 查所有 无筛选参数的
     */
    public void getAllCoinList(String token, final int page, String type) {
        FaceApiTest.getV1ApiServiceTest().getAllTrade2(token, page, type)
                .compose(RxSchedulers.<ApiModel<EntrustModel>>io_main())
                .subscribe(new RxObserver<EntrustModel>(getContexts(), getFragments().getTags(), true) {
                    @Override
                    public void onSuccess(EntrustModel model) {
                        try {
                            Log.e("cjh>>>", "全部委托记录 初次进来 查所有 无筛选参数的  EntrustModel:" + new Gson().toJson(model));
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
     * 获取全部委托记录 有筛选条件的
     */
    public void getEntrustsAll(String token, final int page, final String pay_id, String type, String coinName) {
        FaceApiTest.getV1ApiServiceTest().getAllTrade(token, page, pay_id, type, coinName)
                .compose(RxSchedulers.<ApiModel<EntrustModel>>io_main())
                .subscribe(new RxObserver<EntrustModel>(getContexts(), getFragments().getTags(), true) {
                    @Override
                    public void onSuccess(EntrustModel model) {
                        try {
                            Log.e("cjh>>>id:" + pay_id, "  全部委托记录 有筛选条件的：EntrustModel:" + new Gson().toJson(model));
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
     * 当前委托 撤销选择的item委托订单
     */
    public void cancelOrder(String token, final EntrustInfo entity) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("order_id", entity.getOrder_id())
                .addParam("type", entity.getType())
                .addParam("is_big", entity.getIs_big())
                .builder();
        FaceApiTest.getV1ApiServiceTest().cancelOrder(token, body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getContexts(), getFragments().getTags(), true) {
                    @Override
                    public void onSuccess(String model) {
                        try {
                            // 撤销成功 刷新列表
                            Toast.makeText(getContexts(), "撤销成功！", Toast.LENGTH_SHORT).show();
                            getFragments().page = 1;
                            getAllCoinList(FaceApplication.getToken(), getFragments().page, "2");
                            EventBus.getDefault().post("clearAssets");
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
        adapter = new CoinOrderAdapter(getContexts());
        adapter.setListener(new CoinOrderAdapter.itemClick() {
            @Override
            public void Click(EntrustInfo entity) {
                getFragments().showOrderDialog(entity);
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
                        if (!EmptyUtil.isEmpty(getFragments().entity)) {
                            getEntrustsAll(FaceApplication.getToken(), getFragments().page, getFragments().entity.getCoin_id() + "", getFragments().entity.getType() + "", getFragments().entity.getCoin_nme());
                        } else {
                            getAllCoinList(FaceApplication.getToken(), getFragments().page, "2");
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
                        if (!EmptyUtil.isEmpty(getFragments().entity)) {
                            getEntrustsAll(FaceApplication.getToken(), getFragments().page, getFragments().entity.getCoin_id() + "", getFragments().entity.getType() + "", getFragments().entity.getCoin_nme());
                        } else {
                            getAllCoinList(FaceApplication.getToken(), getFragments().page, "2");
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
