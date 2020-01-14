package com.coin.market.fragment.order;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;

import com.coin.market.R;
import com.coin.market.databinding.FragmentOrderLayoutBinding;
import com.coin.market.fragment.order.OrderFragment;
import com.coin.market.fragment.order.mould.OrderHisFragment;
import com.coin.market.fragment.order.mould.OrderMouldFragment;
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

public class OrderViewModel extends BaseFragmentViewModel<OrderFragment, FragmentOrderLayoutBinding> {

    private ViewPageAdapter mAdapter;

    public OrderViewModel(OrderFragment fragment, FragmentOrderLayoutBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initView() {
        initFragment();
//        getUserInfo(FaceApplication.getToken());
    }

    /**
     * 初始化ViewPager Adapter
     */
    private void initFragment() {
        mAdapter = new ViewPageAdapter(getFragments().getChildFragmentManager(), new ArrayList<Fragment>(), new ArrayList<String>());
        mAdapter.fragmentsList.add(OrderMouldFragment.getOrderMouldFragment(0));//持有记录
        mAdapter.fragmentsList.add(OrderHisFragment.getOrderHisFragment(1));//历史记录

        getBinding().orderViewPager.setAdapter(mAdapter);
        getBinding().orderViewPager.setCurrentItem(0);
        getBinding().orderViewPager.setOffscreenPageLimit(mAdapter.getCount());
        getBinding().orderViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        getBinding().orderList.setTextSize(15);
        getBinding().orderListHis.setTextSize(15);
        getBinding().orderList.setTextColor(getFragments().getResources().getColor(R.color.color_999999));
        getBinding().orderListHis.setTextColor(getFragments().getResources().getColor(R.color.color_999999));
        switch (item) {
            case 0:
                getBinding().orderList.setTextSize(24);
                getBinding().orderList.setTextColor(getFragments().getResources().getColor(R.color.txt_color_32));
                TextPaint tp = getBinding().orderList .getPaint();
                tp.setFakeBoldText(true);
                TextPaint tp1 = getBinding().orderListHis.getPaint();
                tp1.setFakeBoldText(false);
                break;
            case 1:
                getBinding().orderListHis.setTextSize(24);
                getBinding().orderListHis.setTextColor(getFragments().getResources().getColor(R.color.txt_color_32));
                TextPaint tp3 = getBinding().orderList .getPaint();
                tp3.setFakeBoldText(false);
                TextPaint tp4 = getBinding().orderListHis.getPaint();
                tp4.setFakeBoldText(true);
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
