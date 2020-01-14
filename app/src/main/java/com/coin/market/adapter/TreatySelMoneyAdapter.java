package com.coin.market.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coin.market.R;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.CoinListEntity;
import teng.wang.comment.model.MoneyEntity;

/**
 * @author Quotation 本金(USDT)
 * @version v1.0
 * @Time 2018-9-4
 */

public class TreatySelMoneyAdapter extends RecyclerArrayAdapter<MoneyEntity> {
    public Context mContext;
    public TreatySelMoneyAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<MoneyEntity> {

        private TextView tv_money;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_fragment_treaty_selmoney);
            tv_money = $(R.id.tv_money);
        }

        @Override
        public void setData(final MoneyEntity entity) {
            try {
                if (0==entity.bgcolor)
                   tv_money.setBackground(getContext().getResources().getDrawable(R.drawable.bg_article_btn_selector));
                else
                   tv_money.setBackground(getContext().getResources().getDrawable(R.drawable.bg_article_btn_selector_red));
                tv_money.setText(entity.getMoney()+"");
                tv_money.setSelected(entity.isSelect());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
