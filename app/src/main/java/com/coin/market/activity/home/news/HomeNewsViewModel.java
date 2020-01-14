package com.coin.market.activity.home.news;

import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;

import com.coin.market.adapter.HomeNewsAdapter;
import com.coin.market.databinding.ActivityHomeNewsLayoutBinding;
import com.coin.market.jpush.JPushModel;
import com.coin.market.util.EmptyUtil;

import teng.wang.comment.api.FaceApiMbr;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.ArticlesDTO;
import teng.wang.comment.model.BulletinEntity;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;

/**
 * @author: Lenovo
 * @date: 2019/8/8
 * @info: 首页新闻 列表 ViewModel
 */
public class HomeNewsViewModel extends BaseActivityViewModel<HomeNewsActivity, ActivityHomeNewsLayoutBinding> {

    private HomeNewsAdapter adapter;
    private ArticlesDTO entity;

    public HomeNewsViewModel(HomeNewsActivity activity, ActivityHomeNewsLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        //setCoinAdapter();
        if (!EmptyUtil.isEmpty(getActivity().getIntent().getSerializableExtra("BulletinEntity"))){
            getData();
        }else{
            JPushModel jPushModel = (JPushModel) getActivity().getIntent().getSerializableExtra("jPush");
            getNews(1, jPushModel.getId());
        }
    }

    private void getData(){
        if (!EmptyUtil.isEmpty(getActivity().getIntent().getSerializableExtra("BulletinEntity"))){
            entity = (ArticlesDTO) getActivity().getIntent().getSerializableExtra("BulletinEntity");
            setData(getActivity().position);
        }
    }

    // 设置界面
    private void setData(int position){
        getBinding().mTvTitle.setText(entity.getTitle());
        getBinding().mTvNumb.setText(entity.getViews()+"");
        getBinding().mTvTime.setText(entity.getCreateDate());
        getBinding().mTvContent.setText(entity.getContent());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            getBinding().mTvContent.setText(Html.fromHtml(entity.getContent(), Html.FROM_HTML_MODE_LEGACY));
        }else{
            getBinding().mTvContent.setText(Html.fromHtml(entity.getContent()));
        }
    }

    /**
     *   初始化Adapter
     */
    public void setCoinAdapter() {
        adapter = new HomeNewsAdapter(getActivity());
        getBinding().homeNewsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        getBinding().homeNewsRecycler.setAdapter(adapter);
    }

    /**
     *  获取新闻详情List
     */
    public void getNews(int showType, final String id) {
        FaceApiTest.getV1ApiServiceTest().getArticles(id)
                .compose(RxSchedulers.<ApiModel<ArticlesDTO>>io_main())
                .subscribe(new RxObserver<ArticlesDTO>(getActivity(), getActivity().Tag, showType == 1 ? true : false) {
                    @Override
                    public void onSuccess(ArticlesDTO mentity) {
                        try {
                            entity = mentity;
                            setData(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
