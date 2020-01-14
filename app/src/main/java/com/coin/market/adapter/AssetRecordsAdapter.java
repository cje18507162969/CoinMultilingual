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

import teng.wang.comment.model.CoinRecordEntity;
import teng.wang.comment.model.InAndOutEntity;

/**
 * @author: Lenovo
 * @date: 2019/8/5
 * @info: 资产 财务记录  单个币种的列表构造器
 */
public class AssetRecordsAdapter extends RecyclerArrayAdapter<InAndOutEntity> {

    public AssetRecordsAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<InAndOutEntity> {

        private TextView title, numb, type, time;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_asset_records_layout);
            title = $(R.id.item_asset_records_title);
            numb = $(R.id.item_asset_records_numb);
            type = $(R.id.item_asset_records_type);
            time = $(R.id.item_asset_records_time);
        }

        @Override
        public void setData(final InAndOutEntity entity) {
            try {

                if ("in".equals(entity.flow)){
                    numb.setText( "+"+entity.getNumber());
                }else{
                    numb.setText( "-"+entity.getNumber());
                }

                if (!EmptyUtil.isEmpty(entity.getType())) {
                    //（充值：recharge、提现：withdraw、开仓：buy、平仓：sell, 手续费：fee）
                    switch (entity.getType()){
                        case "recharge":
                            title.setText(""+getContext().getResources().getString(R.string.tv_assetrecord_recharge));
                            break;
                        case "withdraw":
                            title.setText(""+getContext().getResources().getString(R.string.tv_assetrecord_withdraw));
                            break;
                        case "buy":
                            title.setText(""+getContext().getResources().getString(R.string.tv_assetrecord_openaposition));
                            break;
                        case "sell":
                            title.setText(""+getContext().getResources().getString(R.string.tv_close_position));
                            break;
                        case "fee":
                            title.setText(""+getContext().getResources().getString(R.string.mine_coin_out_money));
                            break;
                    }


                }
                if (!EmptyUtil.isEmpty(entity.createDate)) {
                    time.setText(entity.createDate);
                }

                type.setText(getContext().getResources().getString(R.string.tv_assetrecord_completed));
//                if (TextUtils.isEmpty(entity.getAddress())) {
//                    // 充币提币
//                    type.setText("已完成");
//                } else {
//                    // 转入转出
//                    if (!entity.getStatus().equals("disable")) {
//                        switch (entity.getStatus()) {
//                            case "0":
//                                type.setText("待处理");
//                                break;
//                            case "1":
//                                type.setText("已处理");
//                                break;
//                            case "2":
//                                type.setText("已撤销");
//                                break;
//                            default:
//                                break;
//                        }
//                    }else {
//                        type.setText("已完成");
//                    }
//                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
