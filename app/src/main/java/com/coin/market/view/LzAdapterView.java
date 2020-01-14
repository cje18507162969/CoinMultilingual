package com.coin.market.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coin.market.R;


/**
 * Created by LiYang on 2019/1/14.
 */

public class LzAdapterView extends LinearLayout {

    private int color;
    private Context context;
    private String number;

    public LzAdapterView(Context context, int color, String number) {
        super(context);
        this.context = context;
        this.color = color;
        this.number = number;
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contextView = inflater.inflate(R.layout.view_lz_adapter_layout, this);
        LinearLayout mLin_news_details = contextView.findViewById(R.id.view_lz_adapter_layout);
        TextView view_lz_adapter_text = contextView.findViewById(R.id.view_lz_adapter_text);
        if (!TextUtils.isEmpty(number)){
            view_lz_adapter_text.setText(number);
        }
        switch (color) {
            case 1:
                //红色
                view_lz_adapter_text.setBackgroundResource(R.drawable.shape_lz_red_layout);
                break;
            case 2:
                //蓝色
                view_lz_adapter_text.setBackgroundResource(R.drawable.shape_lz_blue_layout);
                break;
            case 3:
                //绿色
                view_lz_adapter_text.setBackgroundResource(R.drawable.shape_lz_green_layout);
                break;
            default:
                break;
        }

    }

}
