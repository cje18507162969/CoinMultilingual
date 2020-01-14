package com.coin.market.activity.mine.coinrecharge;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.activity.mine.coinrecord.CoinRecordActivity;
import com.coin.market.databinding.ActivityCoinRechargeLayoutBinding;
import com.coin.market.fragment.coins.CoinsFragment;
import com.coin.market.util.DateUtils;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.SavePhoto;
import com.youth.banner.Banner;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.AccountsDTO;
import teng.wang.comment.utils.ZXingUtils;

/**
 * @author: Lenovo
 * @date: 2019/7/15
 * @info: 币种充值  显示二维码 密钥地址
 */
public class CoinRechargeActivity extends BaseActivity implements View.OnClickListener {

    private ActivityCoinRechargeLayoutBinding binding;
    private CoinRechargeViewModel model;
    private CoinsFragment fragment;
    private FragmentTransaction transaction;
    public String coinId, coinName;
    public List<AccountsDTO> adList;
    public String imgurl;

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, CoinRechargeActivity.class));
    }

    public static void JUMP(Context context, String coinId, String coinName) {
        context.startActivity(new Intent(context, CoinRechargeActivity.class)
                .putExtra("coinId", coinId)
                .putExtra("coinName", coinName));
    }

    public static void JUMP(Context context, String coinId, String coinName, List<AccountsDTO> adList) {
        context.startActivity(new Intent(context, CoinRechargeActivity.class)
                .putExtra("coinId", coinId)
                .putExtra("coinName", coinName)
                .putExtra("adList",(Serializable) adList));
    }
    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_coin_recharge_layout);
        model = new CoinRechargeViewModel(this, binding);
        if (!EmptyUtil.isEmpty(coinName)){
            binding.rechargeCoinName.setText(coinName);
        }
        //initFragment();
    }

    @Override
    protected void initIntentData() {
        coinId = getIntent().getStringExtra("coinId");
        coinName = getIntent().getStringExtra("coinName");
    }

    @Override
    protected void initTitleData() {
        //binding.titleBar.txtTitle.setText("币种选择");
        binding.titleBar.txtTitle.setVisibility(View.GONE);
        binding.titleBar.mImgScan.setVisibility(View.VISIBLE);
        binding.titleBar.mImgScan.setImageResource(R.drawable.mine_history);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.coinRechargeCopy.setOnClickListener(this);
        binding.titleBar.mImgScan.setOnClickListener(this);
        binding.coinsButton.setOnClickListener(this);
        binding.saveQrButton.setOnClickListener(this);
    }

    private void initFragment() {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_slide_in,R.anim.anim_slide_out);
        if (null==fragment){
            fragment = new CoinsFragment();
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("adList",(Serializable) adList);
//            fragment.setArguments(bundle);
            transaction.add(R.id.coins_fr_layout, fragment, fragment.getTag());
            fragment.fragmentisHidden(transaction);
        }else {
            if (fragment.isVisible()){
                transaction.hide(fragment);
            }else {
                transaction.show(fragment);
            }
        }
        transaction.commit();

        // 选择币种的回调
        fragment.setListener(new CoinsFragment.mOnClickCallback() {
            @Override
            public void selectItem(String id, String name, String minNumb) {
                coinId = id;
                coinName = name;
                if (!EmptyUtil.isEmpty(coinName)){
                    binding.rechargeCoinName.setText(coinName);
                }
                model.getCoinAddress(FaceApplication.getToken(), id);
            }
        });
    }


    //这种方法状态栏是空白，显示不了状态栏的信息
    private void saveCurrentImage() {
        //获取当前屏幕的大小
        Bitmap bitmap2 = getScreenShot();
        if (bitmap2 != null) {
            SavePhoto savePhoto = new SavePhoto(this);
            savePhoto.saveBitmap(bitmap2, DateUtils.getTime() + ".JPEG");
        }


    }
    private Bitmap getScreenShot() {
        //获取当前屏幕的大小
        //获取当前屏幕的大小
        binding.coinRechargeQr.setImageBitmap(ZXingUtils.createQRImage(imgurl, 800, 800));
        int width = binding.coinRechargeQr.getWidth();
        int height = binding.coinRechargeQr.getHeight();
        //生成相同大小的图片
        Bitmap bitmap = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888 );
        View dView = binding.coinRechargeQr;
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();

        bitmap = Bitmap.createBitmap(dView.getDrawingCache());
        return bitmap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_Return: // 退出
                finish();
                break;
            case R.id.save_qr_button: // 保存二维码
                try {
                    saveCurrentImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.coins_button: // 选择币种
                initFragment();
                break;
            case R.id.coin_recharge_copy: // 保存 币地址
                // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(binding.coinRechargeAddress.getText());
                Toast.makeText(mContext, getResources().getString(R.string.forget_ycopy), Toast.LENGTH_SHORT).show();
                break;
            case R.id.mImg_Scan: // 充币历史
                CoinRecordActivity.JUMP(this, "CB", coinId,""+coinName);
                break;
            default:
                break;
        }
    }

}
