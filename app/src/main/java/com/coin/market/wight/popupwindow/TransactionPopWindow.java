package com.coin.market.wight.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.coin.market.R;
import com.coin.market.util.EmptyUtil;


/**
 * PopWindow  交易弹窗  展示选项： 充币，划转，模板盘口，添加自选
 */

public class TransactionPopWindow extends PopupWindow implements View.OnClickListener {

    private Context context;
    private View cb, hz;

    private static selectOnClick listener;

    public void setListener(selectOnClick listener) {
        this.listener = listener;
    }

    public TransactionPopWindow(Context context) {
        super(context);
        this.context = context;
        initalize();
    }

    private void initalize() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_transaction_pp_layout, null);
        cb = view.findViewById(R.id.transaction_pp_cb);//充币
        hz = view.findViewById(R.id.transaction_pp_hz);//划转
        cb.setOnClickListener(this);
        hz.setOnClickListener(this);
        setContentView(view);
        initWindow();
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
            case R.id.transaction_pp_cb: // 充币
                if (!EmptyUtil.isEmpty(listener)){
                    listener.OnClick(0);
                }
                dismiss();
                break;
            case R.id.transaction_pp_hz: // 划转
                if (!EmptyUtil.isEmpty(listener)){
                    listener.OnClick(1);
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
