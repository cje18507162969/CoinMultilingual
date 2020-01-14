package com.coin.market.fragment.treaty;

import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.adapter.CoinOrderBBAdapter;
import com.coin.market.adapter.TreatyLatestDealAdapter;
import com.coin.market.adapter.TreatySelMoneyAdapter;
import com.coin.market.adapter.UpAndDownAdapter;
import com.coin.market.databinding.FragmentTreatyLayoutBinding;
import com.coin.market.fragment.treaty.TreatyFragment;
import com.coin.market.util.AnimationUtils;
import com.coin.market.util.ArithTulis;
import com.coin.market.util.DecimalDigitsInputFilter;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.PopUtil;
import com.coin.market.util.StringUtils;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.luck.picture.lib.decoration.RecycleViewDivider;
import com.luck.picture.lib.tools.ScreenUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.RequestBody;
import teng.wang.comment.SPConstants;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.api.RequestBodyUtils;
import teng.wang.comment.api.TickerAccountModel;
import teng.wang.comment.base.BaseFragmentViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.layoutmanager.AutoLineFeedLayoutManager;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.CoinEntity;
import teng.wang.comment.model.ContraceTradeDTO;
import teng.wang.comment.model.ContraceTradeModel;
import teng.wang.comment.model.DepthItemModel;
import teng.wang.comment.model.DepthModel;
import teng.wang.comment.model.EntrustInfo;
import teng.wang.comment.model.EntrustModel;
import teng.wang.comment.model.MarketsDTO;
import teng.wang.comment.model.MarketsModel;
import teng.wang.comment.model.MoneyEntity;
import teng.wang.comment.model.ReceivedData;
import teng.wang.comment.model.TradeOrderModel;
import teng.wang.comment.model.UserInfosDTO;
import teng.wang.comment.model.UsersEntity;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.DataKeeper;
import teng.wang.comment.utils.log.Log;

/**
 * @author 交易Fragment ViewModel 合约
 * @version v1.0
 * @Time 2018-9-3
 */

public class TreatyViewModel extends BaseFragmentViewModel<TreatyFragment, FragmentTreatyLayoutBinding> {

    private double buyMaxNumb, sellMaxNumb;
    // 行情adapter
    private UpAndDownAdapter topAdapter, bottomAdapter; // 深度 涨跌
    private TreatyLatestDealAdapter adapter; // 最新成交 Adapter
    public TreatySelMoneyAdapter selMoneyAdapter;  // 本金选择金额 adapter
    public ReceivedData.csListData csListData = null;

