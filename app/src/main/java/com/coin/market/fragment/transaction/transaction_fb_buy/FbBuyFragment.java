package com.coin.market.fragment.transaction.transaction_fb_buy;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coin.market.R;
import com.coin.market.databinding.FragmentTransactionFbBuyLayoutBinding;

import teng.wang.comment.base.BaseFragment;

/**
 * @author: Lenovo
 * @date: 2019/7/31
 * @info: 法币交易 我要买 fragment
 */
public class FbBuyFragment extends BaseFragment implements View.OnClickListener {

    private FragmentTransactionFbBuyLayoutBinding binding;
    private FbBuyViewModel model;

    public static FbBuyFragment getFbBuyFragment() {
        return new FbBuyFragment();
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup viewGroup) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transaction_fb_buy_layout, viewGroup, false);
        return binding.getRoot();
    }

    @Override
    protected void onFragmentFirstVisible() {
        model = new FbBuyViewModel(this, binding);
        model.initKeyboardStatus();
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void initTitleData() {
    }


    public void onClick(View view) {
        switch (view.getId()) {

            default:
                break;
        }
    }
}
