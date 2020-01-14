package com.coin.market.util;

import android.app.Activity;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.coin.market.R;

/**
 * @author: Lenovo
 * @date: 2019/8/6
 * @info:
 */
public class androidUtils {

    /**
     * 工具类  震动
     */
    public static void vibrator(Context context, long time){
        Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(time);
    }

    /**
     * 工具类  获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getMobileWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels; // 得到宽度
        return width;
    }

    /**
     * 工具类  获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getMobileHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.heightPixels; // 得到宽度
        return width;
    }

    /**
     * 工具类  复制文字到剪切板
     *
     * @param context
     * @return
     */
    public static void getCopyText(Context context, String content) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", content);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        Toast.makeText(context,context.getResources().getString(R.string.forget_ycopy)+ content , Toast.LENGTH_SHORT).show();
    }

    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

}
