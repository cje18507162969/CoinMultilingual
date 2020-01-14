package com.coin.market.fragment.transaction.transaction_fb_sell;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coin.market.R;
import com.coin.market.databinding.FragmentTransactionFbSellLayoutBinding;

import teng.wang.comment.base.BaseFragment;

/**
 * @author: Lenovo
 * @date: 2019/7/31
 * @info: 法币交易 我要卖 fragment
 */
public class FbSellFragment extends BaseFragment {

    private FragmentTransactionFbSellLayoutBinding binding;
    private FbSellViewModel model;

    public static FbSellFragment getFbSellFragment() {
        return new FbSellFragment();
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup viewGroup) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transaction_fb_sell_layout, viewGroup, false);
        return binding.getRoot();
    }

    @Override
    protected void onFragmentFirstVisible() {
        model = new FbSellViewModel(this, binding);
        model.initKeyboardStatus();
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void initTitleData() {
    }

}
