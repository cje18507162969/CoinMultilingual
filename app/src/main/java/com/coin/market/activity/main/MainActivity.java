package com.coin.market.activity.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.activity.mine.fingerprint.FingerprintActivity;
import com.coin.market.activity.mine.gesture.GestureActivity;
import com.coin.market.activity.password.ForgetPasswordActivity;
import com.coin.market.activity.register.ShopRegisterActivity;
import com.coin.market.databinding.ActivityMainLayoutBinding;
import com.coin.market.databinding.ActivityShopLoginLayoutBinding;
import teng.wang.comment.model.UsersEntity;
import com.coin.market.jpush.ExampleUtil;
import com.coin.market.jpush.TagAliasOperatorHelper;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.MyTimeTask;
import com.coin.market.util.StringUtils;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.netease.nis.captcha.Captcha;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.annotations.NonNull;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import teng.wang.comment.SPConstants;
import teng.wang.comment.api.FaceApi;
import teng.wang.comment.api.FaceApiMbr;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.api.RequestBodyUtils;
import teng.wang.comment.base.AppInnerDownLode;
import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.UpdateImageModel;
import teng.wang.comment.model.VersionModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.DataKeeper;
import teng.wang.comment.utils.Tools;
import teng.wang.comment.utils.jurisdiction.PermissionsHelper;
import teng.wang.comment.utils.log.Log;

import static com.coin.market.jpush.TagAliasOperatorHelper.sequence;

public class MainActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private Context context;
    private ActivityMainLayoutBinding mainLayoutBinding;
    private ActivityShopLoginLayoutBinding mLoginLayoutBinding;
    private MainModel mainModel;
    private File file;
    private AlertDialog.Builder mDialog;
    private final int RESULT_REQUEST_CODE = 1;
    private boolean isCheckedPas = true;
    private boolean isVerify; // 是否需要验证  第一次进入App  需要验证

    private Captcha mCaptcha;//网易滑动验证API

    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    public static boolean isForeground = false;
    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    private EditText msgText;

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

