package com.coin.market.fragment.quotationlist;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coin.market.R;
import com.coin.market.databinding.FragmentQuotationListLayoutBinding;

import teng.wang.comment.base.BaseFragment;

/**
 * @author: Lenovo
 * @date: 2019/8/7
 * @info: 行情list
 */
public class QuotationListFragment extends BaseFragment implements View.OnClickListener {

    private FragmentQuotationListLayoutBinding binding;
    private QuotationListViewModel model;
    public String name = "USDT";
    public String type = "name";
    public int upDown;  // upDown 升降序
    private int SortName, SortPrice, SortRange;

    public static QuotationListFragment getQuotationListFragment(String name){
        QuotationListFragment fragment = new QuotationListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup viewGroup) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_quotation_list_layout, viewGroup, false);
        Bundle bundle = getArguments();
        name = bundle.getString("name");
        return binding.getRoot();
    }

    @Override
    protected void onFragmentFirstVisible() {
        model = new QuotationListViewModel(this, binding);
        model.initKeyboardStatus();
    }

    @Override
    protected void setListener() {
        binding.quotaSortNameButton.setOnClickListener(this);
        binding.quotaSortPriceButton.setOnClickListener(this);
        binding.quotaSortRangeButton.setOnClickListener(this);
    }

    private void setButton(int sort) {
        binding.quotaSortNameImg.setImageResource(R.drawable.quotes_sort);
        binding.quotaSortPriceImg.setImageResource(R.drawable.quotes_sort);
        binding.quotaSortRangeImg.setImageResource(R.drawable.quotes_sort);
        switch (sort) {
            case 1:
                binding.quotaSortNameImg.setImageResource(R.drawable.quotes_ascending);
                upDown = 0;
                break;
            case 2:
                binding.quotaSortNameImg.setImageResource(R.drawable.quotes_descending);
                upDown = 1;
                break;
            case 3:
                binding.quotaSortPriceImg.setImageResource(R.drawable.quotes_ascending);
                upDown = 0;
                break;
            case 4:
                binding.quotaSortPriceImg.setImageResource(R.drawable.quotes_descending);
                upDown = 1;
                break;
            case 5:
                binding.quotaSortRangeImg.setImageResource(R.drawable.quotes_ascending);
                upDown = 0;
                break;
            case 6:
                binding.quotaSortRangeImg.setImageResource(R.drawable.quotes_descending);
                upDown = 1;
                break;
            default:
                break;
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.quota_sort_name_button: // 按名称排序
                type = "name";
                if (SortName==0){
                    setButton(1);
                    SortName = 1;
                }else {
                    setButton(2);
                    SortName = 0;
                }
                model.getMarketList(name, type, upDown);
                break;
            case R.id.quota_sort_price_button: // 按最新价排序
                type = "price";
                if (SortPrice==0){
                    setButton(3);
                    SortPrice = 1;
                }else {
                    setButton(4);
                    SortPrice = 0;
                }
                model.getMarketList(name, type, upDown);
                break;
            case R.id.quota_sort_range_button: // 按涨跌幅
                type = "rate";
                if (SortRange==0){
                    setButton(5);
                    SortRange = 1;
                }else {
                    setButton(6);
                    SortRange = 0;
                }
                model.getMarketList(name, type, upDown);
                break;
            default:
                break;
        }
    }
}
