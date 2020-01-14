package com.coin.market.fragment.getcoins;

import android.support.v7.widget.LinearLayoutManager;

import com.coin.market.R;
import com.coin.market.adapter.CoinListAdapter;
import com.coin.market.adapter.CoinListSelAdapter;
import com.coin.market.databinding.FragmentCoinsLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.util.List;

import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseFragmentViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.AccountsDTO;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.CoinListEntity;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.log.Log;

public class GetCoinsFragmentViewModel extends BaseFragmentViewModel<GetCoinsFragment, FragmentCoinsLayoutBinding> {

    private CoinListSelAdapter adapter;

    public GetCoinsFragmentViewModel(GetCoinsFragment fragment, FragmentCoinsLayoutBinding binding) {
        super(fragment, binding);
    }


    @Override
    protected void initView() {
        initQuotationAdapter(); // 初始化 币种列表 adapter
        getCoinList(); // 获取币种列表数据
    }

    /**
     *  初始化 货币行情分类  Navigation adapter
     */
    private void initQuotationAdapter() {
        adapter = new CoinListSelAdapter(getContexts());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContexts());
        getBinding().selectRecycler.setLayoutManager(layoutManager);
        getBinding().selectRecycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // item 跳转去此币种的充币详情  Qr二维码  这里Fragment直接finish掉  发广播给二维码页面 刷新数据
                if (!EmptyUtil.isEmpty(getFragments().listener)){
                    getFragments().listener.selectItem(adapter.getItem(position).getCoinId()+"", adapter.getItem(position).getCoinName(), adapter.getItem(position));//adapter.getItem(position).getExport_min() 最小提币数量
                    getFragments().fragmentHid();
                }
            }
        });
    }

    /**
     *    获取充币  币种类型
     */
    public void getCoinList() {
        FaceApiTest.getV1ApiServiceTest().getTbCionList(FaceApplication.getToken())
                .compose(RxSchedulers.<ApiModel<List<AccountsDTO>>>io_main())
                .subscribe(new RxObserver<List<AccountsDTO>>(getContexts(), getFragments().getTag(), false) {
                    @Override
                    public void onSuccess(List<AccountsDTO> list) {
                        try {
                            Log.e("cjh>>>", "提币选择币种：" + new Gson().toJson(list));
                            if (!EmptyUtil.isEmpty(adapter) && !EmptyUtil.isEmpty(list)) {
                                adapter.clear();
                                adapter.addAll(list);
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
