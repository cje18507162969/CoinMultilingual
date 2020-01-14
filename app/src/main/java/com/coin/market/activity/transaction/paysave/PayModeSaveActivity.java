package com.coin.market.activity.transaction.paysave;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coin.market.R;
import com.coin.market.databinding.ActivityPayModeSaveLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.utils.PictureSelectorUtil;

/**
 * @author: Lenovo
 * @date: 2019/8/2
 * @info: 保存添加的银行卡、支付宝、微信
 */
public class PayModeSaveActivity extends BaseActivity implements View.OnClickListener {

    private ActivityPayModeSaveLayoutBinding binding;
    private PayModeSaveViewModel model;
    public String PayMode; // 支付方式
    public List<LocalMedia> selectList;
    private File file;

    public static void JUMP(Context context, String PayMode) {
        context.startActivity(new Intent(context, PayModeSaveActivity.class).putExtra("PayMode", PayMode));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pay_mode_save_layout);
        model = new PayModeSaveViewModel(this, binding);

        if (!TextUtils.isEmpty(PayMode)) {
            setTypeUi(PayMode);
        }
    }

    @Override
    protected void initIntentData() {
        PayMode = getIntent().getStringExtra("PayMode");
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.payModeAddImg.setOnClickListener(this);
        binding.payModeSaveButton.setOnClickListener(this);
    }

    private void setTypeUi(String type) {
        switch (type) {
            case "unionpay":
                binding.payModeSaveTitle.setText("添加银行卡");
                binding.payModeSaveQrLayout.setVisibility(View.GONE);
                break;
            case "alipay":
                binding.payModeSaveTitle.setText("添加支付宝");
                binding.payModeSaveBankLayout.setVisibility(View.GONE);
                binding.payModeCodeText.setText("账号");
                binding.payModeCodeEdit.setHint("请输入账号");
                break;
            case "wxpay":
                binding.payModeSaveTitle.setText("添加微信");
                binding.payModeSaveBankLayout.setVisibility(View.GONE);
                binding.payModeCodeText.setText("账号");
                binding.payModeCodeEdit.setHint("请输入账号");
                break;
            default:
                break;
        }
    }

    /**
     * 图片选择回调结果
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    List<LocalMedia> localMedia = PictureSelector.obtainMultipleResult(data);
                    file = new File(localMedia.get(0).getPath());
                    Glide.with(this).load(localMedia.get(0).getPath()).into(binding.payModeAddImg);
                    //model.updateImage(file);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_Return:
                finish();
                break;
            case R.id.pay_mode_add_img:
                selectList = new ArrayList<>();
                PictureSelectorUtil.startPictureSelectorActivity(this, 1, selectList);
                break;
            case R.id.pay_mode_save_button:
                // 保存
                if (TextUtils.isEmpty(binding.payModeNameEdit.getText().toString().trim())){
                    Toast.makeText(mContext, "请输入姓名", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (PayMode.equals("unionpay")){
                    model.addPayMode(FaceApplication.getToken(), PayMode,   // 收款方式
                            binding.payModeNameEdit.getText().toString().trim(),// 名称
                            binding.payModeCodeEdit.getText().toString(),// 账号
                            binding.payBankCodeEdit.getText().toString(),// 开户行
                            binding.payAddressCodeEdit.getText().toString(), // 开户行支行
                            ""); // 图片地址
                }else {
                    if (EmptyUtil.isEmpty(file)){
                        Toast.makeText(mContext, "请上传图片", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    model.updateImage(file);
                }
                break;
            default:
                break;
        }
    }
}
