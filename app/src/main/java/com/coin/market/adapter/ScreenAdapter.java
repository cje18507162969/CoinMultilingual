package com.coin.market.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.EmptyUtil;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.ExchangeDialogModel;

/**
 * Created by LiYang on 2018/9/6.
 */

public class ScreenAdapter extends RecyclerArrayAdapter<ExchangeDialogModel> {

    private static mOnClickCallback listener;

    public ScreenAdapter(Context context) {
        super(context);
    }

    public void setListener(mOnClickCallback listener) {
        this.listener = listener;
    }


    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<ExchangeDialogModel> {

        private TextView title;
        private LinearLayout layout;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_screen_layout);
            title = $(R.id.item_screen_title);
            layout = $(R.id.item_screen_layout);
        }

        @Override
        public void setData(final ExchangeDialogModel entity) {
            try {
                title.setText(entity.getTitle());
                if (getPosition() == 0){
                    title.setTextColor(getContext().getResources().getColor(R.color.app_style_blue));
                }else {
                    title.setTextColor(getContext().getResources().getColor(R.color.color_999999));
                }
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!EmptyUtil.isEmpty(listener)){
                            listener.selectItem(entity.getTitle());
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *   选择item的回调
     */
    public interface mOnClickCallback {

        void selectItem(String title);

    }

}
