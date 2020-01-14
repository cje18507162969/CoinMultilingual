package com.coin.market.util;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.coin.market.R;

/**
 *  popuwindow
 */
public class PopUtil {
    public static PopupWindow pop;
    public static Context mContext;
    /**
     * 弹出提示窗
     *
     * @param context
     * @param v       pop弹出位置
     */
    public static TextView tv_content;
    public static TextView tv_continue;
    public static TextView tv_cancel;

    public static TextView gain_price;
    public static TextView loss_price;
    public static TextView gain_price_tv;
    public static TextView loss_price_tv;
    public static TextView tv_title;

    public static double gainnum = 1;
    public static double lossnum = 1;

    public static int gainloss = 300;

    public static void ShowPopTs(
            Context context,
            String title,
            String continues,
            String cancel,
            View v,
            final PopContinueCallback callback) {
        mContext = context;

//        final String Str = mContext.getResources().getString(R.string.input_station);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View popMenu = inflater.inflate(R.layout.dialog_pop_ts, null);
        if (null==pop){
            pop = new PopupWindow(popMenu, WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT, true);
            backgroundAlpha(0.6f);
            //设置SelectPicPopupWindow弹出窗体可点击
            pop.setFocusable(true);


            gain_price = (TextView) popMenu.findViewById(R.id.gain_price);
            loss_price = (TextView) popMenu.findViewById(R.id.loss_price);
            gain_price_tv = (TextView) popMenu.findViewById(R.id.gain_price_tv);
            loss_price_tv = (TextView) popMenu.findViewById(R.id.loss_price_tv);

            gain_price.setText(
                    String.format(
                            mContext.getResources().getString(
                                    R.string.mbr_treaty_create_gain),(int)(gainnum * gainloss)+""));

            loss_price.setText(
                    String.format(
                            mContext.getResources().getString(
                                    R.string.mbr_treaty_create_zk),(int)(lossnum * gainloss)+""));

            gainnum = gainnum*100;
            lossnum = lossnum*100;
            gain_price_tv.setText((int)gainnum+"");
            loss_price_tv.setText((int)lossnum+"");

            tv_title = (TextView) popMenu.findViewById(R.id.tv_title);
            tv_title.setText(title);

            tv_continue = (TextView) popMenu.findViewById(R.id.tv_continue);
            tv_continue.setText(continues);

            tv_cancel = (TextView) popMenu.findViewById(R.id.tv_cancel);
            tv_cancel.setText(cancel);

            popMenu.findViewById(R.id.gain_delete_price).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (10 == gainnum)
                        return;
                    gainnum = gainnum -10;
                    double gain = gainnum / 100f;
                    gain_price_tv.setText((int)gainnum+"");
                    gain_price.setText(
                            String.format(
                                    mContext.getResources().getString(
                                            R.string.mbr_treaty_create_gain),(int)(gain * gainloss)+""));
                }
            });

            popMenu.findViewById(R.id.gain_add_price).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (100 == gainnum)
                        return;
                    gainnum = gainnum + 10;
                    double gain = gainnum / 100f;
                    gain_price_tv.setText((int)gainnum+"");
                    gain_price.setText(
                            String.format(
                                    mContext.getResources().getString(
                                            R.string.mbr_treaty_create_gain),(int)(gain * gainloss)+""));
                }
            });


            popMenu.findViewById(R.id.delete_price).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String losspross = loss_price_tv.getText().toString().trim();
                    lossnum = Double.valueOf(losspross);
                    if (10 == lossnum)
                        return;
                    callback.onDel(loss_price_tv,loss_price,gainloss,lossnum);

                }
            });

            popMenu.findViewById(R.id.add_price).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (100 == lossnum)
                        return;
                    lossnum = lossnum +10;
                    loss_price_tv.setText((int)lossnum+"");
                    double loss = lossnum / 100f;
                    loss_price.setText(
                            String.format(
                                    mContext.getResources().getString(
                                            R.string.mbr_treaty_create_gain),(int)(loss * gainloss)+""));

                }
            });
            popMenu.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backgroundAlpha(1f);
                    pop.dismiss();
                    pop = null;
                }
            });
            popMenu.findViewById(R.id.ll_pop).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backgroundAlpha(1f);
                    pop.dismiss();
                    pop = null;
                }
            });
            popMenu.findViewById(R.id.tv_continue).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backgroundAlpha(1f);
                    pop.dismiss();
                    pop = null;
                    String losspross = loss_price_tv.getText().toString().trim();
                    double loss = Integer.valueOf(losspross) / 100f;
                    callback.onContinue((int)(loss * gainloss),(int)gainnum,Integer.valueOf(losspross));
                }
            });
            pop.showAtLocation(v, Gravity.BOTTOM,
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    backgroundAlpha(1f);
                }
            });
        }


    }


    /**
     *  创建成功
     * @param context
     * @param title
     * @param continues
     * @param cancel
     * @param v
     * @param callback
     */
    public static void ShowPopCreateSuccess(
            Context context,
            String title,
            String continues,
            String cancel,
            View v,
            final PopSuccessCallback callback) {

        mContext = context;

//        final String Str = mContext.getResources().getString(R.string.input_station);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View popMenu = inflater.inflate(R.layout.dialog_pop_create_success, null);
        if (null == pop){
            backgroundAlpha(0.6f);
            pop = new PopupWindow(popMenu, WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT, true);
            //设置SelectPicPopupWindow弹出窗体可点击
            pop.setFocusable(true);


            gain_price = (TextView) popMenu.findViewById(R.id.gain_price);


            tv_title = (TextView) popMenu.findViewById(R.id.tv_title);
            tv_title.setText(title);

            tv_continue = (TextView) popMenu.findViewById(R.id.tv_continue);
            tv_continue.setText(continues);

            tv_cancel = (TextView) popMenu.findViewById(R.id.tv_cancel);
            tv_cancel.setText(cancel);


            popMenu.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backgroundAlpha(1f);
                    pop.dismiss();
                    pop = null;
                }
            });
            popMenu.findViewById(R.id.ll_pop).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pop.dismiss();
                    pop = null;
                    backgroundAlpha(1f);
                }
            });
            popMenu.findViewById(R.id.tv_continue).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backgroundAlpha(1f);
                    pop.dismiss();
                    pop = null;
                    callback.onContinue();
                }
            });
            pop.showAtLocation(v, Gravity.BOTTOM,
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    backgroundAlpha(1f);
                }
            });
        }


    }


    /**
     *  操作成功
     * @param context
     * @param title
     * @param continues
     * @param cancel
     * @param v
     * @param callback
     */
    public static void ShowPopKnow(
            Context context,
            String title,
            String continues,
            String cancel,
            View v,
            final PopSuccessCallback callback) {

        mContext = context;
        backgroundAlpha(0.6f);
//        final String Str = mContext.getResources().getString(R.string.input_station);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View popMenu = inflater.inflate(R.layout.dialog_pop_know, null);
        if (null == pop) {
            pop = new PopupWindow(popMenu, WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT, true);
            //设置SelectPicPopupWindow弹出窗体可点击
            pop.setFocusable(true);


            gain_price = (TextView) popMenu.findViewById(R.id.gain_price);


            tv_title = (TextView) popMenu.findViewById(R.id.tv_title);
            tv_title.setText(title);

            tv_continue = (TextView) popMenu.findViewById(R.id.tv_continue);
            tv_continue.setText(continues);


            popMenu.findViewById(R.id.ll_pop).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backgroundAlpha(1f);
                    pop.dismiss();
                    pop = null;
                }
            });
            popMenu.findViewById(R.id.tv_continue).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backgroundAlpha(1f);
                    pop.dismiss();
                    pop = null;
                    callback.onContinue();
                }
            });
            pop.showAtLocation(v, Gravity.BOTTOM,
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    backgroundAlpha(1f);
                }
            });
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public static void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        ((Activity) mContext).getWindow().setAttributes(lp);
        ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }



    /**
     * 继续
     */
    public interface PopContinueCallback {
        void onContinue(int lossPrice,int gain, int loss);  // 盈利  亏损
        void onDel(TextView loss_price_tv,TextView loss_price,int gainloss,double loss);  // 盈利  亏损
    }

    /**
     * 成功
     */
    public interface PopSuccessCallback {
        void onContinue();
    }
}
