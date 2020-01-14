package com.coin.market.fragment.transaction.transactionlist;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.activity.transaction.addpaymode.AddPayModeActivity;
import com.coin.market.adapter.TransactionListAdapter;
import com.coin.market.databinding.FragmentTransactionListLayoutBinding;
import com.coin.market.activity.transaction.order.TransactionOrderInfoActivity;
import com.coin.market.util.EmptyUtil;
import com.coin.market.wight.dialog.PayMethodDialog;
import com.coin.market.wight.dialog.TransactionDialog;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.util.List;

import okhttp3.RequestBody;
import teng.wang.comment.api.FaceApiMbr;
import teng.wang.comment.api.RequestBodyUtils;
import teng.wang.comment.base.BaseFragmentViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.OTCTradeBean;
import teng.wang.comment.model.OTCTradeItemBean;
import teng.wang.comment.model.PayMethodsBean;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.log.Log;

/**
 * @author: Lenovo
 * @date: 2019/7/31
 * @info: 交易列表ViewModel
 */
public class TransactionListViewModel extends BaseFragmentViewModel<TransactionListFragment, FragmentTransactionListLayoutBinding> {

    public int page;
    // 行情adapter
    private TransactionListAdapter adapter;
    private TransactionDialog dialog;
    private TextView transactionButtonText;
    private LinearLayout number_price, button;
    public String headerNumb = "";
    public String headerPrice = "";
    public String headerSelect = "";
    public String select = "价格"; // 0为按CNY购买 1位按数量购买

    public TransactionListViewModel(TransactionListFragment fragment, FragmentTransactionListLayoutBinding binding) {
        super(fragment, binding);
    }


    @Override
    protected void initView() {
        page = 1;
        initTransactionAdapter(); // 初始化 行情adapter
        http();
    }

    public void http(){
        if (getFragments().BuyOrSell.equals("buy")) {
            getOTCDataList(FaceApplication.getToken(),
                    "sell",
                    getFragments().entity.getName(),
                    getFragments().price,
                    getFragments().payName,
                    page,
                    1);
        } else {
            getOTCDataList(FaceApplication.getToken(),
                    "buy",
                    getFragments().entity.getName(),
                    getFragments().price,
                    getFragments().payName,
                    page, 1);
        }
    }

