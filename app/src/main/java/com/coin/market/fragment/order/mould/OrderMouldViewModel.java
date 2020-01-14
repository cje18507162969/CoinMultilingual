package com.coin.market.fragment.order.mould;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.adapter.OrderMouldAdapter;
import com.coin.market.databinding.FragmentOrdermodelLayoutBinding;
import com.coin.market.util.PopUtil;
import com.coin.market.util.StringUtils;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.List;

import okhttp3.RequestBody;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.api.RequestBodyUtils;
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
 * @author 订单 ViewModel
 * @version v1.0
 * @Time 2018-9-3
 */

public class OrderMouldViewModel extends BaseFragmentViewModel<OrderMouldFragment, FragmentOrdermodelLayoutBinding> {

    public OrderMouldAdapter orderMouldAdapter;
    /**
     * 设置合约参数
     */
    public String contractId;  // 合约id
    public String mprofit;  // 盈利比例
    public String mloss;  // 盈利比例

    public OrderMouldViewModel(OrderMouldFragment fragment, FragmentOrdermodelLayoutBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initView() {
        SendReq();
    }

    public void SendReq() {
        page = 1;
        limit = 10;
        getRefreshData();
        initOrder();
        getContracetradePage(page, 1);
    }

    public void SendReqOrder() {
        page = 1;
        limit = 10;
        getContracetradePage(page, 1);
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
                getContracetradePage(page,1);
            }
        });
        getBinding().orderSwipeRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(final RefreshLayout refreshLayout) {

                if (page == 1)
                    page = 2;
                getContracetradePage(page,1);

            }
        });

    }
    /**
     * 虚拟数据
     */
    public ReceivedData.csListData csListData = null;

    public void initData() {
        Gson gson = new Gson();
        String result = StringUtils.readJsonFromAssets(getContexts(), "cs.json");
        csListData = gson.fromJson(result, ReceivedData.csListData.class);

    }

    /**
     * 初始化 持有记录
     */

    public double lossnum = 0;
    public void initOrder() {
        orderMouldAdapter = new OrderMouldAdapter(
                getContexts(),
                getFragments().orderstate,
                new OrderMouldAdapter.PopOnClickCallback() {

                    @Override
                    public void onPC(int positiion) {
                        Log.e("cjh>>> ", "自动平仓:");
                        try {
                            getContracetradePage(1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onPC(String id, final int positiion) {
                        Log.e("cjh>>> ", "持有记录 平仓: positiion" + positiion);
                        contractId = id;
                        PopUtil.ShowPopCreateSuccess(
                                getContexts(),
                                getContexts().getResources().getString(R.string.tv_close_position_sf),
                                getContexts().getResources().getString(R.string.ok),
                                getContexts().getResources().getString(R.string.cancel),
                                getBinding().rvOrder,
                                new PopUtil.PopSuccessCallback() {
                                    @Override
                                    public void onContinue() {
                                        closeoutContracetrade(contractId, positiion);
                                    }
                                });
                    }

                    @Override
                    public void onProfitLoss(double ProfitLoss) {
                        getFragments().ProfitLoss = ProfitLoss;
                    }

                    @Override
                    public void onSet(String id, final int positiion, double profit, double loss) {
                        contractId = id;
                        PopUtil.gainnum = profit;
                        PopUtil.lossnum = loss;
                        double money = orderMouldAdapter.getAllData().get(positiion).getMoney().doubleValue();
                        PopUtil.gainloss = (int) money;
                        PopUtil.ShowPopTs(getContexts(),
                                getContexts().getResources().getString(R.string.tv_setting),
                                getContexts().getResources().getString(R.string.ok),
                                getContexts().getResources().getString(R.string.cancel),
                                getBinding().rvOrder,
                                new PopUtil.PopContinueCallback() {

                                    @Override
                                    public void onDel(TextView loss_price_tv,TextView loss_price,int gainloss,double loss) {
                                        lossnum = loss - 10;
                                        double mloss = lossnum / 100f;
//                                        Log.e("cje>>>","lossnum "+loss+ "ProfitLoss "+getFragments().ProfitLoss);
                                        if (getFragments().ProfitLoss<0){
                                            double pl =( 0 - getFragments().ProfitLoss);
                                            if ((mloss * gainloss) < pl){
                                                Toast.makeText(getContexts(),
                                                        getFragments().getResources().getString(R.string.tv_nobelower),
                                                        Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }

                                        loss_price_tv.setText((int)lossnum+"");
                                        loss_price.setText(
                                                String.format(
                                                        getFragments().getResources().getString(
                                                                R.string.mbr_treaty_create_zk),(int)(mloss * gainloss)+""));
                                    }

                                    @Override
                                    public void onContinue(int lossPrice,int gain, int loss) {
                                        if (getFragments().ProfitLoss<0){
                                            double pl =( 0 - getFragments().ProfitLoss);
                                            if (lossPrice < pl){
                                                Toast.makeText(getContexts(),getFragments().getResources().getString(R.string.tv_nobelower),Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }
                                        float lossRate = loss / 100f;
                                        float gainRate = gain / 100f;
                                        BigDecimal b = new BigDecimal(String.valueOf(gainRate));
                                        double d = b.doubleValue();
                                        BigDecimal lossRates = new BigDecimal(String.valueOf(lossRate));
                                        double ds = lossRates.doubleValue();
                                        updateContracetrade(contractId, positiion, d, ds);

                                    }
                                });

                    }
                });
        orderMouldAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });

        getBinding().rvOrder.setLayoutManager(new LinearLayoutManager(getContexts()));
        getBinding().rvOrder.setAdapter(orderMouldAdapter);

    }

    /**
     * 获取持有记录
     * contract/contracetrade/page
     */
    public int page = 1;
    public int limit = 10;

    public void getContracetradePage(int mpage,int show) {
        page = mpage;
        limit = 10;
        FaceApiTest.getV1ApiServiceTest().getContracetradePage(FaceApplication.getToken(), mpage, limit)
                .compose(RxSchedulers.<ApiModel<ContraceTradeModel>>io_main())
                .subscribe(new RxObserver<ContraceTradeModel>(getContexts(), getFragments().getTag(), show == 1 ? true : false) {
                    @Override
                    public void onSuccess(ContraceTradeModel model) {
                        getBinding().orderSwipeRefreshLayout.finishRefresh();
                        getBinding().orderSwipeRefreshLayout.finishLoadmore();
                        Log.e("cjh>>> ", "持有记录:" + new Gson().toJson(model));
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


    public void getContracetradePage(int mpage) {
        page = mpage;
        limit = 10;
        FaceApiTest.getV1ApiServiceTest().getContracetradePage(FaceApplication.getToken(), mpage, limit)
                .compose(RxSchedulers.<ApiModel<ContraceTradeModel>>io_main())
                .subscribe(new RxObserver<ContraceTradeModel>(getContexts(), getFragments().getTag(), false) {
                    @Override
                    public void onSuccess(ContraceTradeModel model) {
                        Log.e("cjh>>> ", "持有记录 自动持平:" + new Gson().toJson(model));
                        setAdapter(model.list);
//                        EventBus.getDefault().post("goOrderListHis");

                    }
                });
    }


    /**
     * 设置adapter
     *
     * @param lsJson
     */
    public void setAdapter(List<ContraceTradeDTO> lsJson) {
        try {

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
                    orderMouldAdapter.clear();
                    orderMouldAdapter.notifyDataSetChanged();
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 持有记录 平仓
     *
     * @param id
     */
    public void closeoutContracetrade(String id, final int positiion) {
        FaceApiTest.getV1ApiServiceTest().getCloseout(FaceApplication.getToken(), id)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getContexts(), getFragments().getTag(), true) {
                    @Override
                    public void onSuccess(String model) {
                        Log.e("cjh>>> ", "持有记录 平仓:" + new Gson().toJson(model));
                        try {
                            orderMouldAdapter.remove(positiion);
                            getContracetradePage(1, 0);

                            PopUtil.ShowPopKnow(
                                    getContexts(),
                                    getContexts().getResources().getString(R.string.operate_success),
                                    getContexts().getResources().getString(R.string.know),
                                    getContexts().getResources().getString(R.string.cancel),
                                    getBinding().rvOrder,
                                    new PopUtil.PopSuccessCallback() {
                                        @Override
                                        public void onContinue() {
                                            EventBus.getDefault().post("goOrderListHis");
                                        }
                                    });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 持有记录 设置
     *
     * @param id
     */
    public void updateContracetrade(String id, final int positiion, final double profit, final double loss) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("id", id)
                .addParam("profit", profit + "")
                .addParam("loss", loss + "")
                .builder();
        FaceApiTest.getV1ApiServiceTest().updateContracetrade(FaceApplication.getToken(), body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getContexts(), getFragments().getTag(), true) {
                    @Override
                    public void onSuccess(String model) {
                        Log.e("cjh>>> ", "持有记录 setting:" + new Gson().toJson(model));
                        try {
                            PopUtil.ShowPopKnow(
                                    getContexts(),
                                    getContexts().getResources().getString(R.string.operate_success),
                                    getContexts().getResources().getString(R.string.ok),
                                    getContexts().getResources().getString(R.string.cancel),
                                    getBinding().rvOrder,
                                    new PopUtil.PopSuccessCallback() {
                                        @Override
                                        public void onContinue() {
                                            if (null != orderMouldAdapter) {
                                                ContraceTradeDTO ctd = orderMouldAdapter.getAllData().get(positiion);
                                                ctd.setLoss(loss);
                                                ctd.setProfit(profit);
                                                orderMouldAdapter.notifyItemChanged(positiion);
                                            }
                                        }
                                    });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 获取当前价格
     *
     * @param name name btcusdt、ethusdt、  //BTC_USDT
     */
    public void getPrice(String name) {
        FaceApiTest.getV1ApiServiceTest().getPrice(FaceApplication.getToken(), name)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getContexts(), getFragments().getTag(), false) {
                    @Override
                    public void onSuccess(String price) {
                        try {
                            if (null == orderMouldAdapter)
                                return;

                            List<ContraceTradeDTO> allData = orderMouldAdapter.getAllData();
                            for (ContraceTradeDTO ct :
                                    allData) {

                                ct.setPrice(StringUtils.getStringTwo(price));
                            }
                            orderMouldAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
