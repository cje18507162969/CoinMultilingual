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
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.util.EmptyUtil;

import okhttp3.RequestBody;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.api.RequestBodyUtils;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.TimerButton;


/**
 * @author: Lenovo
 * @date: 2019/8/6
 * @info: 提币 验证码
 */
public class GetCoinDialog implements View.OnClickListener {

    private Context context;
    private Dialog dialog;
    private TextView txt_cancel, phone;
    private Button confirm;
    private TimerButton timerButton;
    private EditText editText;
    private Display display;
    private String tag;

    private OnPassClickListener onItemClickListener;

    public GetCoinDialog(Context context, String tag) {
        this.context = context;
        this.tag = tag;
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
    public GetCoinDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.view_get_coin_code_dialog_layout, null);

        // 设置Dialog最小宽度为屏幕宽度
        view.setMinimumWidth(display.getWidth());

        txt_cancel = view.findViewById(R.id.edit_password_cancel_button);
        editText = view.findViewById(R.id.edit_password_edit);
        confirm = view.findViewById(R.id.edit_password_confirm_button);
        phone = view.findViewById(R.id.phone);
        timerButton = view.findViewById(R.id.mTimeBtn);
        txt_cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
        timerButton.setOnClickListener(this);

        phone.setText(FaceApplication.getMobile());

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

    public GetCoinDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public GetCoinDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    /**
     *   发送邮箱验证码
     */
    public void getCode(String token, String mobile) {
        if (EmptyUtil.isEmpty(tag)){
            return;
        }
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("mobile", mobile)
                .addParam("type", "export")
                .builder();
        FaceApiTest.getV1ApiServiceTest().sendMobileCode(body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(context, tag, true) {
                    @Override
                    public void onSuccess(String data) {
                        try {
                            timerButton.startTimer();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
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
            case R.id.mTimeBtn:
                getCode(FaceApplication.getToken(),FaceApplication.getMobile());
                break;
            default:
                break;

        }
    }

    public interface OnPassClickListener {
        void onClick(String str);
    }

}
