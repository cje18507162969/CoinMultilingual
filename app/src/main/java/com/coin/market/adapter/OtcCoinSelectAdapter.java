package com.coin.market.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coin.market.R;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.CoinListEntity;
import teng.wang.comment.model.FB_BBEntity;
import teng.wang.comment.model.OTCCoinTypeEntity;

/**
 * @author Quotation 行情
 * @version v1.0
 * @Time 2018-9-4
 */

public class OtcCoinSelectAdapter extends RecyclerArrayAdapter<FB_BBEntity.ListdetailBean> {

    private static String accountType;

    public OtcCoinSelectAdapter(Context context, String accountType) {
        super(context);
        this.accountType = accountType;
    }

    public void setAccountType(String accountType){
        this.accountType = accountType;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<FB_BBEntity.ListdetailBean> {

        private TextView name;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_coin_list_layout);
            name = $(R.id.item_coin_list_text);
        }

        @Override
        public void setData(final FB_BBEntity.ListdetailBean entity) {
            try {
                name.setText(entity.getCoin_name());
                if (accountType.equals("fb_type")){
                    // 法币

                }else if (accountType.equals("bb_type")){
                    // 币币

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
