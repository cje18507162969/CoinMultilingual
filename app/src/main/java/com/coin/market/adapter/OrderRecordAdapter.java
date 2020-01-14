package com.coin.market.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.StringUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.CoinRecordEntity;
import teng.wang.comment.model.OtcOrderAllEntity;

/**
 * @author 订单adapter
 * @version v1.0
 * @Time 2018-9-4
 */

public class OrderRecordAdapter extends RecyclerArrayAdapter<OtcOrderAllEntity.ListBean> {

    public OrderRecordAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<OtcOrderAllEntity.ListBean> {

        private TextView title, numb, type, time, amount, code, status;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_order_record_layout);
            title = $(R.id.coin_order_coin_name);
            numb = $(R.id.coin_order_numb);
            type = $(R.id.coin_order_type);
            time = $(R.id.coin_order_time);
            amount = $(R.id.coin_order_deal);
            code = $(R.id.coin_order_code);
            status = $(R.id.coin_order_status);
        }

        @Override
        public void setData(final OtcOrderAllEntity.ListBean entity) {
            try {

                title.setText(entity.getCoinName());
                numb.setText(StringUtils.double2String(entity.getNumber(), 6) + "");
                time.setText(entity.getCreatedAt());
                amount.setText(StringUtils.double2String(entity.getAmount(), 2)+"");
                code.setText(entity.getOrderNumber());
                if (entity.getOtcTradeType().equals("buy")) {
                    type.setText("购买");
                    type.setTextColor(getContext().getResources().getColor(R.color.app_style_blue));
                } else {
                    type.setText("出售");
                    type.setTextColor(getContext().getResources().getColor(R.color.app_home_coin_text_color_red));
                }
                switch (entity.getOtcStatus()) {
                    case "noalreadypay":
                        status.setText("未支付");
                        break;
                    case "alreadysuccess":
                        status.setText("已完成");
                        break;
                    case "alreadycancel":
                        status.setText("已取消");
                        break;
                    case "waitverify":
                        status.setText("待确认");
                        break;
                    case "order_inthecomplaint":
                        status.setText("申诉中");
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
