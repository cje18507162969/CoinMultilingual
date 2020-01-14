package com.coin.market.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coin.market.R;


public class DiceView extends RelativeLayout {

    private Context context;
    private int resource;
    private String text;
    private TextView dice_tv;
    private RelativeLayout dice_rl,dice_layout;

    public DiceView(Context context, String text, int resource) {
        super(context);
        this.context = context;
        this.text = text;
        this.resource = resource;
        init();
    }

    private void init(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contextView = inflater.inflate(R.layout.view_dice_view, this);

        this.setBackgroundColor(context.getResources().getColor(R.color.transparent_while_90));

        dice_tv = contextView.findViewById(R.id.dice_tv);
        dice_rl = contextView.findViewById(R.id.dice_rl);
        dice_layout = contextView.findViewById(R.id.dice_layout);
        if (!TextUtils.isEmpty(text)){
            dice_tv.setText(text+"");
        }
        dice_rl.setVisibility(GONE);
        if (resource!=0){
            dice_rl.setBackgroundResource(resource);
            dice_rl.setVisibility(VISIBLE);
        }
    }

    /**
     *      修改筹码的数值
     */
    public void setTitle(String text) {
        this.text = text;
        dice_tv.setText(text);
    }

    /**
     *      修改筹码样式
     */
    public void setResource(int resource) {
        dice_rl.setBackgroundResource(resource);
    }

    public void removeChip(){
        this.removeView(dice_layout);
    }

}

