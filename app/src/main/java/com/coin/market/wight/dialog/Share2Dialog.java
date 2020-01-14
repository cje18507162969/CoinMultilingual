package com.coin.market.wight.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.EmptyUtil;

import teng.wang.comment.utils.ZXingUtils;


/**
 * @author: Lenovo
 * @date: 2019/8/6
 * @info: 安全验证  登录密码
 */
public class Share2Dialog implements View.OnClickListener {

    private Context context;
    private Dialog dialog;
    private ImageView cancel, share_qr;
    private Display display;
    private String url;

    public Share2Dialog(Context context, String url) {
        this.context = context;
        this.url = url;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    @SuppressWarnings("deprecation")
    public Share2Dialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_share_layout, null);

        // 设置Dialog最小宽度为屏幕宽度
        view.setMinimumWidth(display.getWidth());

        share_qr = view.findViewById(R.id.share_qr);
        cancel = view.findViewById(R.id.share_cancel);
        cancel.setOnClickListener(this);

        share_qr.setImageBitmap(ZXingUtils.createQRImage(url, 300, 300));

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
        return this;
    }

    public Share2Dialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public Share2Dialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.share_cancel:
                dialog.dismiss();
                break;
            default:
                break;

        }
    }

}
