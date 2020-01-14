package com.coin.market.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.coin.market.util.EmptyUtil;


/**
 * Created by LiYang on 2019/1/14.
 */

public class DiceAddView extends RelativeLayout{

    private Context context;
    private DiceView diceView;
    private int Total;
    private int number;
    private int img = 0;
    private static onClickButton listener;

    public DiceAddView(Context context, int number, int img, onClickButton listener) {
        super(context);
        this.context = context;
        this.number = number;
        this.img = img;
        this.listener = listener;
        init();
    }

    public DiceAddView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public void setListener(onClickButton listener) {
        this.listener = listener;
    }

    private void init(){
        Total = number;
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!EmptyUtil.isEmpty(listener)){
                    listener.onClick(Total);
                }
            }
        });
    }

    //此方法 如果更改了筹码    只使用第一次给进来的筹码  设置值
    public void addNumber(int numb, int img){
        if (this.img != img && this.img==0){
            this.img = img;
            initChip(Total, img);
        }
        if (numb!=number){
            number = numb;
            Total = Total+number;
        }else {
            Total+=numb;
        }
        setChip(Total);
    }

    public int getMoney(){
        return this.Total;
    }

    public void setMoney(int money){
        this.Total = money;
    }

    public interface onClickButton{
        void onClick(int Numb);
    }

    public void initChip(int number, int img){
        diceView = new DiceView(context, number+"", img);
        this.addView(diceView);
    }

    private void setChip(int number){
        diceView.setTitle(number+"");
    }

    public void removeView(){
        if (!EmptyUtil.isEmpty(diceView)){
            this.removeView(diceView);
            Total = 0;
            number = 0;
            img = 0;
        }

    }

}
