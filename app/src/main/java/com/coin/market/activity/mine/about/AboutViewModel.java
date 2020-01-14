package com.coin.market.activity.mine.about;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.coin.market.databinding.ActivityAboutLayoutBinding;

import teng.wang.comment.api.FaceApi;
import teng.wang.comment.base.AppInnerDownLode;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.VersionModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.Tools;

/**
 * @author: Lenovo
 * @date: 2019/7/29
 * @info: 关于我们 ViewModel
 */
public class AboutViewModel extends BaseActivityViewModel<AboutActivity, ActivityAboutLayoutBinding> {

    private AlertDialog.Builder mDialog;

    public AboutViewModel(AboutActivity activity, ActivityAboutLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        setVersion();
        //getVersion(0);
    }

    private void setVersion() {
        getBinding().aboutVersionCode.setText(Tools.getVersionName(getActivity()) + "");
    }

    /**
     * 版本更新
     */
    public void getVersion(final int i) {
        FaceApi.getV1ApiService().getVersion("Android")
                .compose(RxSchedulers.<ApiModel<VersionModel>>io_main())
                .subscribe(new RxObserver<VersionModel>(getActivity(), getActivity().Tag, false) {
                    @Override
                    public void onSuccess(VersionModel model) {
                        try {
                            //如果后台返回版本号大于本地版本，有新版本 显示红点
                            if (Double.parseDouble(model.getVersionCode()) > Tools.getVersion(getActivity())) {
                                getBinding().aboutVersionPoint.setVisibility(View.VISIBLE);
                                if (i>0) {
                                    forceUpdate(getActivity(),"MBR", model.getDownloadAddress(), model.getDownloadTips());
                                }
                            } else {
                                getBinding().aboutVersionPoint.setVisibility(View.GONE);
                                if (i>0) {
                                    Toast.makeText(getActivity(), "您已是最新版本！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
//                }
                AppInnerDownLode.downLoadApk(getActivity(), downUrl, appName);
            }
        }).setCancelable(false).create().show();
    }

}
