package com.coin.market.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.StringUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.CoinRecordEntity;

/**
 * @author: Lenovo
 * @date: 2019/8/5
 * @info: 资产 币币账户 法币账户列表 构造器
 */
public class AssetsAccountAdapter extends RecyclerArrayAdapter<CoinRecordEntity> {

    public AssetsAccountAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<CoinRecordEntity> {

        private TextView title, available, frozen, convert;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_assets_account_layout);
            title = $(R.id.assets_account_title);
            available = $(R.id.assets_account_available);
            frozen = $(R.id.assets_account_frozen);
            convert = $(R.id.assets_account_convert);
        }

        @Override
        public void setData(final CoinRecordEntity entity) {
            try {
                title.setText(entity.getTitle());
                available.setText(StringUtils.double2String(Double.parseDouble(entity.getNumb()), 8));
                frozen.setText(StringUtils.double2String(Double.parseDouble(entity.getDeal()), 8));
                convert.setText(entity.getPrice());
                if (entity.getType().equals("0")){

                }else if (entity.getType().equals("1")){
                    available.setText("****");
                    frozen.setText("****");
                    convert.setText("****");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
