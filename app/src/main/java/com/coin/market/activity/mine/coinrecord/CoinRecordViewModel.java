package com.coin.market.activity.mine.coinrecord;


import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;

import com.coin.market.R;
import com.coin.market.activity.mine.coinrecordinfo.CoinInfoActivity;
import com.coin.market.adapter.CoinRecordAdapter;
import com.coin.market.databinding.ActivityCoinRecordLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.util.List;

import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.CoinRecordEntity;
import teng.wang.comment.model.GetCoinRecordModel;
import teng.wang.comment.model.ImportsEntity;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.log.Log;

/**
 * @author: Lenovo
 * @date: 2019/7/16
 * @info: 充币记录 ViewModel
 */
public class CoinRecordViewModel extends BaseActivityViewModel<CoinRecordActivity, ActivityCoinRecordLayoutBinding> {

    private int page = 1;
    private CoinRecordAdapter adapter;
    private List<CoinRecordEntity> entityList;

    public CoinRecordViewModel(CoinRecordActivity activity, ActivityCoinRecordLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        page = 1;
        setCoinAdapter();
        if (!EmptyUtil.isEmpty(getActivity().Jump)){
            if (getActivity().Jump.equals("TB")){
                getExport(page);
            }else if (getActivity().Jump.equals("CB")){
                getImport(page);
            }
        }
    }

    /**
     *   初始化Adapter
     */
    public void setCoinAdapter() {
        adapter = new CoinRecordAdapter(getActivity(), getActivity().type);
        getBinding().coinRecordRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        getBinding().coinRecordRecycler.setAdapter(adapter);
        getBinding().coinRecordRecycler.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBinding().coinRecordRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page = 1;
                        if (getActivity().Jump.equals("TB")){
                            getExport(page);
                        }else if (getActivity().Jump.equals("CB")){
                            getImport(page);
                        }
                    }
                }, 1000);
            }
        });
        adapter.setMore(R.layout.view_more, new RecyclerArrayAdapter.OnMoreListener() {
            @Override
            public void onMoreShow() {
                getBinding().coinRecordRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page++;
                        if (getActivity().Jump.equals("TB")){
                            getExport(page);
                        }else if (getActivity().Jump.equals("CB")){
                            getImport(page);
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
//        adapter.setNoMore(R.layout.view_nomore);

        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (getActivity().Jump.equals("TB")){
                    Integer status = adapter.getItem(position).getStatus();
                    if(1==status){
                        CoinInfoActivity.JUMP(getActivity(),getActivity().getIntent().getStringExtra("Jump"), adapter.getItem(position),""+getActivity().coinName);
                    }
                }else {
                    CoinInfoActivity.JUMP(getActivity(),getActivity().getIntent().getStringExtra("Jump"), adapter.getItem(position),getActivity().coinName+"");
                }


            }
        });
    }

    /**
     * 获取提币历史记录
     */
    public void getExport(int mpage) {
        FaceApiTest.getV1ApiServiceTest().getExportCoinRecord(FaceApplication.getToken(),mpage,"10")
                .compose(RxSchedulers.<ApiModel<ImportsEntity>>io_main())
                .subscribe(new RxObserver<ImportsEntity>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(ImportsEntity entity) {
                        try {
                            Log.e("cjh>>>获取提币历史记录:","GetCoinRecordModel：" + new Gson().toJson(entity));
                            if (page == 1 || page == 0) {
                                if (null != entity && entity.getList().size() > 0) {
                                    adapter.clear();
                                    adapter.addAll(entity.getList());
                                } else {
                                    //占位图
                                    getBinding().coinRecordRecycler.setEmptyView(R.layout.comment_view_seat_layout);
                                    getBinding().coinRecordRecycler.showEmpty();
                                }
                            } else {
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
     * 获取充币历史记录
     */

    public void getImport(int mpage) {
        FaceApiTest.getV1ApiServiceTest().getImportCoinRecord(FaceApplication.getToken(),mpage,"10")
                .compose(RxSchedulers.<ApiModel<ImportsEntity>>io_main())
                .subscribe(new RxObserver<ImportsEntity>(getActivity(), getActivity().Tag,  true) { //showType == 1 ? true : false
                    @Override
                    public void onSuccess(ImportsEntity entity) {
                        try {
                            Log.e("cjh>>>获取充币历史记录:","GetCoinRecordModel：" + new Gson().toJson(entity));
                            if (page == 1 || page == 0) {
                                if (null != entity && entity.getList().size() > 0) {
                                    adapter.clear();
                                    adapter.addAll(entity.getList());
                                } else {
                                    //占位图
                                    getBinding().coinRecordRecycler.setEmptyView(R.layout.comment_view_seat_layout);
                                    getBinding().coinRecordRecycler.showEmpty();
                                }
                            } else {
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
}
