package com.coin.market.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.StringUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.BillboardEntity;
import teng.wang.comment.model.PointsModel;

/**
 * @author Home 首页 ViewPager 指示器
 * @version v1.0
 * @Time 2018-9-4
 */

public class PointAdapter extends RecyclerArrayAdapter<PointsModel> {

    public PointAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<PointsModel> {

        private TextView icon;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_point_layout);
            icon = $(R.id.point);
        }

        @Override
        public void setData(final PointsModel entity) {
            try {
                icon.setBackgroundResource(R.drawable.shape_point_two_layout);
                if (entity.isSelect()){
                    icon.setBackgroundResource(R.drawable.shape_point_one_layout);
                }else {
                    icon.setBackgroundResource(R.drawable.shape_point_two_layout);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
