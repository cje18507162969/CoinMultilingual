package com.coin.market.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.DateUtils;
import com.coin.market.util.StringUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.QuotationDealModel;

/**
 * @author: Lenovo
 * @date: 2019/8/5
 * @info: 行情 H5 成交Adapter
 */
public class QuotationDealAdapter extends RecyclerArrayAdapter<QuotationDealModel> {

    public QuotationDealAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<QuotationDealModel> {

        private TextView time, direction, price, numb;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_quotation_deal_layout);
            time = $(R.id.item_quotation_time);
            direction = $(R.id.item_quotation_direction);
            price = $(R.id.item_quotation_price);
            numb = $(R.id.item_quotation_numb);
        }

        @Override
        public void setData(final QuotationDealModel entity) {
            try {

                time.setText(DateUtils.longtoString(Long.parseLong(entity.getCreated_at())));
                price.setText(StringUtils.double2String(entity.getPrice(), entity.getPriceScale()));
                numb.setText(StringUtils.double2String(entity.getNumber(), entity.getAmountScale()) + "");

                if (entity.getType() == 0) {
                    direction.setTextColor(getContext().getResources().getColor(R.color.app_home_coin_text_color));
                    direction.setText("买入");
                } else {
                    direction.setTextColor(getContext().getResources().getColor(R.color.app_home_coin_text_color_red));
                    direction.setText("卖出");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
