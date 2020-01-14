package com.coin.market.wight.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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

/**
 * @author: Lenovo
 * @date: 2019/8/1
 * @info: 交易弹窗  多用于提示作用
 */
public class TransactionContentDialog extends Dialog implements View.OnClickListener {

    private static mOnClickCallback listener;

    private int isOnClick;
    private View mView;
    private Context mContext;
    private LinearLayout linearLayout;
    private TextView cancel, confrim, text1, text2, titleTv;
    private ImageView img;
    private String title, content1, content2;

    public void setListener(mOnClickCallback listener) {
        this.listener = listener;
    }

    public TransactionContentDialog(Context context, String title, String content1, String content2) {
        this(context, 0, null, title, content1, content2);
    }

    public TransactionContentDialog(Context context, int theme, View contentView,String title, String content1, String content2) {
        super(context, theme == 0 ? R.style.Theme_Light_Dialog : theme);  //Theme.Light.Dialog
        this.mView = contentView;
        this.mContext = context;
        this.title = title;
        this.content1 = content1;
        this.content2 = content2;
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
        text1 = mView.findViewById(R.id.dialog_transaction_confirm_text1);
        text2 = mView.findViewById(R.id.dialog_transaction_confirm_text2);
        titleTv = mView.findViewById(R.id.dialog_transaction_confirm_title);
        linearLayout.setLayoutParams(new FrameLayout.LayoutParams((int) (getMobileWidth(mContext) * 0.8), LinearLayout.LayoutParams.WRAP_CONTENT));
        //linearLayout.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        setCancelable(false);

        // 作为买家   付款确认的弹框
        img.setVisibility(View.GONE);
        titleTv.setText(title);
        text1.setText(content1);
        text2.setText(content2);
        text2.setTextColor(getContext().getResources().getColor(R.color.app_style_blue));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_transaction_confirm_button:
                if (!EmptyUtil.isEmpty(listener)) {
                    listener.confirmOnClick();
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
                } else {
                    img.setImageResource(R.drawable.transaction_inactivated);
                    isOnClick = 0;
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
        void confirmOnClick();
    }

}
