package com.coin.market.fragment.share;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;

import com.coin.market.R;
import com.coin.market.adapter.ShareAdapter;
import com.coin.market.databinding.FragmentShareLayoutBinding;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseFragmentViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.ShareListModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.log.Log;

/**
 * @author: Lenovo
 * @date: 2019/8/7
 * @info: 行情List ViewModel
 */
public class ShareViewModel extends BaseFragmentViewModel<ShareFragment, FragmentShareLayoutBinding> {

    private ShareAdapter adapter;


    public ShareViewModel(ShareFragment fragment, FragmentShareLayoutBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initView() {
        setAdapter();
        getAllCoinList(getFragments().page);
    }


    /**
     * 初始化Adapter
     */
    public void setAdapter() {
        adapter = new ShareAdapter(getContexts(), "0");
        getBinding().shareRecycler.setLayoutManager(new LinearLayoutManager(getContexts()));
        getBinding().shareRecycler.setAdapter(adapter);
        getBinding().shareRecycler.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBinding().shareRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getFragments().page = 1;
                        getAllCoinList(getFragments().page);
                    }
                }, 1000);
            }
        });
        adapter.setMore(R.layout.view_more, new RecyclerArrayAdapter.OnMoreListener() {
            @Override
            public void onMoreShow() {
                getBinding().shareRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getFragments().page++;
                        getAllCoinList(getFragments().page);
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
     * 获取全部委托记录  初次进来 查所有 无筛选参数的
     */
    public void getAllCoinList(final int page) {
        FaceApiTest.getV1ApiServiceTest().getInviteRecord(FaceApplication.getToken(),page,10)
                .compose(RxSchedulers.<ApiModel<ShareListModel>>io_main())
                .subscribe(new RxObserver<ShareListModel>(getContexts(), getFragments().getTags(), true) {
                    @Override
                    public void onSuccess(ShareListModel model) {
                        try {
                            Log.e("cjh>>>", "邀请记录 EntrustModel:" + new Gson().toJson(model));
                            if (page == 1) {
                                if (null != model && model.getList().size() > 0) {
                                    adapter.clear();
                                    adapter.addAll(model.getList());
                                } else {
                                    //占位图
                                    getBinding().shareRecycler.setEmptyView(R.layout.comment_view_share_layout);
                                    getBinding().shareRecycler.showEmpty();
                                }
                            } else {
                                if (model.getList().size() <= 0) {
                                    adapter.stopMore();
                                } else {
                                    adapter.addAll(model.getList());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
