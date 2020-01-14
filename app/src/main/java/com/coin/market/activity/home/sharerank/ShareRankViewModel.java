package com.coin.market.activity.home.sharerank;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;

import com.coin.market.R;
import com.coin.market.adapter.ShareRankAdapter;
import com.coin.market.databinding.ActivityShareRankLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.CoinListEntity;
import teng.wang.comment.model.ShareRankModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.log.Log;

/**
 * @author: cjh
 * @date: 2019/10/18
 * @info:
 */
public class ShareRankViewModel extends BaseActivityViewModel <ShareRankActivity, ActivityShareRankLayoutBinding>{

    private ShareRankAdapter adapter;
    public List<CoinListEntity> coins = new ArrayList<CoinListEntity>();

    public ShareRankViewModel(ShareRankActivity activity, ActivityShareRankLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        initOrderAdapter();
        getShareRank(FaceApplication.getToken(), getActivity().page, getActivity().time,0);
    }

    /**
     * 适配器展示
     */
    public void initOrderAdapter() {
        adapter = new ShareRankAdapter(getActivity());
        getBinding().rankRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        getBinding().rankRecycler.setAdapter(adapter);
        getBinding().rankRecycler.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBinding().rankRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().page = 1;
                        getShareRank(FaceApplication.getToken(), getActivity().page, getActivity().time,0);
                    }
                }, 1000);
            }
        });
        adapter.setMore(R.layout.view_more, new RecyclerArrayAdapter.OnMoreListener() {
            @Override
            public void onMoreShow() {
                getBinding().rankRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().page++;
                        getShareRank(FaceApplication.getToken(), getActivity().page, getActivity().time,0);
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

    /**
     *  分享 排行榜列表
     */
    public void getShareRank(String token, final int page, String time, int showType) {
        FaceApiTest.getV1ApiServiceTest(). getShareRank(token, page, time)
                .compose(RxSchedulers.<ApiModel<List<ShareRankModel>>>io_main())
                .subscribe(new RxObserver<List<ShareRankModel>>(getActivity(), getActivity().Tag, showType==1?true:false) {
                    @Override
                    public void onSuccess(List<ShareRankModel> list) {
                        try {
                            Log.e("cjh>>>", "分享 排行榜列表:" + new Gson().toJson(list));

                            if(page==1){
                                if(null!=list&&list.size()>0){
                                    adapter.clear();
                                    adapter.addAll(list);
                                }else{
                                    //占位图
                                    getBinding().rankRecycler.setEmptyView(R.layout.comment_view_share_layout);
                                    getBinding().rankRecycler.showEmpty();
                                }
                            }else{
                                if(list.size()<=0){
                                    adapter.stopMore();
                                }else{
                                    adapter.addAll(list);
                                }
                            }

                            if (!EmptyUtil.isEmpty(list.get(0))){
                                getBinding().tvName1.setText(StringUtils.splitPhone(list.get(0).getMobile()));
                                getBinding().tvProfit1.setText(StringUtils.double2String(Double.parseDouble(list.get(0).getResultLeaderBoardNumber()), 6));
                            }
                            if (!EmptyUtil.isEmpty(list.get(1))){
                                getBinding().tvName2.setText(StringUtils.splitPhone(list.get(1).getMobile()));
                                getBinding().tvProfit2.setText(StringUtils.double2String(Double.parseDouble(list.get(1).getResultLeaderBoardNumber()), 6));
                            }
                            if (!EmptyUtil.isEmpty(list.get(2))){
                                getBinding().tvName3.setText(StringUtils.splitPhone(list.get(2).getMobile()));
                                getBinding().tvProfit3.setText(StringUtils.double2String(Double.parseDouble(list.get(2).getResultLeaderBoardNumber()), 6));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
