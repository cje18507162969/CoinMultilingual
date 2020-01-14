package com.coin.market.activity.mine.settings;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.coin.market.R;
import com.coin.market.activity.main.MainActivity;
import com.coin.market.activity.mine.updatepassword.UpdatePasswordActivity;
import com.coin.market.databinding.ActivitySettingsLayoutBinding;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import teng.wang.comment.SPConstants;
import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.UsersEntity;
import teng.wang.comment.utils.DataKeeper;
import teng.wang.comment.widget.ActionSelectDialog;
import teng.wang.comment.widget.ActionSheetDialog;

/**
 * @author: Lenovo
 * @date: 2019/7/29
 * @info: 设置
 */
public class SettingsActivity extends BaseActivity implements View.OnClickListener {

    private int isOFF;
    private SettingsViewModel model;
    private ActivitySettingsLayoutBinding binding;

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, SettingsActivity.class));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings_layout);
        model = new SettingsViewModel(this, binding);
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.settingsBackButton.setOnClickListener(this);
        binding.settingsLanguageButton.setOnClickListener(this);
        binding.settingsAccelerateButton.setOnClickListener(this);
        binding.settingsUpdatePassword.setOnClickListener(this);
    }

    /**
     * 退出登录提示弹窗
     */
    private void showDialog() {
        new ActionSheetDialog(this)
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .setTitle(getResources().getString(R.string.mine_log_out))
                .setOnDismissListener(new ActionSheetDialog.DialogOnDismissListener() {
                    @Override
                    public void onDialogDismiss(ActionSheetDialog dialog) {

                    }
                })
                .addSheetItem(getResources().getString(R.string.ok), ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        model.loginOut(FaceApplication.getToken());

                    }
                }).show();
    }

    /**
     *  选择语言
     */
    private void selectLanguageDialog(){
        new ActionSelectDialog(this)
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .setTitle(getResources().getString(R.string.forget_language_q))
                .setOnDismissListener(new ActionSelectDialog.DialogOnDismissListener() {
                    @Override
                    public void onDialogDismiss(ActionSelectDialog dialog) {

                    }
                })
                .addSheetItem(getResources().getString(R.string.forget_ch), ActionSelectDialog.SheetItemColor.Red, new ActionSelectDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        setLanguage(0);
                    }
                })
                .addSheetItem(getResources().getString(R.string.forget_en), ActionSelectDialog.SheetItemColor.Red, new ActionSelectDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        setLanguage(1);
                    }
                })
                .addSheetItem(getResources().getString(R.string.forget_ko), ActionSelectDialog.SheetItemColor.Red, new ActionSelectDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        try {
                            setLanguage(2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .addSheetItem(getResources().getString(R.string.forget_ja), ActionSelectDialog.SheetItemColor.Red, new ActionSelectDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        try {
                            setLanguage(3);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).show();

    }

    /**
     * 保存是否选择了app语言
     */
    private void saveSelect(String language){
        UsersEntity models = FaceApplication.getUserInfoModel();
        if (null!=models){
            models.setAppLanguage(true);
            models.setLanguage(language);
        }else{
            models = new UsersEntity();
            models.setAppLanguage(true);
            models.setLanguage(language);
        }
        DataKeeper.put(SettingsActivity.this, SPConstants.USERINFOMODEL,models);
    }

    /**
     * 切换语言
     * @param type
     */
    private void setLanguage(int type){
        switch (type){
            case 0:
                Locale.setDefault(Locale.CHINA);
                Configuration config = getBaseContext().getResources().getConfiguration();
                config.locale = Locale.CHINA;
                saveSelect("zh-CN");
                getBaseContext().getResources().updateConfiguration(config , getBaseContext().getResources().getDisplayMetrics());
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            case 1:
                Locale.setDefault(Locale.ENGLISH);
                Configuration config1 = getBaseContext().getResources().getConfiguration();
                config1.locale = Locale.ENGLISH;
                saveSelect("en-US");
                getBaseContext().getResources().updateConfiguration(config1 , getBaseContext().getResources().getDisplayMetrics());
                Intent intent1 = new Intent(SettingsActivity.this, MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
                finish();
                break;
            case 2:
                Locale.setDefault(Locale.KOREA);
                Configuration config2 = getBaseContext().getResources().getConfiguration();
                config2.locale = Locale.KOREA;
                saveSelect("ko-KR");
                getBaseContext().getResources().updateConfiguration(config2 , getBaseContext().getResources().getDisplayMetrics());
                Intent intent2 = new Intent(SettingsActivity.this, MainActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent2);
                finish();
                break;
            case 3:
                Locale.setDefault(Locale.JAPAN);
                Configuration config3 = getBaseContext().getResources().getConfiguration();
                config3.locale = Locale.JAPAN;
                saveSelect("ja-JP");
                getBaseContext().getResources().updateConfiguration(config3 , getBaseContext().getResources().getDisplayMetrics());
                Intent intent3 = new Intent(SettingsActivity.this, MainActivity.class);
                intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent3);
                finish();
                break;


        }


    }

    public void loginOut() {
        //TODO 接受退出登录事件,删除本地用户信息数据库信息，数据库数据等
        DataKeeper.removeAll(this);
        EventBus.getDefault().post("exitLogin");
        finish();  //mTv_Already_activated  mTv_userName
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_Return:
                finish();
                break;
            case R.id.settings_language_button:
                selectLanguageDialog();
                break;
            case R.id.settings_back_button: // 退出登录
                showDialog();
                break;
            case R.id.settings_update_password: // 修改密码
                UpdatePasswordActivity.JUMP(this);
                break;
            case R.id.settings_accelerate_button:
                if (isOFF == 0) {
                    binding.settingsAccelerateButton.setImageResource(R.drawable.mine_switchoff);
                    isOFF = 1;
                } else {
                    binding.settingsAccelerateButton.setImageResource(R.drawable.mine_switchon);
                    isOFF = 0;
                }
                break;
            default:
                break;
        }
    }

}
