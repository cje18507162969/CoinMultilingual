package com.coin.market.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.CoinRecordEntity;
import teng.wang.comment.model.OTCTradeItemBean;
import teng.wang.comment.model.OtcOrderAllEntity;

/**
 * @author: Lenovo
 * @date: 2019/8/5
 * @info: 我的广告列表 adapter
 */
public class MyPublishAdapter  extends RecyclerArrayAdapter<OTCTradeItemBean> {

    private static mOnClickCallback listener;

    public MyPublishAdapter(Context context) {
        super(context);
    }

    public void setListener(mOnClickCallback listener){
        this.listener = listener;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<OTCTradeItemBean> {

        private TextView type, numb, quota, time, cancel;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_my_publish_layout);
            type = $(R.id.item_my_publish_type);
            numb = $(R.id.item_my_publish_numb);
            quota = $(R.id.item_my_publish_quota);
            time = $(R.id.item_my_publish_time);
            cancel = $(R.id.item_my_publish_cancel);
        }

        @Override
        public void setData(final OTCTradeItemBean entity) {
            try {
                numb.setText(StringUtils.double2String(entity.getNumber(), 8) + " " + entity.getCoinName());

                time.setText(entity.getCreatedAt());
                quota.setText("¥ " + entity.getMinTradeMoney() + "-" + entity.getMaxTradeMoney());

                if (entity.getOtcTradeType().equals("buy")){
                    type.setTextColor(getContext().getResources().getColor(R.color.app_home_coin_text_color));
                    type.setText("购买");
                }else {
                    type.setText("出售");
                    type.setTextColor(getContext().getResources().getColor(R.color.app_home_coin_text_color_red));
                }

                if (entity.getOtcStatus().equals("publish")){
                    cancel.setText("撤销");
                }else if (entity.getOtcStatus().equals("cancel")){
                    cancel.setText("已撤销");
                }

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!EmptyUtil.isEmpty(listener)){
                            if (entity.getOtcStatus().equals("publish")){
                                listener.cancelOnClick(entity.getId());
                            }
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *   选择item的回调
     */
    public interface mOnClickCallback {

        void cancelOnClick(String otcTradeId);

    }

}