    /**
     * 初始化 法币交易 币种列表 一键买币 addHeader
     */
    private void initTransactionAdapter() {
        adapter = new TransactionListAdapter(getContexts(), getFragments().BuyOrSell);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContexts());
        getBinding().transactionListRecycler.setLayoutManager(layoutManager);
        getBinding().transactionListRecycler.setAdapter(adapter);
        if (getFragments().BuyOrSell.equals("buy")){
            adapter.addHeader(new RecyclerArrayAdapter.ItemView() {
                @Override
                public View onCreateView(ViewGroup parent) {

                    View header = LayoutInflater.from(getContexts()).inflate(R.layout.view_transaction_list_head_layout, null);
                    transactionButtonText = header.findViewById(R.id.transaction_button_text);
                    transactionButtonText.setText(getFragments().BuyOrSell.equals("buy") ? "购买" : "出售");
                    number_price = header.findViewById(R.id.number_price);
                    button = header.findViewById(R.id.header_buy_button);
                    final EditText edit = header.findViewById(R.id.header_edit);
                    final TextView number_price_text = header.findViewById(R.id.number_price_text);
                    headerSelect = "buyprice";
                    number_price.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (headerSelect.equals("buyprice")){
                                number_price_text.setText("按数量购买");
                                select = "数量";
                                headerSelect = "buynumber";
                                edit.setHint("请输入数量");
                            }else if (headerSelect.equals("buynumber")){
                                number_price_text.setText("按价格购买");
                                select = "价格";
                                headerSelect = "buyprice";
                                edit.setHint("请输入价格");
                            }
                        }
                    });
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (TextUtils.isEmpty(edit.getText().toString().trim())){
                                Toast.makeText(getContexts(), "请输入" + select, Toast.LENGTH_SHORT).show();
                                return;
                            }else {
                                if (headerSelect.equals("buynumber")){ // 数量
                                    headerNumb = edit.getText().toString().trim();
                                    headerPrice = "";
                                }else {
                                    headerPrice = edit.getText().toString().trim();
                                    headerNumb = "";
                                }
                                getPayMentMethod(FaceApplication.getToken(), getFragments().entity.getCoinId()+"", headerPrice, headerNumb, headerSelect, 1);
                            }
                        }
                    });
                    return header;
                }

                @Override
                public void onBindView(View headerView) {

                }
            });
        }


        adapter.setListener(new TransactionListAdapter.mOnClickCallback() {
            @Override
            public void onClick(OTCTradeItemBean entity) {
                // 点击购买 弹出购买的框框
                    dialog = new TransactionDialog(getContexts(), entity, entity.getOtcTradeType());
                    dialog.show();
                    dialog.setListener(new TransactionDialog.mOnClickCallback() {
                        @Override
                        public void confirmOnClick(String id, String numb) {
                            if (TextUtils.isEmpty(numb)) {
                                Toast.makeText(getContexts(), "请输入购买数量", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            // 请求下单
                            otcDownOrder(FaceApplication.getToken(), id, numb, 1);
                        }
                    });
//                else {
//                    dialog.setTime(35);
//                    dialog.show();
//                }
                //new MessageWindow(getContexts(), "请输入提取金额").showAtBottom(getBinding().transactionListRecycler);
            }
        });

        getBinding().transactionListRecycler.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBinding().transactionListRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page = 1;
                        http();
                    }
                }, 1000);
            }
        });
        adapter.setMore(R.layout.view_more, new RecyclerArrayAdapter.OnMoreListener() {
            @Override
            public void onMoreShow() {
                getBinding().transactionListRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page++;
                        http();
                    }
                }, 1000);
            }

            @Override
            public void onMoreClick() {

            }
        });
        adapter.setNoMore(R.layout.view_nomore);
    }

    /**
     * 获取法币列表数据
     */
    public void getOTCDataList(String token, final String tradeType, String coinName, String price, String payName, final int page, int showType) {
        FaceApiMbr.getV1ApiServiceMbr().getTradeList(token, tradeType, coinName, price, payName, page)
                .compose(RxSchedulers.<ApiModel<OTCTradeBean>>io_main())
                .subscribe(new RxObserver<OTCTradeBean>(getContexts(), getFragments().getTags(), showType == 1 ? true : false) {
                    @Override
                    public void onSuccess(OTCTradeBean entity) {
                        try {
                            Log.e("cjh>>>", "OTCTradeBean:" + new Gson().toJson(entity));
                            if (page == 1) {
                                if (!EmptyUtil.isEmpty(adapter) && entity.getList().size()>0) {
                                    adapter.clear();
                                    adapter.addAll(entity.getList());
                                } else {
                                    //占位图
                                    getBinding().transactionListRecycler.setEmptyView(R.layout.comment_view_seat_layout);
                                    getBinding().transactionListRecycler.showEmpty();
                                }
                            } else {
                                if (entity.getList().size() <= 0) {
                                    adapter.stopMore();
                                } else {
                                    adapter.addAll(entity.getList());
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     *  下单
     */
    public void otcDownOrder(String token, final String orderId, String money, int showType) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("otctradeId", orderId)
                .addParam("amount", money)
                .builder();
        FaceApiMbr.getV1ApiServiceMbr().otcDownOrder(token, body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getContexts(), getFragments().getTags(), showType == 1 ? true : false) {
                    @Override
                    public void onSuccess(String str) {
                        try {
                            Log.e("cjh>>>", "OTC下单:" + new Gson().toJson(str));
                            dialog.dismiss();
                            Toast.makeText(getContexts(), "下单成功！", Toast.LENGTH_SHORT).show();
                            // 跳转去 请付款
                            TransactionOrderInfoActivity.JUMP(getContexts(), str, getFragments().BuyOrSell + "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrors(int eCode) {
                        if (!EmptyUtil.isEmpty(dialog)){
                            if (dialog.isShowing()){
                                dialog.dismiss();
                            }
                        }
                    }
                });
    }

    /**
     *  一键买币 下单
     */
    public void oneOtcDownOrder(String token, final String coinId, String sumPrice, String buyNumber, String buyType, String payMethod) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("coinId", coinId)
                .addParam("sumPrice", sumPrice)
                .addParam("buyNumber", buyNumber)
                .addParam("buyType", buyType)
                .addParam("payMethod", payMethod)
                .builder();
        FaceApiMbr.getV1ApiServiceMbr().oneOtcDownOrder(token, body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getContexts(), getFragments().getTags(), true) {
                    @Override
                    public void onSuccess(String str) {
                        try {
                            if (!EmptyUtil.isEmpty(dialog)){
                                dialog.dismiss();
                            }
                            Toast.makeText(getContexts(), "一键买币 下单成功！", Toast.LENGTH_SHORT).show();
                            // 跳转去 请订单记录的列表
                            //OrderRecordActivity.JUMP(getContexts());
                            TransactionOrderInfoActivity.JUMP(getContexts(), str, getFragments().BuyOrSell + "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrors(int eCode) {

                    }
                });
    }

    /**
     *    获取收款方式
     */
    public void getPayMentMethod(final String token, final String coinId, final String sumPrice, final String buyNumber, final String buyType, int showType) {
        FaceApiMbr.getV1ApiServiceMbr().getPayMentMethod(token)
                .compose(RxSchedulers.<ApiModel<List<PayMethodsBean>>>io_main())
                .subscribe(new RxObserver<List<PayMethodsBean>>(getContexts(), getFragments().getTags(), showType==1? true:false) {
                    @Override
                    public void onSuccess(List<PayMethodsBean> list) {
                        try {
                            if (list.size()>0){
                                Log.e("cjh>>>", "支付方式：PayMethodsBean" + new Gson().toJson(list));
                                PayMethodDialog dialog = new PayMethodDialog(getContexts(), list);
                                dialog.builder()
                                        .setCancelable(true)
                                        .setCanceledOnTouchOutside(true)
                                        .setOnClickListener(new PayMethodDialog.OnItemClickListener() {
                                            @Override
                                            public void onClick(PayMethodsBean bean) {
                                                oneOtcDownOrder(token, coinId, sumPrice, buyNumber, buyType, bean.getPaymentMethod());
                                            }
                                        });
                                dialog.show();
                            }else {
                                AddPayModeActivity.JUMP(getContexts());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
