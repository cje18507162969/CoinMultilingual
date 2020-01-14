package com.coin.market.fragment.share;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coin.market.R;
import com.coin.market.databinding.FragmentShareLayoutBinding;

import teng.wang.comment.base.BaseFragment;
import teng.wang.comment.base.FaceApplication;

/**
 * @author: cjh
 * @date: 2019/10/21
 * @info: 返佣记录
 */
public class Share2Fragment extends BaseFragment {

    public static Share2Fragment getShare2Fragment(String name){
        Share2Fragment fragment = new Share2Fragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", name);
        fragment.setArguments(bundle);
        return fragment;
    }

    private FragmentShareLayoutBinding binding;
    private Share2ViewModel model;
    public String name = "";
    public String time = "";  // 邀请记录 全部 1 生效 2  失效 3
    public int page = 1;

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup viewGroup) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_share_layout, viewGroup, false);
        Bundle bundle = getArguments();
        name = bundle.getString("type");
        return binding.getRoot();
    }

    @Override
    protected void onFragmentFirstVisible() {
        model = new Share2ViewModel(this, binding);
        init();
        model.initKeyboardStatus();
    }

    private void init(){
        binding.tv2.setVisibility(View.VISIBLE);
        binding.tv2.setText("时间");
        binding.tv3.setText("返佣折合USDT");
    }

    public void setType(String time){
        this.time = time;
        page = 1;
        model.getBackMoney(FaceApplication.getToken(), page, 1);
    }

}
