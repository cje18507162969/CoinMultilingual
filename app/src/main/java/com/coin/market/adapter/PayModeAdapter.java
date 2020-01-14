package com.coin.market.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.EmptyUtil;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.PayMethodsBean;

/**
 * @author 订单adapter
 * @version v1.0
 * @Time 2018-9-4
 */

public class PayModeAdapter extends RecyclerArrayAdapter<PayMethodsBean> {

    private static mOnClickCallback listener;

    public PayModeAdapter(Context context) {
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

        private ImageView logo, select;
        private TextView title, activation, name, phone;
        private LinearLayout selectButton;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_pay_mode_layout);
            logo = $(R.id.pay_mode_name_logo);
            select = $(R.id.pay_mode_name_select);
            title = $(R.id.pay_mode_title);
            activation = $(R.id.pay_mode_name_activation);
            name = $(R.id.pay_mode_name);
            phone = $(R.id.pay_mode_phone);
            selectButton = $(R.id.pay_mode_select_butotn);
        }

        @Override
        public void setData(final PayMethodsBean entity) {
            try {

                name.setText(entity.getPayName());
                phone.setText(entity.getPayAccount());

                switch (entity.getPaymentMethod()) {
                    case "alipay":
                        logo.setImageResource(R.drawable.transaction_add_alipay);
                        title.setText(getContext().getResources().getString(R.string.transaction_fb_order_pay_mode_alipay_text));
                        break;
                    case "wxpay":
                        logo.setImageResource(R.drawable.transaction_add_wechat);
                        title.setText(getContext().getResources().getString(R.string.transaction_fb_order_pay_mode_weix_text));
                        break;
                    case "unionpay":
                        logo.setImageResource(R.drawable.transaction_add_bankcard);
                        title.setText(getContext().getResources().getString(R.string.transaction_fb_order_pay_mode_bank_text));
                        break;
                    default:
                        break;
                }

                selectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!EmptyUtil.isEmpty(listener)){
                            listener.onSelect(getPosition());
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface mOnClickCallback {
        void onSelect(int item);
    }

}
