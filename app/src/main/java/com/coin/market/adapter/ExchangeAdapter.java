package com.coin.market.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class ExchangeAdapter extends RecyclerArrayAdapter<ExchangeDialogModel> {

    private static mOnClickCallback listener;

    public ExchangeAdapter(Context context) {
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

        private TextView title, content;
        private ImageView selected;
        private RelativeLayout layout;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_exchange_layout);
            layout = $(R.id.item_exchange_layout);
            title = $(R.id.item_exchange_title);
            content = $(R.id.item_exchange_content);
            selected = $(R.id.item_exchange_selected);
        }

        @Override
        public void setData(final ExchangeDialogModel entity) {
            try {
                title.setText(entity.getTitle());
                content.setText("余额" + entity.getBalance());
                if (entity.getSelected()==1){
                    selected.setVisibility(View.VISIBLE);
                }else if (entity.getSelected()==0){
                    selected.setVisibility(View.GONE);
                }
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!EmptyUtil.isEmpty(listener)){
                            listener.collectionItenDelete(entity.getTitle());
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

        void collectionItenDelete(String title);

    }

}
