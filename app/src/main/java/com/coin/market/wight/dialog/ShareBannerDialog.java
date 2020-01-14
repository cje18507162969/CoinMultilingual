package com.coin.market.wight.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coin.market.R;

import java.util.Arrays;
import java.util.List;

import teng.wang.comment.utils.StringUtils;
import teng.wang.comment.utils.ZXingUtils;
import teng.wang.comment.widget.BannerViewPager;

/**
 * @author 从下方弹出  列表选择item
 * @version v1.0
 * @Time 2018-8-22
 */
public class ShareBannerDialog implements OnClickListener {

    private Context context;
    private Dialog dialog;
    private Display display;
    private boolean isClickCancl = false;
    private LinearLayout bt_save;
    private TextView cancel, tv1, tv2, tv3;
    private RelativeLayout layout_1, layout_2, layout_3;
    private ImageView imageView;
    private ImageView img1;
    private String shareUrl = "";
    public SaveCallback mCallback;

    public ShareBannerDialog(Context context, String url,SaveCallback callback) {
        this.context = context;
        this.shareUrl = url;
        mCallback = callback;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public boolean isClickCancl() {
        return isClickCancl;
    }

    public void show() {
        dialog.show();
    }

    @SuppressWarnings("deprecation")
    public ShareBannerDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(
                R.layout.dialog_share_banner_layout, null);

        // 设置Dialog最小宽度为屏幕宽度
        view.setMinimumWidth(display.getWidth());

        layout_1 = view.findViewById(R.id.layout_1);
        layout_2 = view.findViewById(R.id.layout_2);
        layout_3 = view.findViewById(R.id.layout_3);
        tv1 = view.findViewById(R.id.tv1);
        tv2 = view.findViewById(R.id.tv2);
        tv3 = view.findViewById(R.id.tv3);
        img1 = view.findViewById(R.id.img1);
        bt_save = (LinearLayout) view.findViewById(R.id.bt_save);
        cancel = (TextView) view.findViewById(R.id.share_cancel);

        switch (StringUtils.getLanguageStatus()){
            case 0:
                img1.setImageResource(R.drawable.img_share_coinsquare);
                break;
            case 1:
                img1.setImageResource(R.mipmap.pic_invitefriendsanalysis_en);
                break;
            case 2:
                img1.setImageResource(R.mipmap.pic_invitefriendsanalysis_ko);
                break;
            case 3:
                img1.setImageResource(R.mipmap.pic_invitefriendsanalysis_ja);
                break;
        }

        layout_1.setOnClickListener(this);
        layout_2.setOnClickListener(this);
        layout_3.setOnClickListener(this);
        cancel.setOnClickListener(this);
        bt_save.setOnClickListener(this);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);

        imageView = new ImageView(context);
        List<Integer> imgs = Arrays.asList(R.drawable.img_share_coinsquare, R.drawable.share_img_2, R.drawable.share_img_3);
        for (int i = 0; i < 3; i++) {
            BannerViewPager.BannerItemBean bean = new BannerViewPager.BannerItemBean();
            bean.setPic(imgs.get(i) + "");
        }

        return this;
    }

    private void save() {
        mCallback.save();

//        imageView.setImageResource(R.drawable.img_bg_share_coinsquare);
//        try {
//            Bitmap bitmap = ImageUtil.drawTextToLeftBottom(context, ((BitmapDrawable) imageView.getDrawable()).getBitmap(), "欢迎注册Coin", 18, R.color.ksw_md_solid_disable_273, 10, 10);
//            Bitmap bitmap1 = ImageUtil.drawTextToLeftBottom(context, bitmap, "邀请你加入Coin Square交易所，长按识别", 14, R.color.ksw_md_solid_disable_4d, 10, 10);
//            Bitmap bitmap2 = ImageUtil.createWaterMaskRightBottom(context, bitmap1, ((BitmapDrawable) getQr(shareUrl).getDrawable()).getBitmap(), 10, 10);
//            ImageView img = new ImageView(context);
//            SavePhoto savePhoto = new SavePhoto(context);
//            savePhoto.saveBitmap(bitmap2, DateUtils.getTime() + ".JPEG");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private ImageView getQr(String str) {
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(ZXingUtils.createQRImage(str, 120, 120));
        return imageView;
    }

    public ShareBannerDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public ShareBannerDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    private void selectImg(int item) {
        tv1.setVisibility(View.VISIBLE);
        tv2.setVisibility(View.VISIBLE);
        tv3.setVisibility(View.VISIBLE);
        switch (item) {
            case R.id.layout_1:
                tv1.setVisibility(View.GONE);
                break;
            case R.id.layout_2:
                tv2.setVisibility(View.GONE);
                break;
            case R.id.layout_3:
                tv3.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_cancel:
                dialog.dismiss();
                break;
            case R.id.layout_1:
                selectImg(view.getId());
//                imageView.setImageResource(R.drawable.share_img_1);
                break;
            case R.id.layout_2:
                selectImg(view.getId());
                imageView.setImageResource(R.drawable.share_img_2);
                break;
            case R.id.layout_3:
                selectImg(view.getId());
                imageView.setImageResource(R.drawable.share_img_3);
                break;
            case R.id.bt_save:
//                if (tv1.getVisibility()==View.VISIBLE && tv2.getVisibility()==View.VISIBLE && tv3.getVisibility()==View.VISIBLE){
//                    Toast.makeText(context, "请选择图片", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                save();
                dialog.dismiss();
                break;
            case R.id.share_cancel:
                dialog.dismiss();
                break;
            default:
                break;

        }
    }

    private OnItemClickListener onItemClickListener;

    public void setOnClickListener(OnItemClickListener onPosNegClickListener) {
        this.onItemClickListener = onPosNegClickListener;
    }

    public interface OnItemClickListener {
        void onClick(String title);
    }

    /**
     * 成功
     */
    public interface SaveCallback {
        void save();
    }
}