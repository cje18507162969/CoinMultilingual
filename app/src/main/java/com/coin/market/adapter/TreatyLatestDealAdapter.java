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

import teng.wang.comment.model.ContraceTradeDTO;

/**
 * @author  最新成交adapter
 * @version v1.0
 * @Time 2018-9-4
 */

public class TreatyLatestDealAdapter extends RecyclerArrayAdapter<ContraceTradeDTO> {

    private static itemClick listener;

    public TreatyLatestDealAdapter(Context context) {
        super(context);
    }

    public void setListener(itemClick listener) {
        this.listener = listener;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<ContraceTradeDTO> {

        private TextView tv_time, tv_orientation, tv_price;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_treaty_latest_deal);
            tv_time = $(R.id.tv_time);
            tv_orientation = $(R.id.tv_orientation);
            tv_price = $(R.id.tv_price);
        }

        @Override
        public void setData(final ContraceTradeDTO entity) {
            try {
                if (!TextUtils.isEmpty(entity.getUpdateDate())){
                    String[] split = entity.getUpdateDate().split(" ");
                    tv_time.setText(split[1]+"");
                }


                if (!"rise".equals(entity.getBuysType())){
                    tv_orientation.setSelected(true);
                    tv_orientation.setText(getContext().getString(R.string.tv_sell_more));

                }else{
                    tv_orientation.setSelected(false);
                    tv_orientation.setText(getContext().getString(R.string.tv_sell_short));
                }
                tv_price.setText(StringUtils.double2String(entity.getContractPriceOpen().doubleValue(),2));

//
//                cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (!EmptyUtil.isEmpty(listener)){
//                            listener.Click(entity);
//                        }
//                    }
//                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface itemClick {
        void Click(ContraceTradeDTO entity);
    }

}
