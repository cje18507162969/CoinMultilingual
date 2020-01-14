package com.coin.market.activity.mine.coinorderinfo;

import android.support.v7.widget.LinearLayoutManager;

import com.coin.market.R;
import com.coin.market.adapter.CoinOrderInfoAdapter;
import com.coin.market.databinding.ActivityCoinOrderInfoLayoutBinding;
import com.coin.market.util.EmptyUtil;

import java.util.List;

import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.CoinOrderInfoEntity;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;

/**
 * @author: Lenovo
 * @date: 2019/7/23
 * @info: 成交明细 ViewModel
 */
public class CoinOrderInfoModel extends BaseActivityViewModel<CoinOrderInfoActivity, ActivityCoinOrderInfoLayoutBinding> {

    private CoinOrderInfoAdapter adapter;

    public CoinOrderInfoModel(CoinOrderInfoActivity activity, ActivityCoinOrderInfoLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        initAdapter();
        getOrderInfoList(FaceApplication.getToken(), getActivity().orderId, getActivity().type);
    }

    private void initAdapter(){
        adapter = new CoinOrderInfoAdapter(getActivity());
        getBinding().coinOrderInfoRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        getBinding().coinOrderInfoRecycler.setAdapter(adapter);
    }

    /**
     *  历史详情 成交明细下方的List 记录
     */
    public void getOrderInfoList(String token, String orderId, String type) {
        FaceApiTest.getV1ApiServiceTest().getTradeDetails(token, orderId, type)
                .compose(RxSchedulers.<ApiModel<List<CoinOrderInfoEntity>>>io_main())
                .subscribe(new RxObserver<List<CoinOrderInfoEntity>>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(List<CoinOrderInfoEntity> list) {
                        try {
                            if (!EmptyUtil.isEmpty(adapter)){
                                for (int i = 0; i < list.size(); i++) {
                                    list.get(i).setCoinName(getActivity().coinName);
                                    list.get(i).setPayName(getActivity().payName);
                                    list.get(i).setType(getActivity().type);
                                }
                                adapter.clear();
                                adapter.addAll(list);
                            }else {
                                getBinding().coinOrderInfoRecycler.setEmptyView(R.layout.comment_view_seat_layout);
                                getBinding().coinOrderInfoRecycler.showEmpty();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
