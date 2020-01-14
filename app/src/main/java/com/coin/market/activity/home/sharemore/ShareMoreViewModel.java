package com.coin.market.activity.home.sharemore;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.coin.market.databinding.ActivityShareMoreLayoutBinding;
import com.coin.market.fragment.share.Share2Fragment;
import com.coin.market.fragment.share.ShareFragment;

import java.util.ArrayList;

import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.widget.ViewPageAdapter;

/**
 * @author: cjh
 * @date: 2019/10/18
 * @info:
 */
public class ShareMoreViewModel extends BaseActivityViewModel <ShareMoreActivity, ActivityShareMoreLayoutBinding>{

    private ViewPageAdapter mAdapter;
    public ShareFragment fragment;
    public Share2Fragment fragment2;

    public ShareMoreViewModel(ShareMoreActivity activity, ActivityShareMoreLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        initFragment();
    }

    private void initFragment(){
        mAdapter = new ViewPageAdapter(getActivity().getSupportFragmentManager(), new ArrayList<Fragment>(), new ArrayList<String>());
        fragment = ShareFragment.getShareFragment("0");
        mAdapter.fragmentsList.add(fragment);//邀请记录
        if (2 == FaceApplication.getLevel()){
            fragment2 = Share2Fragment.getShare2Fragment("1");
            mAdapter.fragmentsList.add(fragment2);//返佣历史
        }
        getBinding().coinOrderViewpager.setAdapter(mAdapter);
        getBinding().coinOrderViewpager.setCurrentItem(0);
        getBinding().coinOrderViewpager.setOffscreenPageLimit(mAdapter.getCount());

        getBinding().coinOrderViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setAdapterItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setAdapterItem(int position){
        getBinding().coinOrderViewpager.setCurrentItem(position);
        getActivity().setTabButton(position);
    }

}
