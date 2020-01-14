package com.coin.market.fragment.transaction.transaction_bb;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputFilter;
import android.view.View;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.adapter.CoinOrderBBAdapter;
import com.coin.market.adapter.UpAndDownAdapter;
import com.coin.market.databinding.FragmentTransactionBbLayoutBinding;
import com.coin.market.util.AnimationUtils;
import com.coin.market.util.ArithTulis;
import com.coin.market.util.DecimalDigitsInputFilter;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import org.greenrobot.eventbus.EventBus;
import org.litepal.util.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.RequestBody;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.api.RequestBodyUtils;
import teng.wang.comment.api.TickerAccountModel;
import teng.wang.comment.base.BaseFragmentViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.CoinEntity;
import teng.wang.comment.model.DepthItemModel;
import teng.wang.comment.model.DepthModel;
import teng.wang.comment.model.EntrustInfo;
import teng.wang.comment.model.EntrustModel;
import teng.wang.comment.model.TradeOrderModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.log.Log;

/**
 * @author 交易Fragment ViewModel 币币
 * @version v1.0
 * @Time 2018-9-3
 */

public class TransactionBBViewModel extends BaseFragmentViewModel<TransactionBBFragment, FragmentTransactionBbLayoutBinding> {

    private double buyMaxNumb, sellMaxNumb;
    // 行情adapter
    private UpAndDownAdapter topAdapter, bottomAdapter; // 深度 涨跌
    private CoinOrderBBAdapter adapter; // 当前委托 Adapter

    public TransactionBBViewModel(TransactionBBFragment fragment, FragmentTransactionBbLayoutBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initView() {
//        initQuotationAdapter(); // 初始化 行情adapter
//        initTabLayout(); // 初始化 tab
        //getQuotation(type,itemType); //各种行情
        getRefreshData();
        initTopAdapter();
        initBottomAdapter();
        setCoinAdapter();
        getOneMarket();
    }

    // 初始化 交易价格 实时买卖adapter
    private void initTopAdapter() {
        topAdapter = new UpAndDownAdapter(getContexts());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContexts());
        getBinding().bbUpRecycler.setLayoutManager(layoutManager);
        getBinding().bbUpRecycler.setAdapter(topAdapter);
        getBinding().bbUpRecycler.setNestedScrollingEnabled(false);//禁止滑动
        topAdapter.setListener(new UpAndDownAdapter.itemClick() {
            @Override
            public void Click(DepthItemModel itemModel) {
                itemClick(itemModel.getPrice());
            }
        });
    }

