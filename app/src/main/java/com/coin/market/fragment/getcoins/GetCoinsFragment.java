package com.coin.market.fragment.getcoins;

import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coin.market.R;
import com.coin.market.databinding.FragmentCoinsLayoutBinding;

import java.util.List;

import teng.wang.comment.base.BaseFragment;
import teng.wang.comment.model.AccountsDTO;

/**
 *   选择币种的 通用Fragment
 */
public class GetCoinsFragment extends BaseFragment implements View.OnClickListener {

    public static mOnClickCallback listener;
    private GetCoinsFragmentViewModel viewModel;
    private FragmentCoinsLayoutBinding binding;
    private FragmentTransaction transaction;
    public void setListener(mOnClickCallback listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup viewGroup) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_coins_layout, viewGroup, false);
        binding.titleBar.txtTitle.setVisibility(View.GONE);
        return binding.getRoot();
    }

    @Override
    protected void onFragmentFirstVisible() {
        viewModel = new GetCoinsFragmentViewModel(this, binding);
        viewModel.initKeyboardStatus();
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

        void selectItem(String id, String name, AccountsDTO ad);

    }

}
