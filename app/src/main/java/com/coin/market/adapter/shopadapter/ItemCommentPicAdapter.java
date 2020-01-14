package com.coin.market.adapter.shopadapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.coin.market.R;
import com.coin.market.activity.showpicture.ShowPictureActivity;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.utils.glide.GlideUtil;

/**
 * Created by LiYang on 2018/9/27.
 */

public class ItemCommentPicAdapter extends RecyclerArrayAdapter<String> {
    public ItemCommentPicAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<String> {
        private ImageView fiv;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_img_on_pictures_layout);
            fiv = $(R.id.fiv);
        }

        @Override
        public void setData(final String person) {
            try {
                if(person.equals("")|| TextUtils.isEmpty(person)){
                    fiv.setVisibility(View.GONE);
                }else{
                    fiv.setVisibility(View.VISIBLE);
                    GlideUtil.displayImage(getContext(), fiv, person);

                    fiv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getContext().startActivity(new Intent(getContext(), ShowPictureActivity.class).putExtra("SHOWPICTUREURL",person));
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
