package com.coin.market.fragment.quotation;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coin.market.R;
import com.coin.market.databinding.FragmentQuotationLayoutBinding;

import teng.wang.comment.base.BaseFragment;

/**
 * @author 行情Fragment
 * @version v1.0
 * @Time 2018-9-3
 */
public class QuotationFragment extends BaseFragment implements View.OnClickListener {

    private QuotationViewModel quotationViewModel;
    private FragmentQuotationLayoutBinding quotationLayoutBinding;

    public static QuotationFragment getQuotationFragment() {
        return new QuotationFragment();
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup viewGroup) {
        quotationLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_quotation_layout, viewGroup, false);
        return quotationLayoutBinding.getRoot();
    }

    @Override
    protected void onFragmentFirstVisible() {
        quotationViewModel = new QuotationViewModel(this, quotationLayoutBinding);
        quotationViewModel.initKeyboardStatus();
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void initTitleData() {
        quotationLayoutBinding.titleBar.txtTitle.setText("行情");
        quotationLayoutBinding.titleBar.imgReturn.setVisibility(View.GONE);
        quotationLayoutBinding.titleBar.mImgScan.setImageResource(R.drawable.index_search);
        quotationLayoutBinding.titleBar.mImgScan.setVisibility(View.GONE);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            default:
                break;
        }
    }
}