//    @Override
//    protected void setStatusBar() {
//        StatusBarUtil.setColor(this, getResources().getColor(R.color.white), 0);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        EventBus.getDefault().post("StopTimer");
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //取消 设置的别名
        //setJPushAlias("");
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
        JPushInterface.onResume(this);
        setTimer();
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
        JPushInterface.onPause(this);
        stopTimer();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTheme(R.style.LeadingAndMainTheme);
        context = this;
        setSteepStatusBar(false);
        EventBus.getDefault().register(this);
        getPermissions();
        // 注册极光相关
        registerMessageReceiver();
        initJPush();

        if (!FaceApplication.isLogin()) {
            mLoginLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_shop_login_layout);
        } else {
            mainLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_layout);
            mainModel = new MainModel(this, mainLayoutBinding);
            // 是否需要验证
            if (!isVerify) { // 这里需要验证
                if (!EmptyUtil.isEmpty(DataKeeper.get(context, SPConstants.MBR_VERIFY_METHOD, ""))) { //获取验证方式
                    if (DataKeeper.get(context, SPConstants.MBR_VERIFY_METHOD, "").equals("1")) {
                        // 手势验证
                        GestureActivity.JUMP(context, "verify");
                    } else if (DataKeeper.get(context, SPConstants.MBR_VERIFY_METHOD, "").equals("2")) {
                        // 指纹验证
                        FingerprintActivity.JUMP(context, "verify");
                    }
                }
            }
            if (!EmptyUtil.isEmpty(FaceApplication.getUserInfoModel())){
//                String alias = "Topcoin_" + FaceApplication.getUserInfoModel().getId();
//                setJPushAlias(alias);
            }
        }
    }

    // 初始化 JPush。如果已经初始化，但没有登录成功，则执行重新登录。
    private void initJPush() {
        JPushInterface.init(getApplicationContext());
    }

    // 设置极光别名
    private void setJPushAlias(String alias){
        if(TextUtils.isEmpty(alias)){
            return;
        }
        TagAliasOperatorHelper.TagAliasBean tagAliasBean = new TagAliasOperatorHelper.TagAliasBean();
        tagAliasBean.action = 2;
        tagAliasBean.isAliasAction = true;
        tagAliasBean.alias = alias;
        sequence++;
        TagAliasOperatorHelper.getInstance().handleAction(getApplicationContext(),sequence,tagAliasBean);
    }

    @Override
    protected void setListener() {
        setLoginListener();
    }

    private void setLoginListener() {
        if (!EmptyUtil.isEmpty(mainLayoutBinding)) {
            mainLayoutBinding.homeTabHome.setOnClickListener(this);
            mainLayoutBinding.homeTabAssets.setOnClickListener(this);
            mainLayoutBinding.homeTabQuotation.setOnClickListener(this);
            mainLayoutBinding.homeTabTransaction.setOnClickListener(this);
        }

        if (null != mLoginLayoutBinding) {
            initEdt();
            mLoginLayoutBinding.mBtnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String mobile = mLoginLayoutBinding.mEdtPhoneNumber.getText().toString().trim();
                    String pwd = mLoginLayoutBinding.mEdtPassword.getText().toString().trim();
                    if (TextUtils.isEmpty(mobile)){
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.mine_security_edit_phone_hide), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(pwd)){
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.mine_input_pwd), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    sendLogin(mobile,pwd);
                }
            });
            mLoginLayoutBinding.mTvRegistration.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 注册
                    ShopRegisterActivity.JUMP(mContext);
                }
            });
            mLoginLayoutBinding.mTvPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ForgetPasswordActivity.JUMP(context); // 忘记密码
                }
            });
            mLoginLayoutBinding.mImgDeleteAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLoginLayoutBinding.mEdtPhoneNumber.setText(null);
                }
            });
            mLoginLayoutBinding.mImgDisplayCipher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isCheckedPas) {
                        isCheckedPas = false;
                        //如果选中，显示密码
                        mLoginLayoutBinding.mEdtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    } else {
                        isCheckedPas = true;
                        //否则隐藏密码
                        mLoginLayoutBinding.mEdtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                }
            });
        }
    }

    private void initEdt() {
        mLoginLayoutBinding.mEdtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence)) {
                    mLoginLayoutBinding.mBtnLogin.setEnabled(true);
                    mLoginLayoutBinding.mBtnLogin.setAlpha(1.0f);
                } else {
                    mLoginLayoutBinding.mBtnLogin.setEnabled(false);
                    mLoginLayoutBinding.mBtnLogin.setAlpha(0.5f);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mLoginLayoutBinding.mEdtPhoneNumber.setSelection(mLoginLayoutBinding.mEdtPhoneNumber.getText().toString().length());
            }
        });
        mLoginLayoutBinding.mEdtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence)) {
                    mLoginLayoutBinding.mBtnLogin.setEnabled(true);
                    mLoginLayoutBinding.mBtnLogin.setAlpha(1.0f);
                } else {
                    mLoginLayoutBinding.mBtnLogin.setEnabled(false);
                    mLoginLayoutBinding.mBtnLogin.setAlpha(0.5f);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mLoginLayoutBinding.mEdtPassword.setSelection(mLoginLayoutBinding.mEdtPassword.getText().toString().length());
            }
        });
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
                    updateImage(file);
                    break;
                case AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE:
                    finish();
                    //执行Toast显示或者其他逻辑处理操作
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 登录接口
     */
    public void sendLogin(String username, final String password) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("mobile", username)
                .addParam("password", password)
                .builder();
        FaceApiTest.getV1ApiServiceTest().mbrLogin(body)
                .compose(RxSchedulers.<ApiModel<UsersEntity>>io_main())
                .subscribe(new RxObserver<UsersEntity>(this, Tag, true) {
                    @Override
                    public void onSuccess(UsersEntity model) {
                        UsersEntity models = FaceApplication.getUserInfoModel();
                        if (null==models){
                            models = new UsersEntity();
                        }
                        model.setPassword(password);
                        models.setPassword(password);
                        models.setAuthentication(model.getAuthentication());
                        models.setDelFlag(model.getDelFlag());
                        models.setDeptId(model.getDeptId());
                        models.setEmail(model.getEmail());
                        models.setInviteCode(model.getInviteCode());
                        models.setLevel(model.getLevel());
                        models.setLinkAddress(model.getLinkAddress());
                        models.setTenantCode(model.getTenantCode());
                        models.setMobile(model.getMobile());
                        models.setPid(model.getPid());
                        models.setStatus(model.getStatus());
                        models.setTeam(model.getTeam());
                        models.setToken(model.getToken());
                        models.setTotalFee(model.getTotalFee());
                        models.setType(model.getType());
                        DataKeeper.put(MainActivity.this, SPConstants.USERINFOMODEL,models);
                        model.setPassword(password);

                        /**登录成功，发出event事件通知页面更新*/
                        DataKeeper.put(MainActivity.this, SPConstants.USERINFOMODEL, model);
                        EventBus.getDefault().post("exitLogin");
//                        setTitle(); 
                        Log.e("cjh>>> ","UserInfoModel :" + new Gson().toJson(model));
                    }
                });
    }

    /**
     * 权限弹窗
     */
    private void forceUpdate(final Context context, final String appName, final String downUrl, final String updateinfo) {
        mDialog = new AlertDialog.Builder(context);
        mDialog.setTitle(appName + "又更新咯！");
        mDialog.setMessage(updateinfo);
        mDialog.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                if (!canDownloadState()) {
//                    showDownloadSetting();
//                    return;
//
                AppInnerDownLode.downLoadApk(MainActivity.this, downUrl, appName);
            }
        }).setCancelable(false).create().show();
    }

    /**
     * 上传头像
     */
    public void updateImage(File file) {
        RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
        RequestBody uploadBody = new MultipartBody.Builder()
                .addFormDataPart("userId", FaceApplication.isLoginId())
                .addFormDataPart("image", "softpo.jpg", body)
                .build();
        FaceApi.getV1ApiService().updateImage(uploadBody)
                .compose(RxSchedulers.<ApiModel<UpdateImageModel>>io_main())
                .subscribe(new RxObserver<UpdateImageModel>(this, Tag, true) {
                    @Override
                    public void onSuccess(UpdateImageModel model) {
                        try {
                            DataKeeper.put(MainActivity.this, "imgPic", model.image);
                            //GlideUtil.displayImage(MoneyActivity.this, mMineLayoutBinding.mImgUserPic, model.image);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 版本更新
     */
    public void getVersion() {
        FaceApiTest.getV1ApiServiceTest().getVersion("Android")
                .compose(RxSchedulers.<ApiModel<VersionModel>>io_main())
                .subscribe(new RxObserver<VersionModel>(this, Tag, false) {
                    @Override
                    public void onSuccess(VersionModel model) {
                        try {
                            Log.e("cjh 版本更新>>>", "VersionModel:" + new Gson().toJson(model));
                            UsersEntity models = FaceApplication.getUserInfoModel();
                            if (null!=models){
                                models.setLinkAddress(model.getLinkAddress()+"");
                            }else{
                                models = new UsersEntity();
                                models.setLinkAddress(model.getLinkAddress()+"");
                            }
                            DataKeeper.put(MainActivity.this, SPConstants.USERINFOMODEL,models);
                            //如果后台返回版本号大于本地版本，提示弹窗用户更新下载最新apk
//                            showVersionDialog(model.getFlg(), model.getDownloadAddress());
                            if (Double.parseDouble(model.getVersionCode()) > Tools.getVersion(MainActivity.this)) {
                                //forceUpdate(MainActivity.this, "MBR", model.getDownloadAddress(), model.getDownloadTips());
                                showVersionDialog(model.getFlg(), model.getLinkAddress(),model.getDownloadTips());
                            }
//                            showVersionDialog(model.getFlg(), "https://obs-30c9.obs.ap-southeast-1.myhuaweicloud.com/aldAndroid-APK/ald_v2.1.1_release.apk.1");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    private void showVersionDialog(int flg, final String url,String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle(getResources().getString(R.string.forget_update_ts)); //设置标题
        builder.setMessage(getResources().getString(R.string.forget_flg_ts)+": "+message); //设置内容
        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getString(R.string.confirm_text), new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
        if (flg != 1) {
            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() { //设置取消按钮
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }

    /**
     * 申请权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT > 22) {
            if (!PermissionsHelper.sdCardPermission(this)) {
                PermissionsHelper.requestPermission(this, "应用需要您的存储权限", 100, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
            } else {
                getVersion();
            }
//            if (!PermissionsHelper.windowPermission(this)) {
//                PermissionsHelper.requestPermission(this, "应用需要您的存储权限", 100, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW});
//            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                if (!Settings.canDrawOverlays(mContext)) {
//                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mContext.getPackageName()));
//                    mContext.startActivity(intent);
//                    return;
//                }
//                //do something...
//            }
        } else {
            getVersion();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //将请求结果传递EasyPermission库处理
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 请求权限成功。
     * 可以弹窗显示结果，也可执行具体需要的逻辑操作
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        getVersion();
    }

    /**
     * 请求权限失败
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        /**
         * 若是在权限弹窗中，用户勾选了'NEVER ASK AGAIN.'或者'不在提示'，且拒绝权限。
         * 这时候，需要跳转到设置界面去，让用户手动开启。
         */
        finish();

    }

    /**
     * 禁止主页面右滑返回
     */
    @Override
    protected boolean enableSliding() {
        return false;
    }

    /**
     * 监听手机返回按钮
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }


    private int tabbar_index = 0;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_tab_home:
                tabbar_index = 0;
                mainLayoutBinding.homeViewPager.setCurrentItem(0, false);
                break;
            case R.id.home_tab_quotation:
                tabbar_index = 1;
                mainLayoutBinding.homeViewPager.setCurrentItem(1, false);
                break;
            case R.id.home_tab_transaction:
                tabbar_index = 2;
                mainLayoutBinding.homeViewPager.setCurrentItem(2, false);
                break;
            case R.id.home_tab_assets:
                if (3 == FaceApplication.getLevel()){
                    changeTabBar(tabbar_index);
                    Toast.makeText(this,"资产模块暂未开放",Toast.LENGTH_SHORT).show();
                }else{
                    tabbar_index = 3;
                    mainLayoutBinding.homeViewPager.setCurrentItem(3, false);
                }
                break;
            default:
                break;
        }
    }

    private void changeTabBar(int tab){
        switch (tab) {
            case 0:
                mainLayoutBinding.homeRgMainTab.check(R.id.home_tab_home);
                break;
            case 1:
                mainLayoutBinding.homeRgMainTab.check(R.id.home_tab_quotation);
                break;
            case 2:
                mainLayoutBinding.homeRgMainTab.check(R.id.home_tab_transaction);
                break;
            case 3:
                mainLayoutBinding.homeRgMainTab.check(R.id.home_tab_assets);
                break;
            default:
                break;

        }
    }
    private static final int TIMER = 999;
    private MyTimeTask task;

    private void setTimer() {
        task = new MyTimeTask(6000, new TimerTask() {
            @Override
            public void run() {
                myHandler.sendEmptyMessage(TIMER);
                //或者发广播，启动服务都是可以的

            }
        });
        task.start();
    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIMER:
                    //在此执行定时操作
//                    Log.e("cjh>>>","来啦");
                    EventBus.getDefault().post("startTimer");
                    break;
                default:
                    break;
            }
        }
    };

    private void stopTimer() {
        task.stop();
    }

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                    setCostomMsg(showMsg.toString());
                }
            } catch (Exception e) {
            }
        }
    }

    private void setCostomMsg(String msg) {
        if (null != msgText) {
            msgText.setText(msg);
            msgText.setVisibility(android.view.View.VISIBLE);
        }
    }

    /**
     *  极光推送广播  清空别名
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setAlias(String str) {
        try {
            if (null != str && str.equals("JPush")) {
                setJPushAlias("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证成功 来刷新
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void VerifySuccess(String str) {
        try {
            if (!TextUtils.isEmpty(str) && str.equals("VerifySuccess")) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fragment 滑到交易
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void goOrder(String str) {
        try {
            if (!TextUtils.isEmpty(str) && str.equals("goOrder")) {
                mainModel.setAdapterItem(2);
                EventBus.getDefault().post("OrderRefresh");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fragment 滑到合约
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void goTransactionBb(String str) {
        try {
            if (!TextUtils.isEmpty(str) && str.equals("goTreaty")) {
                mainModel.setAdapterItem(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 身份认证成功  finish掉前面的所有页面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void finish(String str) {
        try {
            if (!TextUtils.isEmpty(str) && str.equals("IdentitySuccess")) {
                mainModel.setAdapterItem(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录监听事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void login(String model) {
        try {
            if (null != model && model.equals("exitLogin")) {
                finish();
                MainActivity.JUMP(this);
            }else if (null != model && model.equals("goLogin")){
                finish();
                MainActivity.JUMP(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
