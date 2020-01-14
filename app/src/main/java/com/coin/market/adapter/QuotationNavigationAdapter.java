package com.coin.market.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.StringUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.CoinEntity;
import teng.wang.comment.model.RankingEntity;

/**
 * @author Quotation 行情
 * @version v1.0
 * @Time 2018-9-4
 */

public class QuotationNavigationAdapter extends RecyclerArrayAdapter<CoinEntity> {

    public QuotationNavigationAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<CoinEntity> {

        private TextView name, name2, gain;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_quotation_navigation_layout);
            name = $(R.id.quotation_name);
            name2 = $(R.id.quotation_name2);
            gain = $(R.id.quotation_gain);
        }

        @Override
        public void setData(final CoinEntity entity) {
            try {
                name.setText(entity.getName());
                name2.setText("/" + entity.getPay_name());
                gain.setText(StringUtils.double2String(entity.getPrice(), entity.getPriceScale())+"");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
