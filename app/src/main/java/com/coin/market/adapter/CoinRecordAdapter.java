package com.coin.market.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coin.market.R;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.CoinRecordEntity;
import teng.wang.comment.model.ImportsDTO;
import teng.wang.comment.model.ImportsDTO;

/**
 * @author 提币充币历史
 * @version v1.0
 * @Time 2018-9-4
 */

public class CoinRecordAdapter extends RecyclerArrayAdapter<ImportsDTO> {

    private static int Type = 0;
    public static Context mContext;

    public CoinRecordAdapter(Context context, int type) {
        super(context);
        this.Type = type;
        mContext = context;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<ImportsDTO> {

        private TextView title, numb, type, time;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_coin_record_layout);
            title = $(R.id.coin_record_title);
            numb = $(R.id.coin_record_numb);
            type = $(R.id.coin_record_type);
            time = $(R.id.coin_record_time);
        }

        @Override
        public void setData(final ImportsDTO entity) {
            try {
                //title.setText(entity.getTitle());
                numb.setText(entity.getNumber()+""); //数量
                time.setText(entity.getCreateDate()+""); //时间



                if (Type == 0){  // 状态 待处理--0 已处理--1 已撤销--2 ,
                    title.setText(mContext.getResources().getString(R.string.mine_ordinary_withdrawal));
                    // 状态
                    switch (entity.getStatus()) {
                        case 0:
                            type.setText(mContext.getResources().getString(R.string.mine_pending));
                            break;
                        case 1:
                            type.setText(mContext.getResources().getString(R.string.mine_processed));
                            break;
                        case 2:
                            type.setText(mContext.getResources().getString(R.string.mine_revoked));
                          break;

                        default:
                            break;
                    }
                }else {
                    title.setText(mContext.getResources().getString(R.string.mine_ordinary_deposit));
                    type.setText(mContext.getResources().getString(R.string.mine_completed));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
