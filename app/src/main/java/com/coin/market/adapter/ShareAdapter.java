package com.coin.market.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.DateUtils;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.ShareListModel;

/**
 * @author: cjh
 * @date: 2019/10/21
 * @info:
 */
public class ShareAdapter extends RecyclerArrayAdapter<ShareListModel.DataBean> {

    private static String type;

    public ShareAdapter(Context context, String Type) {
        super(context);
        this.type = Type;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<ShareListModel.DataBean> {

        private TextView tv_1, tv_2, tv_3;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_share_layout);

            tv_1 = $(R.id.item_tv_1);
            tv_2 = $(R.id.item_tv_2);
            tv_3 = $(R.id.item_tv_3);
        }

        @Override
        public void setData(final ShareListModel.DataBean entity) {
            try {
                tv_1.setText(entity.getMobile());
                tv_3.setText(entity.createDate+"");
                tv_2.setVisibility(View.GONE);
                if (!EmptyUtil.isEmpty(entity.getRebateStatus())){
                    tv_2.setVisibility(View.GONE);
                    if (entity.getRebateStatus().equals("1")){
                        tv_1.setTextColor(Color.parseColor("#323232"));
                        tv_3.setTextColor(Color.parseColor("#323232"));
                    }else {
                        tv_1.setTextColor(getContext().getResources().getColor(R.color.color_999999));
                        tv_3.setTextColor(getContext().getResources().getColor(R.color.color_999999));
                    }
                }
                if (type.equals("2")&&!TextUtils.isEmpty(entity.fee)){
                    tv_2.setVisibility(View.VISIBLE);
                    tv_1.setTextColor(Color.parseColor("#323232"));
                    tv_2.setText(DateUtils.StringToDate(entity.createDate));
                    tv_2.setTextColor(Color.parseColor("#999999"));
                    tv_3.setTextColor(Color.parseColor("#323232"));
                    tv_3.setText("+" + StringUtils.double2String(Double.parseDouble(entity.fee), 6)+"");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
