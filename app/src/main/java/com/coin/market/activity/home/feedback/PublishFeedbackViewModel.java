package com.coin.market.activity.home.feedback;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.adapter.PublishFeedAdapter;
import com.coin.market.databinding.ActivityPublishFeedbackLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;
import com.coin.market.wight.SpacesItemDecoration;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.api.RequestBodyUtils;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.FeedBackTypeModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;

/**
 * @author: cjh
 * @date: 2019/10/23
 * @info:
 */
public class PublishFeedbackViewModel extends BaseActivityViewModel<PublishFeedbackActivity, ActivityPublishFeedbackLayoutBinding> {

    private PublishFeedAdapter adapter;
    private FeedBackTypeModel model;

    public PublishFeedbackViewModel(PublishFeedbackActivity activity, ActivityPublishFeedbackLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        initQuotationAdapter();
        getFeedBackType();
    }

    // 初始化 货币行情分类 adapter
    private void initQuotationAdapter() {

        final int divider = 2;

        RecyclerView.ItemDecoration gridItemDecoration = new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                //不为banner类型
                if (adapter.getItemViewType(parent.getChildAdapterPosition(view)) != 0) {
                    StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
                    int spanIndex = layoutParams.getSpanIndex();
                    outRect.top = divider;
                    if (spanIndex == 0) {
                        // left
                        outRect.left = divider;
                        outRect.right = divider / 2;
                    } else{
                        outRect.right = divider;
                        outRect.left = divider / 2;
                    }
                }
            }
        };

        adapter = new PublishFeedAdapter(getActivity());
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        getBinding().publishRecycler.addItemDecoration(gridItemDecoration);
        getBinding().publishRecycler.setLayoutManager(manager);
        getBinding().publishRecycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                for (int i = 0; i < adapter.getAllData().size(); i++) {
                    adapter.getAllData().get(i).setSelect(false);
                }
                getActivity().type = adapter.getAllData().get(position).getCode() + "";
                adapter.getAllData().get(position).setSelect(true);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 发表意见反馈
     */
    public void saveIssueFeedback(String issueDescription, String issueLabel, String pic) {
//        RequestBody body = new RequestBodyUtils.Builder()
//                .addParam("issueDescription", issueDescription)
//                .addParam("issueLabel", issueLabel)
//                .addParam("picPhoto", picPhoto)
//                .builder();
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("issueDescription", issueDescription)
                .addParam("issueLabel", issueLabel)
                .addParam("tenantCode", FaceApplication.getTenantCode())
                .addParam("pic", pic)
                .builder();
        FaceApiTest.getV1ApiServiceTest().saveIssueFeedback(FaceApplication.getToken(), body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(String str) {
                        try {
                            getActivity().imgs.clear();
                            if (!TextUtils.isEmpty(str)){
                                Toast.makeText(getActivity(), ""+str, Toast.LENGTH_SHORT).show();
                            }

                            FeedbackActivity.JUMP(getActivity());
                            getActivity().finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrors(int eCode) {
                        super.onErrors(eCode);
                        getActivity().imgs.clear();
                    }
                });
    }

    /**
     * 获取反馈类型
     advisory     咨询
     suggest     建议
     other     其他
     */
    public void getFeedBackType() {
        String[] str = new String[]{"advisory","suggest","other"};
        String[] str1 = new String[]{getActivity().getResources().getString(R.string.tv_advisory),
                getActivity().getResources().getString(R.string.tv_suggest),
                getActivity().getResources().getString(R.string.tv_other)};
        List<FeedBackTypeModel.ListBean> list = new ArrayList<>();
        for (int i = 0; i < str.length; i++) {
            list.add(new FeedBackTypeModel.ListBean(str[i],str1[i]));
        }
        adapter.clear();
        adapter.addAll(list);
//        FaceApiTest.getV1ApiServiceTest().getFeedBackType()
//                .compose(RxSchedulers.<ApiModel<FeedBackTypeModel>>io_main())
//                .subscribe(new RxObserver<FeedBackTypeModel>(getActivity(), getActivity().Tag, false) {
//                    @Override
//                    public void onSuccess(FeedBackTypeModel bean) {
//                        try {
//                            if (!EmptyUtil.isEmpty(bean)) {
//                                model = bean;
//                                adapter.clear();
//                                adapter.addAll(model.getList());
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
    }

}
