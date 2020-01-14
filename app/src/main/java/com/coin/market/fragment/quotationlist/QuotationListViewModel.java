package com.coin.market.fragment.quotationlist;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.activity.quotation.QuotationActivity;
import com.coin.market.adapter.QuotationAdapter;
import com.coin.market.databinding.FragmentQuotationListLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.util.List;

import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseFragmentViewModel;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.CoinEntity;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.log.Log;

/**
 * @author: Lenovo
 * @date: 2019/8/7
 * @info: 行情List ViewModel
 */
public class QuotationListViewModel extends BaseFragmentViewModel<QuotationListFragment, FragmentQuotationListLayoutBinding> {

    // 行情adapter
    private QuotationAdapter adapter;

    public QuotationListViewModel(QuotationListFragment fragment, FragmentQuotationListLayoutBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initView() {
        initQuotationAdapter(); // 初始化 行情adapter
        getMarketList(getFragments().name, getFragments().type, getFragments().upDown); //各种行情
    }

    /**
     *  行情
     */
    public void getMarketList(String name, String type, int desc) {
        FaceApiTest.getV1ApiServiceTest().getMarketList(name, type, desc)
                .compose(RxSchedulers.<ApiModel<List<CoinEntity>>>io_main())
                .subscribe(new RxObserver<List<CoinEntity>>(getContexts(), getFragments().getTags(), false) {
                    @Override
                    public void onSuccess(List<CoinEntity> list) {
                        try {
                            Log.e("cjh>>>", "getMarketList:" + new Gson().toJson(list));
                            if (!EmptyUtil.isEmpty(adapter) && !EmptyUtil.isEmpty(list)) {
                                adapter.clear();
                                adapter.addAll(list);
                            }else {
                                //占位图
                                getBinding().quotationRecycler.setEmptyView(R.layout.comment_view_seat_layout);
                                getBinding().quotationRecycler.showEmpty();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    // 初始化 货币行情分类 adapter
    private void initQuotationAdapter() {
        adapter = new QuotationAdapter(getContexts());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContexts());
        getBinding().quotationRecycler.setLayoutManager(layoutManager);
        getBinding().quotationRecycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // item 跳转去此币种的行情 (波线图 k线图 WebView)
                if (adapter.getItem(position).getIs_trade()==0){
                    Toast.makeText(getContexts(), "暂未开放！", Toast.LENGTH_SHORT).show();
                }else {
//                    QuotationActivity.JUMP(getContexts(), adapter.getItem(position));
                }
            }
        });
        getBinding().quotationRecycler.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBinding().quotationRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getMarketList(getFragments().name, getFragments().type, getFragments().upDown); //各种行情
                    }
                }, 1000);
            }
        });
    }

}
