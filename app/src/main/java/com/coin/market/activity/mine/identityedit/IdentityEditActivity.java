package com.coin.market.activity.mine.identityedit;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coin.market.R;
import com.coin.market.databinding.ActivityIdentityeditLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.utils.PictureSelectorUtil;

import static org.litepal.LitePalApplication.getContext;

/**
 * @author: Lenovo
 * @date: 2019/7/29
 * @info: 身份认证  填写身份认证 下一步
 */
public class IdentityEditActivity extends BaseActivity implements View.OnClickListener {

    private ActivityIdentityeditLayoutBinding binding;
    private IdentityEditViewModel model;
    public List<LocalMedia> selectList;
    public String url1, url2, url3;
    private File file;
    private int selectImg = 0;
    private String save = "save";

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, IdentityEditActivity.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
    protected void initViews(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        setSteepStatusBar(false);
        EventBus.getDefault().register(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_identityedit_layout);
        model = new IdentityEditViewModel(this, binding);
        String language = FaceApplication.getLanguage();
        if ("ja-JP".equals(language)){
            binding.tvCountry.setText(getResources().getString(R.string.tv_jp));
        }else if ("ko-KR".equals(language)){
            binding.tvCountry.setText(getResources().getString(R.string.tv_ko));
        }else if ("en-US".equals(language)){
            binding.tvCountry.setText(getResources().getString(R.string.tv_en));
        }else {
            binding.tvCountry.setText(getResources().getString(R.string.mine_identity_china_text));
        }

    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
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
    protected void setListener() {
        binding.identityeditEditName.setFilters(new InputFilter[]{filter});
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.identityeditNextButton.setOnClickListener(this);
        binding.settledCardImg1.setOnClickListener(this);
        binding.settledCardImg2.setOnClickListener(this);
        binding.settledCardImg3.setOnClickListener(this);
        binding.identityeditEditName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setEidt();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.identityeditEditCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setEidt();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * 判定输入汉字
     *
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    InputFilter filter = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (!isChinese(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }
    };



    private void setEidt(){
        if (!TextUtils.isEmpty(binding.identityeditEditCard.getText().toString()) && !TextUtils.isEmpty(binding.identityeditEditName.getText().toString())){
            binding.identityeditNextButton.setAlpha(1.0f);
        }else {
            binding.identityeditNextButton.setAlpha(0.5f);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
            case R.id.identityedit_next_button:
                if (TextUtils.isEmpty(binding.identityeditEditName.getText().toString().trim())){
                    Toast.makeText(mContext, getResources().getString(R.string.mine_identity_edit_name_hide), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(binding.identityeditEditCard.getText().toString().trim())){
                    Toast.makeText(mContext, getResources().getString(R.string.mine_identity_edit_card_hide), Toast.LENGTH_SHORT).show();
                    return;
                }

                try {

                    if (binding.identityeditEditName.getText().toString().trim().length()<2){
                        Toast.makeText(mContext, getResources().getString(R.string.tv_illegalname), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!StringUtils.IDCardValidate(binding.identityeditEditCard.getText().toString().trim())){
                        Toast.makeText(mContext,getResources().getString(R.string.tv_idcard_invalid) , Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                model.Identity(FaceApplication.getToken(), binding.identityeditEditName.getText().toString().trim(),binding.identityeditEditCard.getText().toString().trim());
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     *  身份认证成功  finish掉前面的所有页面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void finish(String str) {
        try {
            if (!TextUtils.isEmpty(str) && str.equals("IdentitySuccess")) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
