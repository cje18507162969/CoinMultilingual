package com.coin.market.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coin.market.R;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.CoinListEntity;

/**
 * @author Quotation 行情
 * @version v1.0
 * @Time 2018-9-4
 */

public class CoinListAdapter extends RecyclerArrayAdapter<CoinListEntity> {

    public CoinListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<CoinListEntity> {

        private TextView name;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_coin_list_layout);
            name = $(R.id.item_coin_list_text);
        }

        @Override
        public void setData(final CoinListEntity entity) {
            try {
                name.setText(entity.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
