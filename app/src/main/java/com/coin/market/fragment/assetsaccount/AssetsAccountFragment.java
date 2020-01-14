package com.coin.market.fragment.assetsaccount;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coin.market.R;
import com.coin.market.databinding.FragmentAssetsAccountLayoutBinding;
import com.coin.market.util.EmptyUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import teng.wang.comment.base.BaseFragment;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.CoinWorthModel;

/**
 * @author: Lenovo
 * @date: 2019/8/5
 * @info:  资产 币币账户 法币账户 通用Fragment
 */
public class AssetsAccountFragment extends BaseFragment {

    public String type = ""; // 账户类型  bb：币币   fb：法币
    private FragmentAssetsAccountLayoutBinding binding;
    private AssetsAccountViewModel assetsAccountViewModel;
    public CoinWorthModel entity;

    public static AssetsAccountFragment getAssetsAccountFragment(String type, CoinWorthModel entity){
        AssetsAccountFragment fragment = new AssetsAccountFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putSerializable("CoinWorthModel", entity);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    // 每次显示都刷新币币 法币资产数据
    @Override
    protected void onFragmentVisible() {
        super.onFragmentVisible();
        if (!EmptyUtil.isEmpty(assetsAccountViewModel) && !EmptyUtil.isEmpty(type)) {
            if (type.equals("bb")) {
                assetsAccountViewModel.getBBAssetsData(FaceApplication.getToken());
            } else if (type.equals("fb")) {
                assetsAccountViewModel.getFBAssetsData(FaceApplication.getToken(), 1);
            }
        }
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup viewGroup) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_assets_account_layout, viewGroup, false);
        return binding.getRoot();
    }

    @Override
    protected void onFragmentFirstVisible() {
        EventBus.getDefault().register(this);
        Bundle bundle = getArguments();
        type = bundle.getString("type");
        entity = (CoinWorthModel) bundle.getSerializable("CoinWorthModel");
        assetsAccountViewModel = new AssetsAccountViewModel(this, binding);
        assetsAccountViewModel.initKeyboardStatus();
    }

    @Override
    protected void setListener() {
    }

    public void onClick(View view) {
        switch (view.getId()) {

            default:
                break;
        }
    }

    /**
     *   重新刷新 资产数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getData(CoinWorthModel model) {
        try {
            if (!EmptyUtil.isEmpty(model)) {

                if (type.equals("fb")){ // 法币
                    assetsAccountViewModel.getFBAssetsData(FaceApplication.getToken(), 0);
                }else { // 币币
                    this.entity = model;
                    assetsAccountViewModel.setData();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