    public TreatyViewModel(TreatyFragment fragment, FragmentTreatyLayoutBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initView() {

        initTopAdapter();
        initBottomAdapter();
        setCoinAdapter();  //最新成交
        initSelMoney();
        getRefreshData();
        RefreshCoinData("BTC_USDT");

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
        adapter = new TreatyLatestDealAdapter(getContexts());
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
            }
        });
        adapter.setListener(new TreatyLatestDealAdapter.itemClick() {
            @Override
            public void Click(ContraceTradeDTO entity) {
//                getFragments().showDialog(entity);
            }
        });
        getBinding().transactionBbEntrust.setLayoutManager(new LinearLayoutManager(getContexts()));
        getBinding().transactionBbEntrust.setAdapter(adapter);
        getBinding().transactionBbEntrust.addItemDecoration(new RecycleViewDivider(
                getContexts(), LinearLayoutManager.HORIZONTAL,
                ScreenUtils.dip2px(getContexts(), 1),
                ContextCompat.getColor(getContexts(), R.color.app_default)));
        getBinding().transactionBbEntrust.setNestedScrollingEnabled(false);//禁止滑动
    }

    /**
     * 虚拟数据
     */
    public void initData(){
        Gson gson = new Gson();
        String result = StringUtils.readJsonFromAssets(getContexts(), "cs.json");
        csListData = gson.fromJson(result, ReceivedData.csListData.class);

    }


    /**
     * 获取用户信息
     * 判断是否已经身份认证
     */
    public void getUserInfo(final String token) {
        FaceApiTest.getV1ApiServiceTest().userInfo(token)
                .compose(RxSchedulers.<ApiModel<UserInfosDTO>>io_main())
                .subscribe(new RxObserver<UserInfosDTO>(getContexts(), getFragments().getTag(), false) {
                    @Override
                    public void onSuccess(UserInfosDTO data) {
                        try {
                            Log.e("cjh>>> token:" + token, "userInfo:" + new Gson().toJson(data));
                            UsersEntity model = FaceApplication.getUserInfoModel();
                            model.setMobile(data.getMobile()+"");
                            model.setAuthentication(data.getAuthentication());
                            DataKeeper.put(getContexts(), SPConstants.USERINFOMODEL,model);
                            Log.e("cjh>>> token:" + token, "实名状态:" +FaceApplication.getAuthentication());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrors(int eCode) {
                        // 如果token过期 让用户重新登录  重新获取token
                        if (eCode == 9001) {
                        }
                    }
                });
    }
    /**
     * 本金 选择  100  300  500  1000  2000  5000  10000
     */
    public void initSelMoney(){
        initData();
        List<MoneyEntity> data = csListData.data;
        selMoneyAdapter = new TreatySelMoneyAdapter(getContexts());
        selMoneyAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                List<MoneyEntity> allData = selMoneyAdapter.getAllData();
                for (MoneyEntity me:
                        allData) {
                    me.setSelect(false);
                }
                selMoneyAdapter.getAllData().get(position).setSelect(true);
                selMoneyAdapter.notifyDataSetChanged();
                int money = selMoneyAdapter.getAllData().get(position).getMoney();
                getFragments().selMoney = money;
                changeCostView(money,getFragments().PoloBarnum);
            }
        });
        selMoneyAdapter.addAll(data);
        getBinding().rvMoney.setLayoutManager(new AutoLineFeedLayoutManager());
        getBinding().rvMoney.setAdapter(selMoneyAdapter);
        int money = data.get(0).getMoney();
        changeCostView(money,getFragments().PoloBarnum);
    }


    /**
     * 改变背景颜色
     * 0 green  1 red
     */
    public void changeBgcolor(int color){
        if (null!=selMoneyAdapter){
            List<MoneyEntity> allData = selMoneyAdapter.getAllData();
            for (MoneyEntity me : allData) {
               me.setBgcolor(color);
            }
            selMoneyAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 改变费用相关控件
     */
    public void changeCostView(int money, int polobarnum){
        getFragments().allmoney = money + money * 0.1;
        getBinding().tvPolebarMoney.setText(
                String.format(
                        getContexts().getResources().getString(
                                R.string.mbr_treaty_polebar_money),money*polobarnum+""));
        getBinding().tvserviceCharge.setText(
                String.format(
                        getContexts().getResources().getString(
                                R.string.mbr_treaty_service_charge),money*0.1+""));
        getBinding().tvoorderMoney.setText(
                String.format(
                        getContexts().getResources().getString(
                                R.string.mbr_treaty_order_money),(money+money*0.1)+""));
    }
    /**
     * 刷新数据
     */
    private void getRefreshData() {

        getBinding().quotationSwipeRefreshLayout.setEnableRefresh(true);//是否启用下拉刷新功能
        getBinding().quotationSwipeRefreshLayout.setEnableLoadmore(false);//是否启用上拉加载功能
        getBinding().quotationSwipeRefreshLayout.setEnableAutoLoadmore(false);//是否启用自动加载功能
        getBinding().quotationSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getDepth(getFragments().marketId); // 深度数据
                getCoin();
                page = 1;
                getContracetradelogNew(page);
            }
        });
        getBinding().quotationSwipeRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(final RefreshLayout refreshLayout) {

                if (page == 1)
                    page = 2;
                getContracetradelogNew(page);
            }
        });

    }

    /**
     * 刷新交易界面数据
     */
    public void RefreshCoinData(String marketId) {
        getDepth(marketId); // 深度数据
        getCoin();
        page = 1;
        getContracetradelogNew(page);
//        getTickerAccount(FaceApplication.getToken(), marketId); // 账户资金
//        getEntrust(FaceApplication.getToken(), marketId); // 当前委托
//        getBinding().bbAllPriceName.setText(getFragments().PayName);
//        getBinding().maxMoneyName.setText(getFragments().CoinName);
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
        FaceApiTest.getV1ApiServiceTest().getCoinDepth(FaceApplication.getToken(),"BTC_USDT")
                .compose(RxSchedulers.<ApiModel<DepthModel>>io_main())
                .subscribe(new RxObserver<DepthModel>(getContexts(), getFragments().getTags(), false) {
                    @Override
                    public void onSuccess(DepthModel model) {
                        try {
//                            Log.e("cjh>>>marketId:" + marketId, " 深度数据:" + new Gson().toJson(model));
                            if (!EmptyUtil.isEmpty(topAdapter) && !EmptyUtil.isEmpty(model)) {
                                if (!TextUtils.isEmpty(StringUtils.double2String(model.available,2)))
                                    getFragments().available = Double.valueOf(model.available);
                                getBinding().tvAvailable.setText(model.available+"");
                                getBinding().tvProfit.setText(model.profit+"");
                                Double dProfit = Double.valueOf(model.profit);
                                if (dProfit >= 0) {
                                    getBinding().tvProfit.setTextColor(getContexts().getResources().getColor(R.color.txt_color_4ead));
                                } else {
                                    getBinding().tvProfit.setTextColor(getContexts().getResources().getColor(R.color.txt_color_b44c));

                                }
                                getFragments().price_cny = Double.parseDouble(StringUtils.double2String(model.getPrice_cny(), 2));





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
//                                        Collections.reverse(model.getBuys());
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
//                                        Collections.reverse(model.getSells());
//                                        List<DepthItemModel> sells = new ArrayList<DepthItemModel>();
//                                        for (int i = 0; i < 5; i++) {
//                                            sells.add(model.getSells().get(i));
//                                        }
//                                        Collections.reverse(sells);
                                        List<DepthItemModel> sells = new ArrayList<DepthItemModel>();
                                        int sells_size = model.getSells().size()-1;
                                        for (int i = sells_size; i >sells_size-5; i--) {
                                            sells.add(model.getSells().get(i));
                                        }
                                        topAdapter.clear();
                                        topAdapter.addAll(sells);
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
     * 货币行情  上方货币行情
     */
    public void getCoin() {
        page = 1;
        limit = 1;
        FaceApiTest.getV1ApiServiceTest().getCoin(1,1)
                .compose(RxSchedulers.<ApiModel<MarketsModel>>io_main())
                .subscribe(new RxObserver<MarketsModel>(getContexts(), getFragments().getTags(), false) {
                    @Override
                    public void onSuccess(MarketsModel model) {
                        try {
                            if (null==model)
                                return;
//                            Log.e("cjh>>>", "实时行情 第一条maketsEntity:" + new Gson().toJson(model.getList()));
                            List<MarketsDTO> list = model.getList();
                            if (null!=list&&list.size()>0){
                                MarketsDTO marketsDTO = list.get(0);
                                CoinEntity coinmodel = new CoinEntity();
                                coinmodel.setId(marketsDTO.getId()+"");
                                getFragments().coinEntity = marketsDTO;
                                getBinding().bbCurrentPrice.setText("≈" + marketsDTO.getPriceCny() + "CNY");
                                getBinding().bbCurrentPrice2.setText(StringUtils.double2String(marketsDTO.getPrice().doubleValue(), marketsDTO.getPriceScale()));
                                getBinding().tvPrice.setText(StringUtils.double2String(marketsDTO.getPrice().doubleValue(), marketsDTO.getPriceScale()));
                                getBinding().tvRate.setText(marketsDTO.getRate()+"%");
                                if (marketsDTO.getRate().doubleValue() >= 0) {
                                    getBinding().tvProfit.setTextColor(getContexts().getResources().getColor(R.color.txt_color_4ead));
                                    getBinding().tvPrice.setTextColor(getContexts().getResources().getColor(R.color.txt_color_4ead));
                                    getBinding().tvRate.setBackground(getContexts().getResources().getDrawable(R.drawable.shape_4ead_layout));
                                    getBinding().ll.setBackground(getContexts().getResources().getDrawable(R.drawable.shape_6bcea6_layout));
                                    getBinding().bbCurrentPrice2.setSelected(false);
                                } else {
                                    getBinding().tvProfit.setTextColor(getContexts().getResources().getColor(R.color.txt_color_b44c));
                                    getBinding().tvPrice.setTextColor(getContexts().getResources().getColor(R.color.txt_color_b44c));
                                    getBinding().tvRate.setBackground(getContexts().getResources().getDrawable(R.drawable.shape_b44c5f_layout));
                                    getBinding().ll.setBackground(getContexts().getResources().getDrawable(R.drawable.shape_d359_layout));
                                    getBinding().bbCurrentPrice2.setSelected(true);
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrors(int eCode) {
                        //getFragments().timerCancel();
                    }
                });
    }


    /**
     * 最新成交
     */
    public int page = 1;
    public int limit = 10;
    public void getContracetradelogNew(int mpage) {
        page = mpage;
        limit = 20;
        FaceApiTest.getV1ApiServiceTest().getContracetradelogNew(page,limit)
                .compose(RxSchedulers.<ApiModel<ContraceTradeModel>>io_main())
                .subscribe(new RxObserver<ContraceTradeModel>(getContexts(), getFragments().getTags(), false) {
                    @Override
                    public void onSuccess(ContraceTradeModel model) {
                        try {
                            getBinding().quotationSwipeRefreshLayout.finishRefresh();
                            getBinding().quotationSwipeRefreshLayout.finishLoadmore();

                            if (null==model)
                                return;
//                            Log.e("cjh>>>", "合约 最新成交:" + new Gson().toJson(model));
                            setAdapter(model.list);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrors(int eCode) {
                        //getFragments().timerCancel();
                        getBinding().quotationSwipeRefreshLayout.finishRefresh();
                        getBinding().quotationSwipeRefreshLayout.finishLoadmore();
                    }
                });
    }



    /**
     *  设置adapter
     * @param lsJson
     */
    public void setAdapter(List<ContraceTradeDTO> lsJson){

        if (null==adapter){
            return;
        }
        if(page == 1){
            if (null!=lsJson&&lsJson.size()>0){
                getBinding().recyclerEmpty.setVisibility(View.GONE);
                getFragments().list.clear();
                adapter.clear();
                getFragments().list.addAll(lsJson);
                adapter.addAll(lsJson);
            }else {
                getBinding().recyclerEmpty.setVisibility(View.VISIBLE);
                return;
            }

        }
        if(page > 1){
            page ++;
            getFragments().list.addAll(lsJson);
            adapter.addAll(lsJson);
            adapter.notifyItemRangeInserted(getFragments().list.size()-lsJson.size(),lsJson.size());
            getBinding().transactionBbEntrust.smoothScrollToPosition(getFragments().list.size()-lsJson.size()-1);
        }else {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     *   创建合约
     *     buysType = "rise";  // 购买类型（rise：涨 drop：跌）
     *     contractName = "BTC_USDT";// 合约名称
     *     money = "100";  //本金
     *     pryBar = "25";  //杠杆
     *     mloss = "1";  //亏损比例  100 1  90 0.9
     *     profit = "1";  //盈利比例
     */
    public void Contracetrade(String buysType,
                              String contractName,
                              String money,
                              String pryBar,
                              String profit,
                              String mloss) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("buysType", buysType)
                .addParam("contractName", contractName)
                .addParam("money", money)
                .addParam("pryBar", pryBar)
                .addParam("profit", profit)
                .addParam("loss", mloss)
                .builder();
        FaceApiTest.getV1ApiServiceTest().contracetrade(FaceApplication.getToken(),body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getContexts(), getFragments().getTags(), true) {
                    @Override
                    public void onSuccess(String model) {
                        try {
                            PopUtil.ShowPopCreateSuccess(
                                    getContexts(),
                                    getContexts().getResources().getString(R.string.mbr_treaty_sf_create_success),
                                    getContexts().getResources().getString(R.string.ok),
                                    getContexts().getResources().getString(R.string.cancel),
                                    getBinding().transactionSelectCoin,
                                    new PopUtil.PopSuccessCallback() {
                                        @Override
                                        public void onContinue() {
                                            EventBus.getDefault().post("goOrder");
                                        }
                                    });
                            if (null==model)
                                return;
//                            Log.e("cjh>>>", "实时行情 第一条maketsEntity:" + new Gson().toJson(model.getList()));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrors(int eCode) {
                        //getFragments().timerCancel();
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
//                                    adapter.addAll(model.getData());
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
