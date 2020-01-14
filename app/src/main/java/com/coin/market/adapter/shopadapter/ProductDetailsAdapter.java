package com.coin.market.adapter.shopadapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.coin.market.R;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;


/**
 * @author Ycc 产品详情图片列表，可能多张图
 * @version v1.0
 * @Time 2018-8-23
 */

public class ProductDetailsAdapter extends RecyclerArrayAdapter<String> {
    public ProductDetailsAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }
    private static class PicPersonViewHolder extends BaseViewHolder<String> {
        private ImageView mImgProductDetails;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_product_details_layout);
            mImgProductDetails = $(R.id.mImg_product_details);
        }

        @Override
        public void setData(final String person) {
            try {
                Glide.with(getContext()).load(person).into(mImgProductDetails);
                //GlideUtil.displayImage(getContext(),mImgProductDetails,person);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
