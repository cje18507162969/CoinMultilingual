package com.coin.market.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.StringUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.BillboardEntity;
import teng.wang.comment.model.CoinEntity;
import teng.wang.comment.model.HomeCoinEntity;
import teng.wang.comment.model.RankingEntity;

/**
 * @author Home 首页 行情分类
 * @version v1.0
 * @Time 2018-9-4
 */

public class HomeMarketAdapter extends RecyclerArrayAdapter<BillboardEntity> {

    public static int itemType;

    public HomeMarketAdapter(Context context, int itemType) {
        super(context);
        this.itemType = itemType;
    }

    public void setType(int itemType){
        this.itemType = itemType;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<BillboardEntity> {

        private TextView name, name2, price, gain;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_billboard_layout);
            name = $(R.id.billboard_name);
            name2 = $(R.id.billboard_name2);
            price = $(R.id.billboard_price);
            gain = $(R.id.billboard_gain);
        }

        @Override
        public void setData(final BillboardEntity entity) {
            try {

                // 根据type 显示不同榜单的样式
                switch (itemType) {
                    case 0:
                        // 涨幅榜      shape_green_layout ：绿色
                        gain.setBackground(getContext().getResources().getDrawable(R.drawable.shape_red_layout));
                        gain.setTextColor(getContext().getResources().getColor(R.color.white));
                        name.setText(entity.getName());
                        name2.setText(" /" + entity.getRename());
                        name2.setVisibility(View.VISIBLE);
                        price.setText(StringUtils.double2String(entity.getPrice(),entity.getPriceScale()) + "");  // 最新价
                        gain.setText(entity.getRate() + "%");  // 百分比
                        if (entity.getRate()>0){
                            gain.setText("+" + entity.getRate() + "%");  // 百分比
                            gain.setBackground(getContext().getResources().getDrawable(R.drawable.shape_green_layout)); // 绿
                        }else if (entity.getRate()==0){
                            gain.setBackground(getContext().getResources().getDrawable(R.drawable.shape_gray_layout)); // 灰
                        }
                        break;
                    case 1:
                        // 成交榜
                        gain.setBackground(getContext().getResources().getDrawable(R.drawable.shape_blue_min_transparent_layout));
                        gain.setTextColor(getContext().getResources().getColor(R.color.app_style_blue));
                        name.setText(entity.getName());
//                        name2.setText(" /" + entity.getRename());
                        name2.setVisibility(View.GONE);
                        price.setText(StringUtils.double2String(entity.getPrice(),4)+"");  // 最新价（CNY）
                        if (entity.getVolume()>100000000){ // 千 万 亿
                            StringUtils.getNewNumber(entity.getVolume()+"", 8);
                            gain.setText(StringUtils.getNewNumber(entity.getVolume()+"", 8));  // 24H成交额（CNY）
                        }else {
                            gain.setText(entity.getVolume()+"");
                        }
                        gain.setText(StringUtils.getNewNumber2(entity.getVolume()));

                        break;
                    case 2:
                        // 新币榜
                        gain.setBackground(getContext().getResources().getDrawable(R.drawable.shape_red_layout)); // 红
                        gain.setBackground(getContext().getResources().getDrawable(R.drawable.shape_gray_layout)); // 灰
                        gain.setBackground(getContext().getResources().getDrawable(R.drawable.shape_red_layout)); // 绿
                        gain.setTextColor(getContext().getResources().getColor(R.color.white));
                        name.setText(entity.getName());
                        name2.setVisibility(View.GONE);
                        price.setText(StringUtils.double2String(entity.getPrice(), 4) + "");  // 24小时成交额
                        gain.setText(entity.getRate() + "%");  // 百分比
                        if (entity.getRate()>0){
                            gain.setText("+" + entity.getRate() + "%");  // 百分比
                            gain.setBackground(getContext().getResources().getDrawable(R.drawable.shape_green_layout)); // 绿
                        }else if (entity.getRate()==0){
                            gain.setBackground(getContext().getResources().getDrawable(R.drawable.shape_gray_layout)); // 灰
                        }
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
