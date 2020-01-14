package com.coin.market.activity.home.share;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.coin.market.R;
import com.coin.market.activity.home.sharemore.ShareMoreActivity;
import com.coin.market.activity.home.sharerank.ShareRankActivity;
import com.coin.market.activity.home.sharerule.ShareRuleActivity;
import com.coin.market.databinding.ActivityShareLayoutBinding;
import com.coin.market.util.DateUtils;
import com.coin.market.util.SavePhoto;
import com.coin.market.util.androidUtils;
import com.coin.market.wight.dialog.Share2Dialog;
import com.coin.market.wight.dialog.ShareBannerDialog;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.utils.StatusBarUtil;
import teng.wang.comment.utils.StringUtils;
import teng.wang.comment.utils.ZXingUtils;

/**
 * @author: cjh
 * @date: 2019/10/18
 * @info: 分享
 */
public class ShareActivity extends BaseActivity implements View.OnClickListener {


    private ShareViewModel model;
    private ActivityShareLayoutBinding binding;
    public String shareUrl;

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, ShareActivity.class));
    }

    @Override
    protected void setStatusBar() {
        try {
            StatusBarUtil.setTransparentForImageViewInFragment(this, null);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 50);
            params.setMargins(0, androidUtils.getStatusBarHeight(this), 0, 0);
            binding.titleLayout.setLayoutParams(params);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(true);
//        StatusBarUtil.setTranslucent(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_share_layout);
        model = new ShareViewModel(this, binding);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null!=binding){
            switch (StringUtils.getLanguageStatus()){
                case 0:
                    binding.iv.setImageResource(R.drawable.pr_topbg);
                    binding.llSave.setBackgroundResource(R.drawable.img_bg_share_coinsquare);
                    break;
                case 1:
                    binding.iv.setImageResource(R.mipmap.pic_invitefriends_bg_en);
                    binding.llSave.setBackgroundResource(R.mipmap.pic_poster_analyze_en);
                    break;
                case 2:
                    binding.iv.setImageResource(R.mipmap.pic_invitefriends_bg_ko);
                    binding.llSave.setBackgroundResource(R.mipmap.pic_poster_analyze_ko);
                    break;
                case 3:
                    binding.iv.setImageResource(R.mipmap.pic_invitefriends_bg_ja);
                    binding.llSave.setBackgroundResource(R.mipmap.pic_poster_analyze_ja);
                    break;
            }
        }
    }

    @Override
    protected void initTitleData() {
        if (2==FaceApplication.getLevel()){
            binding.llFy.setVisibility(View.VISIBLE);
        }else{
            binding.llFy.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setListener() {
        binding.shareBack.setOnClickListener(this);
        binding.shareRule.setOnClickListener(this);
        binding.btShareMore.setOnClickListener(this);
        binding.btShareRank.setOnClickListener(this);
        binding.copyUrl.setOnClickListener(this);
        binding.copyCode.setOnClickListener(this);
        binding.shareMdm.setOnClickListener(this);
        binding.shareHb.setOnClickListener(this);
    }

    private void showShareDialog(String url) {
        Share2Dialog dialog = new Share2Dialog(this, url);
        dialog.builder().setCancelable(true).setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void showShareDialog2(String url) {
        ShareBannerDialog dialog = new ShareBannerDialog(this, url, new ShareBannerDialog.SaveCallback() {
            @Override
            public void save() {
                saveCurrentImage();
            }
        });
        dialog.builder().setCancelable(true).setCanceledOnTouchOutside(true);
        dialog.show();
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
        binding.ivVcard.setImageBitmap(ZXingUtils.createQRImage(shareUrl, 200, 200));
        int width = binding.llSave.getWidth();
        int height = binding.llSave.getHeight();
        //生成相同大小的图片
        Bitmap bitmap = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888 );
        View dView = binding.llSave;
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();

        bitmap = Bitmap.createBitmap(dView.getDrawingCache());
        return bitmap;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_back:
                finish();
                break;
            case R.id.bt_share_more: // 我的收益 进入邀请、返佣记录
                ShareMoreActivity.JUMP(this);
                break;
            case R.id.bt_share_rank: // 邀请榜单 更多排行榜
                ShareRankActivity.JUMP(this);
                break;
            case R.id.share_rule: // 规则
//                ShareRuleActivity.JUMP(this);
                break;
            case R.id.copy_url: // 复制链接
                androidUtils.getCopyText(this, binding.tvUrl.getText().toString());
                break;
            case R.id.copy_code: // 复制邀请码
                androidUtils.getCopyText(this, binding.tvCode.getText().toString());
                break;
            case R.id.share_mdm:
                showShareDialog(shareUrl);
                break;
            case R.id.share_hb:
                //initFragment();
                showShareDialog2(FaceApplication.getLinkAddress());
                break;
            default:
                break;
        }
    }


}
