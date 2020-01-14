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
 * @info: 邀请记录
 */
public class ShareFragment extends BaseFragment {

    public static ShareFragment getShareFragment(String name){
        ShareFragment fragment = new ShareFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", name);
        fragment.setArguments(bundle);
        return fragment;
    }

    private FragmentShareLayoutBinding binding;
    private ShareViewModel model;
    public String name = "";
    public String type = "1";  // 邀请记录 全部 1 生效 2  失效 3
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
        model = new ShareViewModel(this, binding);
        init();
        model.initKeyboardStatus();
    }

    private void init(){
        binding.tv2.setVisibility(View.GONE);
        binding.tv3.setText(getResources().getString(R.string.tv_time));
    }

    public void setType(String type){
        this.type = type;
        page = 1;
        model.getAllCoinList(page);
    }

}
