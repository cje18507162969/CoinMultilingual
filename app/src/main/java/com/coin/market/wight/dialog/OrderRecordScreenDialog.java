package com.coin.market.wight.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.EmptyUtil;


/**
 * 订单记录  筛选
 */

public class OrderRecordScreenDialog extends Dialog implements View.OnClickListener {

    private itemClick listener;

    private View mView;
    private Context mContext;
    private LinearLayout linearLayout, returnButton;
    private TextView title, reset, confirm, buy, sell, payment_no, payment_yes, complete, cancel, appeal;
    private int type, status;

    public void setListener(itemClick listener){
        this.listener = listener;
    }

    public OrderRecordScreenDialog(Context context) {
        this(context, 0, null);
    }

    public OrderRecordScreenDialog(Context context, int theme, View contentView) {
        super(context, theme == 0 ? R.style.ActionFromTopDialogStyle : theme);
        this.mView = contentView;
        this.mContext = context;
        if (mView == null) {
            mView = View.inflate(mContext, R.layout.dialog_order_record_screen_layout, null);
        }
        init();
        initView();
        initListener();
    }

    private void init() {
        this.setContentView(mView);
        //设置背景是屏幕的0.85(占比)
    }

    private void initView() {

        returnButton = mView.findViewById(R.id.img_Return);
        title = mView.findViewById(R.id.txt_Title);
        reset = mView.findViewById(R.id.screen_reset_button);
        confirm = mView.findViewById(R.id.screen_confirm_button);
        buy = mView.findViewById(R.id.order_screen_buy_button);
        sell = mView.findViewById(R.id.order_screen_sell_button);
        payment_no = mView.findViewById(R.id.order_screen_payment_no_button);
        payment_yes = mView.findViewById(R.id.order_screen_payment_yes_button);
        complete = mView.findViewById(R.id.order_screen_complete_button);
        cancel = mView.findViewById(R.id.order_screen_cancel_button);
        appeal = mView.findViewById(R.id.order_screen_appeal_button);

        linearLayout = mView.findViewById(R.id.dialog_order_record_screen_layout);
        linearLayout.setLayoutParams(new FrameLayout.LayoutParams((int) (getMobileWidth(mContext) * 1), LinearLayout.LayoutParams.WRAP_CONTENT));
        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity(Gravity.CENTER | Gravity.TOP);
        title.setVisibility(View.GONE);

    }

    private void initListener() {
        returnButton.setOnClickListener(this);
        reset.setOnClickListener(this);
        confirm.setOnClickListener(this);
        buy.setOnClickListener(this);
        sell.setOnClickListener(this);
        payment_no.setOnClickListener(this);
        payment_yes.setOnClickListener(this);
        complete.setOnClickListener(this);
        cancel.setOnClickListener(this);
        appeal.setOnClickListener(this);
    }

    private void setClassButton(int type) {
        setClassButtonClear();
        this.type = type;
        if (type == 1) {
            buy.setBackground(getContext().getResources().getDrawable(R.drawable.shape_blue_line_button_layout));
            buy.setTextColor(getContext().getResources().getColor(R.color.app_style_blue));
        } else if (type==2){
            sell.setBackground(getContext().getResources().getDrawable(R.drawable.shape_blue_line_button_layout));
            sell.setTextColor(getContext().getResources().getColor(R.color.app_style_blue));
        }
    }

    // 清空交易类型 type
    private void setClassButtonClear(){
        type = 0;
        buy.setBackground(getContext().getResources().getDrawable(R.drawable.shape_gray_button_layout));
        buy.setTextColor(getContext().getResources().getColor(R.color.app_home_coin_text_color_gray));
        sell.setBackground(getContext().getResources().getDrawable(R.drawable.shape_gray_button_layout));
        sell.setTextColor(getContext().getResources().getColor(R.color.app_home_coin_text_color_gray));
    }

    // 清空订单状态 status
    private void setStatusButtonClear(){
        status = 0;
        payment_no.setBackground(getContext().getResources().getDrawable(R.drawable.shape_gray_button_layout));
        payment_yes.setBackground(getContext().getResources().getDrawable(R.drawable.shape_gray_button_layout));
        complete.setBackground(getContext().getResources().getDrawable(R.drawable.shape_gray_button_layout));
        cancel.setBackground(getContext().getResources().getDrawable(R.drawable.shape_gray_button_layout));
        appeal.setBackground(getContext().getResources().getDrawable(R.drawable.shape_gray_button_layout));
        payment_no.setTextColor(getContext().getResources().getColor(R.color.app_home_coin_text_color_gray));
        payment_yes.setTextColor(getContext().getResources().getColor(R.color.app_home_coin_text_color_gray));
        complete.setTextColor(getContext().getResources().getColor(R.color.app_home_coin_text_color_gray));
        cancel.setTextColor(getContext().getResources().getColor(R.color.app_home_coin_text_color_gray));
        appeal.setTextColor(getContext().getResources().getColor(R.color.app_home_coin_text_color_gray));
    }

    private void setStatusButton(int status) {
        setStatusButtonClear();
        this.status = status;
        switch (status) {
            case 1:
                //未付款
                payment_no.setBackground(getContext().getResources().getDrawable(R.drawable.shape_blue_line_button_layout));
                payment_no.setTextColor(getContext().getResources().getColor(R.color.app_style_blue));
                break;
            case 2:
                //已付款
                payment_yes.setBackground(getContext().getResources().getDrawable(R.drawable.shape_blue_line_button_layout));
                payment_yes.setTextColor(getContext().getResources().getColor(R.color.app_style_blue));
                break;
            case 3:
                //已完成
                complete.setBackground(getContext().getResources().getDrawable(R.drawable.shape_blue_line_button_layout));
                complete.setTextColor(getContext().getResources().getColor(R.color.app_style_blue));
                break;
            case 4:
                //已取消
                cancel.setBackground(getContext().getResources().getDrawable(R.drawable.shape_blue_line_button_layout));
                cancel.setTextColor(getContext().getResources().getColor(R.color.app_style_blue));
                break;
            case 5:
                //申诉中
                appeal.setBackground(getContext().getResources().getDrawable(R.drawable.shape_blue_line_button_layout));
                appeal.setTextColor(getContext().getResources().getColor(R.color.app_style_blue));
                break;
            default:
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_Return:    //退出
                this.dismiss();
                break;
            case R.id.screen_reset_button:    //重置,
                setStatusButtonClear();
                setClassButtonClear();
//                this.dismiss();
                break;
            case R.id.screen_confirm_button:    //确定,
                if (!EmptyUtil.isEmpty(listener)){
                    listener.selectClick(type, status);
                }
                dismiss();
                break;
            case R.id.order_screen_buy_button:    //买入
                setClassButton(1);
                break;
            case R.id.order_screen_sell_button:    //卖出
                setClassButton(2);
                break;
            case R.id.order_screen_payment_no_button:    //未付款
                setStatusButton(1);
                break;
            case R.id.order_screen_payment_yes_button:    //已付款
                setStatusButton(2);
                break;
            case R.id.order_screen_complete_button:    //已完成
                setStatusButton(3);
                break;
            case R.id.order_screen_cancel_button:    //已取消
                setStatusButton(4);
                break;
            case R.id.order_screen_appeal_button:    //申诉中
                setStatusButton(5);
                break;

            default:
                break;
        }
    }

    public interface itemClick {
        void selectClick(int type, int status);
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

}
