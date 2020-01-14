package com.coin.market.activity.mine.transferrecord;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.coin.market.R;
import com.coin.market.databinding.ActivityTransferRecordLayoutBinding;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.widget.ActionSelectDialog;

/**
 * @author: Lenovo
 * @date: 2019/7/17
 * @info: 划转 记录
 */
public class TransferRecordActivity extends BaseActivity implements View.OnClickListener {

    private ActivityTransferRecordLayoutBinding binding;
    private TransferRecordViewModel model;
    public String coinId, accountType;

    public static void JUMP(Context context, String coinId, String accountType) {
        context.startActivity(new Intent(context, TransferRecordActivity.class).putExtra("coinId", coinId).putExtra("accountType", accountType));
    }

    @Override
    protected void initIntentData() {
        coinId = getIntent().getStringExtra("coinId");
        accountType = getIntent().getStringExtra("accountType");
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transfer_record_layout);
        model = new TransferRecordViewModel(this, binding);
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
        binding.titleBar.mImgScan.setVisibility(View.VISIBLE);
        binding.titleBar.mImgScan.setImageResource(R.drawable.mine_filter);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.titleBar.mImgScan.setOnClickListener(this);
    }

    /**
     * 筛选记录的 Dialog
     */
    private void showDiaLog() {
        new ActionSelectDialog(this)
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .setTitle("")
                .setOnDismissListener(new ActionSelectDialog.DialogOnDismissListener() {
                    @Override
                    public void onDialogDismiss(ActionSelectDialog dialog) {

                    }
                })
                .addSheetItem("全部", ActionSelectDialog.SheetItemColor.Blue, new ActionSelectDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        model.transferRecord(FaceApplication.getToken(), coinId, "");
                    }
                })
                .addSheetItem("币币账户到法币账户", ActionSelectDialog.SheetItemColor.Blue, new ActionSelectDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        model.transferRecord(FaceApplication.getToken(), coinId, "bb_type");
                    }
                })
                .addSheetItem("法币账户到币币账户", ActionSelectDialog.SheetItemColor.Blue, new ActionSelectDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        try {
                            model.transferRecord(FaceApplication.getToken(), coinId, "fb_type");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_Return:
                finish();
                break;
            case R.id.mImg_Scan:
                showDiaLog();
                break;
            default:
                break;
        }
    }

}
