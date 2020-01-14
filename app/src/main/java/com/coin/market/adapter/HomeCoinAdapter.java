package com.coin.market.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.StringUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.CoinEntity;
import teng.wang.comment.model.HomeCoinEntity;
import teng.wang.comment.model.MarketsDTO;

/**
 * @author  Home 首页 货币 item
 * @version v1.0
 * @Time 2018-9-4
 */

public class HomeCoinAdapter extends RecyclerArrayAdapter<MarketsDTO> {

    public HomeCoinAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }
    private static class PicPersonViewHolder extends BaseViewHolder<MarketsDTO> {
        private LinearLayout layout;
        private TextView name, price, gain, value;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_home_cion_layout);
            layout = $(R.id.item_coin_layout);  // item 布局
            name = $(R.id.top_name1_tv);  // 货币名字
            price = $(R.id.top_price1_tv);  // 货币价格
            gain = $(R.id.top_rate1_tv);  // 货币涨幅
            value = $(R.id.top_price_cny1_tv); // 货币价值
        }

        @Override
        public void setData(final MarketsDTO entity) {
            try {
                int aaa = layout.getHeight();
                int bbb = layout.getWidth();
                name.setText(entity.getName());
                price.setText(StringUtils.double2String(entity.getPrice().doubleValue(),entity.getPriceScale())+"");
                gain.setText(entity.getRate()+"%");
                value.setText("≈¥" + entity.getPriceCny() + " CNY");
                WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
                int width = wm.getDefaultDisplay().getWidth();
                LinearLayout.LayoutParams params_1= (LinearLayout.LayoutParams) layout.getLayoutParams();
                params_1.weight = width/3;
                if (entity.getRate().doubleValue()>0){ //大于0 绿色
                    price.setTextColor(getContext().getResources().getColor(R.color.app_home_coin_text_color));
                    gain.setTextColor(getContext().getResources().getColor(R.color.app_home_coin_text_color));
                    gain.setText("+" + entity.getRate()+"%");
                }else {
                    price.setTextColor(getContext().getResources().getColor(R.color.app_home_coin_text_color_red));
                    gain.setTextColor(getContext().getResources().getColor(R.color.app_home_coin_text_color_red));
                }

//                params_1.height = 240;
//                layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (width/3)));
//                int aa = layout.getHeight();
//                int bb = layout.getWidth();
//                layout.setLayoutParams(params_1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
