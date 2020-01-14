package com.coin.market.wight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.coin.market.R;

public class CusSeekBar extends View {

    private Context context;
    private AttributeSet attrs;

    public static float DENSITY;
    public static float textPaddingLeft;
    public static float textPaddingTop;
    public static float textPaddingRight;
    public static float textPaddingBottom;

    private RectF rectF = new RectF();
    private Path path = new Path();
    private Paint p = new Paint();
    private float indicatorR;//指示器半径
    private int themeColor;
    private int textColor;
    private float textSize;
    private float progressHeight;
    private int progress;

    public CusSeekBar(Context context) {
        super(context);
        init(context, null);
    }

    public CusSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CusSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        this.attrs = attrs;
        DENSITY = context.getResources().getDisplayMetrics().density;
        textPaddingLeft = DENSITY * 5;
        textPaddingTop = DENSITY * 1;
        textPaddingRight = DENSITY * 5;
        textPaddingBottom = DENSITY * 3;
        if (null != attrs) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CusSeekBar);
            indicatorR = typedArray.getDimension(R.styleable.CusSeekBar_indicator_r, getResources().getDimension(R.dimen.dp_5));
            themeColor = typedArray.getColor(R.styleable.CusSeekBar_theme_color, getResources().getColor(R.color.app_home_coin_text_color));
            textColor = typedArray.getColor(R.styleable.CusSeekBar_text_color, getResources().getColor(R.color.white));
            textSize = typedArray.getDimension(R.styleable.CusSeekBar_text_size_seek, getResources().getDimension(R.dimen.sp_10));
            progressHeight = typedArray.getDimension(R.styleable.CusSeekBar_progress_height, getResources().getDimension(R.dimen.dp_2));
            progress = typedArray.getInteger(R.styleable.CusSeekBar_progress, 0);
            progress = progress > 100 ? 100 : progress < 0 ? 0 : progress;
//            MyLog.i("CusSeekBar init = " + progress);
            typedArray.recycle();
        }
    }

    public void setColor(int type){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CusSeekBar);
        if (type==0) {
            themeColor = typedArray.getColor(R.styleable.CusSeekBar_theme_color, getResources().getColor(R.color.app_home_coin_text_color));
        }else {
            themeColor = typedArray.getColor(R.styleable.CusSeekBar_theme_color, getResources().getColor(R.color.app_home_coin_text_color_red));
        }
//        p.setColor(resources);
//        themeColor = resources;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int paddingTop = getPaddingTop();
        float p_s_y = textSize / 2 * 3 + indicatorR + textPaddingTop + textPaddingBottom + paddingTop;
        float psx = progress * (getWidth() - indicatorR * 2 - progressHeight) / 100f + indicatorR + progressHeight / 2f;

        //绘制进度条
        p.reset();
        p.setAntiAlias(true);
        p.setStrokeWidth(progressHeight);
        p.setColor(Color.parseColor("#C4C7C9"));
        canvas.drawLine(indicatorR, p_s_y, getWidth() - indicatorR, p_s_y, p);
        p.setColor(themeColor);
        canvas.drawLine(indicatorR, p_s_y, psx, p_s_y, p);

        //绘制指示器
        p.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(psx, p_s_y, indicatorR, p);
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.WHITE);
        canvas.drawCircle(psx, p_s_y, indicatorR - progressHeight / 2, p);

//        MyLog.i("CusSeekBar onDraw = " + progress);
        //绘制指示器上方文字以及背景
        String text = String.valueOf(progress + "%");
        p.setTextSize(textSize);
        float textMeasureWidth = p.measureText(text) + textPaddingLeft + textPaddingRight;
        float left = psx - textMeasureWidth / 2f;
        rectF.left = left < 0 ? 0 : left > getWidth() - textMeasureWidth ? getWidth() - textMeasureWidth : left;
        rectF.right = rectF.left + textMeasureWidth;
        rectF.top = textPaddingTop + paddingTop;
        rectF.bottom = rectF.top + textSize + textPaddingBottom;
        path.reset();
        path.moveTo(rectF.left, rectF.top + DENSITY * 3);
        path.lineTo(psx, p_s_y - indicatorR);
        path.lineTo(rectF.right, rectF.top + DENSITY * 3);
        //        path.close();
        path.addRoundRect(rectF, DENSITY * 3, DENSITY * 3, Path.Direction.CCW);

        p.setColor(themeColor);
        canvas.drawPath(path, p);
        //        canvas.drawRect(rectF,p);
        p.setColor(textColor);
        p.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(text, (rectF.left + rectF.right) / 2f, rectF.bottom - textPaddingBottom, p);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float psx = progress * (getWidth() - indicatorR * 2 - progressHeight) / 100f + indicatorR + progressHeight / 2f;
            if (Math.abs(psx - event.getX()) < DENSITY * 10) {
                getParent().requestDisallowInterceptTouchEvent(true);
            } else {
                return false;
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        MyLog.i("x = " + event.getX());
        float x = event.getX();
        if (x < indicatorR + progressHeight / 2f) {
            progress = 0;
        } else if (x >= getWidth() - indicatorR - progressHeight / 2f) {
            progress = 100;
        } else {
            progress = (int) ((x - indicatorR - progressHeight / 2f) * 100f / (getWidth() - indicatorR * 2 - progressHeight));
//            MyLog.i("CusSeekBar onTouchEvent = " + progress);
        }
        postInvalidate();
        if (null != listener)
            listener.onChange(progress);
        return true;
    }

    private OnChangeListener listener;

    public interface OnChangeListener {
        void onChange(int progress);
    }

    public void setListener(OnChangeListener listener) {
        this.listener = listener;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        postInvalidate();
//        if (null != listener)
//            listener.onChange(progress);
    }
}
