package com.coin.market.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by jkloshhm on 2017/11/24.
 *
 * @author jkloshhm
 */

public class MScrollView extends ScrollView {

    private OnScrollListener mOnScrollListener;

    public MScrollView(Context context) {
        super(context);
    }

    public MScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 监听ScroView的滑动情况
     *
     * @param l    变化后的X轴位置
     * @param t    变化后的Y轴的位置
     * @param oldl 原先的X轴的位置
     * @param oldt 原先的Y轴的位置
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (null != mOnScrollListener) {
            mOnScrollListener.onScroll(t);
        }
    }


    /**
     * 设置滚动接口
     *
     * @param listener
     */
    public void setOnScrollListener(OnScrollListener listener) {
        this.mOnScrollListener = listener;
    }

    /**
     * 滚动的回调接口
     */
    public interface OnScrollListener {
        /**
         * MScrollView滑动的Y方向距离变化时的回调方法
         *
         * @param scrollY
         */
        void onScroll(int scrollY);

    }
}