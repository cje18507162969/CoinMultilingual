package com.coin.market.activity.home.feedback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coin.market.R;
import com.coin.market.adapter.shopadapter.FeedBackImgAdapter;
import com.coin.market.adapter.shopadapter.GridImageFeedAdapter;
import com.coin.market.databinding.ActivityPublishFeedbackLayoutBinding;
import com.coin.market.oss.Constances;
import com.coin.market.oss.OSSUtils;
import com.coin.market.oss.OssService;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.ImageUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teng.wang.comment.api.FaceApi;
import teng.wang.comment.api.FaceApiMbr;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.api.RequestBodyUtils;
import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.FeedBackImgModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.FullyGridLayoutManager;
import teng.wang.comment.utils.PictureSelectorUtil;
import teng.wang.comment.utils.StatusBarUtil;
import teng.wang.comment.utils.log.Log;
import teng.wang.comment.widget.SVProgressHUD;

/**
 * @author 发表反馈
 * @version v1.0
 * @Time 2018-9-14
 */

public class PublishFeedbackActivity extends BaseActivity implements View.OnClickListener {

    private Context context;
    private List<LocalMedia> selectList = new ArrayList<>();
    private ActivityPublishFeedbackLayoutBinding mPublishFeedbackLayoutBinding;
    private PublishFeedbackViewModel model;
    public String type;
    private List<FeedBackImgModel> imgModels = new ArrayList<FeedBackImgModel>();
    public List<String> imgs = new ArrayList<String>();
    private File file;
    public int itemImg;

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, PublishFeedbackActivity.class));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        context = this;
        mPublishFeedbackLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_publish_feedback_layout);
        model = new PublishFeedbackViewModel(this, mPublishFeedbackLayoutBinding);
    }

    @Override
    protected void initTitleData() {
        for (int i = 0; i < 3; i++) {
            FeedBackImgModel bean = new FeedBackImgModel();
            imgModels.add(bean);
        }
        selectList = new ArrayList<>();
        mPublishFeedbackLayoutBinding.titleBar.txtTitle.setText(getResources().getString(R.string.home_feedback));
        mPublishFeedbackLayoutBinding.titleBar.mTvPreservation.setVisibility(View.VISIBLE);
        mPublishFeedbackLayoutBinding.titleBar.mTvPreservation.setTextColor(this.getResources().getColor(R.color.color_999999));
        mPublishFeedbackLayoutBinding.mEdtFeedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() >= 1) {
                    mPublishFeedbackLayoutBinding.mBtnExitLogon.setAlpha(1.0f);
                } else {
                    mPublishFeedbackLayoutBinding.mBtnExitLogon.setAlpha(0.5f);
                }
                mPublishFeedbackLayoutBinding.mTvDigitDisplay.setText("" + String.valueOf(charSequence.toString().length() + "/150"));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPublishFeedbackLayoutBinding.mEdtFeedback.setSelection(mPublishFeedbackLayoutBinding.mEdtFeedback.getText().toString().length());
            }
        });
    }

    @Override
    protected void setListener() {
        mPublishFeedbackLayoutBinding.mBtnExitLogon.setOnClickListener(this);
        mPublishFeedbackLayoutBinding.titleBar.imgReturn.setOnClickListener(this);
        mPublishFeedbackLayoutBinding.titleBar.mTvPreservation.setOnClickListener(this);
        mPublishFeedbackLayoutBinding.titleBar.mTvPreservation.setText(getResources().getString(R.string.transaction_fb_his_list));
        mPublishFeedbackLayoutBinding.iv1.setOnClickListener(this);
        mPublishFeedbackLayoutBinding.iv2.setOnClickListener(this);
        mPublishFeedbackLayoutBinding.iv3.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mBtn_Exit_logon:
                if (TextUtils.isEmpty(mPublishFeedbackLayoutBinding.mEdtFeedback.getText().toString().trim())) {
                    Toast.makeText(this, getResources().getString(R.string.tv_please_describe), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(type)) {
                    Toast.makeText(this, getResources().getString(R.string.tv_please_seltype), Toast.LENGTH_SHORT).show();
                    return;
                }
//                for (int i = 0; i < imgModels.size(); i++) {
//                    if (!EmptyUtil.isEmpty(imgModels.get(i).getImgUrl())){
//                        imgs.add(imgModels.get(i).getImgUrl());
//                    }
//                }
                model.saveIssueFeedback(mPublishFeedbackLayoutBinding.mEdtFeedback.getText().toString().trim(), type, getPic());
                break;
            case R.id.img_Return:
                finish();
                break;
            case R.id.iv1:
                openPicture(1);
                break;
            case R.id.iv2:
                openPicture(2);
                break;
            case R.id.iv3:
                openPicture(3);
                break;
            case R.id.mTv_Preservation:
                FeedbackActivity.JUMP(this);
                break;
            default:
                break;
        }
    }

    /**
     * 拼接图片地址 ，隔开
     *
     */
    StringBuilder sb = null;
    private String getPic(){
        sb = new StringBuilder();
        if (TextUtils.isEmpty(imageurl)){
            return "";
        }
        if (!TextUtils.isEmpty(imageurl1)){
            sb= sb.append(imageurl).append(",").append(imageurl1);
            if (!TextUtils.isEmpty(imageurl1)){
                sb.append(",").append(imageurl2);
            }

        }else {
            return imageurl;
        }

        return sb.toString().trim();

    }

    private void openPicture(int itemImg){
        this.itemImg = itemImg;
        PictureSelectorUtil.startPictureSelectorActivity((Activity) context, 1, selectList);
    }


    List<LocalMedia> localMedia = new ArrayList<>();
    //问题反馈图片
    String imageurl = "";
    String imageurl1 = "";
    String imageurl2 = "";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    localMedia = new ArrayList<>();
                    localMedia = PictureSelector.obtainMultipleResult(data);
                    file = new File(localMedia.get(0).getPath());
                    try {
                        OSSUtils.getInstance().put("", localMedia.get(0).getPath(), "0", new OSSUtils.Callback() {
                            @Override
                            public void complete(boolean success, String msg, String localFile, String filenamerul) {
                                if (success) {
                                    if (filenamerul != null) {

                                        System.out.println("line 上传成功后的新图片地址：" + filenamerul);
                                        switch (itemImg) {
                                            case 1:
                                                mPublishFeedbackLayoutBinding.iv1.setImageBitmap(
                                                        ImageUtil.getLoacalBitmap(
                                                                localMedia.get(0).getPath()));
                                                mPublishFeedbackLayoutBinding.iv2.setVisibility(View.VISIBLE);
                                                imageurl = filenamerul;
                                                break;
                                            case 2:
                                                mPublishFeedbackLayoutBinding.iv3.setVisibility(View.VISIBLE);
                                                mPublishFeedbackLayoutBinding.iv2.setImageBitmap(
                                                        ImageUtil.getLoacalBitmap(
                                                                localMedia.get(0).getPath()));
                                                imageurl1 = filenamerul;
                                                break;
                                            case 3:
                                                mPublishFeedbackLayoutBinding.iv3.setImageBitmap(
                                                        ImageUtil.getLoacalBitmap(
                                                                localMedia.get(0).getPath()));
                                                imageurl2 = filenamerul;
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }
                                if (!success) {
                                    Toast.makeText(PublishFeedbackActivity.this, "fail",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


//                        updateImage(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //addImage(data);
                    break;
                default:
                    break;
            }
        }
    }

    //阿里云上传
    public void OssUploadImage(String filename,String filePath) {
        //初始化OssService类，参数分别是Content，accessKeyId，accessKeySecret，endpoint，bucketName（后4个参数是您自己阿里云Oss中参数）
        OssService ossService = new OssService(
                this,
                Constances.AccessKeyId,
                Constances.AccessKeySecret,
                Constances.EndPoint,
                Constances.BucketName);
        //初始化OSSClient
        ossService.initOSSClient();
        //开始上传，参数分别为content，上传的文件名filename，上传的文件路径filePath
        ossService.beginupload(this, filename, filePath);
        //上传的进度回调
        ossService.setProgressCallback(new OssService.ProgressCallback() {
            @Override
            public void onProgressCallback(final double progress) {
                Log.d("cje上传进度：" + progress);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                    }
                });
            }
        });
    }
    /**
     * 上传图片
     */
    public void updateImage(File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        FaceApiMbr.getV1ApiServiceMbr().uploadQr(body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(this, Tag, true) {
                    @Override
                    public void onSuccess(String str) {
                        try {
                            switch (itemImg) {
                                case 1:
                                    Glide.with(context).load(str).into(mPublishFeedbackLayoutBinding.iv1);
                                    mPublishFeedbackLayoutBinding.iv2.setVisibility(View.VISIBLE);
                                    imgModels.get(0).setImgUrl(str);
                                    break;
                                case 2:
                                    Glide.with(context).load(str).into(mPublishFeedbackLayoutBinding.iv2);
                                    mPublishFeedbackLayoutBinding.iv3.setVisibility(View.VISIBLE);
                                    imgModels.get(1).setImgUrl(str);
                                    break;
                                case 3:
                                    Glide.with(context).load(str).into(mPublishFeedbackLayoutBinding.iv3);
                                    imgModels.get(2).setImgUrl(str);
                                    break;
                                default:
                                    break;
                            }
                            //Toast.makeText(PublishFeedbackActivity.this, "" + str, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
