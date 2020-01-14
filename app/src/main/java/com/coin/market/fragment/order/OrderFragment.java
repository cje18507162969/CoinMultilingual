package com.coin.market.fragment.order;

import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coin.market.R;
import com.coin.market.databinding.FragmentOrderLayoutBinding;
import com.coin.market.wight.popupwindow.TransactionFbPopWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import teng.wang.comment.base.BaseFragment;
import teng.wang.comment.utils.StatusBarUtil;

/**
 * @author   订单Fragment
 * @version v1.0
 * @Time 2018-9-3
 */
public class OrderFragment extends BaseFragment implements View.OnClickListener{

    private OrderViewModel bbViewModel;
    private FragmentOrderLayoutBinding orderLayoutBinding;
    public String isShop;

    public static OrderFragment getOrderFragment() {
        return new OrderFragment();
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup viewGroup) {
        orderLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_layout, viewGroup, false);
        return orderLayoutBinding.getRoot();
    }

    @Override
    protected void onFragmentVisible() {
        super.onFragmentVisible();
        StatusBarUtil.setColor(getActivity(), getResources().getColor(R.color.txt_color_f1f4), 0);
    }

    @Override
    protected void onFragmentFirstVisible() {

        bbViewModel = new OrderViewModel(this, orderLayoutBinding);
        bbViewModel.initKeyboardStatus();
    }

    @Override
    protected void setListener() {
        orderLayoutBinding.orderList.setOnClickListener(this);
        orderLayoutBinding.orderListHis.setOnClickListener(this);
        orderLayoutBinding.orderList.performClick();
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
     * Fragment 滑到交易
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Order(String str) {
        try {
            if (null!=bbViewModel){
                if (!TextUtils.isEmpty(str) && str.equals("OrderRefresh")) {
                    orderLayoutBinding.orderViewPager.setCurrentItem(0);
                    EventBus.getDefault().post("Refresh");
                }else if (!TextUtils.isEmpty(str) && str.equals("goOrderListHis")) {
                    orderLayoutBinding.orderViewPager.setCurrentItem(1);
//                    EventBus.getDefault().post("RefreshHis");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.order_list: // 持有记录
                orderLayoutBinding.orderViewPager.setCurrentItem(0);
                break;
            case R.id.order_list_his: // 历史记录
                orderLayoutBinding.orderViewPager.setCurrentItem(1);
                break;
            default:
                break;
        }
    }
}
