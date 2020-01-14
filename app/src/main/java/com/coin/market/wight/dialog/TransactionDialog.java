package com.coin.market.wight.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.util.ButtonUtils;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import teng.wang.comment.model.OTCTradeItemBean;

/**
 * @author: Lenovo
 * @date: 2019/8/1
 * @info: 交易弹窗     选择数量 价格 下单 倒计时
 */
public class TransactionDialog extends Dialog implements View.OnClickListener {

    private static mOnClickCallback listener;

    private EditText edit;
    private String type;
    private View mView;
    private Context mContext;
    private LinearLayout linearLayout;
    private TabLayout tabLayout;
    private TextView all, confrim, price_tv, quota_tv, numb_tv, all_numb_tv, name_tv;
    private TextView time;
    private int recLen = 35;
    private int NumbOrPrice = 0; //按价格出售为：0   按数量出售为：1
    private double editNumb, allAmount; //editNumb 为输入数   allAmount为 交易总额
    private List<String> mTitles = new ArrayList<String>();
    private OTCTradeItemBean bean;

    public void setListener(mOnClickCallback listener){
        this.listener = listener;
    }

    public TransactionDialog(Context context, OTCTradeItemBean bean, String BuyOrSell) {
        this(context, 0, null, bean, BuyOrSell);
    }

    public TransactionDialog(Context context, int theme, View contentView, OTCTradeItemBean bean, String BuyOrSell) {
        super(context, theme == 0 ? R.style.ActionSheetDialogStyle : theme);
        this.mView = contentView;
        this.mContext = context;
        this.bean = bean;
        this.type = BuyOrSell;
        if (mView == null) {
            mView = View.inflate(mContext, R.layout.dialog_transaction_layout, null);
        }
        init();
        initView();
        setListener();
        initTab();
        setTime(35);
    }

    private void init() {
        this.setContentView(mView);
    }

    private void setListener(){
        time.setOnClickListener(this);
        confrim.setOnClickListener(this);
    }

    private void initView() {
        tabLayout = mView.findViewById(R.id.dialog_transaction_tab);
        linearLayout = mView.findViewById(R.id.dialog_transaction_layout);
        all = mView.findViewById(R.id.dialog_transaction_all);
        time = mView.findViewById(R.id.dialog_time_button);
        confrim = mView.findViewById(R.id.dialog_transaction_confirm_button);
        edit = mView.findViewById(R.id.transaction_edit);
        name_tv = mView.findViewById(R.id.name_tv);  // 币名字
        price_tv = mView.findViewById(R.id.price_tv); // 单价
        quota_tv = mView.findViewById(R.id.quota_tv); // 限额
        numb_tv = mView.findViewById(R.id.numb_tv); // 数量
        all_numb_tv = mView.findViewById(R.id.all_numb_tv); // 全部数量
        linearLayout.setLayoutParams(new FrameLayout.LayoutParams((int) (getMobileWidth(mContext) * 1), LinearLayout.LayoutParams.WRAP_CONTENT));

        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        setCancelable(true);

        if (type.equals("sell")){
            name_tv.setText("买入" + bean.getCoinName());
        }else {
            name_tv.setText("卖出" + bean.getCoinName());
        }

        if (type.equals("sell")){
            all.setText("全部买入");
            mTitles.add("按价格购买");
            mTitles.add("按数量购买");
        }else {
            all.setText("全部卖出");
            mTitles.add("按价格出售");
            mTitles.add("按数量出售");
        }
        if (!EmptyUtil.isEmpty(bean)){
            setData(bean);
        }

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!EmptyUtil.isEmpty(bean)){
                    if (NumbOrPrice == 0){
                        edit.setText(StringUtils.double2String(bean.getPrice()*bean.getNumber(), 2)+"");
                    }else {
                        edit.setText(StringUtils.double2String(bean.getNumber(), 2)+"");
                    }

                }
            }
        });

        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable)){
                    all_numb_tv.setText("0");
                    numb_tv.setText("0");
                    return;
                }
                editNumb = Double.parseDouble(editable+"");
                if (NumbOrPrice==0){
                    allAmount = editNumb;
                    numb_tv.setText(""+ (StringUtils.double2String(editNumb/bean.getPrice(), 8)));
                    all_numb_tv.setText(editable);
                }else {
                    allAmount = editNumb*bean.getPrice();
                    all_numb_tv.setText(allAmount+"");
                    numb_tv.setText(""+ StringUtils.double2String(Double.parseDouble(editable+""), 8));
                }
            }
        });

    }

    // 数据传过来 显示
    private void setData(OTCTradeItemBean bean){
        numb_tv.setText(" " + "0");
        quota_tv.setText("¥" + bean.getMinTradeMoney()+"-¥" + bean.getMaxTradeMoney());
        price_tv.setText("¥" + bean.getPrice());
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (recLen > 0) {
                recLen--;
                time.setText(recLen + "s后自动取消");
                handler.postDelayed(this, 1000);
            } else if (recLen == 0) {
                dismiss();
            }
        }
    };

    public void setTime(int time) {
        this.recLen = time;
        handler.postDelayed(runnable, 1000);
    }

    private void initTab() {
        try {
            tabLayout.addTab(tabLayout.newTab().setText(mTitles.get(0)));
            tabLayout.addTab(tabLayout.newTab().setText(mTitles.get(1)));
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    switch (tab.getPosition()) {
                        case 0:
                            // 按价格
                            NumbOrPrice = 0;
                            edit.setText("");
                            edit.setHint("请输入价格");
                            break;
                        case 1:
                            // 按数量
                            NumbOrPrice = 1;
                            edit.setText("");
                            edit.setHint("请输入数量");
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_time_button:
                dismiss();
                break;
            case R.id.dialog_transaction_confirm_button:
                if (TextUtils.isEmpty(edit.getText().toString().trim())){
                    if (NumbOrPrice==0){
                        Toast.makeText(mContext, "请输入价格", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(mContext, "请输入数量", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                if (!ButtonUtils.isFastDoubleClick(R.id.dialog_transaction_confirm_button)) {
                    //写你相关操作即可
                    if (!EmptyUtil.isEmpty(listener)){
                        listener.confirmOnClick(bean.getId(), allAmount+"");
                    }
                }
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

    /**
     *   选择item的回调
     */
    public interface mOnClickCallback {
        void confirmOnClick(String id, String numb);
    }

}
