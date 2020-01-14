package com.coin.market.activity.mine.coinaddressdda;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.databinding.ActivityCoinAddAddressLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;
import com.example.qrcode.Constant;
import com.example.qrcode.ScannerActivity;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.utils.jurisdiction.PermissionsHelper;

/**
 * @author: Lenovo
 * @date: 2019/7/18
 * @info: 添加ETH地址
 */
public class CoinAddAddressActivity extends BaseActivity implements View.OnClickListener {

    private final int RESULT_REQUEST_CODE = 1;
    private ActivityCoinAddAddressLayoutBinding binding;
    private CoinAddAddressViewModel model;
    private String CoinId;

    public static void JUMP(Context context, String CoinId) {
        context.startActivity(new Intent(context, CoinAddAddressActivity.class).putExtra("coinId", CoinId));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_coin_add_address_layout);
        model = new CoinAddAddressViewModel(this, binding);
    }

    @Override
    protected void initIntentData() {
        if (!EmptyUtil.isEmpty(getIntent().getStringExtra("coinId"))) {
            CoinId = getIntent().getStringExtra("coinId");
        }
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.coinAddressButton.setOnClickListener(this);
        binding.coinOutQr.setOnClickListener(this);
        binding.addAddressEdit1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence) && !TextUtils.isEmpty(binding.addAddressEdit2.getText().toString())) {
                    binding.coinAddressButton.setAlpha(1.0f);
                    binding.coinAddressButton.setEnabled(true);
                } else {
                    binding.coinAddressButton.setAlpha(0.5f);
                    binding.coinAddressButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.addAddressEdit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence) && !TextUtils.isEmpty(binding.addAddressEdit1.getText().toString())) {
                    binding.coinAddressButton.setAlpha(1.0f);
                    binding.coinAddressButton.setEnabled(true);
                } else {
                    binding.coinAddressButton.setAlpha(0.5f);
                    binding.coinAddressButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void goScanner() {
        try {
            Intent intent = new Intent(this, ScannerActivity.class);
            //这里可以用intent传递一些参数，比如扫码聚焦框尺寸大小，支持的扫码类型。
            //设置扫码框的宽
//            intent.putExtra(Constant.EXTRA_SCANNER_FRAME_WIDTH, 200);
//            //设置扫码框的高
//            intent.putExtra(Constant.EXTRA_SCANNER_FRAME_HEIGHT, 200);
//            //设置扫码框距顶部的位置
//            intent.putExtra(Constant.EXTRA_SCANNER_FRAME_TOP_PADDING, 100);
            //设置是否启用从相册获取二维码。
            intent.putExtra(Constant.EXTRA_IS_ENABLE_SCAN_FROM_PIC, true);
            startActivityForResult(intent, RESULT_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
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
                    break;
                case AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE:
                    finish();
                    //执行Toast显示或者其他逻辑处理操作
                    break;
                case RESULT_REQUEST_CODE:
                    if (data == null) return;
                    //String type = data.getStringExtra(Constant.EXTRA_RESULT_CODE_TYPE);
                    try {
                        Log.e("cjh>>> QrCode ","content：" + data.getStringExtra(Constant.EXTRA_RESULT_CONTENT));
                        String content = data.getStringExtra(Constant.EXTRA_RESULT_CONTENT); // 结果

                        //如果包含 ethereum 过滤掉 取到地址   如果不包含直接显示
                        if (content.contains("ethereum")){
                            binding.addAddressEdit1.setText(StringUtils.splitData(content, "ethereum:", "?").trim());
                            Log.e("cjh>>>","QrCode" + StringUtils.splitData(content, "ethereum:", "?").trim());
                        }else {
                            binding.addAddressEdit1.setText(content.trim());
                            Log.e("cjh>>>","QrCode" + content.trim());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
            case R.id.coin_out_qr:
                if (Build.VERSION.SDK_INT > 22) {
                    if (!PermissionsHelper.cameraPermission(this)) {
                        PermissionsHelper.requestPermission(this, "应用需要您的相机权限", 100, new String[]{Manifest.permission.CAMERA});
                    } else {
                        goScanner();
                    }
                } else {
                    goScanner();
                }
                break;
            case R.id.coin_address_button:
                if (TextUtils.isEmpty(binding.addAddressEdit1.getText().toString().trim())) {
                    Toast.makeText(mContext, "请输入或粘贴地址", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(binding.addAddressEdit2.getText().toString().trim())) {
                    Toast.makeText(mContext, "请输入备注", Toast.LENGTH_SHORT).show();
                    return;
                }
                model.addCoinAddress(FaceApplication.getToken(), CoinId, binding.addAddressEdit1.getText().toString().trim(), binding.addAddressEdit2.getText().toString().trim());
                break;
            default:
                break;
        }
    }

}
