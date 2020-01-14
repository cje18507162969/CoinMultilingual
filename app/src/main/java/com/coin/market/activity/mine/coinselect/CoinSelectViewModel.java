package com.coin.market.activity.mine.coinselect;

import android.support.v7.widget.LinearLayoutManager;

import com.coin.market.R;
import com.coin.market.activity.mine.coinout.CoinOutActivity;
import com.coin.market.activity.mine.coinrecharge.CoinRechargeActivity;
import com.coin.market.adapter.CoinListAdapter;
import com.coin.market.databinding.ActivityCoinSelectLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.util.List;

import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.CoinListEntity;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.log.Log;

public class CoinSelectViewModel extends BaseActivityViewModel<CoinSelectActivity, ActivityCoinSelectLayoutBinding> {

    private CoinListAdapter adapter;

    public CoinSelectViewModel(CoinSelectActivity activity, ActivityCoinSelectLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        initQuotationAdapter(); // 初始化 币种列表 adapter
        getCoinList(); // 获取币种列表数据
    }

    /**
     * 初始化 货币行情分类  Navigation adapter
     */
    private void initQuotationAdapter() {
        adapter = new CoinListAdapter(getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        getBinding().selectRecycler.setLayoutManager(layoutManager);
        getBinding().selectRecycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                switch (getActivity().getIntent().getStringExtra("Jump")) {
                    case "CB":
                        // item 跳转去此币种的充币详情  Qr二维码
                        CoinRechargeActivity.JUMP(getActivity(), adapter.getItem(position).getCoin_id() + "", adapter.getItem(position).getName());
                        getActivity().finish();
                        break;
                    case "TB":
                        // item 跳转去此币种的提币详情  Qr二维码
                        CoinOutActivity.JUMP(getActivity(), adapter.getItem(position).getCoin_id() + "", adapter.getItem(position).getName());
                        getActivity().finish();
                        break;
                    default:
                        break;
                }

            }
        });
    }

    /**
     * 获取充币 币种类型
     */
    public void getCoinList() {
//        FaceApiTest.getV1ApiServiceTest().getCionList()
//                .compose(RxSchedulers.<ApiModel<List<CoinListEntity>>>io_main())
//                .subscribe(new RxObserver<List<CoinListEntity>>(getActivity(), getActivity().Tag, false) {
//                    @Override
//                    public void onSuccess(List<CoinListEntity> list) {
//                        try {
//                            Log.e("cjh>>>", "List<CoinListEntity>:" + new Gson().toJson(list));
//                            if (!EmptyUtil.isEmpty(adapter) && !EmptyUtil.isEmpty(list)) {
//                                adapter.clear();
//                                adapter.addAll(list);
//                            } else {
//                                //占位图
//                                getBinding().selectRecycler.setEmptyView(R.layout.comment_view_seat_layout);
//                                getBinding().selectRecycler.showEmpty();
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
    }

}
