package com.coin.market.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coin.market.R;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.CoinListEntity;

/**
 * @author  筛选 Adapter
 * @version v1.0
 * @Time 2018-9-4
 */

public class CoinScreenAdapter extends RecyclerArrayAdapter<CoinListEntity> {

    public CoinScreenAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<CoinListEntity> {

        private TextView name;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_coin_screen_layout);
            name = $(R.id.item_coin_screen_text);
        }

        @Override
        public void setData(final CoinListEntity entity) {
            try {
                name.setText(entity.getName());
                if (entity.getType() ==1){
                    name.setBackgroundResource(R.drawable.shape_blue_line_button_layout);
                    name.setTextColor(getContext().getResources().getColor(R.color.app_style_blue));
                }else {
                    name.setBackgroundResource(R.drawable.shape_gray_button_layout);
                    name.setTextColor(getContext().getResources().getColor(R.color.app_home_text));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
