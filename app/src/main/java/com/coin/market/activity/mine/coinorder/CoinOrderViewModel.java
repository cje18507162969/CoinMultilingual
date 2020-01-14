package com.coin.market.activity.mine.coinorder;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.coin.market.databinding.ActivityCoinOrderLayoutBinding;
import com.coin.market.fragment.coinorder.CoinOrderFragment;
import com.coin.market.fragment.coinorder.CoinOrderMoreFragment;
import com.coin.market.util.EmptyUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.CoinListEntity;
import teng.wang.comment.model.TabCoinsEntity;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.widget.ViewPageAdapter;

/**
 * @author: Lenovo
 * @date: 2019/7/19
 * @info:
 */
public class CoinOrderViewModel extends BaseActivityViewModel<CoinOrderActivity, ActivityCoinOrderLayoutBinding> {

    private ViewPageAdapter mAdapter;
    public List<CoinListEntity> coins = new ArrayList<CoinListEntity>();

    public CoinOrderViewModel(BaseActivity activity, ActivityCoinOrderLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        initFragment();
        getCoinList();
    }

    private void initFragment(){
        mAdapter = new ViewPageAdapter(getActivity().getSupportFragmentManager(), new ArrayList<Fragment>(), new ArrayList<String>());
        mAdapter.fragmentsList.add(CoinOrderFragment.getCoinOrderFragment("0"));//全部委托
        mAdapter.fragmentsList.add(CoinOrderMoreFragment.getCoinOrderMoreFragment("1"));//历史记录
        getBinding().coinOrderViewpager.setAdapter(mAdapter);
        getBinding().coinOrderViewpager.setCurrentItem(0);
        getBinding().coinOrderViewpager.setOffscreenPageLimit(mAdapter.getCount());

        getBinding().coinOrderViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setAdapterItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setAdapterItem(int position){
        getBinding().coinOrderViewpager.setCurrentItem(position);
        getActivity().setTabButton(position);
    }

    /**
     *    获取充币 币种类型
     */
    public void getCoinList() {
        FaceApiTest.getV1ApiServiceTest().getTabCoins()
                .compose(RxSchedulers.<ApiModel<List<TabCoinsEntity>>>io_main())
                .subscribe(new RxObserver<List<TabCoinsEntity>>(getActivity(), getActivity().Tag, false) {
                    @Override
                    public void onSuccess(List<TabCoinsEntity> list) {
                        try {
                            Log.e("cjh  筛选获取币种类型>>>", "TabCoinsEntity:" + new Gson().toJson(list));
                            if (!EmptyUtil.isEmpty(list)) {
                                List<CoinListEntity> coinList = new ArrayList<CoinListEntity>();
                                for (int i = 0; i < list.size(); i++) {
                                    CoinListEntity entity = new CoinListEntity();
                                    entity.setCoin_id(list.get(i).getCoin_id());
                                    entity.setName(list.get(i).getName());
                                    coinList.add(entity);
                                }
                                getActivity().dialog.setDatsa(coinList);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
