package com.coin.market.activity.mine.coinout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.activity.mine.coinaddress.CoinAddressActivity;
import com.coin.market.activity.mine.coinrecharge.CoinRechargeActivity;
import com.coin.market.activity.mine.coinrecord.CoinRecordActivity;
import com.coin.market.databinding.ActivityCoinOutLayoutBinding;
import com.coin.market.fragment.getcoins.GetCoinsFragment;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;
import com.coin.market.wight.dialog.GetCoinDialog;
import com.example.qrcode.Constant;
import com.example.qrcode.ScannerActivity;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.AccountsDTO;
import teng.wang.comment.model.CoinAddressEntity;
import teng.wang.comment.utils.jurisdiction.PermissionsHelper;
import teng.wang.comment.widget.ActionSheetDialog;
import teng.wang.comment.widget.MessageWindow;

/**
 * @author: Lenovo
 * @date: 2019/7/17
 * @info: 提币
 */
public class CoinOutActivity extends BaseActivity implements View.OnClickListener {

    private final int RESULT_REQUEST_CODE = 1;
    private ActivityCoinOutLayoutBinding binding;
    private CoinOutViewModel model;
    private GetCoinsFragment fragment;
    private FragmentTransaction transaction;
    public String CoinId, coinName;
    public double coinAllMoney = 0;

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, CoinOutActivity.class));
    }

    public static void JUMP(Context context, String coinId, String coinName) {
        context.startActivity(new Intent(context, CoinOutActivity.class).putExtra("coinId", coinId).putExtra("coinName", coinName));
    }

    public static void JUMP(Context context, String coinId, String coinName, List<AccountsDTO> adList) {
        context.startActivity(new Intent(context, CoinOutActivity.class)
                .putExtra("coinId", coinId)
                .putExtra("coinName", coinName)
                .putExtra("adList",(Serializable) adList));
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_coin_out_layout);
        model = new CoinOutViewModel(this, binding);
        if (!EmptyUtil.isEmpty(coinName)){
            setCoinName(coinName);
        }
        model.getCoinBalance(FaceApplication.getToken(), CoinId);
        model.getCoinInfo(FaceApplication.getToken(), CoinId);
        //initFragment();
    }

    @Override
    protected void initIntentData() {
        CoinId = getIntent().getStringExtra("coinId");
        coinName = getIntent().getStringExtra("coinName");

    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
        binding.titleBar.mImgScan.setVisibility(View.VISIBLE);
        binding.titleBar.mImgScan.setImageResource(R.drawable.mine_history);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.titleBar.mImgScan.setOnClickListener(this);
        binding.coinOutButton.setOnClickListener(this);
        binding.coinOutAddressButton.setOnClickListener(this);
        binding.coinOutQr.setOnClickListener(this);
        binding.coinAllButton.setOnClickListener(this);
        binding.coinOutConfirmButton.setOnClickListener(this);
        binding.coinGetMoneyEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if (!TextUtils.isEmpty(editable.toString())){
                        binding.allGetMoney.setText(Double.parseDouble(editable.toString())-Double.parseDouble(binding.coinServiceCharge.getText().toString())+"");
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setCoinName(String coinName){
        binding.coinOutName.setText(coinName);
        binding.coinOutSelectCoinName.setText(coinName);
        binding.getMoneyNameText.setText(coinName);
    }

    private void initFragment() {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_slide_in, R.anim.anim_slide_out);
        if (null == fragment) {
            fragment = new GetCoinsFragment();
            transaction.add(R.id.coins_fr_layout, fragment, fragment.getTag());
            fragment.fragmentisHidden(transaction);
        } else {
            if (fragment.isVisible()) {
                transaction.hide(fragment);
            } else {
                transaction.show(fragment);
            }
        }
        transaction.commit();

        // 选择币种的回调
        fragment.setListener(new GetCoinsFragment.mOnClickCallback() {
            @Override
            public void selectItem(String id, String name, AccountsDTO ad) {
                CoinId = id;
                coinName = name;
                setCoinName(coinName);
                binding.coinGetMoneyEdit.setText("");
                coinAllMoney = ad.isExport;
                binding.coinServiceCharge.setText(ad.fee + "");
                binding.allMoneyMinText.setText(getResources().getString(R.string.transaction_fb_transact_assets_account_available_text)
                        + StringUtils.double2String(ad.isExport, 4) + " " + name);
                model.getCoinBalance(FaceApplication.getToken(), CoinId);
                model.getCoinInfo(FaceApplication.getToken(), CoinId);

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
                        String content = data.getStringExtra(Constant.EXTRA_RESULT_CONTENT);
                        if (content.contains("ethereum")){
                            binding.coinOutAddress.setText(StringUtils.splitData(content, "ethereum:", "?").trim());
                            Log.e("cjh>>>","QrCode" + StringUtils.splitData(content, "ethereum:", "?").trim());
                        }else {
                            binding.coinOutAddress.setText(content.trim());
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
            case R.id.coin_out_confirm_button: // 提币接口
                if (4!=FaceApplication.getAuthentication()){
                    Toast.makeText(mContext, getResources().getString(R.string.toast_authentication), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(binding.coinOutAddress.getText().toString())){
                    Toast.makeText(mContext, getResources().getString(R.string.toast_tb_address_null), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(binding.coinGetMoneyEdit.getText().toString())){
                    Toast.makeText(mContext, getResources().getString(R.string.toast_tb_num_input), Toast.LENGTH_SHORT).show();
                    return;
                }

                // 需要短信验证的
                showConfirmDialog(getResources().getString(R.string.dialog_tb_ok));
                // 不需要短信验证的
                //showGetCoinDialog();
                break;
            case R.id.mImg_Scan:
                // 跳转到 历史
                CoinRecordActivity.JUMP(this, "TB", CoinId,coinName);
                break;
            case R.id.coin_all_button:
                if (coinAllMoney>0){
                    binding.coinGetMoneyEdit.setText(StringUtils.double2String(coinAllMoney, 4)+"");
                }else {
                    binding.coinGetMoneyEdit.setText("0");
                }
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
            case R.id.coin_out_address_button: // 跳转到 币种地址列表
                CoinAddressActivity.JUMP(this, CoinId, coinName);
                break;
            case R.id.coin_out_button:
                initFragment();
                break;
            default:
                break;
        }
    }

    /**
     *  是否删除该条 币种地址
     */
    private void showGetCoinDialog() {
        new ActionSheetDialog(this)
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .setTitle("是否提币")
                .setOnDismissListener(new ActionSheetDialog.DialogOnDismissListener() {
                    @Override
                    public void onDialogDismiss(ActionSheetDialog dialog) {

                    }
                })
                .addSheetItem("确定", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        model.getGetCoin(FaceApplication.getToken(), CoinId, binding.coinOutAddress.getText().toString(), binding.coinGetMoneyEdit.getText().toString());
                    }
                }).show();
    }

    /**
     *   是否提币 确认操作
     */
    private void showConfirmDialog(final String Title) {
        GetCoinDialog dialog = new GetCoinDialog(this, Tag);
        dialog.builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true)
                .setOnClickListener(new GetCoinDialog.OnPassClickListener() {
                    @Override
                    public void onClick(String str) {
                        model.getGetCoin(FaceApplication.getToken(), CoinId, binding.coinOutAddress.getText().toString(), binding.coinGetMoneyEdit.getText().toString(), str);
                    }
                });
        dialog.show();
    }

    /**
     *  选择地址 的广播监听
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void select(CoinAddressEntity entity) {
        try {
            if (!EmptyUtil.isEmpty(entity)){
                binding.coinOutAddress.setText(entity.getAddress());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
