package com.coin.market.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.text.DecimalFormat;

import teng.wang.comment.model.EntrustInfo;
import teng.wang.comment.model.EntrustModel;
import teng.wang.comment.model.EntrustMoreModel;

/**
 * @author  订单adapter
 * @version v1.0
 * @Time 2018-9-4
 */

public class CoinOrderMoreAdapter extends RecyclerArrayAdapter<EntrustInfo> {

    private static itemClick listener;

    public CoinOrderMoreAdapter(Context context) {
        super(context);
    }

    public void setListener(itemClick listener) {
        this.listener = listener;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<EntrustInfo> {

        private LinearLayout button;
        private ImageView arrow;
        private TextView type, title, time , buttonText;
        private TextView title1, title2, title3, title4, title5, title6, value1, value2, value3, value4, value5, value6;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_coin_order_more_layout);
            type = $(R.id.coin_order_type);
            title = $(R.id.coin_order_coin_name);
            time = $(R.id.coin_order_time);
            title1 = $(R.id.coin_order_rice_title);
            title2 = $(R.id.coin_order_numb_title);
            title3 = $(R.id.coin_order_deal_title);
            title4 = $(R.id.coin_order_all_money_title);
            title5 = $(R.id.coin_order_price_title);
            title6 = $(R.id.coin_order_value_title);
            value1 = $(R.id.coin_order_rice);
            value2 = $(R.id.coin_order_numb);
            value3 = $(R.id.coin_order_deal);
            value4 = $(R.id.coin_order_all_money_text);
            value5 = $(R.id.coin_order_price_text);
            value6 = $(R.id.coin_order_value_text);
            button = $(R.id.cancel_button);
            buttonText = $(R.id.cancel_button_text);
            arrow = $(R.id.coin_order_more_arrow);
        }

        @Override
        public void setData(final EntrustInfo entity) {
            try {
                title.setText(entity.getCoin_name() + "/" + entity.getPay_name());
                title1.setText("时间"); //时间
                title2.setText("委托价" + entity.getPay_name()); //委托价
                title3.setText("委托量" + entity.getCoin_name()); //委托量
                title4.setText("总成交额" + entity.getPay_name()); //总成交额
                title5.setText("成交均价" + entity.getPay_name()); //成交均价
                title6.setText("成交量" + entity.getCoin_name()); //成交量

                value1.setText(entity.getCreated_at()); //时间
                value2.setText(entity.getPrice()+""); //委托价
                value3.setText(entity.getNumber()+""); //委托量
                value4.setText(entity.getVolume()+""); //总成交额
                value5.setText(entity.getAveragePrice()  +""); //成交均价
                value6.setText(entity.getDeal()+""); //成交量

//                DecimalFormat decimalFormat = new DecimalFormat();
//                value6.setText(decimalFormat.format(Double.parseDouble(entity.getDeal())));

                if (entity.getType()==0){
                    type.setText("买入");
                    type.setTextColor(getContext().getResources().getColor(R.color.app_home_coin_text_color));
                }else {
                    type.setText("卖出");
                    type.setTextColor(getContext().getResources().getColor(R.color.app_home_coin_text_color_red));
                }
                if (entity.getStatus()==1){
                    buttonText.setText("已成交");
                }else if (entity.getStatus()==2){
                    buttonText.setText("已撤销");
                    arrow.setVisibility(View.GONE);
                }
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!EmptyUtil.isEmpty(listener)){
                            if (entity.getStatus()==1){
                                listener.Click(entity);
                            }

                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface itemClick {
        void Click(EntrustInfo entity);
    }

}
