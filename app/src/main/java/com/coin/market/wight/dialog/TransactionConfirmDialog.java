package com.coin.market.wight.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.EmptyUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Lenovo
 * @date: 2019/8/1
 * @info: 交易弹窗    确认放行  我已成功付款 共用
 */
public class TransactionConfirmDialog extends Dialog implements View.OnClickListener {

    private static mOnClickCallback listener;

    private int isOnClick;
    private View mView;
    private Context mContext;
    private LinearLayout linearLayout;
    private TextView cancel, confrim, text1, text2, title;
    private ImageView img;
    private String id, type;

    public void setListener(mOnClickCallback listener) {
        this.listener = listener;
    }

    public TransactionConfirmDialog(Context context, String id, String type) {
        this(context, 0, null, id, type);
    }

    public TransactionConfirmDialog(Context context, int theme, View contentView, String id, String type) {
        super(context, theme == 0 ? R.style.Theme_Light_Dialog : theme);  //Theme.Light.Dialog
        this.mView = contentView;
        this.mContext = context;
        this.id = id;
        this.type = type;
        if (mView == null) {
            mView = View.inflate(mContext, R.layout.dialog_transaction_confirm_layout, null);
        }
        init();
        initView();
        setListener();
    }

    private void init() {
        this.setContentView(mView);
    }

    private void setListener() {
        cancel.setOnClickListener(this);
        confrim.setOnClickListener(this);
        img.setOnClickListener(this);
    }

    private void initView() {
        linearLayout = mView.findViewById(R.id.dialog_transaction_layout);
        cancel = mView.findViewById(R.id.dialog_transaction_cancel_button);
        confrim = mView.findViewById(R.id.dialog_transaction_confirm_button);
        img = mView.findViewById(R.id.dialog_transaction_img);
        title = mView.findViewById(R.id.dialog_transaction_confirm_title);
        text1 = mView.findViewById(R.id.dialog_transaction_confirm_text1);
        text2 = mView.findViewById(R.id.dialog_transaction_confirm_text2);
        linearLayout.setLayoutParams(new FrameLayout.LayoutParams((int) (getMobileWidth(mContext) * 0.8), LinearLayout.LayoutParams.WRAP_CONTENT));
        //linearLayout.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        setCancelable(false);
        if (type.equals("buy")) {
            // 作为买家   付款确认的弹框
            img.setVisibility(View.GONE);
            title.setText("付款确认");
            text1.setText(getContext().getResources().getString(R.string.transaction_fb_dialog_transaction_confirm_in_text1));
            text2.setText(getContext().getResources().getString(R.string.transaction_fb_dialog_transaction_confirm_in_text2));
            text2.setTextColor(getContext().getResources().getColor(R.color.app_style_blue));
        } else {
            // 作为卖家   确认放行的弹框
            title.setText("确认放行");
            img.setVisibility(View.VISIBLE);
            text1.setText(getContext().getResources().getString(R.string.transaction_fb_dialog_transaction_confirm_out_text1));
            text2.setText(getContext().getResources().getString(R.string.transaction_fb_dialog_transaction_confirm_out_text2));
            text2.setTextColor(getContext().getResources().getColor(R.color.color_999999));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_transaction_confirm_button:
                if (!EmptyUtil.isEmpty(listener)) {
                    if (type.equals("sell")) {
                        // 卖家要勾选 核对选项
                        if (isOnClick == 0) {
                            return;
                        }
                    }
                    listener.confirmOnClick(id);
                    dismiss();
                }
                break;
            case R.id.dialog_transaction_cancel_button:
                dismiss();
                break;
            case R.id.dialog_transaction_img:
                // 勾选框
                if (isOnClick == 0) {
                    img.setImageResource(R.drawable.transaction_activation);
                    isOnClick = 1;
                    text2.setTextColor(getContext().getResources().getColor(R.color.app_home_text));
                } else {
                    img.setImageResource(R.drawable.transaction_inactivated);
                    isOnClick = 0;
                    text2.setTextColor(getContext().getResources().getColor(R.color.color_999999));
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
     * 选择item的回调
     */
    public interface mOnClickCallback {
        void confirmOnClick(String id);
    }

}
