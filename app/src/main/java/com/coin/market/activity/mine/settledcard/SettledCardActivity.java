package com.coin.market.activity.mine.settledcard;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coin.market.R;
import com.coin.market.databinding.ActivitySettledCardLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.SettledEditModel;
import teng.wang.comment.utils.PictureSelectorUtil;

/**
 * @author: Lenovo
 * @date: 2019/9/3
 * @info:  商家申请 上传身份证
 */
public class SettledCardActivity extends BaseActivity implements View.OnClickListener {

    private ActivitySettledCardLayoutBinding binding;
    private SettledCardViewModel model;
    private boolean select = false;
    private SettledEditModel settledEditModel;
    private File file;
    private int selectImg = 0;
    public String url1, url2, url3;
    public List<LocalMedia> selectList;

    public static void JUMP(Context context, SettledEditModel settledEditModel) {
        context.startActivity(new Intent(context, SettledCardActivity.class).putExtra("SettledEditModel", settledEditModel));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initIntentData() {
        settledEditModel = (SettledEditModel) getIntent().getSerializableExtra("SettledEditModel");
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settled_card_layout);
        model = new SettledCardViewModel(this, binding);
    }

    @Override
    protected void onSaveInstanceState(Bundle outBundle) {
        super.onSaveInstanceState(outBundle);
        outBundle.putString("url1", url1);
        outBundle.putString("url2", url2);
        outBundle.putString("url3", url3);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        url1 = savedInstanceState.getString("url1");
        url2 = savedInstanceState.getString("url2");
        url3 = savedInstanceState.getString("url3");
        if (!EmptyUtil.isEmpty(binding)){
            if (!TextUtils.isEmpty(url1)){
                Glide.with(this).load(url1).into(binding.settledCardImg1);
            }
            if (!TextUtils.isEmpty(url2)){
                Glide.with(this).load(url2).into(binding.settledCardImg2);
            }
            if (!TextUtils.isEmpty(url3)){
                Glide.with(this).load(url3).into(binding.settledCardImg3);
            }
        }
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.selectButton.setOnClickListener(this);
        binding.settledCardImg1.setOnClickListener(this);
        binding.settledCardImg2.setOnClickListener(this);
        binding.settledCardImg3.setOnClickListener(this);
        binding.settledCardNextButton.setOnClickListener(this);
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
                    model.updateImage(file, selectImg);
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
            case R.id.settled_card_img1:
                selectImg = 1;
                selectList = new ArrayList<>();
                PictureSelectorUtil.startPictureSelectorActivity(this, 1, selectList);
                break;
            case R.id.settled_card_img2:
                selectImg = 2;
                selectList = new ArrayList<>();
                PictureSelectorUtil.startPictureSelectorActivity(this, 1, selectList);
                break;
            case R.id.settled_card_img3:
                selectImg = 3;
                selectList = new ArrayList<>();
                PictureSelectorUtil.startPictureSelectorActivity(this, 1, selectList);
                break;
            case R.id.select_button:
                if (!select){
                    binding.selectButton.setImageResource(R.drawable.transaction_activation);
                    select = true;
                }else {
                    binding.selectButton.setImageResource(R.drawable.transaction_inactivated);
                    select = false;
                }
                break;
            case R.id.settled_card_next_button:
                if (TextUtils.isEmpty(url1)){
                    Toast.makeText(mContext, "请上传身份证正面照", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(url2)){
                    Toast.makeText(mContext, "请上传身份证反面照", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(url3)){
                    Toast.makeText(mContext, "请上传手持身份证照", Toast.LENGTH_SHORT).show();
                    return;
                }
//                if (!select){
//                    Toast.makeText(mContext, "请上传手持身份证照", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                settledEditModel.setIdCardFront(url1);
                settledEditModel.setIdCardReverseSide(url2);
                settledEditModel.setHandHeldIdCard(url3);
                model.bindShop(FaceApplication.getToken(), settledEditModel);
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
