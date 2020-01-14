package com.coin.market.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coin.market.R;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.ImportsDTO;
import teng.wang.comment.model.ImportsDTO;

/**
 * @author Quotation 币种
 * @version v1.0
 * @Time 2018-9-4
 */

public class ImportsListAdapter extends RecyclerArrayAdapter<ImportsDTO> {

    public ImportsListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<ImportsDTO> {

        private TextView name;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_coin_list_layout);
            name = $(R.id.item_coin_list_text);
        }

        @Override
        public void setData(final ImportsDTO entity) {
            try {
                name.setText(entity.getTxid());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
