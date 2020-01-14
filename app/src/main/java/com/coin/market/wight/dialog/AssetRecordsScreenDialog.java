package com.coin.market.wight.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.adapter.ExchangeAdapter;
import com.coin.market.adapter.ScreenAdapter;
import com.coin.market.util.EmptyUtil;

import java.util.List;

import teng.wang.comment.model.ExchangeDialogModel;

/**
 * @author: Lenovo
 * @date: 2019/8/5
 * @info:  财务记录 筛选弹窗
 */
public class AssetRecordsScreenDialog implements View.OnClickListener {

    private Context context;
    private Dialog dialog;
    private List<ExchangeDialogModel> modelList;
    private TextView txt_cancel;
    private Display display;
    private boolean isClickCancl = false;

    public AssetRecordsScreenDialog(Context context, List<ExchangeDialogModel> modelList) {
        this.context = context;
        this.modelList = modelList;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public boolean isClickCancl() {
        return isClickCancl;
    }

    public void show() {
        dialog.show();
    }

    @SuppressWarnings("deprecation")
    public AssetRecordsScreenDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.view_screen_dialog_layout, null);

        // 设置Dialog最小宽度为屏幕宽度
        view.setMinimumWidth(display.getWidth());

        txt_cancel = (TextView) view.findViewById(R.id.txt_cancel);
        txt_cancel.setOnClickListener(this);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);

        RecyclerView recyclerView = view.findViewById(R.id.money_exchange_recycler);
        ScreenAdapter adapter = new ScreenAdapter(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        if (!EmptyUtil.isEmpty(modelList)){
            adapter.addAll(modelList);
        }

        adapter.setListener(new ScreenAdapter.mOnClickCallback() {
            @Override
            public void selectItem(String title) {
                if (!EmptyUtil.isEmpty(onItemClickListener)){
                    onItemClickListener.onClick(title);
                    dialog.dismiss();
                }
                //call = title;
            }
        });

        return this;
    }

    public AssetRecordsScreenDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public AssetRecordsScreenDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_cancel:
                dialog.dismiss();
                break;
            default:
                break;
        }
    }

    private ExchangeDialog.OnItemClickListener onItemClickListener;

    public void setOnClickListener(ExchangeDialog.OnItemClickListener onPosNegClickListener) {
        this.onItemClickListener = onPosNegClickListener;
    }

    public interface OnClickListener {
        void onClick(String title);
    }

}
