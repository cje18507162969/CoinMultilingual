package com.coin.market.wight;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeText extends TextView {

    public MarqueeText(Context context) {
        super(context, null);
    }

    public MarqueeText(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public MarqueeText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //返回textview是否处在选中的状态
    //而只有选中的textview才能够实现跑马灯效果
    @Override
    public boolean isFocused() {
        return true;
    }

}
