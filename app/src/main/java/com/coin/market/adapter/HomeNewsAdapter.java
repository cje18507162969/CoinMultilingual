package com.coin.market.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coin.market.R;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.BulletinEntity;

/**
 * @author: Lenovo
 * @date: 2019/8/8
 * @info: 首页 新闻公告 构造器
 */
public class HomeNewsAdapter extends RecyclerArrayAdapter<BulletinEntity.DataBean> {


    public HomeNewsAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<BulletinEntity.DataBean> {
        private TextView title, numb, time, content;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_home_news_layout);
            title = $(R.id.mTv_title);
            numb = $(R.id.mTv_numb);
            time = $(R.id.mTv_time);
            content = $(R.id.mTv_content);
        }

        @Override
        public void setData(final BulletinEntity.DataBean data) {
            try {
                title.setText(data.getTitle());
                numb.setText(data.getViews());
                time.setText(data.getCreated_at());
                content.setText(data.getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
