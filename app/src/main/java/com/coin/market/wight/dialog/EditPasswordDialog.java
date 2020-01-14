package com.coin.market.wight.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.EmptyUtil;


/**
 * @author: Lenovo
 * @date: 2019/8/6
 * @info: 安全验证  登录密码
 */
public class EditPasswordDialog implements View.OnClickListener {

    private Context context;
    private Dialog dialog;
    private TextView txt_cancel;
    private Button confirm;
    private EditText editText;
    private Display display;

    private OnPassClickListener onItemClickListener;

    public EditPasswordDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public void setOnClickListener(OnPassClickListener onClickListener) {
        this.onItemClickListener = onClickListener;
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    @SuppressWarnings("deprecation")
    public EditPasswordDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.view_edit_password_dialog_layout, null);

        // 设置Dialog最小宽度为屏幕宽度
        view.setMinimumWidth(display.getWidth());

        txt_cancel = view.findViewById(R.id.edit_password_cancel_button);
        editText = view.findViewById(R.id.edit_password_edit);
        confirm = view.findViewById(R.id.edit_password_confirm_button);
        txt_cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence)) {
                    confirm.setAlpha(1.0f);
                    confirm.setEnabled(true);
                } else {
                    confirm.setAlpha(0.4f);
                    confirm.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return this;
    }

    public EditPasswordDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public EditPasswordDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_password_cancel_button:
                dialog.dismiss();
                break;
            case R.id.edit_password_confirm_button:
                if (!EmptyUtil.isEmpty(onItemClickListener)) {
                    onItemClickListener.onClick(editText.getText().toString().trim());
                    dismiss();
                }
                break;
            default:
                break;

        }
    }

    public interface OnPassClickListener {
        void onClick(String str);
    }

}
