package com.coin.market.fragment.transaction.transaction_fb;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.coin.market.R;
import com.coin.market.databinding.FragmentTransactionFbLayoutBinding;
import com.coin.market.fragment.transaction.transaction_fb_buy.FbBuyFragment;
import com.coin.market.fragment.transaction.transaction_fb_sell.FbSellFragment;

import java.util.ArrayList;

import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseFragmentViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.UserInfosDTO;
import teng.wang.comment.model.UserModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.widget.ViewPageAdapter;

/**
 * @author 交易Fragment ViewModel
 * @version v1.0
 * @Time 2018-9-3
 */

public class TransactionFBViewModel extends BaseFragmentViewModel<TransactionFBFragment, FragmentTransactionFbLayoutBinding> {

    private ViewPageAdapter mAdapter;

    public TransactionFBViewModel(TransactionFBFragment fragment, FragmentTransactionFbLayoutBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initView() {
        initFragment();
        getUserInfo(FaceApplication.getToken());
    }

    /**
     * 初始化ViewPager Adapter
     */
    private void initFragment() {
        mAdapter = new ViewPageAdapter(getFragments().getChildFragmentManager(), new ArrayList<Fragment>(), new ArrayList<String>());
        mAdapter.fragmentsList.add(FbBuyFragment.getFbBuyFragment());//我要买
        mAdapter.fragmentsList.add(FbSellFragment.getFbSellFragment());//我要卖

        getBinding().transactionFbViewPager.setAdapter(mAdapter);
        getBinding().transactionFbViewPager.setCurrentItem(0);
        getBinding().transactionFbViewPager.setOffscreenPageLimit(mAdapter.getCount());
        getBinding().transactionFbViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setButton(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setButton(int item) {
        getBinding().transactionFbBuy.setTextSize(14);
        getBinding().transactionFbSell.setTextSize(14);
        getBinding().transactionFbBuy.setTextColor(getFragments().getResources().getColor(R.color.color_999999));
        getBinding().transactionFbSell.setTextColor(getFragments().getResources().getColor(R.color.color_999999));
        switch (item) {
            case 0:
                getBinding().transactionFbBuy.setTextSize(20);
                getBinding().transactionFbBuy.setTextColor(getFragments().getResources().getColor(R.color.app_home_text));
                break;
            case 1:
                getBinding().transactionFbSell.setTextSize(20);
                getBinding().transactionFbSell.setTextColor(getFragments().getResources().getColor(R.color.app_home_text));
                break;
            default:
                break;
        }
    }

    /**
     *   获取用户信息 判断是否可以发布广告
     */
    public void getUserInfo(final String token) {
        FaceApiTest.getV1ApiServiceTest().userInfo(token)
                .compose(RxSchedulers.<ApiModel<UserInfosDTO>>io_main())
                .subscribe(new RxObserver<UserInfosDTO>(getContexts(), getFragments().getTag(), true) {
                    @Override
                    public void onSuccess(UserInfosDTO data) {
                        try {
//                            getFragments().isShop = data.getId_flag();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
