package com.coin.market.activity.home.feedback;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;

import com.coin.market.R;
import com.coin.market.adapter.FeedbackAdapter;
import com.coin.market.databinding.ActivityFeedbackFeedbackLayoutBinding;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.api.FaceApi;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.FeedbackModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;

/**
 * @author 意见反馈model
 * @version v1.0
 * @Time 2018-9-14
 */

public class FeedbackViewModel extends BaseActivityViewModel<FeedbackActivity, ActivityFeedbackFeedbackLayoutBinding> {

    public int page = 1;
    public FeedbackAdapter mFeedbackAdapter;

    public FeedbackViewModel(FeedbackActivity activity, ActivityFeedbackFeedbackLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        setQuestionData();
        page = 1;
        getQuestionList(page, 1);
    }

    /**
     * 适配器展示
     */
    public void setQuestionData() {
        mFeedbackAdapter = new FeedbackAdapter(getActivity());
        getBinding().feedbackRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        getBinding().feedbackRecycler.setAdapter(mFeedbackAdapter);
        getBinding().feedbackRecycler.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBinding().feedbackRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page = 1;
                        getQuestionList(page, 0);
                    }
                }, 1000);
            }
        });
        mFeedbackAdapter.setMore(R.layout.view_more, new RecyclerArrayAdapter.OnMoreListener() {
            @Override
            public void onMoreShow() {
                getBinding().feedbackRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page++;
                        getQuestionList(page, 0);
                    }
                }, 1000);
            }

            @Override
            public void onMoreClick() {

            }
        });
        mFeedbackAdapter.setError(R.layout.view_error_foot, new RecyclerArrayAdapter.OnErrorListener() {
            @Override
            public void onErrorShow() {

            }

            @Override
            public void onErrorClick() {
                mFeedbackAdapter.resumeMore();
            }
        });
        mFeedbackAdapter.setNoMore(R.layout.view_nomore);
    }

    /**
     * 获取意见反馈列表
     */
    public void getQuestionList(final int page, int showType) {
        FaceApiTest.getV1ApiServiceTest().getFeedBackList(FaceApplication.getToken(), page,10)
                .compose(RxSchedulers.<ApiModel<FeedbackModel>>io_main())
                .subscribe(new RxObserver<FeedbackModel>(getActivity(), getActivity().Tag, showType == 1 ? true : false) {
                    @Override
                    public void onSuccess(FeedbackModel model) {
                        try {
                            if (page == 1 || page == 0) {
                                if (null != model && model.getList().size() > 0) {
                                    mFeedbackAdapter.clear();
                                    mFeedbackAdapter.addAll(model.getList());
                                } else {
                                    //占位图
                                    getBinding().feedbackRecycler.setEmptyView(R.layout.comment_view_seat_layout);
                                    getBinding().feedbackRecycler.showEmpty();
                                }
                            } else {
                                if (model.getList().size() <= 0) {
                                    mFeedbackAdapter.stopMore();
                                } else {
                                    mFeedbackAdapter.addAll(model.getList());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
