package com.coin.market.fragment.coinorder;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coin.market.R;
import com.coin.market.databinding.FragmentCoinOrderLayoutBinding;
import com.coin.market.util.EmptyUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import teng.wang.comment.base.BaseFragment;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.CoinListEntity;

/**
 * @author: Lenovo
 * @date: 2019/7/19
 * @info: 个人中心 订单管理 历史记录 Fragment
 */
public class CoinOrderMoreFragment extends BaseFragment {

    private FragmentCoinOrderLayoutBinding binding;
    private CoinOrderMoreFragmentViewModel model;
    public String type; // 0是全部委托  1是历史委托
    public CoinListEntity entity;
    public int page = 1;

    public static CoinOrderMoreFragment getCoinOrderMoreFragment(String type) {
        CoinOrderMoreFragment fragment = new CoinOrderMoreFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup viewGroup) {
        EventBus.getDefault().register(this);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_coin_order_layout, viewGroup, false);
        Bundle bundle = getArguments();
        type = bundle.getString("type");
        return binding.getRoot();
    }

    @Override
    protected void onFragmentFirstVisible() {
        model = new CoinOrderMoreFragmentViewModel(this, binding);
        model.initKeyboardStatus();
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void initTitleData() {

    }

    /**
     * 选择了历史委托筛选条件的回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void number(CoinListEntity entity) {
        try {
            if (!EmptyUtil.isEmpty(entity)) {
                page = 1;
                this.entity = entity;
                this.model.getTradeCoinList(FaceApplication.getToken(), page, entity.getCoin_id()+"", entity.getType()+"", entity.getCoin_nme());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
