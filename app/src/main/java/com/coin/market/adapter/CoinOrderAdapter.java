package com.coin.market.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.text.DecimalFormat;

import teng.wang.comment.model.CoinRecordEntity;
import teng.wang.comment.model.DepthItemModel;
import teng.wang.comment.model.EntrustInfo;
import teng.wang.comment.model.EntrustModel;

/**
 * @author  订单adapter
 * @version v1.0
 * @Time 2018-9-4
 */

public class CoinOrderAdapter extends RecyclerArrayAdapter<EntrustInfo> {

    private static itemClick listener;

    public CoinOrderAdapter(Context context) {
        super(context);
    }

    public void setListener(itemClick listener) {
        this.listener = listener;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<EntrustInfo> {

        private TextView title, numb, type, time, deal, price, cancel, priceTitle, numbTitle, dealTitle;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_coin_order_layout);
            title = $(R.id.coin_order_coin_name);
            numb = $(R.id.coin_order_numb);
            type = $(R.id.coin_order_type);
            time = $(R.id.coin_order_time);
            deal = $(R.id.coin_order_deal);
            price = $(R.id.coin_order_rice);
            cancel = $(R.id.cancel_button);
            priceTitle = $(R.id.coin_order_rice_title);
            numbTitle = $(R.id.coin_order_numb_title);
            dealTitle = $(R.id.coin_order_deal_title);
        }

        @Override
        public void setData(final EntrustInfo entity) {
            try {
                title.setText(entity.getCoin_name() + "/" + entity.getPay_name());
                numb.setText(entity.getNumber()+"");
                time.setText(entity.getCreated_at());
                priceTitle.setText("价格("+ entity.getPay_name()+")");
                numbTitle.setText("数量("+ entity.getCoin_name()+")");
                dealTitle.setText("实际成交("+ entity.getCoin_name()+")");
                if (!EmptyUtil.isEmpty(entity.getDeal())){
                    DecimalFormat decimalFormat = new DecimalFormat();
                    deal.setText(decimalFormat.format(Double.parseDouble(entity.getDeal().doubleValue()+"")));
                }else {
                    deal.setText("0.00");
                }
                price.setText(entity.getPrice()+"");
                if (entity.getType()==0){
                    type.setText("买入");
                    type.setTextColor(getContext().getResources().getColor(R.color.app_home_coin_text_color));
                }else {
                    type.setText("卖出");
                    type.setTextColor(getContext().getResources().getColor(R.color.app_home_coin_text_color_red));
                }
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!EmptyUtil.isEmpty(listener)){
                            listener.Click(entity);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface itemClick {
        void Click(EntrustInfo entity);
    }

}
