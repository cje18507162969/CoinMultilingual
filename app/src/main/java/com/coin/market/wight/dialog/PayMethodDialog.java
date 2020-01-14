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
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.adapter.SelectAdapter;
import com.coin.market.util.EmptyUtil;

import java.util.List;

import teng.wang.comment.model.PayMethodsBean;

/**
 * @author  从下方弹出  列表选择item
 * @version v1.0
 * @Time 2018-8-22
 */
public class PayMethodDialog implements OnClickListener {

    private Context context;
    private Dialog dialog;
    private List<PayMethodsBean> pays;
    private TextView txt_cancel;
    private Display display;

    public PayMethodDialog(Context context, List<PayMethodsBean> pays) {
        this.context = context;
        this.pays = pays;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public void show() {
        dialog.show();
    }

    @SuppressWarnings("deprecation")
    public PayMethodDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(
                R.layout.exchange_dialog_layout, null);

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
        SelectAdapter adapter = new SelectAdapter(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        if (!EmptyUtil.isEmpty(pays)){
            adapter.addAll(pays);
        }
        adapter.setListener(new SelectAdapter.mOnClickCallback() {
            @Override
            public void OnClick(PayMethodsBean bean) {
                if (!EmptyUtil.isEmpty(onItemClickListener)){
                    onItemClickListener.onClick(bean);
                }
                dialog.dismiss();
            }
        });

        return this;
    }

    public PayMethodDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public PayMethodDialog setCanceledOnTouchOutside(boolean cancel) {
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
        void onClick(PayMethodsBean bean);
    }

}