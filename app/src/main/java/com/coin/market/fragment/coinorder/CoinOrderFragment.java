package com.coin.market.fragment.coinorder;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coin.market.R;
import com.coin.market.databinding.FragmentCoinOrderLayoutBinding;
import com.coin.market.util.EmptyUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import teng.wang.comment.base.BaseFragment;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.CoinListEntity;
import teng.wang.comment.model.EntrustInfo;
import teng.wang.comment.widget.ActionSheetDialog;

/**
 * @author: Lenovo
 * @date: 2019/7/19
 * @info: 个人中心 订单管理 全部委托 历史记录 Fragment
 */
public class CoinOrderFragment extends BaseFragment {

    private FragmentCoinOrderLayoutBinding binding;
    private CoinOrderFragmentViewModel model;
    public String type; // 0是全部委托  1是历史委托
    public CoinListEntity entity;
    public int page = 1;

    public static CoinOrderFragment getCoinOrderFragment(String type) {
        CoinOrderFragment fragment = new CoinOrderFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup viewGroup) {
        EventBus.getDefault().register(this);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_coin_order_layout, viewGroup, false);
        Bundle bundle = getArguments();
        type = bundle.getString("type");
        return binding.getRoot();
    }

    @Override
    protected void onFragmentFirstVisible() {
        model = new CoinOrderFragmentViewModel(this, binding);
        model.initKeyboardStatus();
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void initTitleData() {

    }

    /**
     * 撤销弹窗
     */
    public void showOrderDialog(final EntrustInfo entity) {
        new ActionSheetDialog(getActivity())
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .setTitle("是否撤销")
                .setOnDismissListener(new ActionSheetDialog.DialogOnDismissListener() {
                    @Override
                    public void onDialogDismiss(ActionSheetDialog dialog) {

                    }
                })
                .addSheetItem("撤销", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        model.cancelOrder(FaceApplication.getToken(), entity);
                    }
                }).show();
    }

    /**
     * 选择了筛选条件的回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void number(CoinListEntity entity) {
        try {
            if (!EmptyUtil.isEmpty(entity)) {
                page = 1;
                this.entity = entity;
                this.model.getEntrustsAll(FaceApplication.getToken(), page, entity.getCoin_id() + "", entity.getType() + "", entity.getCoin_nme());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
