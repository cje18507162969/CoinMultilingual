package com.coin.market.activity.main;


import android.support.v4.app.Fragment;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.databinding.ActivityMainLayoutBinding;
import com.coin.market.fragment.assets.AssetsFragment;
import com.coin.market.fragment.home.HomeFragment;
import com.coin.market.fragment.order.OrderFragment;
import com.coin.market.fragment.quotation.QuotationFragment;
import com.coin.market.fragment.transaction.TransactionFragment;
import com.coin.market.fragment.treaty.TreatyFragment;

import java.util.ArrayList;

import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.widget.ViewPageAdapter;

public class MainModel extends BaseActivityViewModel<MainActivity, ActivityMainLayoutBinding> implements RadioGroup.OnCheckedChangeListener {

    private ViewPageAdapter mAdapter;

    public MainModel(MainActivity activity, ActivityMainLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        initTabButton();
        initFragment();
    }

    private void initTabButton() {
        getBinding().homeRgMainTab.setOnCheckedChangeListener(this);
    }

    private void initFragment() {
        mAdapter = new ViewPageAdapter(getActivity().getSupportFragmentManager(), new ArrayList<Fragment>(), new ArrayList<String>());
        mAdapter.fragmentsList.add(HomeFragment.getHomeFragment());//首页
        mAdapter.fragmentsList.add(TreatyFragment.getTreatyFragment());//合约
        mAdapter.fragmentsList.add(OrderFragment.getOrderFragment());//订单
        mAdapter.fragmentsList.add(AssetsFragment.getAssetsFragment());//资产
//        mAdapter.fragmentsList.add(TransactionFragment.getTransactionFragment());//订单
        getBinding().homeViewPager.setAdapter(mAdapter);
        getBinding().homeViewPager.setCurrentItem(0);
        getBinding().homeViewPager.setOffscreenPageLimit(mAdapter.getCount());
    }

    public void setAdapterItem(int position){
        getBinding().homeViewPager.setCurrentItem(position);
        if (1==position){
            getBinding().homeRgMainTab.check(R.id.home_tab_quotation);
        }else if (2==position){
            getBinding().homeRgMainTab.check(R.id.home_tab_transaction);
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.home_tab_home:
                getBinding().homeRgMainTab.check(R.id.home_tab_home);
                break;
            case R.id.home_tab_quotation:
                getBinding().homeRgMainTab.check(R.id.home_tab_quotation);
                break;
            case R.id.home_tab_transaction:
                getBinding().homeRgMainTab.check(R.id.home_tab_transaction);
                break;
            case R.id.home_tab_assets:
                if (3 == FaceApplication.getLevel()){
                    return;
                }else {
                    getBinding().homeRgMainTab.check(R.id.home_tab_assets);
                }

                break;
            default:
                break;

        }
    }

}
