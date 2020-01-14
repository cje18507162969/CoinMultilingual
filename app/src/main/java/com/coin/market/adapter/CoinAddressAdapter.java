package com.coin.market.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.EmptyUtil;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.CoinAddressEntity;
import teng.wang.comment.model.CoinRecordEntity;
import teng.wang.comment.model.EntrustInfo;

/**
 * @author Quotation 行情
 * @version v1.0
 * @Time 2018-9-4
 */

public class CoinAddressAdapter extends RecyclerArrayAdapter<CoinAddressEntity> {

    private static itemClick listener;

    public CoinAddressAdapter(Context context) {
        super(context);
    }

    public void setListener(itemClick listener){
        this.listener = listener;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<CoinAddressEntity> {

        private TextView name, address, delete;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_coin_address_layout);
            name = $(R.id.coin_record_type);
            address = $(R.id.item_coin_address_code);
            delete = $(R.id.address_delete);
        }

        @Override
        public void setData(final CoinAddressEntity entity) {
            try {
                name.setText(entity.getTag()+"");
                address.setText(entity.getAddress());
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!EmptyUtil.isEmpty(listener)){
                            listener.Click(entity.getId());
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface itemClick {
        void Click(String addressId);
    }

}
