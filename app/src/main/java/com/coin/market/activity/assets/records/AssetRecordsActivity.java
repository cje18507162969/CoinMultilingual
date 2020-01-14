package com.coin.market.activity.assets.records;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.coin.market.R;
import com.coin.market.activity.mine.coinout.CoinOutActivity;
import com.coin.market.activity.mine.coinrecharge.CoinRechargeActivity;
import com.coin.market.activity.mine.transfer.TransferActivity;
import com.coin.market.databinding.ActivityAssetRecordsLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;
import com.coin.market.wight.dialog.AssetRecordsScreenDialog;
import com.coin.market.wight.dialog.ExchangeDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.AccountsDTO;
import teng.wang.comment.model.ExchangeDialogModel;

/**
 * @author: Lenovo
 * @date: 2019/8/5
 * @info: 资产 财务记录  单个币种的列表
 */
public class AssetRecordsActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAssetRecordsLayoutBinding binding;
    private AssetRecordsViewModel model;
    public String coinId, coinName;
    private AccountsDTO entity;
    public String type = ""; // bb fb

    public List<ExchangeDialogModel> modelList = new ArrayList<>();

    public static void JUMP(Context context, AccountsDTO entity, String type) {
        context.startActivity(new Intent(context, AssetRecordsActivity.class).putExtra("AccountsDTO", entity).putExtra("type", type));
    }

    @Override
    protected void initIntentData() {
        entity = (AccountsDTO) getIntent().getSerializableExtra("AccountsDTO");
        coinId = entity.getCoinId();
        coinName = entity.getCoinName();
        type = getIntent().getStringExtra("type");
        modelList = new ArrayList<>();
//        model.getTypeList();
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        EventBus.getDefault().register(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_asset_records_layout);
        model = new AssetRecordsViewModel(this, binding);
        if (!EmptyUtil.isEmpty(entity)) {
            setTopUi(entity);
        }
        if (type.equals("fb")) {
            binding.assetRecordsButtonChargeCoin.setVisibility(View.GONE);
            binding.assetRecordsButtonGetCoin.setVisibility(View.GONE);
            binding.assetRecordsTransactionText.setText("法币交易");
        } else {
            binding.assetRecordsTransactionText.setText("币币交易");
        }
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.assetRecordsScreen.setOnClickListener(this);
        binding.assetRecordsButtonChargeCoin.setOnClickListener(this);
        binding.assetRecordsButtonGetCoin.setOnClickListener(this);
        binding.assetRecordsButtonTransactionCoin.setOnClickListener(this);
        binding.assetRecordsButtonTransferCoin.setOnClickListener(this);
    }

    private void setTopUi(AccountsDTO entity) {
        binding.assetRecordsTitle.setText(entity.getCoinName());
        binding.assetsAccountAvailable.setText(StringUtils.double2String(entity.getAvailable().doubleValue(), 8));
        binding.assetsAccountFrozen.setText(StringUtils.double2String(entity.getDisabled().doubleValue(), 8));
        binding.assetsAccountConvert.setText(entity.getPriceCny()+"");
    }

    // MBR
    private List<ExchangeDialogModel> initDialogData() {
        List<ExchangeDialogModel> modelList = new ArrayList<>();
        String arr[] = {
                getResources().getString(R.string.tv_assetrecord_all),
                getResources().getString(R.string.tv_assetrecord_recharge),
                getResources().getString(R.string.tv_assetrecord_openaposition),
                getResources().getString(R.string.tv_close_position),
                getResources().getString(R.string.tv_assetrecord_withdraw)};
        for (int i = 0; i < arr.length; i++) {
            ExchangeDialogModel model = new ExchangeDialogModel();
            model.setTitle(arr[i]);
            modelList.add(model);
        }
        return modelList;
    }

    // MBR
    private List<ExchangeDialogModel> initFBDialogData() {
        List<ExchangeDialogModel> modelList = new ArrayList<>();
        String arr[] = {"全部", "法币账户转到币币账户", "币币账户转到法币账户"};
        for (int i = 0; i < arr.length; i++) {
            ExchangeDialogModel model = new ExchangeDialogModel();
            model.setTitle(arr[i]);
            modelList.add(model);
        }
        return modelList;
    }

    /**
     *  币币 财务记录的筛选
     */

    private void showconfirmDialog(final List<ExchangeDialogModel> list) {
        AssetRecordsScreenDialog dialog = new AssetRecordsScreenDialog(this, list);
        dialog.builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .setOnClickListener(new ExchangeDialog.OnItemClickListener() {
                    @Override
                    public void onClick(String title) { ////{"全部", "充值", "开仓", "平仓", "提现"};
                        String all = getResources().getString(R.string.tv_assetrecord_all);
                        String recharge = getResources().getString(R.string.tv_assetrecord_recharge);
                        String openaposition = getResources().getString(R.string.tv_assetrecord_openaposition);
                        String close_position = getResources().getString(R.string.tv_close_position);
                        String withdraw = getResources().getString(R.string.tv_assetrecord_withdraw);
                        if (title.equals(all)){
                            model.transferRecord(FaceApplication.getToken(), coinId, 1,"");
                        }else if (title.equals(recharge)){
                            model.transferRecord(FaceApplication.getToken(), coinId, 1,"recharge");
                        }else if (title.equals(openaposition)){
                            model.transferRecord(FaceApplication.getToken(), coinId, 1,"buy");
                        }else if (title.equals(close_position)){
                            model.transferRecord(FaceApplication.getToken(), coinId, 1,"sell");
                        }else if (title.equals(withdraw)){
                            model.transferRecord(FaceApplication.getToken(), coinId, 1,"withdraw");
                        }

                    }
                });
        dialog.show();
    }

    /**
     *  币币 财务记录的筛选
     */
    private void showFBConfirmDialog(final List<ExchangeDialogModel> list) {
        AssetRecordsScreenDialog dialog = new AssetRecordsScreenDialog(this, list);
        dialog.builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .setOnClickListener(new ExchangeDialog.OnItemClickListener() {
                    @Override
                    public void onClick(String title) {
                        switch (title) {
                            case "全部":
//                                model.transferRecord(FaceApplication.getToken(), coinId, "");
                                break;
                            case "法币账户转到币币账户":
//                                model.transferRecord(FaceApplication.getToken(), coinId, "fb_type");
                                break;
                            case "币币账户转到法币账户":
//                                model.transferRecord(FaceApplication.getToken(), coinId, "bb_type");
                                break;
                            default:
                                break;
                        }
                    }
                });
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_Return:
                finish();
                break;
            case R.id.asset_records_button_charge_coin: // 充币
                CoinRechargeActivity.JUMP(this, coinId, coinName);
                break;
            case R.id.asset_records_button_get_coin: // 提币
                CoinOutActivity.JUMP(this, coinId, coinName);
                break;
            case R.id.asset_records_button_transfer_coin: // 划转
                if (type.equals("fb")) {
                    TransferActivity.JUMP(this, "fb_type", coinId);
                } else {
                    TransferActivity.JUMP(this, "bb_type", coinId);
                }
                break;
            case R.id.asset_records_button_transaction_coin: // 币币交易
                if (type.equals("fb")) {
                    EventBus.getDefault().post("goTransactionFb");
                } else {
                    EventBus.getDefault().post("goTransactionBb");
                }
                finish();
                break;
            case R.id.asset_records_screen:
                if (type.equals("fb")){
                    showFBConfirmDialog(initFBDialogData());
                }else {
                    showconfirmDialog(initDialogData());

                }
                break;
            default:
                break;
        }
    }

    /**
     * 划转成功 重新刷新数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void transfer(String str) {
        try {
            if (null != str && str.equals("Transfer")) {
                model.getFBAssetsData(FaceApplication.getToken(), 0); // 刷新币的可用
                if (type.equals("fb")){
//                    model.transferRecord(FaceApplication.getToken(), coinId, ""); // 刷新币的财务记录列表
                }else {
                    model.getCoinList(FaceApplication.getToken(), coinId, 0); // 刷新币的财务记录列表
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
