package com.coin.market.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;
import com.coin.market.wight.ProgressBgView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.DepthItemModel;
import teng.wang.comment.model.UpAndDownEntity;

/**
 * @author Quotation 行情
 * @version v1.0
 * @Time 2019-7-30
 */

public class UpAndDownAdapter extends RecyclerArrayAdapter<DepthItemModel> {

    private static itemClick listener;
    private static double maxNumbBuy, maxNumbSell;
    public static int priceScale;
    public static int amountScale;

    public void getScale(int priceScale, int amountScale){
        this.priceScale = priceScale;
        this.amountScale = amountScale;
    }

    public UpAndDownAdapter(Context context) {
        super(context);
    }

    public void setListener(itemClick listener) {
        this.listener = listener;
    }

    public void setMaxNumbBuy(double maxNumb) {
        this.maxNumbBuy = maxNumb;
    }

    public void setMaxNumbSell(double maxNumb) {
        this.maxNumbSell = maxNumb;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<DepthItemModel> {

        private RelativeLayout layout;
        private TextView price, numb;
        private ProgressBgView progress;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_up_down_layout);
            price = $(R.id.up_down_price);
            numb = $(R.id.up_down_numb);
            layout = $(R.id.up_down_layout);
            progress = $(R.id.bg_progress_view);
        }

        @Override
        public void setData(final DepthItemModel entity) {
            try {
//                price.setText(StringUtils.double2String(entity.getPrice(), entity.getPriceScale())+"");
//                numb.setText(StringUtils.double2String(entity.getNumber(), entity.getAmountScale())+"");
                price.setText(entity.getPrice()+"");
                numb.setText(entity.getNumber()+"");
                if (entity.getType()==0){
                    price.setTextColor(getContext().getResources().getColor(R.color.app_home_coin_text_color));
                }else {
                    price.setTextColor(getContext().getResources().getColor(R.color.app_home_coin_text_color_red));
                }
                if (entity.getType() == 0){
                    if (maxNumbBuy>0){
                        progress.setBgColor(getContext().getResources().getColor(entity.getType()==0 ? R.color.app_home_coin_text_color_t : R.color.app_home_coin_text_color_red_t));
                        progress.setProgress( 0D == maxNumbBuy ? 0f : (float) (entity.getNumber() / maxNumbBuy));
                    }
                }else {
                    if (maxNumbSell>0){
                        progress.setBgColor(getContext().getResources().getColor(entity.getType()==0 ? R.color.app_home_coin_text_color_t : R.color.app_home_coin_text_color_red_t));
                        progress.setProgress( 0D == maxNumbSell ? 0f : (float) (entity.getNumber() / maxNumbSell));
                    }
                }

                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!EmptyUtil.isEmpty(listener)){
                            listener.Click(entity);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface itemClick {
        void Click(DepthItemModel itemModel);
    }

}
