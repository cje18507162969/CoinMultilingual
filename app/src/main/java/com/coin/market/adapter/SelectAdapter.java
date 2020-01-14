package com.coin.market.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.EmptyUtil;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.ExchangeDialogModel;
import teng.wang.comment.model.PayMethodsBean;

/**
 * Created by LiYang on 2018/9/6.
 */

public class SelectAdapter extends RecyclerArrayAdapter<PayMethodsBean> {

    private static mOnClickCallback listener;

    public SelectAdapter(Context context) {
        super(context);
    }

    public void setListener(mOnClickCallback listener) {
        this.listener = listener;
    }


    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<PayMethodsBean> {

        private TextView title;
        private LinearLayout layout;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_screen_layout);
            layout = $(R.id.item_screen_layout);
            title = $(R.id.item_screen_title);
        }

        @Override
        public void setData(final PayMethodsBean bean) {
            try {
                switch (bean.getPaymentMethod()) {
                    case "unionpay":
                        title.setText("银行卡");
                        break;
                    case "wxpay":
                        title.setText("微信");
                        break;
                    case "alipay":
                        title.setText("支付宝");
                        break;
                    default:
                        break;
                }
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!EmptyUtil.isEmpty(listener)){
                            listener.OnClick(bean);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *   选择item的回调
     */
    public interface mOnClickCallback {

        void OnClick(PayMethodsBean bean);

    }

}
