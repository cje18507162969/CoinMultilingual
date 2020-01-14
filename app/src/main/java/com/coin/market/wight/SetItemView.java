package com.coin.market.wight;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coin.market.R;


/**
 * 我的界面 设置itme
 *
 * @author Rock
 * @version v1.0
 * @Time 2018-9-3
 */
public class SetItemView extends LinearLayout {

    private TextView setItemText;
    private ImageView setItemImage;
//    private LinearLayout mLinearLayout;

    public SetItemView(Context context) {
        super(context);
    }

    // 如果View是在.xml里声明的，则调用第二个构造函数
    // 自定义属性是从AttributeSet参数传进来的
    public SetItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.view_setitme, this, true);
        setItemImage = findViewById(R.id.setItemImage);
        setItemText = findViewById(R.id.setItmeText);
//        mLinearLayout = findViewById(R.id.rootLayout);
        initTypedArray(context,attrs);
    }

    private void initTypedArray(Context context,AttributeSet attrs){
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SetItemView);
        setItemText.setText(ta.getString(R.styleable.SetItemView_setText));
        setItemImage.setImageResource(ta.getResourceId(R.styleable.SetItemView_setSrc, R.mipmap.ic_launcher));

        ta.recycle();
    }

}
