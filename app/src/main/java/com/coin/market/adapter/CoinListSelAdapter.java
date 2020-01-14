package com.coin.market.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coin.market.R;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.AccountsDTO;

/**
 * @author Quotation 行情
 * @version v1.0
 * @Time 2018-9-4
 */

public class CoinListSelAdapter extends RecyclerArrayAdapter<AccountsDTO> {

    public CoinListSelAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<AccountsDTO> {

        private TextView name;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_coin_list_layout);
            name = $(R.id.item_coin_list_text);
        }

        @Override
        public void setData(final AccountsDTO entity) {
            try {
                name.setText(entity.getCoinName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
