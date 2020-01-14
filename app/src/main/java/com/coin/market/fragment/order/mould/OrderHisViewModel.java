package com.coin.market.fragment.order.mould;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.coin.market.adapter.OrderHisAdapter;
import com.coin.market.databinding.FragmentOrdermodelLayoutBinding;
import com.coin.market.util.StringUtils;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseFragmentViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.ContraceTradeDTO;
import teng.wang.comment.model.ContraceTradeModel;
import teng.wang.comment.model.ReceivedData;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.log.Log;

/**
 * @author 订单 历史记录ViewModel
 * @version v1.0
 * @Time 2018-9-3
 */

public class OrderHisViewModel extends BaseFragmentViewModel<OrderHisFragment, FragmentOrdermodelLayoutBinding> {

    public OrderHisAdapter orderMouldAdapter;
    /**
     * 设置合约参数
     */
    public String contractId;  // 合约id
    public String mprofit ;  // 盈利比例
    public String mloss  ;  // 盈利比例
    public OrderHisViewModel(OrderHisFragment fragment, FragmentOrdermodelLayoutBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initView() {

        page = 1;
        limit = 10;
        getRefreshData();
        initOrder();
        getContracetradelogPage(page);
    }


    /**
     * 虚拟数据
     */
    public ReceivedData.csListData csListData = null;
    public void initData(){
        Gson gson = new Gson();
        String result = StringUtils.readJsonFromAssets(getContexts(), "cs.json");
        csListData = gson.fromJson(result, ReceivedData.csListData.class);

    }

    /**
     * 刷新数据
     */
    private void getRefreshData() {

        getBinding().orderSwipeRefreshLayout.setEnableRefresh(true);//是否启用下拉刷新功能
        getBinding().orderSwipeRefreshLayout.setEnableLoadmore(true);//是否启用上拉加载功能
        getBinding().orderSwipeRefreshLayout.setEnableAutoLoadmore(false);//是否启用自动加载功能
        getBinding().orderSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                getContracetradelogPage(page);
            }
        });
        getBinding().orderSwipeRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(final RefreshLayout refreshLayout) {

                if (page == 1)
                    page = 2;
                getContracetradelogPage(page);

            }
        });

    }



    /**
     *  初始化 持有记录
     */
    public void initOrder() {
        orderMouldAdapter = new OrderHisAdapter(getContexts());
        orderMouldAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });

        getBinding().rvOrder.setLayoutManager(new LinearLayoutManager(getContexts()));
        getBinding().rvOrder.setAdapter(orderMouldAdapter);



    }


    /**
     *  设置adapter
     * @param lsJson
     */
    public void setAdapter(List<ContraceTradeDTO> lsJson){

        if (null==orderMouldAdapter){
            return;
        }
        if(page == 1){
            if (null!=lsJson&&lsJson.size()>0){
                getBinding().recyclerEmpty.setVisibility(View.GONE);
                getFragments().list.clear();
                orderMouldAdapter.clear();
                getFragments().list.addAll(lsJson);
                orderMouldAdapter.addAll(lsJson);
                orderMouldAdapter.notifyDataSetChanged();
            }else {
                getBinding().recyclerEmpty.setVisibility(View.VISIBLE);
                return;
            }

        }
        if(page > 1){
            page ++;
            getFragments().list.addAll(lsJson);
            orderMouldAdapter.addAll(lsJson);
            orderMouldAdapter.notifyItemRangeInserted(getFragments().list.size()-lsJson.size(),lsJson.size());
            getBinding().rvOrder.smoothScrollToPosition(getFragments().list.size()-lsJson.size()-1);
        }else {
            orderMouldAdapter.notifyDataSetChanged();
        }
    }

    public int page = 1;
    public int limit = 10;


    /**
     * 订单 历史记录
     * @param mpage
     */
    public void getContracetradelogPage(int mpage) {
        page = mpage;
        limit = 10;
        FaceApiTest.getV1ApiServiceTest().getContracetradelogPage(FaceApplication.getToken(),mpage,limit)
                .compose(RxSchedulers.<ApiModel<ContraceTradeModel>>io_main())
                .subscribe(new RxObserver<ContraceTradeModel>(getContexts(), getFragments().getTag(), false) {
                    @Override
                    public void onSuccess(ContraceTradeModel model) {
                        getBinding().orderSwipeRefreshLayout.finishRefresh();
                        getBinding().orderSwipeRefreshLayout.finishLoadmore();
                        Log.e("cjh>>> ","历史记录 :" + new Gson().toJson(model));
                        setAdapter(model.list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        getBinding().orderSwipeRefreshLayout.finishRefresh();
                        getBinding().orderSwipeRefreshLayout.finishLoadmore();
                    }
                });
    }






}
