package com.coin.market.fragment.transaction.transaction_fb;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coin.market.R;
import com.coin.market.activity.mine.publish.MyPublishActivity;
import com.coin.market.activity.mine.transfer.TransferActivity;
import com.coin.market.databinding.FragmentTransactionFbLayoutBinding;
import com.coin.market.activity.transaction.launch.LaunchPosterActivity;
import com.coin.market.activity.transaction.orderrecord.OrderRecordActivity;
import com.coin.market.activity.transaction.paymode.PayModeActivity;
import com.coin.market.wight.popupwindow.TransactionFbPopWindow;

import org.greenrobot.eventbus.EventBus;

import teng.wang.comment.base.BaseFragment;

/**
 * @author   交易Fragment
 * @version v1.0
 * @Time 2018-9-3
 */
public class TransactionFBFragment extends BaseFragment implements View.OnClickListener{

    private TransactionFBViewModel bbViewModel;
    private FragmentTransactionFbLayoutBinding fbLayoutBinding;
    private TransactionFbPopWindow popWindow;
    private String CoinId = "";
    public String isShop;

    public static TransactionFBFragment getTransactionFBFragment() {
        return new TransactionFBFragment();
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup viewGroup) {
        fbLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_transaction_fb_layout, viewGroup, false);
        return fbLayoutBinding.getRoot();
    }

    @Override
    protected void onFragmentFirstVisible() {
        bbViewModel = new TransactionFBViewModel(this, fbLayoutBinding);
        bbViewModel.initKeyboardStatus();
    }

    @Override
    protected void setListener() {
        fbLayoutBinding.transactionFbPay.setOnClickListener(this);
        fbLayoutBinding.transactionFbPop.setOnClickListener(this);
        fbLayoutBinding.transactionFbBuy.setOnClickListener(this);
        fbLayoutBinding.transactionFbSell.setOnClickListener(this);
    }

    @Override
    protected void initTitleData() {
    }

    private void initPopupWindow() {
        popWindow = new TransactionFbPopWindow(getActivity(), isShop);
        popWindow.showAtBottom(fbLayoutBinding.transactionFbPop);
        popWindow.setListener(new TransactionFbPopWindow.selectOnClick() {
            @Override
            public void OnClick(int position) {
                // PopupWindow选择item回调
                switch (position) {
                    case 0: // 划转
                        TransferActivity.JUMP(getContext());
                        break;
                    case 1: // 订单记录
                        OrderRecordActivity.JUMP(getContext());
                        break;
                    case 2: // 收款方式
                        PayModeActivity.JUMP(getContext());
                        break;
                    case 3: // 发布广告
                        LaunchPosterActivity.JUMP(getContext());
                        break;
                    case 4: // 我的广告
                        MyPublishActivity.JUMP(getContext());
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.transaction_fb_pay: // 法币 支付方式
                EventBus.getDefault().post("open_pay");
                break;
            case R.id.transaction_fb_pop: // 法币 PopupWindow
                initPopupWindow();
                break;
            case R.id.transaction_fb_buy: // 法币 PopupWindow
                fbLayoutBinding.transactionFbViewPager.setCurrentItem(0);
                break;
            case R.id.transaction_fb_sell: // 法币 PopupWindow
                fbLayoutBinding.transactionFbViewPager.setCurrentItem(1);
                break;
            default:
                break;
        }
    }
}
