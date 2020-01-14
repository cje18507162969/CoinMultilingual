package com.coin.market.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coin.market.R;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.CoinRecordEntity;
import teng.wang.comment.model.TransferRecordModel;

/**
 * @author  划转详情 Adapter
 * @version v1.0
 * @Time 2018-9-4
 */

public class TransferRecordAdapter extends RecyclerArrayAdapter<TransferRecordModel.ListBean> {

    public TransferRecordAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<TransferRecordModel.ListBean> {

        private TextView title, numb, type, time;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_coin_record_layout);
            title = $(R.id.coin_record_title);
            numb = $(R.id.coin_record_numb);
            type = $(R.id.coin_record_type);
            time = $(R.id.coin_record_time);
        }

        @Override
        public void setData(final TransferRecordModel.ListBean entity) {
            try {
                //title.setText(entity.getCoinName());
                numb.setText(entity.getNumber());
                time.setText(entity.getTime());
                if (entity.getType().equals("bb_type")){
                    title.setText("币币账户转到法币账户");
                }else if (entity.getType().equals("fb_type")){
                    title.setText("法币账户转到币币账户");
                }else if (entity.getType().equals("yj_type")){
                    title.setText("申请商家入驻押金");
                }
                type.setText("已完成");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
