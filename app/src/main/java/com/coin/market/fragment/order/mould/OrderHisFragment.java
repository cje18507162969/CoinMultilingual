package com.coin.market.fragment.order.mould;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coin.market.R;
import com.coin.market.databinding.FragmentOrdermodelLayoutBinding;
import com.coin.market.util.MyTimeTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import teng.wang.comment.base.BaseFragment;
import teng.wang.comment.model.ContraceTradeDTO;

/**
 * @author   订单Fragment
 * @version v1.0
 * @Time 2018-9-3
 */
public class OrderHisFragment extends BaseFragment implements View.OnClickListener{

    private OrderHisViewModel bbViewModel;
    private FragmentOrdermodelLayoutBinding ordermouldLayoutBinding;
    public String isShop;
    public static final String ORDERLIST_STATE = "ORDERLIST_STATE";  // 0 持有记录  1 历史记录

    public List<ContraceTradeDTO> list = new ArrayList<>();
    public static OrderHisFragment getOrderHisFragment(int state) {
        Bundle args = new Bundle();
        args.putInt(ORDERLIST_STATE, state);
        OrderHisFragment orderMouldFragment = new OrderHisFragment();
        orderMouldFragment.setArguments(args);
        return orderMouldFragment;
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup viewGroup) {
        ordermouldLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_ordermodel_layout, viewGroup, false);
        return ordermouldLayoutBinding.getRoot();
    }

    @Override
    protected void onFragmentFirstVisible() {
//        orderstate = getArguments().getInt(ORDERLIST_STATE);
        bbViewModel = new OrderHisViewModel(this, ordermouldLayoutBinding);
        bbViewModel.initKeyboardStatus();
    }

    @Override
    protected void onFragmentVisible() {
        super.onFragmentVisible();
        if (null!=bbViewModel){
            if (null!=bbViewModel.orderMouldAdapter){
                bbViewModel.getContracetradelogPage(1);
            }
        }
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initTitleData() {
        EventBus.getDefault().register(this);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }



    /**
     *  刷新
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Order(String str) {
        try {
            if (null!=bbViewModel){
                if (!TextUtils.isEmpty(str) && str.equals("RefreshHis")){
                    bbViewModel.getContracetradelogPage(1);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void onClick(View view) {
        switch (view.getId()) {
            case 0: // 持有记录
                break;

            default:
                break;
        }
    }
}
