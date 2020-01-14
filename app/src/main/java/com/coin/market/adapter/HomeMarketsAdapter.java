package com.coin.market.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.coin.market.R;
import com.coin.market.activity.showpicture.ShowPictureActivity;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.util.Arrays;
import java.util.List;

import teng.wang.comment.model.FeedbackModel;
import teng.wang.comment.model.MarketsDTO;

/**
 * @author  首页实时行情
 * @version v1.0
 * @Time 2018-9-14
 */

public class HomeMarketsAdapter extends RecyclerArrayAdapter<MarketsDTO> {

    public HomeMarketsAdapter(Context context) {
        super(context);
    }

    public void setHeaderView(View headerView) {
//        mHeaderView = headerView;
//        notifyItemInserted(0);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<MarketsDTO> {
        private TextView name, name2, price, gain;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_billboard_layout);
            name = $(R.id.billboard_name);
            name2 = $(R.id.billboard_name2);
            price = $(R.id.billboard_price);
            gain = $(R.id.billboard_gain);
        }

        @Override
        public void setData(final MarketsDTO entity) {
            try {

                // 根据type 显示不同榜单的样式
                // 涨幅榜      shape_green_layout ：绿色
                gain.setBackground(getContext().getResources().getDrawable(R.drawable.shape_red_layout));
                gain.setTextColor(getContext().getResources().getColor(R.color.white));
                name.setText(entity.getName());
//                name2.setText(" /" + entity.getName());
                name2.setVisibility(View.GONE);
                price.setText(StringUtils.double2String(entity.getPrice().doubleValue(),entity.getPriceScale()) + "");  // 最新价
                gain.setText(entity.getRate() + "%");  // 百分比
                if (entity.getRate().doubleValue()>0){
                    gain.setText("+" + entity.getRate() + "%");  // 百分比
                    gain.setBackground(getContext().getResources().getDrawable(R.drawable.shape_green_layout)); // 绿
                }else if (entity.getRate().doubleValue()==0){
                    gain.setBackground(getContext().getResources().getDrawable(R.drawable.shape_gray_layout)); // 灰
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