    // 初始化 交易价格 实时买卖adapter
    private void initBottomAdapter() {
        bottomAdapter = new UpAndDownAdapter(getContexts());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContexts());
        getBinding().bbLowRecycler.setLayoutManager(layoutManager);
        getBinding().bbLowRecycler.setAdapter(bottomAdapter);
        getBinding().bbLowRecycler.setNestedScrollingEnabled(false);//禁止滑动
        bottomAdapter.setListener(new UpAndDownAdapter.itemClick() {
            @Override
            public void Click(DepthItemModel itemModel) {
                itemClick(itemModel.getPrice());
            }
        });
    }

    /**
     * 点击Adapter 设置数据 ui等
     */
    public void itemClick(double price) {
        int p = getFragments().priceScale;
        getFragments().price = Double.parseDouble(StringUtils.double2String(price, getFragments().priceScale));
        double aa = Double.parseDouble(StringUtils.double2String(price, getFragments().priceScale));
        String e = StringUtils.double2String(aa, getFragments().priceScale);
        getBinding().transactionPriceEdit.setText(e);
        AnimationUtils.followAnimation(getBinding().transactionPriceEdit, 60);
        getBinding().bbCny.setText("≈" + StringUtils.double2String(ArithTulis.mul(getFragments().price, getFragments().price_cny), getFragments().priceCnyScale));
        getFragments().setData();
        setEdit(getFragments().priceScale, getFragments().amountScale);
    }

    /**
     * 初始化Adapter
     */
    public void setCoinAdapter() {
        adapter = new CoinOrderBBAdapter(getContexts());
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
            }
        });
        adapter.setListener(new CoinOrderBBAdapter.itemClick() {
            @Override
            public void Click(EntrustInfo entity) {
                getFragments().showDialog(entity);
            }
        });
        getBinding().transactionBbEntrust.setLayoutManager(new LinearLayoutManager(getContexts()));
        getBinding().transactionBbEntrust.setAdapter(adapter);
        getBinding().transactionBbEntrust.setNestedScrollingEnabled(false);//禁止滑动
    }

    /**
     * 刷新数据
     */
    private void getRefreshData() {
        getBinding().quotationSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RefreshCoinData(getFragments().marketId);
                        // 加载完数据设置为不刷新状态，将下拉进度收起来
                        getBinding().quotationSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1200);
            }
        });
    }

    /**
     * 刷新交易界面数据
     */
    public void RefreshCoinData(String marketId) {
        getDepth(marketId); // 深度数据
        getTickerAccount(FaceApplication.getToken(), marketId); // 账户资金
        getEntrust(FaceApplication.getToken(), marketId); // 当前委托
        getBinding().bbAllPriceName.setText(getFragments().PayName);
        getBinding().maxMoneyName.setText(getFragments().CoinName);
    }

    /**
     * 交易首次进去返回一条 数据（MBR）
     */
    public void getOneMarket() {
        FaceApiTest.getV1ApiServiceTest().getOneMarket()
                .compose(RxSchedulers.<ApiModel<CoinEntity>>io_main())
                .subscribe(new RxObserver<CoinEntity>(getContexts(), getFragments().getTags(), true) {
                    @Override
                    public void onSuccess(CoinEntity model) {
                        try {
                            Log.e("cjh>>>CoinEntity:", "  初次进来第一条数据:" + new Gson().toJson(model));
                            Log.e("控制>>>", "priceScale：" + model.getPriceScale());
                            Log.e("控制>>>", "amountScale：" + model.getAmountScale());
                            getFragments().priceScale = model.getPriceScale();
                            getFragments().amountScale = model.getAmountScale();
                            setEdit(getFragments().priceScale, getFragments().amountScale);
                            getFragments().CoinName = model.getName();
                            getFragments().CoinId = model.getCoin_id();
                            getFragments().marketId = model.getMarket_id();
                            getBinding().bbCoinName.setText(model.getName() + "/" + model.getPay_name());
                            RefreshCoinData(getFragments().marketId);
                            model.setName(model.getName());
                            getFragments().coinEntity = model;
                            getBinding().transactionConfirmButton.setText("买入" + model.getName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void setEdit(int priceScale, int amountScale){
        getBinding().transactionPriceEdit.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(priceScale), new InputFilter.LengthFilter(10)});
        getBinding().coinNumbEdit.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(amountScale), new InputFilter.LengthFilter(10)});
    }

    private void getMax(List<DepthItemModel> models, int type) {
        List<DepthItemModel> modelList = new ArrayList<DepthItemModel>();
        for (int i = 0; i < models.size(); i++) {
            modelList.add(models.get(i));
        }

        Collections.sort(modelList, new Comparator<DepthItemModel>() {
            /*  
             * int compare(Student o1, Student o2) 返回一个基本类型的整型，  
             * 返回负数表示：o1 小于o2，  
             * 返回0 表示：o1和o2相等，  
             * 返回正数表示：o1大于o2。  
             */
            @Override
            public int compare(DepthItemModel o1, DepthItemModel o2) {
                double a = o1.getNumber();
                double b = o2.getNumber();
                if (a>b){
                    return 1;
                }
                if (a>b){
                    return 0;
                }
                return -1;
                //System.out.println("a=" + a + "        b=" + b + "         " + o1.getAge().compareTo(o2.getAge()));
                //TODO: 升序排列
                //return o1.getNumber().compareTo(o2.getNumber());
                //TODO: 降序排列
                //return o2.getAge().compareTo(o1.getAge());
            }
        });

        if (type==0){
            buyMaxNumb = modelList.get(modelList.size()-1).getNumber();
        }else {
            sellMaxNumb = modelList.get(modelList.size()-1).getNumber();
        }

//        if (!EmptyUtil.isEmpty(modelList)) {
//            aaa
//        }
    }

    /**
     * 币种 深度数据
     */
    public void getDepth(final String marketId) {
        FaceApiTest.getV1ApiServiceTest().getCoinDepth(FaceApplication.getToken(),marketId)
                .compose(RxSchedulers.<ApiModel<DepthModel>>io_main())
                .subscribe(new RxObserver<DepthModel>(getContexts(), getFragments().getTags(), false) {
                    @Override
                    public void onSuccess(DepthModel model) {
                        try {
                            Log.e("cjh>>>marketId:" + marketId, "  DepthModel:" + new Gson().toJson(model));
                            if (!EmptyUtil.isEmpty(topAdapter) && !EmptyUtil.isEmpty(model)) {

                                Log.e("控制>>>", "priceScale：" + model.getPriceScale());
                                Log.e("控制>>>", "amountScale：" + model.getAmountScale());

                                getFragments().priceScale = model.getPriceScale();
                                getFragments().amountScale = model.getAmountScale();
                                setEdit(getFragments().priceScale, getFragments().amountScale);

                                getFragments().price_cny = Double.parseDouble(StringUtils.double2String(model.getPrice_cny(), 2));
                                getBinding().bbCurrentPrice.setText("≈" + model.getPrice_cny() + "CNY");
                                getBinding().bbCurrentPrice2.setText(StringUtils.double2String(model.getNew_price(), model.getPriceScale()));

                                topAdapter.clear();
                                bottomAdapter.clear();

                                if (!EmptyUtil.isEmpty(model.getBuys())){
                                    getMax(model.getBuys(), 0);
                                    bottomAdapter.setMaxNumbBuy(buyMaxNumb);
                                    bottomAdapter.getScale(model.getPriceScale(), model.getAmountScale());
                                    if (model.getBuys().size() <= 5) {
                                        //Collections.reverse(model.getBuys());
                                        bottomAdapter.clear();
                                        bottomAdapter.addAll(model.getBuys());
                                    } else {
                                        //Collections.reverse(model.getBuys());
                                        bottomAdapter.clear();
                                        bottomAdapter.addAll(getData(model.getBuys()));
                                    }
                                }

                                if (!EmptyUtil.isEmpty(model.getSells())){
                                    getMax(model.getSells(), 1);
                                    topAdapter.setMaxNumbSell(sellMaxNumb);
                                    topAdapter.getScale(model.getPriceScale(), model.getAmountScale());
                                    if (model.getSells().size() <= 5) {
//                                        Collections.reverse(model.getSells());

                                        topAdapter.clear();
                                        topAdapter.addAll(model.getSells());
                                    } else {
                                        // 倒序取末尾5个
                                        Collections.reverse(model.getSells());
                                        List<DepthItemModel> sells = new ArrayList<DepthItemModel>();
                                        for (int i = 0; i < 5; i++) {
                                            sells.add(model.getSells().get(i));
                                        }
                                        Collections.reverse(sells);
                                        topAdapter.clear();
                                        topAdapter.addAll(getData(sells));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 交易界面账户资金
     */
    public void getTickerAccount(String token, final String id) {
        FaceApiTest.getV1ApiServiceTest().getTickerAccount(token, id)
                .compose(RxSchedulers.<ApiModel<TickerAccountModel>>io_main())
                .subscribe(new RxObserver<TickerAccountModel>(getContexts(), getFragments().getTags(), false) {
                    @Override
                    public void onSuccess(TickerAccountModel model) {
                        try {
                            Log.e("cjh>>>id:" + id, "  TickerAccountModel:" + new Gson().toJson(model));
                            if (!EmptyUtil.isEmpty(model)) {
                                getBinding().coinNameText.setText(model.getCoin_name());
                                //getBinding().maxMoney.setText(StringUtils.double2String(model.getPay_balance(), 4) + "");
                                getBinding().maxMoney.setText(StringUtils.double2String(0, 4) + "");
                                getFragments().pay_balance = Double.parseDouble(StringUtils.double2String(model.getPay_balance(), 4));
                                getFragments().coin_balance = Double.parseDouble(StringUtils.double2String(model.getCoin_balance(), 4));
                                getFragments().PayName = model.getPay_name();
                                getBinding().bbAllPriceName.setText(getFragments().PayName);
                                if (getFragments().buyOrSell == 0) {
                                    getBinding().bbAvailableNumb.setText(
                                            getContexts().getResources().getString(R.string.transaction_fb_transact_assets_account_available_text)+"："
                                                    + StringUtils.double2String(model.getPay_balance(), 4) + model.getPay_name());
                                } else {
                                    getBinding().bbAvailableNumb.setText(
                                            getContexts().getResources().getString(R.string.transaction_fb_transact_assets_account_available_text)+"："
                                                    + StringUtils.double2String(model.getCoin_balance(), 4) + model.getCoin_name());
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 币种 当前委托
     */
    public void getEntrust(String token, final String market_id) {
        FaceApiTest.getV1ApiServiceTest().getEntrust(token, market_id)
                .compose(RxSchedulers.<ApiModel<EntrustModel>>io_main())
                .subscribe(new RxObserver<EntrustModel>(getContexts(), getFragments().getTags(), false) {
                    @Override
                    public void onSuccess(EntrustModel model) {
                        try {
                            Log.e("cjh>>>id:" + market_id, "  EntrustModel:" + new Gson().toJson(model));
                            Log.e("cjh>>>当前委托Size:", model.getData().size() + "");
                            if (!EmptyUtil.isEmpty(model)) {
                                if (model.getData().size() > 0) {
                                    adapter.clear();
                                    adapter.addAll(model.getData());
                                    getBinding().recyclerEmpty.setVisibility(View.GONE);
                                    getBinding().transactionBbEntrust.setVisibility(View.VISIBLE);
                                } else {
                                    getBinding().recyclerEmpty.setVisibility(View.VISIBLE);
                                    getBinding().transactionBbEntrust.setVisibility(View.GONE);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 当前委托 撤销选择的item委托订单
     */
    public void cancelOrder(String token, final EntrustInfo entity) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("order_id", entity.getOrder_id())
                .addParam("type", entity.getType())
                .addParam("is_big", entity.getIs_big())
                .builder();
        FaceApiTest.getV1ApiServiceTest().cancelOrder(token, body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getContexts(), getFragments().getTags(), true) {
                    @Override
                    public void onSuccess(String model) {
                        try {
                            // 撤销成功 刷新列表
                            Toast.makeText(getContexts(), "撤销成功！", Toast.LENGTH_SHORT).show();
                            getTickerAccount(FaceApplication.getToken(), getFragments().marketId);
                            getEntrust(FaceApplication.getToken(), getFragments().marketId);
                            EventBus.getDefault().post("clearAssets");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 重新组装数据 使其只有5个
     */
    private List<DepthItemModel> getData(List<DepthItemModel> model) {
        List<DepthItemModel> tops = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            DepthItemModel model1 = new DepthItemModel();
            model1 = model.get(i);
            tops.add(model1);
        }
        return tops;
    }

    /**
     * 交易 挂单 （买入）（卖出）
     */
    public void postOrder(String token, String price, String number, final String marketId, int type) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("price", price)
                .addParam("number", number)
                .addParam("type", type)
                .builder();
        FaceApiTest.getV1ApiServiceTest().postOrder(token, body, marketId)
                .compose(RxSchedulers.<ApiModel<TradeOrderModel>>io_main())
                .subscribe(new RxObserver<TradeOrderModel>(getContexts(), getFragments().getTags(), true) {
                    @Override
                    public void onSuccess(TradeOrderModel model) {
                        try {
                            // 挂单成功 刷新列表
                            Toast.makeText(getContexts(), "挂单成功！", Toast.LENGTH_SHORT).show();
                            RefreshCoinData(getFragments().marketId);
                            EventBus.getDefault().post("clearAssets");
                            getBinding().coinNumbEdit.setText("");
                            getBinding().transactionPriceEdit.setText("");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
