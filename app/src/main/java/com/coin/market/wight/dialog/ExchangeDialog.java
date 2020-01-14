package com.coin.market.wight.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.adapter.ExchangeAdapter;
import com.coin.market.util.EmptyUtil;

import java.util.List;

import teng.wang.comment.model.ExchangeDialogModel;

/**
 * @author  从下方弹出  列表选择item
 * @version v1.0
 * @Time 2018-8-22
 */
public class ExchangeDialog implements OnClickListener {

    private String call;
    private Context context;
    private Dialog dialog;
    private List<ExchangeDialogModel> modelList;
    private ExchangeDialogModel model;
    private TextView txt_cancel, title_1, title_2, content_1, content_2;
    private ImageView selected_1, selected_2;
    private RelativeLayout layout_1, layout_2;
    private LinearLayout lLayout_content;
    private Display display;
    private boolean isClickCancl = false;

    public ExchangeDialog(Context context, List<ExchangeDialogModel> modelList) {
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
    public ExchangeDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(
                R.layout.exchange_dialog_layout, null);

        // 设置Dialog最小宽度为屏幕宽度
        view.setMinimumWidth(display.getWidth());

        lLayout_content = (LinearLayout) view.findViewById(R.id.lLayout_content);
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
        ExchangeAdapter adapter = new ExchangeAdapter(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        if (!EmptyUtil.isEmpty(modelList)){
            adapter.addAll(modelList);
        }

        adapter.setListener(new ExchangeAdapter.mOnClickCallback() {
            @Override
            public void collectionItenDelete(String title) {
                if (!EmptyUtil.isEmpty(onItemClickListener)){
                    onItemClickListener.onClick(title);
                    dialog.dismiss();
                }
                //call = title;
            }
        });

        return this;
    }

    public ExchangeDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public ExchangeDialog setCanceledOnTouchOutside(boolean cancel) {
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

    private OnItemClickListener onItemClickListener;

    public void setOnClickListener(OnItemClickListener onPosNegClickListener) {
        this.onItemClickListener = onPosNegClickListener;
    }

    public interface OnItemClickListener {
        void onClick(String title);
    }

}