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

import teng.wang.comment.model.CoinOrderInfoEntity;
import teng.wang.comment.model.EntrustModel;

/**
 * @author  订单adapter
 * @version v1.0
 * @Time 2018-9-4
 */

public class CoinOrderInfoAdapter extends RecyclerArrayAdapter<CoinOrderInfoEntity> {

    public CoinOrderInfoAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<CoinOrderInfoEntity> {

        private TextView time, price, value, money, dk;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_coin_order_info_layout);
            time = $(R.id.item_coin_info_time);
            price = $(R.id.item_coin_info_price);
            value = $(R.id.item_coin_info_value);
            money = $(R.id.item_coin_info_money);
            dk = $(R.id.coin_info_dk);
        }

        @Override
        public void setData(final CoinOrderInfoEntity entity) {
            try {
                time.setText(entity.getCreated_at()+"");
                price.setText(entity.getPrice()+ entity.getPayName() );
                value.setText(entity.getNumber()+ entity.getCoinName());
                dk.setText(entity.getPts()+"pts");
                if (entity.getType().equals("0")){
                    money.setText(entity.getFee()+ entity.getCoinName());
                }else if (entity.getType().equals("1")){
                    money.setText(entity.getFee()+ entity.getPayName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
