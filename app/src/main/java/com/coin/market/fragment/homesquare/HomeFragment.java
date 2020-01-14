package com.coin.market.fragment.homesquare;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coin.market.R;
import com.coin.market.activity.home.feedback.PublishFeedbackActivity;
import com.coin.market.activity.home.share.ShareActivity;
import com.coin.market.activity.mine.about.AboutActivity;
import com.coin.market.activity.mine.coinout.CoinOutActivity;
import com.coin.market.activity.mine.coinrecharge.CoinRechargeActivity;
import com.coin.market.activity.mine.fingerprint.FingerprintActivity;
import com.coin.market.activity.mine.identity.IdentityActivity;
import com.coin.market.activity.mine.security.SecurityCentreActivity;
import com.coin.market.activity.mine.settings.SettingsActivity;
import com.coin.market.activity.mine.transfer.TransferActivity;
import com.coin.market.databinding.FragmentHomesqureLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.MyTimeTask;
import com.example.qrcode.Constant;
import com.example.qrcode.ScannerActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.TimerTask;

import teng.wang.comment.base.BaseFragment;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.utils.StatusBarUtil;
import teng.wang.comment.utils.jurisdiction.PermissionsHelper;
import teng.wang.comment.widget.BannerViewPager;

public class HomeFragment extends BaseFragment implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private final int RESULT_REQUEST_CODE = 1;
    private HomeViewModel homeViewModel;
    private FragmentHomesqureLayoutBinding mHomeLayoutBinding;
    private LinearLayout mine_user_button, mine_cb, mine_tb, mine_hz; // 个人信息，充币，提币，划转
    private TextView mine_name, mine_uid;
    public String id_flag; // 判断商家入驻  返回 -1 表示没有申请商户  返回 0 代表 审核中  1 代表审核完成 2  代表撤销 此次申请
    public String CoinId = "";
    public String CoinName = "";
    public List<BannerViewPager.BannerItemBean> list;
    public static HomeFragment getHomeFragment() {
        return new HomeFragment();
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup viewGroup) {
        mHomeLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_homesqure_layout, viewGroup, false);
        return mHomeLayoutBinding.getRoot();
    }

    @Override
    protected void onFragmentVisible() {
        super.onFragmentVisible();
        StatusBarUtil.setColor(getActivity(), getResources().getColor(R.color.white), 0);
        try {

            if (!EmptyUtil.isEmpty(homeViewModel)){

                if (FaceApplication.isLogin()) {
                    if (TextUtils.isEmpty(FaceApplication.getMobile())){
                        homeViewModel.getUserInfo(FaceApplication.getToken());
                    }
                    setUserData();
                    homeViewModel.getCoinList();

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onFragmentFirstVisible() {
        StatusBarUtil.setColor(getActivity(), getResources().getColor(R.color.white), 0);
        EventBus.getDefault().register(this);
        homeViewModel = new HomeViewModel(this, mHomeLayoutBinding);
        mHomeLayoutBinding.appBarMain.titleBar.imgReturn.setVisibility(View.VISIBLE);
        mHomeLayoutBinding.appBarMain.titleBar.titleLine.setVisibility(View.VISIBLE);
//        if (TextUtils.isEmpty(DataKeeper.get(getContext(), "imgPic", ""))) {
//            mHomeLayoutBinding.appBarMain.titleBar.imgUserPic.setVisibility(View.VISIBLE);
//            GlideUtil.displayImage(getContext(), mHomeLayoutBinding.appBarMain.titleBar.imgUserPic, FaceApplication.getUserPic());
//        } else {
//            mHomeLayoutBinding.appBarMain.titleBar.imgUserPic.setImageResource(R.mipmap.ic_launcher);
//        }

        mHomeLayoutBinding.navView.setItemIconTintList(null);  // 这一句是设置NavigationView 图标不变为灰色
        homeViewModel.initKeyboardStatus();
    }

    @Override
    protected void setListener() {
        mHomeLayoutBinding.appBarMain.titleBar.mImgScan.setOnClickListener(this);
        mHomeLayoutBinding.appBarMain.titleBar.imgUserPic.setOnClickListener(this);
        mHomeLayoutBinding.appBarMain.titleBar.mTvPreservation.setOnClickListener(this);
//        mHomeLayoutBinding.app.homeShareButton.setOnClickListener(this);
//        mHomeLayoutBinding.appBarMain.homeFbButton.setOnClickListener(this);
//        mHomeLayoutBinding.appBarMain.homeUseragreement.setOnClickListener(this);
//        mHomeLayoutBinding.appBarMain.homeHelpCenterButton.setOnClickListener(this);
//        mHomeLayoutBinding.appBarMain.homeRealnameauthentication.setOnClickListener(this);

        //Navigation  抽屉布局回调
        mHomeLayoutBinding.navView.setNavigationItemSelectedListener(this);
        View handerView = mHomeLayoutBinding.navView.getHeaderView(0);
        mine_user_button = handerView.findViewById(R.id.mine_user_button);
        mine_cb = handerView.findViewById(R.id.mine_cb);
        mine_tb = handerView.findViewById(R.id.mine_tb);
        mine_hz = handerView.findViewById(R.id.mine_hz);
        mine_name = handerView.findViewById(R.id.mine_user_name);
        mine_uid = handerView.findViewById(R.id.mine_user_uid);
        mine_user_button.setOnClickListener(this);
        mine_cb.setOnClickListener(this);
        mine_tb.setOnClickListener(this);
        mine_hz.setOnClickListener(this);

        setTimer();
    }

    @Override
    protected void initTitleData() {
        mHomeLayoutBinding.appBarMain.titleBar.txtTitle.setText(getActivity().getResources().getString(R.string.app_name));
        mHomeLayoutBinding.appBarMain.titleBar.titleImg.setVisibility(View.GONE);
        mHomeLayoutBinding.appBarMain.titleBar.imgUserPic.setVisibility(View.VISIBLE);
        mHomeLayoutBinding.appBarMain.titleBar.mImgLogo.setVisibility(View.VISIBLE);
        mHomeLayoutBinding.appBarMain.titleBar.imgUserPic.setImageResource(R.drawable.ic_personalcenter);
        mHomeLayoutBinding.appBarMain.titleBar.mImgLogo.setImageResource(R.mipmap.logo);
    }

    public void setUserData() {
        if (FaceApplication.isLogin()) {
            mine_name.setText("Hi：" + FaceApplication.getMobile());
//            mine_uid.setText("UID：" + uid);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_share_button: // 跳转到分享
                ShareActivity.JUMP(getContext());
                break;
//            case R.id.home_fb_button: // 法币交易  广播通知 转到法币
//                EventBus.getDefault().post("goTransactionFb");
//                break;
            case R.id.home_realnameauthentication: // 实名认证
                IdentityActivity.JUMP(getActivity());
                break;
            case R.id.home_help_center_button: // 问题反馈
                PublishFeedbackActivity.JUMP(getActivity());
//                HomeWebActivity.JUMP(getActivity(), "帮助中心", "https://www.baidu.com/");
                break;
            case R.id.img_user_pic:
                mHomeLayoutBinding.fragmentHomeLayout.openDrawer(Gravity.LEFT); // 打开
                // mHomeLayoutBinding.fragmentHomeLayout.closeDrawer(Gravity.LEFT); // 关闭
                break;
            case R.id.mine_user_button: // Navigation HeaderView 个人信息  顶部的布局点击
                FingerprintActivity.JUMP(getActivity());
                break;
            case R.id.mine_cb: // Navigation HeaderView 充币 button
                if (TextUtils.isEmpty(CoinId)){
                    homeViewModel.getCoinList();
                    CoinRechargeActivity.JUMP(getActivity(),CoinId,CoinName);
                    return;
                }
                CoinRechargeActivity.JUMP(getActivity(),CoinId,CoinName);
                //CoinSelectActivity.JUMP(getActivity(), "CB");
                break;
            case R.id.mine_tb: // Navigation HeaderView 提币 button
                if (TextUtils.isEmpty(CoinId)){
                    homeViewModel.getCoinList();
                    CoinOutActivity.JUMP(getActivity(),CoinId,CoinName);
                    return;
                }
                CoinOutActivity.JUMP(getActivity(),CoinId,CoinName);
//                CoinSelectActivity.JUMP(getActivity(), "TB");
                break;
            case R.id.mine_hz: // Navigation HeaderView 划转 button
                TransferActivity.JUMP(getActivity());
                break;
            case R.id.mImg_Scan:
                if (Build.VERSION.SDK_INT > 22) {
                    if (!PermissionsHelper.cameraPermission(getActivity())) {
                        PermissionsHelper.requestPermission(getActivity(), "应用需要您的相机权限", 100, new String[]{Manifest.permission.CAMERA});
                    } else {
                        goScanner();
                    }
                } else {
                    goScanner();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 相机
     */
    private void goScanner() {
        try {
            Intent intent = new Intent(getContext(), ScannerActivity.class);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 登录监听事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void number(String model) {
        try {
            if (null != model && model.equals("Transfer")) {
                // mHomeViewModel.getUserInfo(FaceApplication.isLoginId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void spplyShop(String model) {
        try {
            if (null != model && model.equals("ApplyShop")) {
                homeViewModel.getUserInfo(FaceApplication.getToken());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 开启定时任务 刷新首页热门币种  上方货币行情
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void stopTimer(String model) {
        try {
            if (null != model && model.equals("startTimer")) {
                homeViewModel.getCoin();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
//            case R.id.nav_order:    //订单管理
//                CoinOrderActivity.JUMP(getActivity());
//                break;
//            case R.id.nav_publish:    //我的广告
//                MyPublishActivity.JUMP(getActivity());
//                break;
//            case R.id.nav_identity: //身份认证
//
//                break;
//            case R.id.nav_pay_mode: //收款方式
//                PayModeActivity.JUMP(getActivity());
//                break;
            case R.id.nav_security: //安全中心
                SecurityCentreActivity.JUMP(getActivity());
                break;
//            case R.id.nav_shop: //商家入驻
//                switch (id_flag) {
//                    case "-1":
//                        // 没有申请
//                        SettledEditActivity.JUMP(getActivity());
//                        break;
//                    case "0":
//                        // 已申请 审核中
//                        SettledDataActivity.JUMP(getActivity(), "1");
//                        break;
//                    case "1":
//                        // 已申请 审核中
//                        SettledDataActivity.JUMP(getActivity(), "2");
//                        break;
//                    case "2":
//                        // 2  代表撤销 此次申请
//                        SettledEditActivity.JUMP(getActivity());
//                        break;
//                    default:
//                        break;
//                }
//                break;
//            case R.id.nav_about: //关于我们
//                AboutActivity.JUMP(getActivity());
//                break;
//            case R.id.nav_service:// 在线客服
//                //ServiceActivity.JUMP(getActivity());
//                break;
            case R.id.nav_settings: // 设置
                SettingsActivity.JUMP(getActivity());
                break;
            default:
                break;
        }
//        DrawerLayout drawer = mHomeLayoutBinding.fragmentHomeLayout;
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private static final int TIMER = 999;
    private MyTimeTask task;

    private void setTimer() {
        task = new MyTimeTask(5000, new TimerTask() {
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
                    if (null!=homeViewModel){
//                        homeViewModel.getCoin();
                        homeViewModel.getRankingTimer();
                    }
                    break;
                default:
                    break;
            }
        }
    };

}
