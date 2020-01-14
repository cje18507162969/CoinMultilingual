package com.coin.market.fragment.hot;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coin.market.R;
import com.coin.market.activity.quotation.QuotationActivity;
import com.coin.market.adapter.HomeCoinAdapter;
import com.coin.market.databinding.FragmentHotLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;


import teng.wang.comment.base.BaseFragment;
import teng.wang.comment.model.CoinsEntity;
import teng.wang.comment.model.MarketsEntity;

/**
 * @author: cjh
 * @date: 2019/10/21
 * @info:
 */
public class HotFragment extends BaseFragment {


    private HotViewModel hotViewModel;
    private FragmentHotLayoutBinding binding;
    private MarketsEntity marketsEntity;
    private HomeCoinAdapter coinAdapter;  // 上方货币行情

    public static HotFragment getHotFragment(MarketsEntity marketsEntity) {
        HotFragment fragment = new HotFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("marketsEntity", marketsEntity);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup viewGroup) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_hot_layout, viewGroup, false);
        setUi();
        return binding.getRoot();
    }

    @Override
    protected void onFragmentFirstVisible() {
        hotViewModel = new HotViewModel(this, binding);
        hotViewModel.initKeyboardStatus();
    }

    private void setUi() {
        Bundle bundle = getArguments();
        marketsEntity = (MarketsEntity) bundle.getSerializable("marketsEntity");
        if (EmptyUtil.isEmpty(marketsEntity)) {
            return;
        }
        initCoinAdapter();
        coinAdapter.clear();
        coinAdapter.addAll(marketsEntity.getList());
    }

    // 初始化 货币行情adapter
    private void initCoinAdapter() {
        coinAdapter = new HomeCoinAdapter(getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.coinRecycler.setLayoutManager(layoutManager);
        binding.coinRecycler.setAdapter(coinAdapter);
        coinAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                QuotationActivity.JUMP(getActivity(), coinAdapter.getItem(position));
            }
        });
    }

}
