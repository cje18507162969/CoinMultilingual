package com.coin.market.fragment.transaction.transactionlist;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coin.market.R;
import com.coin.market.databinding.FragmentTransactionListLayoutBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import teng.wang.comment.base.BaseFragment;
import teng.wang.comment.model.OTCCoinTypeEntity;
import teng.wang.comment.model.ScreenBean;

/**
 * @author: Lenovo
 * @date: 2019/7/31
 * @info: 交易列表 买卖列表
 */
public class TransactionListFragment extends BaseFragment {

    public String BuyOrSell = ""; // 购买或出售   交易类型(sell、buy)
    private TransactionListViewModel model;
    private FragmentTransactionListLayoutBinding binding;
    public OTCCoinTypeEntity entity;
    public String price, payName; // 筛选的2个条件

    public static TransactionListFragment getTransactionListFragment(String type, OTCCoinTypeEntity entity){
        TransactionListFragment fragment = new TransactionListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putSerializable("OTCCoinTypeEntity", entity);
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transaction_list_layout, viewGroup, false);
        Bundle bundle = getArguments();
        BuyOrSell = bundle.getString("type");
        entity = (OTCCoinTypeEntity) bundle.getSerializable("OTCCoinTypeEntity");
        return binding.getRoot();
    }

    @Override
    protected void onFragmentFirstVisible() {
        try {
            model = new TransactionListViewModel(this, binding);
            model.initKeyboardStatus();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     *   筛选
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void screen(ScreenBean bean) {
        try {
            if (null != bean) {
                price = bean.getMoney();
                payName = bean.getPay();
                this.model.page = 1;
                this.model.http();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *   发布订单  刷新列表
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void publish(String str) {
        try {
            if (null != str && str.equals("publish")) {
                model.page = 1;
                this.model.http();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
