package com.coin.market.activity.mine.security;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.activity.mine.bindemail.BindEmailActivity;
import com.coin.market.activity.mine.fingerprint.FingerprintActivity;
import com.coin.market.activity.mine.gesture.GestureActivity;
import com.coin.market.activity.mine.googleqr.GoogleQrActivity;
import com.coin.market.activity.mine.paypass.PayPassActivity;
import com.coin.market.databinding.ActivitySecurityCentreLayoutBinding;
import com.coin.market.util.DensityUtil;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.androidUtils;
import com.coin.market.wight.dialog.EditPasswordDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import teng.wang.comment.SPConstants;
import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.utils.DataKeeper;

/**
 * @author: Lenovo
 * @date: 2019/7/29
 * @info: 个人中心 安全中心
 */
public class SecurityCentreActivity extends BaseActivity implements View.OnClickListener {

    public int complete = 20;
    private ActivitySecurityCentreLayoutBinding binding;
    private SecurityCentreViewModel model;
    private boolean gesture, fingerprint;
    private int verifyMethod; // 0是都没开 1是开了手势  2是开了指纹登录  （初始化是0）
    private int jump;
    private Context context;

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, SecurityCentreActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        EventBus.getDefault().register(this);
        context = this;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_security_centre_layout);
        model = new SecurityCentreViewModel(this, binding);
        setSecurityLength(complete);
        if (!TextUtils.isEmpty(DataKeeper.get(context, SPConstants.MBR_VERIFY_METHOD, ""))) {
            if (DataKeeper.get(context, SPConstants.MBR_VERIFY_METHOD, "").equals("1")){
                // 手势
                verifyMethod = 1;
                gesture = true;
            }else if (DataKeeper.get(context, SPConstants.MBR_VERIFY_METHOD, "").equals("2")){
                // 指纹
                verifyMethod = 2;
                fingerprint = true;
            }
            selectVerify();
        }
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(FaceApplication.getMobile())){
            binding.securityCentrePhoneText.setText(FaceApplication.getMobile());
        }else{
            binding.securityCentrePhoneImg.setVisibility(View.VISIBLE);
            binding.securityCentrePhoneText.setText(getResources().getString(R.string.mine_identity_go_verify_text));
        }

    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.securityCentrePhoneButton.setOnClickListener(this);
        binding.securityFingerprintImg.setOnClickListener(this);
        binding.securityGestureImg.setOnClickListener(this);
        binding.securityCentreEmailButton.setOnClickListener(this);
        binding.securityCentreGoogleButton.setOnClickListener(this);
        binding.securityCentrePayButton.setOnClickListener(this);
    }


    /**
     *  选择验证方式
     */
    private void showconfirmDialog() {
        EditPasswordDialog dialog = new EditPasswordDialog(this);
        dialog.builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true)
                .setOnClickListener(new EditPasswordDialog.OnPassClickListener() {
                    @Override
                    public void onClick(String str) {
                        if (!FaceApplication.getUserInfoModel().getPassword().equals(str)){//如果密码不正确 没有后面的操作
                            Toast.makeText(context, "密码不正确！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (jump == 1) {
                            // 图形解锁
                            if (EmptyUtil.isEmpty(DataKeeper.get(context, SPConstants.MBR_GESTURRE, ""))){//如果没设置手势
                                GestureActivity.JUMP(context);
                            }else {//如果设置了手势
                                if (gesture) { //如果是打开状态 那么关闭
                                    gesture = false;
                                } else { //如果是关闭 那么打开
                                    gesture = true;
                                    fingerprint = false;
                                }
                            }
                        } else {
                            // 指纹解锁
                            if (TextUtils.isEmpty(DataKeeper.get(context, SPConstants.MBR_FINGERPRINT, ""))){ //如果没有设置指纹
                                //去开启指纹
                                FingerprintActivity.JUMP(context);
                            }else {// 如果有指纹开启指纹验证
                                if (fingerprint) { //如果是打开状态 那么关闭
                                    fingerprint = false;
                                } else { //如果是关闭 那么打开
                                    fingerprint = true;
                                    gesture = false;
                                }
                            }

                        }
                        selectVerify();
                    }
                });
        dialog.show();
    }

    //设置安全红线的 百分比   这里的int值 length  给百分比就好了
    public void setSecurityLength(int length) {
        RelativeLayout.LayoutParams params_1 = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, DensityUtil.dip2px(this, 3));
        params_1.width = ((androidUtils.getMobileWidth(this) - (DensityUtil.px2dip(this, 24))) * length) / 100;

        int pm = androidUtils.getMobileWidth(this);
        int px = DensityUtil.dip2px(this, 24);
        int line = androidUtils.getMobileWidth(this) - (DensityUtil.dip2px(this, 24));
        //int dp = DensityUtil.px2dip(this,pm-line);
        params_1.width = line * length / 100;
        binding.securityLength.setLayoutParams(params_1);
        if (length >= 80) {
            binding.dangerText.setText("高");
            binding.dangerText.setTextColor(this.getResources().getColor(R.color.app_home_coin_text_color));
        } else if (length > 40) {
            binding.dangerText.setText("中");
            binding.dangerText.setTextColor(this.getResources().getColor(R.color.color_999999));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_Return:
                finish();
                break;
            case R.id.security_centre_phone_button:
//                BindEmailActivity.JUMP(this, "phone"); // 手机登录
                break;
            case R.id.security_centre_email_button:
                BindEmailActivity.JUMP(this, "email"); // 邮箱登录
                break;
            case R.id.security_centre_google_button:
                GoogleQrActivity.JUMP(this);
                break;
            case R.id.security_gesture_img: // 手势密码
                jump = 1;
                showconfirmDialog();
                break;
            case R.id.security_fingerprint_img: // 指纹登录
                jump = 2;
                showconfirmDialog();
                break;
            case R.id.security_centre_pay_button: // 设置支付密码
                PayPassActivity.JUMP(this);
                break;
            default:
                break;
        }
    }

    private void selectVerify() {
        if (gesture) {
            verifyMethod = 1;
            binding.securityGestureImg.setImageResource(R.drawable.mine_switchon);
        } else {
            binding.securityGestureImg.setImageResource(R.drawable.mine_switchoff);
        }
        if (fingerprint) {
            if (EmptyUtil.isEmpty(DataKeeper.get(context, SPConstants.MBR_GESTURRE))){
                verifyMethod = 2;
                binding.securityFingerprintImg.setImageResource(R.drawable.mine_switchon);
            }
        } else {
            binding.securityFingerprintImg.setImageResource(R.drawable.mine_switchoff);
        }
        if (!gesture&&!fingerprint){
            verifyMethod = 0;
        }
        DataKeeper.put(context, SPConstants.MBR_VERIFY_METHOD, verifyMethod +"");
    }

    /**
     * 绑定邮箱成功 刷新安全中心数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bindEmail(String model) {
        try {
            if (null != model && model.equals("BindEmail")) {
                this.model.getUserInfo(FaceApplication.getToken());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 绑定谷歌验证器成功 刷新安全中心数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bindGoogle(String model) {
        try {
            if (null != model && model.equals("BindGoogle")) {
                this.model.getUserInfo(FaceApplication.getToken());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开手势验证
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void festure(String str) {
        try {
            if (null != str && str.equals("GestureSuccess")) {
                verifyMethod = 1;
                gesture = true;
                fingerprint = false;
                selectVerify();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开指纹识别验证
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fingerprint(String str) {
        try {
            if (null != str && str.equals("FingerprintSuccess")) {
                verifyMethod = 2;
                gesture = false;
                fingerprint = true;
                selectVerify();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
