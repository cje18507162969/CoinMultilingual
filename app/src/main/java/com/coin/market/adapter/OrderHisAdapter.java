package com.coin.market.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.StringUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.listener.NoMoreClickListener;
import teng.wang.comment.model.ContraceTradeDTO;

/**
 * @author Quotation 订单 历史记录
 */

public class OrderHisAdapter extends RecyclerArrayAdapter<ContraceTradeDTO> {
    public Context mContext;
    public static int mState;  // 0 持有记录  1 现有记录
    public static PopOnClickCallback mClickCallback;
    public OrderHisAdapter(Context context) {
        super(context);
        mContext = context;
    }

    public OrderHisAdapter(Context context, int state, PopOnClickCallback clickCallback) {
        super(context);
        mContext = context;
        mState = state;
        mClickCallback = clickCallback;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }


    private static class PicPersonViewHolder extends BaseViewHolder<ContraceTradeDTO> {

        private TextView tv_name,
                tv_setting,
                tv_pc,
                tv_sell_moreorshort,
                tv_time,
                tv_pc_time,
                tv_h_money,
                tv_h_service_charge,
                tv_h_polebar,
                tv_priceopen,
                tv_profitloss,
                tv_pricenew,
                tv_price_title;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_fragment_order_layput);
            tv_name =$(R.id.tv_name);
            tv_setting =$(R.id.tv_setting);
            tv_pc =$(R.id.tv_pc);
            tv_sell_moreorshort =$(R.id.tv_sell_moreorshort);
            tv_time =$(R.id.tv_time);
            tv_pc_time =$(R.id.tv_pc_time);
            tv_h_money =$(R.id.tv_h_money);
            tv_h_service_charge =$(R.id.tv_h_service_charge);
            tv_h_polebar =$(R.id.tv_h_polebar);
            tv_priceopen =$(R.id.tv_priceopen);
            tv_profitloss =$(R.id.tv_profitloss);
            tv_pricenew =$(R.id.tv_pricenew);
            tv_price_title =$(R.id.tv_price_title);

        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public void setData(final ContraceTradeDTO entity) {
            try {
                tv_name.setText(entity.getContractName());
                tv_pricenew.setText(StringUtils.double2String(entity.getContractPriceClose().doubleValue(),2)+"");
                tv_priceopen.setText(StringUtils.double2String(entity.getContractPriceOpen().doubleValue(),2)+"");


                if (!"rise".equals(entity.getBuysType())){
                    tv_sell_moreorshort.setSelected(true);
                    tv_sell_moreorshort.setText(getContext().getString(R.string.tv_sell_short));

                }else{
                    tv_sell_moreorshort.setSelected(false);
                    tv_sell_moreorshort.setText(getContext().getString(R.string.tv_sell_more));
                }

                tv_profitloss.setText(StringUtils.double2String(entity.getProfitLoss().doubleValue(),2)+"");
                if (entity.getProfitLoss().doubleValue() > 0) {
                    tv_profitloss.setTextColor(getContext().getResources().getColor(R.color.txt_color_4ead));
                } else {
                    tv_profitloss.setTextColor(getContext().getResources().getColor(R.color.txt_color_b44c));
                }

                tv_time.setText(entity.getCreateDate());
                tv_h_money.setText(
                        Html.fromHtml(
                                String.format(getContext().getResources().getString(R.string.tv_h_money),entity.getMoney().intValue()+"")));

                tv_h_service_charge.setText(
                        Html.fromHtml(
                                String.format(getContext().getResources().getString(R.string.tv_h_service_charge),entity.getFree()+"")));
                tv_h_polebar.setText(
                        Html.fromHtml(
                                String.format(getContext().getResources().getString(R.string.tv_h_polebar),entity.getPryBar()+"X")));

                tv_pc_time.setText(
                        Html.fromHtml(
                                String.format(getContext().getResources().getString(R.string.tv_h_time_pc),entity.getUpdateDate()+"")));

                tv_price_title.setText(getContext().getResources().getString(R.string.tv_price_close));
                tv_pc_time.setVisibility(View.VISIBLE);
                tv_pc.setVisibility(View.GONE);
                tv_setting.setVisibility(View.GONE);





            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 成功
     */
    public interface PopOnClickCallback {
        void onPC(String id, int positiion);
        void onSet(String id, double profit, double loss);
    }
}
