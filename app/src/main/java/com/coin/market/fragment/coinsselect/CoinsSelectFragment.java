package com.coin.market.fragment.coinsselect;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coin.market.R;
import com.coin.market.databinding.FragmentCoinsLayoutBinding;

import teng.wang.comment.base.BaseFragment;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.FB_BBEntity;

/**
 *     法币  选择币种的 通用Fragment
 */
public class CoinsSelectFragment extends BaseFragment implements View.OnClickListener {

    public static mOnClickCallback listener;
    private CoinsSelectFragmentViewModel selectFragmentViewModel;
    private FragmentCoinsLayoutBinding binding;
    private FragmentTransaction transaction;
    public String accountType;

    public static CoinsSelectFragment getCoinsSelectFragment(String accountType){
        CoinsSelectFragment fragment = new CoinsSelectFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("accountType", accountType);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setListener(mOnClickCallback listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup viewGroup) {
        Bundle bundle = getArguments();
        accountType = bundle.getString("accountType");
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_coins_layout, viewGroup, false);
        selectFragmentViewModel = new CoinsSelectFragmentViewModel(this, binding);
        binding.titleBar.txtTitle.setVisibility(View.GONE);
        return binding.getRoot();
    }

    @Override
    protected void onFragmentFirstVisible() {
        selectFragmentViewModel.initKeyboardStatus();
    }

    public void getData(String accountType){
        this.accountType = accountType;
        selectFragmentViewModel.getOtcCoins(FaceApplication.getToken(), accountType); // 获取币种列表数据
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
    }

    @Override
    protected void initTitleData() {

    }


    public void fragmentisHidden(FragmentTransaction transaction) {
        this.transaction = transaction;
    }

    public void fragmentHid(){
        if (null != transaction) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.hide(this);
            transaction.commit();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_Return:
                fragmentHid();
                break;
            default:
                break;
        }
    }


    /**
     *   选择item的回调
     */
    public interface mOnClickCallback {

        void selectItem(FB_BBEntity.ListdetailBean entity);

    }

}
