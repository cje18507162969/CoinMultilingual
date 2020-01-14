package com.coin.market.fragment.quotation;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.coin.market.databinding.FragmentQuotationLayoutBinding;
import com.coin.market.fragment.quotationlist.QuotationListFragment;
import com.coin.market.util.EmptyUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseFragmentViewModel;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.TabCoinsEntity;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.widget.ViewPageAdapter;

/**
 * @author 行情Fragment ViewModel
 * @version v1.0
 * @Time 2018-9-3
 */

public class QuotationViewModel extends BaseFragmentViewModel<QuotationFragment, FragmentQuotationLayoutBinding> {

    private ViewPageAdapter mAdapter;

    public QuotationViewModel(QuotationFragment fragment, FragmentQuotationLayoutBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initView() {
        getTabCoins();
    }

    /**
     *      获取行情首页Tab币种
     */
    public void getTabCoins() {
        FaceApiTest.getV1ApiServiceTest().getTabCoins()
                .compose(RxSchedulers.<ApiModel<List<TabCoinsEntity>>>io_main())
                .subscribe(new RxObserver<List<TabCoinsEntity>>(getContexts(), getFragments().getTags(), false) {
                    @Override
                    public void onSuccess(List<TabCoinsEntity> list) {
                        try {
                            Log.e("cjh>>>", "RankingEntity:" + new Gson().toJson(list));
                            if (!EmptyUtil.isEmpty(list)) {
                                initTabLayout(list); // 初始化 tab
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void initTabLayout(List<TabCoinsEntity> list) {
        if (EmptyUtil.isEmpty(list)){
            return;
        }
        List<String> mTitles = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            mTitles.add(list.get(i).getName());
        }
        mAdapter = new ViewPageAdapter(getFragments().getActivity().getSupportFragmentManager(), new ArrayList<Fragment>(), mTitles);
        for (int i = 0; i < mTitles.size(); i++) {
            getBinding().quotationTabLayout.addTab(getBinding().quotationTabLayout.newTab().setText(mTitles.get(i)));
            mAdapter.fragmentsList.add(QuotationListFragment.getQuotationListFragment(mTitles.get(i)));
        }
        getBinding().quotationViewpager.setAdapter(mAdapter);
        getBinding().quotationViewpager.setCurrentItem(0);
        getBinding().quotationViewpager.setOffscreenPageLimit(mAdapter.getCount());
        getBinding().quotationTabLayout.setTabMode(0);
        getBinding().quotationTabLayout.setupWithViewPager(getBinding().quotationViewpager);
//        getBinding().quotationTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                switch (tab.getPosition()) {
//                    case 0:
//                        type = "USDT";
//                        break;
//                    case 1:
//                        type = "HUSD";
//                        break;
//                    case 2:
//                        type = "BTC";
//                        break;
//                    default:
//                        break;
//                }
//                //getQuotation(type, tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
    }

}
