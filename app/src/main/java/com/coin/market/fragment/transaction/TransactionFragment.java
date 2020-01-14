package com.coin.market.fragment.transaction;

import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coin.market.R;
import com.coin.market.databinding.FragmentTransactionLayoutBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import teng.wang.comment.base.BaseFragment;

/**
 * @author 交易Fragment
 * @version v1.0
 * @Time 2018-9-3
 */
public class TransactionFragment extends BaseFragment implements View.OnClickListener{

    private TransactionViewModel quotationViewModel;
    private FragmentTransactionLayoutBinding transactionLayoutBinding;

    public static TransactionFragment getTransactionFragment() {
        return new TransactionFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup viewGroup) {
        transactionLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_transaction_layout, viewGroup, false);
        return transactionLayoutBinding.getRoot();
    }

    @Override
    protected void onFragmentFirstVisible() {
        EventBus.getDefault().register(this);
        quotationViewModel = new TransactionViewModel(this, transactionLayoutBinding);
        quotationViewModel.initKeyboardStatus();
    }

    @Override
    protected void setListener() {
        transactionLayoutBinding.transactionBarMain.transactionBb.setOnClickListener(this);
        transactionLayoutBinding.transactionBarMain.transactionFb.setOnClickListener(this);
    }

    @Override
    protected void initTitleData() {
//        transactionLayoutBinding.titleBar.txtTitle.setText("交易");
//        transactionLayoutBinding.titleBar.imgReturn.setVisibility(View.GONE);
//        transactionLayoutBinding.titleBar.mImgScan.setImageResource(R.drawable.index_search);
//        transactionLayoutBinding.titleBar.mImgScan.setVisibility(View.VISIBLE);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.transaction_bb: // 币币
                quotationViewModel.CurrentItem(0);
                quotationViewModel.setButton(0);
                break;
            case R.id.transaction_fb: // 法币
                quotationViewModel.CurrentItem(1);
                quotationViewModel.setButton(1);
                break;
            default:
                break;
        }
    }

    /**
     *  Fragment 滑到交易
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void goTransactionBB(String str) {
        try {
            if (!TextUtils.isEmpty(str) && str.equals("TransactionBb")) {
                quotationViewModel.CurrentItem(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  Fragment 滑到交易
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void goTransactionFB(String str) {
        try {
            if (!TextUtils.isEmpty(str) && str.equals("TransactionFb")) {
                quotationViewModel.CurrentItem(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  广播弹出 选择币种
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void selectCoin(String model) {
        try {
            if (null != model && model.equals("select_coin")) {
                transactionLayoutBinding.fragmentTransactionLayout.openDrawer(Gravity.LEFT); // 打开
                quotationViewModel.getCoins(quotationViewModel.type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  弹出支付方式
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void selectPay(String model) {
        try {
            if (null != model && model.equals("open_pay")) {
                transactionLayoutBinding.fragmentTransactionLayout.openDrawer(Gravity.RIGHT); // 打开

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
