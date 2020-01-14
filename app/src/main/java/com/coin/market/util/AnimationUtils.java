package com.coin.market.util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.coin.market.R;

/**
 * Created by LiYang on 2018/12/26.
 */

public class AnimationUtils {

    /**
     * 控件放大动画  最初使用的是300 根据自己的需求 设置放大size
     */
    public static void followAnimation(View view, int size){
        ScaleAnimation anim = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(size);
        view.startAnimation(anim);
    }

    /**
     *  控件旋转动画  rotation是否是旋转  a b： 坐标轴  0f, 360f
     */
    public static void rotationAnimation(View view, String rotation, float a, float b) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, rotation, a, b);//以某点旋转360度
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animator);
        animatorSet.setDuration(300);
        animatorSet.start();//开始动画
    }

}
