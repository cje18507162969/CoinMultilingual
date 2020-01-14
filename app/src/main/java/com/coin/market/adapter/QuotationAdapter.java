package com.coin.market.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.StringUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.CoinEntity;
import teng.wang.comment.model.QuotationEntity;
import teng.wang.comment.model.RankingEntity;

/**
 * @author Quotation 行情
 * @version v1.0
 * @Time 2018-9-4
 */

public class QuotationAdapter extends RecyclerArrayAdapter<CoinEntity> {

    public QuotationAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<CoinEntity> {

        private TextView name, name2, price, gain, h, price2;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_quotation_layout);
            name = $(R.id.quotation_name);
            name2 = $(R.id.quotation_name2);
            price = $(R.id.quotation_price);
            gain = $(R.id.quotation_gain);
            h = $(R.id.quotation_h);
            price2 = $(R.id.quotation_price2);
        }

        @Override
        public void setData(final CoinEntity entity) {
            try {
                name.setText(entity.getName());
                name2.setText("/" + entity.getPay_name());
                price.setText(StringUtils.double2String(entity.getPrice(), entity.getPriceScale()) + "");
                gain.setText(entity.getRate() + "%");
                h.setText("24H量 " + entity.getNumber());
                price2.setText("¥ " + entity.getPrice_cny());
                name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                if (entity.getRate()>0){
                    gain.setBackground(getContext().getResources().getDrawable(R.drawable.shape_green_layout));
                }else {
                    gain.setBackground(getContext().getResources().getDrawable(R.drawable.shape_red_layout));
                }
                if (entity.getRate()==0){
                    gain.setBackground(getContext().getResources().getDrawable(R.drawable.shape_gray_layout));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
