package com.coin.market.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.StringUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.ShareRankModel;

/**
 * @author: cjh
 * @date: 2019/10/11
 * @info:
 */

public class ShareRankAdapter extends RecyclerArrayAdapter<ShareRankModel> {

    public ShareRankAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new FeedBackViewHolder(parent);
    }

    private static class FeedBackViewHolder extends BaseViewHolder<ShareRankModel> {

        private TextView tv1,tv2,tv3;

        public FeedBackViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_share_rank_layout);
            tv1 = $(R.id.tv_1);
            tv2 = $(R.id.tv_2);
            tv3 = $(R.id.tv_3);
        }

        @Override
        public void setData(final ShareRankModel bean) {
            try {
                tv1.setText("No." + (getDataPosition()+1));
                tv2.setText(StringUtils.splitPhone(bean.getMobile()));
                tv3.setText(StringUtils.double2String(Double.parseDouble(bean.getResultLeaderBoardNumber()), 6) + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
