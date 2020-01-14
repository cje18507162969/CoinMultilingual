package com.coin.market.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coin.market.R;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.FeedBackTypeModel;

/**
 * @author: cjh
 * @date: 2019/10/23
 * @info: 动态获取 问题反馈 问题类型
 */
public class PublishFeedAdapter extends RecyclerArrayAdapter<FeedBackTypeModel.ListBean> {

    public PublishFeedAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<FeedBackTypeModel.ListBean> {

        private TextView bt_type;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_publish_feedback_layout);
            bt_type = $(R.id.bt_type);
        }

        @Override
        public void setData(final FeedBackTypeModel.ListBean bean) {
            try {
                bt_type.setBackgroundResource(R.drawable.shape_feedback_select_no_bg);
                if (bean.isSelect()){
                    bt_type.setBackgroundResource(R.drawable.shape_feedback_select_bg);
                    bt_type.setTextColor(getContext().getResources().getColor(R.color.white));
                }else {
                    bt_type.setTextColor(getContext().getResources().getColor(R.color.app_style_blue));
                    bt_type.setBackgroundResource(R.drawable.shape_feedback_select_no_bg);
                }
                bt_type.setText(bean.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
