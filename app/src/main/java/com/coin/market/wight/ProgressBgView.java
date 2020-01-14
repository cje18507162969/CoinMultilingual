package com.coin.market.wight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.coin.market.R;


/**
 * 伸缩背景
 */
public class ProgressBgView extends View {


    public static final int LEFT = 0;
    public static final int TOP = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 3;
    private RectF rectF;
    private Paint p;
    private int gravity;
    private float progress;

    public ProgressBgView(Context context) {
        super(context);
    }

    public void setProgress(float progress){
        this.progress = progress;
        if (progress < 0f )this.progress = 0;
        if (progress > 1f )this.progress = 1f;
        postInvalidate();
    }

    public void setBgColor(@ColorInt int color){
        p.setColor(color);
        postInvalidate();
    }

    public ProgressBgView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);

    }



    public ProgressBgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs) {
        rectF = new RectF();
        p = new Paint();
        if (null != attrs){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressBgView);
            gravity = typedArray.getInteger(R.styleable.ProgressBgView_gravity , LEFT);
            p.setColor(typedArray.getColor(R.styleable.ProgressBgView_bg_color , getResources().getColor(R.color.app_home_coin_text_color_red)));
            typedArray.recycle();

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(upRectF() , p);
    }

    private RectF upRectF() {
        switch (gravity){
            case LEFT:
                rectF.set(0,0,getWidth()*progress,getHeight());
                break;
            case TOP:
                rectF.set(0,0,getWidth(),getHeight()*progress);
                break;
            case RIGHT:
                rectF.set(getWidth()*(1-progress) , 0 , getWidth() , getHeight());
                break;
            case BOTTOM:
                rectF.set(0,getHeight()*(1-progress) , getWidth() , getHeight());
                break;
            default://瞎填数字当做LEFT处理
                rectF.set(0,0,getWidth()*progress,getHeight());
                break;
        }
        return rectF;
    }


}
