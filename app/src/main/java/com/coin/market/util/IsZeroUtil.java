package com.coin.market.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by LiYang on 2018/10/24.
 */

public class IsZeroUtil {

    /**
     *     EditText 判断String是否大于0
     */
    public static void isZero(Context context, String s, EditText editText) {
        if (!TextUtils.isEmpty(s)){
            Double bumber = Double.parseDouble(s +"");
            if (bumber<=0){
                Toast.makeText(context, "请输入正确数量!", Toast.LENGTH_SHORT).show();
                if (editText!=null){
                    editText.setText("");
                }
            }

        }
    }

}
