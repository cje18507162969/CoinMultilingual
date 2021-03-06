package com.coin.market.fragment.transaction.transaction_fb_sell;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.coin.market.databinding.FragmentTransactionFbSellLayoutBinding;
import com.coin.market.fragment.transaction.transactionlist.TransactionListFragment;
import com.coin.market.util.EmptyUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import teng.wang.comment.api.FaceApiMbr;
import teng.wang.comment.base.BaseFragmentViewModel;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.OTCCoinTypeEntity;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.widget.ViewPageAdapter;

/**
 * @author: Lenovo
 * @date: 2019/7/31
 * @info: 法币交易 我要卖 FragmentViewModel
 */
public class FbSellViewModel extends BaseFragmentViewModel <FbSellFragment, FragmentTransactionFbSellLayoutBinding>{

    private ViewPageAdapter mAdapter;
    private String type = "";
    private int itemType;

    public FbSellViewModel(FbSellFragment fragment, FragmentTransactionFbSellLayoutBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initView() {
        getTabCoins(); // 初始化 tab
    }

    private void initTabLayout(List<OTCCoinTypeEntity> list) {
        if (EmptyUtil.isEmpty(list)){
            return;
        }
        List<String> mTitles = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            mTitles.add(list.get(i).getName());
        }
        mAdapter = new ViewPageAdapter(getFragments().getActivity().getSupportFragmentManager(), new ArrayList<Fragment>(), mTitles);

        for (int i = 0; i < list.size(); i++) {
            getBinding().transactionBbSellTabLayout.addTab(getBinding().transactionBbSellTabLayout.newTab().setText(list.get(i).getName()));
            mAdapter.fragmentsList.add(TransactionListFragment.getTransactionListFragment("sell", list.get(i)));
        }

        getBinding().fbSellViewpager.setAdapter(mAdapter);
        getBinding().fbSellViewpager.setCurrentItem(0);
        getBinding().fbSellViewpager.setOffscreenPageLimit(mAdapter.getCount());
        getBinding().transactionBbSellTabLayout.setTabMode(0);
        getBinding().transactionBbSellTabLayout.setupWithViewPager(getBinding().fbSellViewpager);
    }

    /**
     *      获取法币 Tab
     */
    public void getTabCoins() {
        FaceApiMbr.getV1ApiServiceMbr().getOTCTabList()
                .compose(RxSchedulers.<ApiModel<List<OTCCoinTypeEntity>>>io_main())
                .subscribe(new RxObserver<List<OTCCoinTypeEntity>>(getContexts(), getFragments().getTags(), false) {
                    @Override
                    public void onSuccess(List<OTCCoinTypeEntity> list) {
                        try {
                            Log.e("cjh>>>", "获取Tab:" + new Gson().toJson(list));
                            if (!EmptyUtil.isEmpty(list)) {
                                initTabLayout(list); // 初始化 tab
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
