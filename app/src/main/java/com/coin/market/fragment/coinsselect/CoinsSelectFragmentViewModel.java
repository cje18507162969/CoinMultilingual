package com.coin.market.fragment.coinsselect;

import android.support.v7.widget.LinearLayoutManager;

import com.coin.market.R;
import com.coin.market.adapter.OtcCoinSelectAdapter;
import com.coin.market.databinding.FragmentCoinsLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.api.FaceApiMbr;
import teng.wang.comment.base.BaseFragmentViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.FB_BBEntity;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.log.Log;

/**
 *     法币  选择币种的 通用Fragment
 */

public class CoinsSelectFragmentViewModel extends BaseFragmentViewModel<CoinsSelectFragment, FragmentCoinsLayoutBinding> {

    private OtcCoinSelectAdapter adapter;
    private FB_BBEntity fbBbEntity;

    public CoinsSelectFragmentViewModel(CoinsSelectFragment fragment, FragmentCoinsLayoutBinding binding) {
        super(fragment, binding);
    }


    @Override
    protected void initView() {
        initQuotationAdapter(); // 初始化 币种列表 adapter
        getOtcCoins(FaceApplication.getToken(), getFragments().accountType); // 获取币种列表数据
    }

    /**
     *  初始化 货币行情分类  Navigation adapter
     */
    private void initQuotationAdapter() {
        adapter = new OtcCoinSelectAdapter(getContexts(), getFragments().accountType);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContexts());
        getBinding().selectRecycler.setLayoutManager(layoutManager);
        getBinding().selectRecycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // 法币选择币种
                if (!EmptyUtil.isEmpty(getFragments().listener)){
                    getFragments().listener.selectItem(adapter.getItem(position));
                    getFragments().fragmentHid();
                }
            }
        });
    }

    /**
     *    划转 获取币种类型
     */
    public void getOtcCoins(String token, final String accountType) {
        FaceApiMbr.getV1ApiServiceMbr().getOTCCoins(token)
                .compose(RxSchedulers.<ApiModel<FB_BBEntity>>io_main())
                .subscribe(new RxObserver<FB_BBEntity>(getContexts(), getFragments().getTag(), false) {
                    @Override
                    public void onSuccess(FB_BBEntity entity) {
                        try {
                            Log.e("cjh>>>", "获取法币 币种选择" + new Gson().toJson(entity));
                            if (!EmptyUtil.isEmpty(adapter) && !EmptyUtil.isEmpty(entity)) {
                                adapter.setAccountType(accountType);
                                adapter.clear();
                                adapter.addAll(entity.getListdetail());
                            }else {
                                //占位图
                                getBinding().selectRecycler.setEmptyView(R.layout.comment_view_seat_layout);
                                getBinding().selectRecycler.showEmpty();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
