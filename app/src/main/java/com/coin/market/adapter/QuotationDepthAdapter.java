package com.coin.market.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.DepthItemModel;
import teng.wang.comment.model.InAndOutEntity;

/**
 * @author: Lenovo
 * @date: 2019/8/5
 * @info: 行情H5下面的列表
 */
public class QuotationDepthAdapter extends RecyclerArrayAdapter<DepthItemModel> {

    public static int priceScale;
    public static int amountScale;

    public QuotationDepthAdapter(Context context) {
        super(context);
    }

    public void getScale(int priceScale, int amountScale){
        this.priceScale = priceScale;
        this.amountScale = amountScale;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<DepthItemModel> {

        private TextView code, numb, price;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_quotation_depth_layout);
            code = $(R.id.item_quotation_code1);
            numb = $(R.id.item_quotation_numb1);
            price = $(R.id.item_quotation_price1);
        }

        @Override
        public void setData(final DepthItemModel entity) {
            try {
                code.setText((getPosition()+ 1) + "");
                numb.setText(StringUtils.double2String(entity.getNumber(), priceScale)+"");
                price.setText(StringUtils.double2String(entity.getPrice(), amountScale)+"");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
