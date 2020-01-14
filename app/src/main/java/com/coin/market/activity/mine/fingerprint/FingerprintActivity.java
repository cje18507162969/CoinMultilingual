package com.coin.market.activity.mine.fingerprint;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.databinding.ActivityFingerprintLayoutBinding;
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import teng.wang.comment.SPConstants;
import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.utils.DataKeeper;

/**
 * @author: Lenovo
 * @date: 2019/8/6
 * @info: 指纹解锁
 */
public class FingerprintActivity extends BaseActivity implements View.OnClickListener {

    private ActivityFingerprintLayoutBinding binding;
    private FingerprintViewModel model;
    private FingerprintIdentify identify;
    private Context context;
    public String verify; // 是否是验证 如果穿过来是verify 说明是验证

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, FingerprintActivity.class));
    }

    public static void JUMP(Context context, String verify) {
        context.startActivity(new Intent(context, FingerprintActivity.class).putExtra("verify", verify));
    }

    @Override
    protected void initIntentData() {
        verify = getIntent().getStringExtra("verify");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        context = this;
        EventBus.getDefault().register(this);
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fingerprint_layout);
        model = new FingerprintViewModel(this, binding);
        binding.fingerprintSayHelloName.setText("Hi," + FaceApplication.isLoginPhone());
        if (!TextUtils.isEmpty(verify)){
            binding.fingerprintCancelButton.setVisibility(View.GONE);
        }
        initFingerprint();
        initDialog();
    }

    private void initFingerprint(){
        identify = new FingerprintIdentify(this);                       // 构造对象
        identify.isFingerprintEnable();                                 // 指纹硬件可用并已经录入指纹
        identify.isHardwareEnable();                                    // 指纹硬件是否可用
        identify.isRegisteredFingerprint();                             // 是否已经录入指纹

        identify.cancelIdentify();                                      // 关闭指纹识别
        identify.resumeIdentify();                                      // 恢复指纹识别并保证错误次数不变

        // 开始验证指纹识别
        identify.startIdentify(40, new BaseFingerprint.FingerprintIdentifyListener() {
            @Override
            public void onSucceed() {
                Toast.makeText(mContext, "指纹识别成功", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post("FingerprintSuccess");
                DataKeeper.put(context, SPConstants.MBR_FINGERPRINT, "1");
                String a = DataKeeper.get(context, SPConstants.MBR_FINGERPRINT , "");
                EventBus.getDefault().post("VerifySuccess");
                finish();
            }

            @Override
            public void onNotMatch(int availableTimes) {
                //Toast.makeText(mContext, "指纹不匹配", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(boolean isDeviceLocked) {
                //Toast.makeText(mContext, "指纹识别失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartFailedByDeviceLocked() {
                //Toast.makeText(mContext, "onStartFailedByDeviceLocked", Toast.LENGTH_SHORT).show();
            }
        });

        // 构造对象，并监听错误回调（错误仅供开发使用）
        identify = new FingerprintIdentify(this, new BaseFingerprint.FingerprintIdentifyExceptionListener() {
            @Override
            public void onCatchException(Throwable exception) {
                Toast.makeText(mContext, "错误监听：" + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void setListener() {
        binding.fingerprintCancelButton.setOnClickListener(this);
        binding.fingerprintLoginButton.setOnClickListener(this);
        binding.fingerprintMoreLoginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fingerprint_cancel_button: //退出
                finish();
                break;
            case R.id.fingerprint_login_button: //指纹
                initDialog();
                break;
            case R.id.fingerprint_more_login_button: //其他账号

                break;
            default:
                break;
        }
    }

    private void initDialog(){
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("指纹登录")
                .setMessage("请验证指纹")
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                })
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create().show();
    }

    /**
     *
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void finish(String str) {
        try {
            if (!TextUtils.isEmpty(str) && str.equals("FingerprintSuccess")) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听手机返回按钮
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!TextUtils.isEmpty(verify)){

            }else {
                finish();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
