package com.coin.market.activity.transaction.orderrecord;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;

import com.coin.market.R;
import com.coin.market.adapter.OrderRecordAdapter;
import com.coin.market.databinding.ActivityOrderRecordLayoutBinding;
import com.coin.market.activity.transaction.order.TransactionOrderInfoActivity;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.api.FaceApiMbr;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.OtcOrderAllEntity;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;

/**
 * @author: Lenovo
 * @date: 2019/8/2
 * @info:
 */
public class OrderRecordViewModel extends BaseActivityViewModel<OrderRecordActivity, ActivityOrderRecordLayoutBinding> {

    public int page;
    private OrderRecordAdapter adapter;

    public OrderRecordViewModel(OrderRecordActivity activity, ActivityOrderRecordLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        setCoinAdapter();
        getOrderList(FaceApplication.getToken(), "", "order_all", 1, 1);
    }

    /**
     * 获取 法币交易订单记录
     */
    public void getOrderList(String token, String otcTradeType, String otcStatus, final int page, int showType) {
        FaceApiMbr.getV1ApiServiceMbr().getMyOtcOrder(token, otcTradeType, otcStatus, page)
                .compose(RxSchedulers.<ApiModel<OtcOrderAllEntity>>io_main())
                .subscribe(new RxObserver<OtcOrderAllEntity>(getActivity(), getActivity().Tag, showType == 1 ? true : false) {
                    @Override
                    public void onSuccess(OtcOrderAllEntity entity) {
                        try {
                            if (page == 1) {
                                if (null != entity && entity.getList().size() > 0) {
                                    adapter.clear();
                                    adapter.addAll(entity.getList());
                                } else {
                                    //占位图
                                    getBinding().coinOrderRecycler.setEmptyView(R.layout.comment_view_seat_layout);
                                    getBinding().coinOrderRecycler.showEmpty();
                                }
                            } else if (page>0){
                                if (entity.getList().size() <= 0) {
                                    adapter.stopMore();
                                } else {
                                    adapter.addAll(entity.getList());
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
        adapter = new OrderRecordAdapter(getActivity());
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                TransactionOrderInfoActivity.JUMP(getActivity(), adapter.getItem(position).getId()+"", adapter.getItem(position).getOtcTradeType());
            }
        });
        getBinding().coinOrderRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        getBinding().coinOrderRecycler.setAdapter(adapter);
        getBinding().coinOrderRecycler.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBinding().coinOrderRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page = 1;
                        getOrderList(FaceApplication.getToken(), getActivity().otcTradeType, getActivity().otcStatus, page, 0);
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
                        page++;
                        getOrderList(FaceApplication.getToken(), getActivity().otcTradeType, getActivity().otcStatus, page, 0);
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
