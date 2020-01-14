package com.coin.market.wight.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.coin.market.R;
import com.coin.market.util.EmptyUtil;


/**
 * PopWindow  交易弹窗  展示选项： 划转，订单记录，收款方式， 我的广告
 */

public class TransactionFbPopWindow extends PopupWindow implements View.OnClickListener {

    private Context context;
    private View hz, ddjl, skfs, fbgg, wdgg;
    private String isShop;

    private static selectOnClick listener;

    public void setListener(selectOnClick listener) {
        this.listener = listener;
    }

    public TransactionFbPopWindow(Context context) {
        super(context);
        this.context = context;
        initalize();
    }

    public TransactionFbPopWindow(Context context, String isShop) {
        super(context);
        this.context = context;
        this.isShop = isShop;
        initalize();
    }

    private void initalize() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_transaction_fb_pp_layout, null);
        hz = view.findViewById(R.id.transaction_pp_hz);// 划转
        ddjl = view.findViewById(R.id.transaction_pp_ddjl);// 订单记录
        skfs = view.findViewById(R.id.transaction_pp_skfs);// 收款方式
        fbgg = view.findViewById(R.id.transaction_pp_fbgg);// 发布广告
        wdgg = view.findViewById(R.id.transaction_pp_wdgg);// 我的广告
        hz.setOnClickListener(this);
        ddjl.setOnClickListener(this);
        skfs.setOnClickListener(this);
        fbgg.setOnClickListener(this);
        wdgg.setOnClickListener(this);
        setContentView(view);
        initWindow();
        setButton(isShop);
    }

    public void setButton(String str){
        this.isShop = str;
        if (!TextUtils.isEmpty(isShop)) {
            if (isShop.equals("1")) {
                fbgg.setVisibility(View.VISIBLE);
                wdgg.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initWindow() {
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        this.setWidth((int) (d.widthPixels * 0.35));
        this.setHeight(LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(60000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        backgroundAlpha((Activity) context, 0.8f);//0.0-1.0
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha((Activity) context, 1f);
            }
        });
    }

    //设置添加屏幕的背景透明度
    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    public void showAtBottom(View view) {
        //弹窗位置设置
        showAsDropDown(view, Math.abs((view.getWidth() - getWidth()) / 2), 10);
        //showAtLocation(view, Gravity.TOP | Gravity.RIGHT, 10, 110);//有偏差
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.transaction_pp_hz: // 划转
                if (!EmptyUtil.isEmpty(listener)) {
                    listener.OnClick(0);
                }
                dismiss();
                break;
            case R.id.transaction_pp_ddjl: // 订单记录
                if (!EmptyUtil.isEmpty(listener)) {
                    listener.OnClick(1);
                }
                dismiss();
                break;
            case R.id.transaction_pp_skfs: // 收款方式
                if (!EmptyUtil.isEmpty(listener)) {
                    listener.OnClick(2);
                }
                dismiss();
                break;
            case R.id.transaction_pp_fbgg: // 发布广告
                if (!EmptyUtil.isEmpty(listener)) {
                    listener.OnClick(3);
                }
                dismiss();
                break;
            case R.id.transaction_pp_wdgg: // 我的广告
                if (!EmptyUtil.isEmpty(listener)) {
                    listener.OnClick(4);
                }
                dismiss();
                break;
            default:
                break;
        }
    }

    // 选项  充币，划转
    public interface selectOnClick {
        void OnClick(int position);
    }

}
