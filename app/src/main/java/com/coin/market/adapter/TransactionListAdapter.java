package com.coin.market.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.OTCTradeBean;
import teng.wang.comment.model.OTCTradeItemBean;

/**
 * @author 交易-法币  我要买 我要卖 列表构造器
 * @version v1.0
 * @Time 2019-7-31
 */

public class TransactionListAdapter extends RecyclerArrayAdapter<OTCTradeItemBean> {

    public static String itemType; // 买或卖


    private static mOnClickCallback listener;

    public void setListener(mOnClickCallback listener) {
        this.listener = listener;
    }

    public TransactionListAdapter(Context context, String itemType) {
        super(context);
        this.itemType = itemType;
    }

    public void setType(String itemType) {
        this.itemType = itemType;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<OTCTradeItemBean> {

        private TextView nameH, name, x, gain, numb, quota, price, button;
        private ImageView card, alipay, weix;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_transaction_list_layout);
            nameH = $(R.id.transaction_name_header);
            name = $(R.id.transaction_list_name);
            x = $(R.id.transaction_list_xx);
            gain = $(R.id.transaction_list_xxx);
            numb = $(R.id.transaction_list_numb);
            quota = $(R.id.transaction_list_quota);
            price = $(R.id.transaction_list_price);
            card = $(R.id.transaction_list_card);
            alipay = $(R.id.transaction_list_alipay);
            weix = $(R.id.transaction_list_weix);
            button = $(R.id.transaction_list_buy_button);
        }

        @Override
        public void setData(final OTCTradeItemBean entity) {
            try {
                if (entity.getOtcTradeType().equals("sell")){
                    button.setText(getContext().getResources().getString(R.string.transaction_fb_buy_button_text));
                }else {
                    button.setText(getContext().getString(R.string.transaction_fb_sell_button_text));
                }
                if (!TextUtils.isEmpty(entity.getSellName())){
                    name.setText(entity.getSellName());
                    nameH.setText(entity.getSellName().subSequence(0,1));
                }else {
                    name.setText(entity.getMobile());
                    nameH.setVisibility(View.GONE);
                }

//                x.setText(entity.getX());
//                gain.setText(entity.getRate());
                numb.setText("数量 " + StringUtils.double2String(entity.getNumber(), 4)+ " " + entity.getCoinName());
                quota.setText("¥" + StringUtils.double2String(entity.getMinTradeMoney(), 2)+"-¥" + StringUtils.double2String(entity.getMaxTradeMoney(), 2));
                price.setText("¥" + entity.getPrice());
                if (!EmptyUtil.isEmpty(entity.getUserPay())){
                    for (int i = 0; i < entity.getUserPay().size(); i++) {
                        if (entity.getUserPay().get(i).equals("wxpay")){
                            weix.setVisibility(View.VISIBLE);
                        }
                        if (entity.getUserPay().get(i).equals("unionpay")){
                            card.setVisibility(View.VISIBLE);
                        }
                        if (entity.getUserPay().get(i).equals("alipay")){
                            alipay.setVisibility(View.VISIBLE);
                        }
                    }
                }


                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!EmptyUtil.isEmpty(listener)){
                            listener.onClick(entity);
                        }
                    }
                });
//                if (entity.getRate() > 0) {
//                    gain.setBackground(getContext().getResources().getDrawable(R.drawable.shape_red_layout));
//                } else {
//                    gain.setBackground(getContext().getResources().getDrawable(R.drawable.shape_green_layout));
//                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *   选择item的回调
     */
    public interface mOnClickCallback {

        void onClick(OTCTradeItemBean entity);

    }

}
