package com.coin.market.activity.mine.transferrecord;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;

import com.coin.market.R;
import com.coin.market.adapter.TransferRecordAdapter;
import com.coin.market.databinding.ActivityTransferRecordLayoutBinding;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.api.FaceApiMbr;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.TransferRecordModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;

/**
 * @author: Lenovo
 * @date: 2019/7/17
 * @info: 划转 历史记录 ViewModel
 */
public class TransferRecordViewModel extends BaseActivityViewModel<TransferRecordActivity, ActivityTransferRecordLayoutBinding> {

    private int page;
    private TransferRecordAdapter adapter;

    public TransferRecordViewModel(TransferRecordActivity activity, ActivityTransferRecordLayoutBinding binding) {
        super(activity, binding);
    }


    @Override
    protected void initView() {
        setCoinAdapter();
        //getCoinList(page, 1);
        transferRecord(FaceApplication.getToken(), getActivity().coinId, getActivity().accountType);
    }

    /**
     * 初始化奖金钱包适配器
     */
    public void setCoinAdapter() {
        adapter = new TransferRecordAdapter(getActivity());
        getBinding().transferRecordRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        getBinding().transferRecordRecycler.setAdapter(adapter);
        getBinding().transferRecordRecycler.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBinding().transferRecordRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page = 1;
                        transferRecord(FaceApplication.getToken(), getActivity().coinId, getActivity().accountType);
                    }
                }, 1000);
            }
        });
//        adapter.setMore(R.layout.view_more, new RecyclerArrayAdapter.OnMoreListener() {
//            @Override
//            public void onMoreShow() {
//                getBinding().transferRecordRecycler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        page++;
//                        getCoinList(page, 0);
//                    }
//                }, 1000);
//            }
//
//            @Override
//            public void onMoreClick() {
//
//            }
//        });
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

        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //CoinInfoActivity.JUMP(getActivity(), getActivity().getIntent().getStringExtra("Jump"), adapter.getItem(position));
            }
        });
    }

    /**
     *  获取历史划转记录
     */
    public void transferRecord(String token, String coinId, String type) {
        FaceApiMbr.getV1ApiServiceMbr().transferRecord(token, coinId, type)
                .compose(RxSchedulers.<ApiModel<TransferRecordModel>>io_main())
                .subscribe(new RxObserver<TransferRecordModel>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(TransferRecordModel entity) {
                        try {
                            if (null != entity && entity.getList().size() > 0) {
                                adapter.clear();
                                adapter.addAll(entity.getList());
                            } else {
                                //占位图
                                getBinding().transferRecordRecycler.setEmptyView(R.layout.comment_view_seat_layout);
                                getBinding().transferRecordRecycler.showEmpty();
                            }
//                            if (page == 1) {
//                                if (null != entity && entity.getList().size() > 0) {
//                                    adapter.clear();
//                                    adapter.addAll(entity.getList());
//                                } else {
//                                    //占位图
//                                    getBinding().transferRecordRecycler.setEmptyView(R.layout.comment_view_seat_layout);
//                                    getBinding().transferRecordRecycler.showEmpty();
//                                }
//                            } else {
//                                if (entity.getList().size() <= 0) {
//                                    adapter.stopMore();
//                                } else {
//                                    adapter.addAll(entity.getList());
//                                }
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
