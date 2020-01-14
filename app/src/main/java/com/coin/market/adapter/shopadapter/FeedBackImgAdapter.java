package com.coin.market.adapter.shopadapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.coin.market.R;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.FeedBackImgModel;


/**
 * @author Ycc 产品详情图片列表，可能多张图
 * @version v1.0
 * @Time 2018-8-23
 */

public class FeedBackImgAdapter extends RecyclerArrayAdapter<FeedBackImgModel> {

    public FeedBackImgAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }
    private static class PicPersonViewHolder extends BaseViewHolder<FeedBackImgModel> {

        private ImageView iv;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_feedback_img_layout);
            iv = $(R.id.iv);
        }

        @Override
        public void setData(final FeedBackImgModel bean) {
            try {
                if (!TextUtils.isEmpty(bean.getImgUrl())){
                    Glide.with(getContext()).load(bean.getImgUrl()).into(iv);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
