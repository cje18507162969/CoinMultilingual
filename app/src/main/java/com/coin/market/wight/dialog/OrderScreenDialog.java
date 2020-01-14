package com.coin.market.wight.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coin.market.adapter.CoinScreenAdapter;
import com.coin.market.R;
import com.coin.market.util.EmptyUtil;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import teng.wang.comment.model.CoinListEntity;


/**
 * 订单管理 筛选
 */

public class OrderScreenDialog extends Dialog implements View.OnClickListener {

    private itemClick listener;

    private View mView;
    private Context mContext;
    private LinearLayout linearLayout, screen_button_layout, CloseButton;
    private TextView screen_reset_button, screen_confirm_button, order_screen_buy_button, order_screen_sell_button, order_screen_all_button;
    private TextView select_dw;
    private RecyclerView recyclerView;
    private CoinScreenAdapter adapter;
    private int buyOrSell = 0;
    private CoinListEntity coinEntity;
    private EditText coin_name_edit;

    private List<CoinListEntity> coins = new ArrayList<CoinListEntity>();

    public void setListener(itemClick listener) {
        this.listener = listener;
    }


    public OrderScreenDialog(Context context, List<CoinListEntity> coins) {
        this(context, 0, null, coins);
    }

    public void setDatsa(List<CoinListEntity> coins) {
        this.coins = coins;
        adapter.clear();
        adapter.addAll(coins);
    }

    public OrderScreenDialog(Context context, int theme, View contentView, List<CoinListEntity> coins) {
        super(context, theme == 0 ? R.style.ActionFromTopDialogStyle : theme);
        this.mView = contentView;
        this.mContext = context;
        this.coins = coins;
        if (mView == null) {
            mView = View.inflate(mContext, R.layout.dialog_order_screen_layout, null);
        }
        init();
        initView();
        initData();
        initListener();
        setDiaLogData();
    }

    private void init() {
        this.setContentView(mView);
        //设置背景是屏幕的0.85(占比)
    }

    private void initView() {

        CloseButton = mView.findViewById(R.id.img_Return);
        recyclerView = mView.findViewById(R.id.order_screen_recycler);
        screen_button_layout = mView.findViewById(R.id.screen_button_layout);
        screen_reset_button = mView.findViewById(R.id.screen_reset_button);
        screen_confirm_button = mView.findViewById(R.id.screen_confirm_button);
        order_screen_buy_button = mView.findViewById(R.id.order_screen_buy_button);
        order_screen_sell_button = mView.findViewById(R.id.order_screen_sell_button);
        order_screen_all_button = mView.findViewById(R.id.order_screen_all_button);
        select_dw = mView.findViewById(R.id.select_dw);
        coin_name_edit = mView.findViewById(R.id.coin_name_edit);

        linearLayout = mView.findViewById(R.id.dialog_order_screen_layout);
        linearLayout.setLayoutParams(new FrameLayout.LayoutParams((int) (getMobileWidth(mContext) * 1), LinearLayout.LayoutParams.WRAP_CONTENT));
        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity(Gravity.CENTER | Gravity.TOP);

        initAdapter();
    }

    private void initData() {

    }

    private void setDiaLogData() {
    }

    private void initListener() {
        CloseButton.setOnClickListener(this);
        order_screen_buy_button.setOnClickListener(this);
        order_screen_sell_button.setOnClickListener(this);
        screen_button_layout.setOnClickListener(this);
        screen_confirm_button.setOnClickListener(this);
        order_screen_all_button.setOnClickListener(this);
    }

    private void initAdapter() {
        adapter = new CoinScreenAdapter(getContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        if (!EmptyUtil.isEmpty(coins)) {
            if (coins.size() > 0)
                adapter.addAll(coins);
        }
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                try {
                    for (int i = 0; i < coins.size(); i++) {
                        coins.get(i).setType(0);
                    }
                    coins.get(position).setType(1);
                    adapter.clear();
                    adapter.addAll(coins);
                    select_dw.setText(coins.get(position).getName());
                    coinEntity = coins.get(position);
                    if (recyclerView.getVisibility() == View.VISIBLE) {
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_Return:    //退出
                this.dismiss();
                break;
            case R.id.screen_button_layout: //选择计价单位
                recyclerView.setVisibility(View.VISIBLE);
                break;
            case R.id.screen_reset_button:    //重置,
                // this.dismiss();
                break;
            case R.id.screen_confirm_button:    //确定,
                if (TextUtils.isEmpty(coin_name_edit.getText().toString().trim())) {
                    Toast.makeText(mContext, "请输入币种", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (EmptyUtil.isEmpty(coinEntity)) {
                    Toast.makeText(mContext, "请选择计价单位", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!EmptyUtil.isEmpty(listener) && !EmptyUtil.isEmpty(coinEntity)) {
                    coinEntity.setType(buyOrSell);
                    coinEntity.setCoin_nme(coin_name_edit.getText().toString().trim());
                    listener.Click(coinEntity);
                }
                this.dismiss();
                break;
            case R.id.order_screen_buy_button:    //买入
                buyOrSell = 0;
                setButton(buyOrSell);
                break;
            case R.id.order_screen_sell_button:    //卖出
                buyOrSell = 1;
                setButton(buyOrSell);
                break;
            case R.id.order_screen_all_button:   // 全部
                buyOrSell = 2;
                setButton(buyOrSell);
                break;
            default:
                break;
        }
    }

    private void setButton(int item) {
        order_screen_sell_button.setTextColor(mContext.getResources().getColor(R.color.app_home_text));
        order_screen_sell_button.setBackground(mContext.getResources().getDrawable(R.drawable.shape_gray_button_layout));
        order_screen_buy_button.setTextColor(mContext.getResources().getColor(R.color.app_home_text));
        order_screen_buy_button.setBackground(mContext.getResources().getDrawable(R.drawable.shape_gray_button_layout));
        order_screen_all_button.setTextColor(mContext.getResources().getColor(R.color.app_home_text));
        order_screen_all_button.setBackground(mContext.getResources().getDrawable(R.drawable.shape_gray_button_layout));
        switch (item) {
            case 0:
                order_screen_buy_button.setTextColor(mContext.getResources().getColor(R.color.app_style_blue));
                order_screen_buy_button.setBackground(mContext.getResources().getDrawable(R.drawable.shape_blue_line_button_layout));
                break;
            case 1:
                order_screen_sell_button.setTextColor(mContext.getResources().getColor(R.color.app_style_blue));
                order_screen_sell_button.setBackground(mContext.getResources().getDrawable(R.drawable.shape_blue_line_button_layout));
                break;
            case 2:
                order_screen_all_button.setTextColor(mContext.getResources().getColor(R.color.app_style_blue));
                order_screen_all_button.setBackground(mContext.getResources().getDrawable(R.drawable.shape_blue_line_button_layout));
                break;
            default:
                break;
        }
    }

    /**
     * 工具类
     *
     * @param context
     * @return
     */
    public static int getMobileWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels; // 得到宽度
        return width;
    }

    public interface itemClick {
        void Click(CoinListEntity entity);
    }

}
