package com.coin.market.fragment.assets;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coin.market.R;
import com.coin.market.activity.mine.coinout.CoinOutActivity;
import com.coin.market.activity.mine.coinrecharge.CoinRechargeActivity;
import com.coin.market.activity.mine.transfer.TransferActivity;
import com.coin.market.databinding.FragmentAssetsLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import teng.wang.comment.base.BaseFragment;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.AccountsDTO;
import teng.wang.comment.model.CoinTotalModel;
import teng.wang.comment.model.CoinWorthModel;
import teng.wang.comment.utils.StatusBarUtil;

/**
 * @author 资产Fragment
 * @version v1.0
 * @Time 2018-9-3
 */
public class AssetsFragment extends BaseFragment implements View.OnClickListener {

    private AssetsViewModel assetsViewModel;
    private FragmentAssetsLayoutBinding assetsLayoutBinding;
    public CoinTotalModel model;
    public String accountType = "bb_type";
    public String coinId; //币种ID
    public String coinName; //币种

    public String type = "0";  // 0  显示  1 隐藏
    public static AssetsFragment getAssetsFragment() {
        return new AssetsFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup viewGroup) {
        EventBus.getDefault().register(this);
        assetsLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_assets_layout, viewGroup, false);
        return assetsLayoutBinding.getRoot();
    }

    @Override
    protected void onFragmentFirstVisible() {
        assetsViewModel = new AssetsViewModel(this, assetsLayoutBinding);
        assetsViewModel.initKeyboardStatus();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onFragmentInvisible() {
        getActivity().getWindow().setStatusBarColor(getResources().getColor(teng.wang.comment.R.color.white));//设置状态栏颜色
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//实现状态栏图标和文字颜色为暗色
    }

    @Override
    protected void onFragmentVisible() {
        StatusBarUtil.setColor(getActivity(), getResources().getColor(R.color.app_style_blue), 0);
        if (!EmptyUtil.isEmpty(assetsViewModel)) {
            assetsViewModel.getBBAssetsData(FaceApplication.getToken());
        }
    }

    @Override
    protected void setListener() {
        assetsLayoutBinding.assetsButtonCb.setOnClickListener(this);
        assetsLayoutBinding.assetsButtonTb.setOnClickListener(this);
        assetsLayoutBinding.assetsButtonHz.setOnClickListener(this);
        assetsLayoutBinding.assetsHide.setOnClickListener(this);
    }

    private void setIsHide(CoinTotalModel model) {
        if (model.isHide()) {
            type = "1";
            assetsLayoutBinding.assetsAllMoney.setText("*****");
            assetsLayoutBinding.assetsAllMoneyCny.setText("≈*****CNY");
            assetsLayoutBinding.assetsHide.setImageResource(R.drawable.assets_hide);
            assetsViewModel.setType(type);

        } else {
            type = "0";
            assetsLayoutBinding.assetsAllMoney.setText(StringUtils.double2String(model.getTotalUSDT().doubleValue(), 8) + "");
            assetsLayoutBinding.assetsAllMoneyCny.setText("≈" + (StringUtils.double2String(model.getTotalCny().doubleValue(), 2)) + "CNY");
            assetsLayoutBinding.assetsHide.setImageResource(R.drawable.assets_show);
            assetsViewModel.setType(type);
        }



    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.assets_button_cb: // 充币
                CoinRechargeActivity.JUMP(getActivity(),coinId,coinName);
//                CoinSelectActivity.JUMP(getContext(), "CB");
                break;
            case R.id.assets_button_tb: // 提币
                CoinOutActivity.JUMP(getActivity(),coinId,coinName);
               // CoinSelectActivity.JUMP(getContext(), "TB");
                break;
            case R.id.assets_button_hz: // 划转
                TransferActivity.JUMP(getContext(), accountType);  // 带状态的跳转到划转
                break;
            case R.id.assets_hide:
                if (!EmptyUtil.isEmpty(model)) {
                    if (model.isHide()) {
                        model.setHide(false);
                    } else {
                        model.setHide(true);
                    }
                    setIsHide(model);
                }

                break;
            default:
                break;
        }
    }

    /**
     * 隐藏资产数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void hide(CoinTotalModel model) {
        try {
            if (!EmptyUtil.isEmpty(model)) {
                setIsHide(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 其他功能做了  改变了个人资产的操作  刷新资产
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void clearAssets(String str) {
        try {
            if (!EmptyUtil.isEmpty(str)) {
                if (str.equals("clearAssets")) {
                    assetsViewModel.clearAdapter();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  划转成功  刷新个人资产数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void transfer(String str) {
        try {
            if (!EmptyUtil.isEmpty(str)) {
                if (str.equals("Transfer")) {
//                    assetsViewModel.clearAssetsData(FaceApplication.getToken());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
