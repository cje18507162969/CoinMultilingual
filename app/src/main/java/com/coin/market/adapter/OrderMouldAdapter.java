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
import com.coin.market.util.PopUtil;
import com.coin.market.util.StringUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.math.BigDecimal;
import java.math.RoundingMode;

import teng.wang.comment.listener.NoMoreClickListener;
import teng.wang.comment.model.ContraceTradeDTO;
import teng.wang.comment.model.ContraceTradeDTO;
import teng.wang.comment.utils.log.Log;

/**
 * @author Quotation 订单记录
 */

public class OrderMouldAdapter extends RecyclerArrayAdapter<ContraceTradeDTO> {
    public Context mContext;
    public static int mState;  // 0 持有记录  1 现有记录
    public static PopOnClickCallback mClickCallback;

    public static int clickPosition = 0;
    public OrderMouldAdapter(Context context) {
        super(context);
        mContext = context;
    }

    public OrderMouldAdapter(Context context,int state,PopOnClickCallback clickCallback) {
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
                double price = Double.valueOf(entity.getPrice());

                tv_pricenew.setText(StringUtils.double2String(price,2)+"");
                tv_priceopen.setText(StringUtils.double2String(entity.getContractPriceOpen().doubleValue(),2)+"");

                double priceopen = entity.getContractPriceOpen().doubleValue();
                double ProfitLoss = 0;

                if (!"rise".equals(entity.getBuysType())){
                    tv_sell_moreorshort.setSelected(true);
                    tv_sell_moreorshort.setText(getContext().getString(R.string.tv_sell_short));
                    ProfitLoss = StringUtils.mul(
                            StringUtils.subDouble(priceopen,price),
                            entity.getContractNumber().doubleValue())*entity.getPryBar();
//                    tv_profitloss.setText(ProfitLoss+"");
                    tv_profitloss.setText(StringUtils.double2String(ProfitLoss,2)+"");
                }else{
                    tv_sell_moreorshort.setSelected(false);
                    tv_sell_moreorshort.setText(getContext().getString(R.string.tv_sell_more));
                    ProfitLoss = StringUtils.mul(
                            StringUtils.subDouble(price,priceopen),
                            entity.getContractNumber().doubleValue())*entity.getPryBar();
                    tv_profitloss.setText(StringUtils.double2String(ProfitLoss,2)+"");
                }
//                Log.e("cje>>>","priceopen: "+priceopen);
//                Log.e("cje>>>","price: "+price);
//                Log.e("cje>>>","ContractNumber: "+StringUtils.getDouble2(entity.getContractNumber().doubleValue()));
//                Log.e("cje>>>","tv_profitloss: "+ProfitLoss);
//                Log.e("cje>>>","tv_profitloss2: "+String.format("%.2f",ProfitLoss));
                if (ProfitLoss >= 0) {
                    tv_profitloss.setTextColor(getContext().getResources().getColor(R.color.txt_color_4ead));
                    double profitYl = entity.getMoney().intValue() * entity.getProfit();
//                    Log.e("cje>>>","ProfitLoss: "+ProfitLoss+" profitYl "+profitYl);
                    if (0!=ProfitLoss&&ProfitLoss>=profitYl){
//                        Log.e("cje>>>","ProfitLoss: "+ProfitLoss+" profitYl "+profitYl);
                        mClickCallback.onPC(getPosition());
                    }
                    if (clickPosition == getPosition()){
                        mClickCallback.onProfitLoss(ProfitLoss);
                    }
                } else {
                    tv_profitloss.setTextColor(getContext().getResources().getColor(R.color.txt_color_b44c));
                    double lossYl =0 - (entity.getMoney().intValue() * entity.getLoss());
                    if (0!=ProfitLoss&&ProfitLoss<=lossYl){
                        mClickCallback.onPC(getPosition());
                    }
                    if (clickPosition == getPosition()){
//                        Log.e("cje>>>>","clickPosition: "+clickPosition+" getPosition() "+getPosition());
                        mClickCallback.onProfitLoss(ProfitLoss);
                    }

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

                tv_price_title.setText(getContext().getResources().getString(R.string.tv_price_dq));
                tv_pc_time.setVisibility(View.GONE);
                tv_pc.setVisibility(View.VISIBLE);
                tv_setting.setVisibility(View.VISIBLE);

                tv_pc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mClickCallback.onPC(entity.getId(),getPosition());
                    }
                });
                tv_setting.setOnClickListener(new NoMoreClickListener() {
                    @Override
                    protected void OnMoreClick(View view) {
                        clickPosition = getPosition();
                        mClickCallback.onSet(entity.getId(),getPosition(),entity.getProfit(),entity.getLoss());
                    }

                    @Override
                    protected void OnMoreErrorClick() {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    /**
     * 成功
     */
    public interface PopOnClickCallback {
        void onPC(String id,int positiion);
        void onSet(String id,int positiion,double profit ,double loss);
        void onPC(int positiion);
        void onProfitLoss(double ProfitLoss);  //盈利
    }
}