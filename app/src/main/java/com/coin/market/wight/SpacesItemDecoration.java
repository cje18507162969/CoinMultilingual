package com.coin.market.wight;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author: cjh
 * @date: 2019/10/23
 * @info: 设置 RecyclerView 间距  （多用于GridLayoutManager 、 StaggeredGridLayoutManager）
 */

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.bottom = space;
        outRect.top = space;

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildPosition(view) == 0)
            outRect.right = space;
    }
}