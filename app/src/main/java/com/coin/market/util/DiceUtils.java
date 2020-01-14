package com.coin.market.util;

import com.coin.market.R;

public class DiceUtils {

    public static int getDrawable(int chip) {
        if (chip == 0) {
            return 0;
        }
        int resources = 0;
        switch (chip) {
            case 5:
                resources = R.drawable.orange_selected;
                break;
            case 10:
                resources = R.drawable.blue_selected;
                break;
            case 50:
                resources = R.drawable.purple_selected;
                break;
            case 100:
                resources = R.drawable.pink_selected;
                break;
            case 500:
                resources = R.drawable.red_selected;
                break;
        }
        return resources;
    }

    public static int getResouce(int chip) {
        if (chip == 0) {
            return 0;
        }
        int resources = 0;
        switch (chip) {
            case 1:
                resources = R.drawable.one;
                break;
            case 2:
                resources = R.drawable.two;
                break;
            case 3:
                resources = R.drawable.three;
                break;
            case 4:
                resources = R.drawable.four;
                break;
            case 5:
                resources = R.drawable.five;
                break;
            case 6:
                resources = R.drawable.six;
                break;
            default:
                break;
        }
        return resources;
    }

}
