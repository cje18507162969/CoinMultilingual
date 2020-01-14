package com.coin.market.activity.mine.settlededit;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.activity.mine.settledcard.SettledCardActivity;
import com.coin.market.databinding.ActivitySettledEditLayoutBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.model.SettledEditModel;

/**
 * @author: Lenovo
 * @date: 2019/9/3
 * @info:  商家 认证审核结果
 */
public class SettledEditActivity extends BaseActivity implements View.OnClickListener {

    private ActivitySettledEditLayoutBinding binding;
    private SettledEditViewModel model;

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, SettledEditActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settled_edit_layout);
        model = new SettledEditViewModel(this, binding);
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.settledEditNextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_Return:
                finish();
                break;
            case R.id.settled_edit_next_button:
                if (TextUtils.isEmpty(binding.settledEditUsername.getText().toString())){
                    Toast.makeText(mContext, "请输入昵称", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(binding.settledEditName.getText().toString())){
                    Toast.makeText(mContext, "请输入姓名", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(binding.settledEditPhone.getText().toString())){
                    Toast.makeText(mContext, "请输入本人电话", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(binding.settledEditWeix.getText().toString())){
                    Toast.makeText(mContext, "请输入微信", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(binding.settledEditEmail.getText().toString())){
                    Toast.makeText(mContext, "请输入邮箱", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(binding.settledEditUrgent.getText().toString())){
                    Toast.makeText(mContext, "请输入紧急联系人", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(binding.settledEditUrgentPhone.getText().toString())){
                    Toast.makeText(mContext, "请输入紧急联系人电话", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(binding.settledEditRelation.getText().toString())){
                    Toast.makeText(mContext, "请输入与本人关系", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(binding.settledEditRelation.getText().toString())){
                    Toast.makeText(mContext, "请输入您本人的常住地址", Toast.LENGTH_SHORT).show();
                    return;
                }
                SettledEditModel settledEditModel = new SettledEditModel();
                settledEditModel.setSellName(binding.settledEditUsername.getText().toString());// 昵称
                settledEditModel.setIdCardName(binding.settledEditName.getText().toString());//姓名
                settledEditModel.setUserTel(binding.settledEditPhone.getText().toString());// 电话
                settledEditModel.setUserWechat(binding.settledEditWeix.getText().toString()); // 微信
                settledEditModel.setUserEmail(binding.settledEditEmail.getText().toString()); // 邮箱
                settledEditModel.setUserTelUserName(binding.settledEditUrgent.getText().toString()); // 紧急联系人
                settledEditModel.setUserTelUserTel(binding.settledEditUrgentPhone.getText().toString()); // 紧急联系人电话
                settledEditModel.setUserRelation(binding.settledEditRelation.getText().toString()); // 关系
                settledEditModel.setUserFrequentlyAddress(binding.settledEditRelation.getText().toString()); // 常住地址
                SettledCardActivity.JUMP(this, settledEditModel);
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void spplyShop(String model) {
        try {
            if (null != model && model.equals("ApplyShop")) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
