package com.coin.market.activity.home.web;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.coin.market.databinding.ActivityHelpCenterLayoutBinding;

import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.utils.log.Log;

/**
 * @author: Lenovo
 * @date: 2019/8/7
 * @info: 帮助中心ViewModel
 */
public class HomeWebViewModel extends BaseActivityViewModel <HomeWebActivity, ActivityHelpCenterLayoutBinding>{

    private String url;

    public HomeWebViewModel(HomeWebActivity activity, ActivityHelpCenterLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        url = getActivity().getIntent().getStringExtra("url");
        getWeb();
    }

    private void getWeb() {
        WebSettings webSettings = getBinding().helpCenterWebview.getSettings();
        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        //设置支持缩放
        webSettings.setBuiltInZoomControls(true);
        getBinding().helpCenterWebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        //加载需要显示的网页
        getBinding().helpCenterWebview.loadUrl(url);
        //设置Web视图
        //getBinding().myWebView.setWebViewClient(new webViewClient());

        getBinding().helpCenterWebview.setWebChromeClient(new WebChromeClient() {
            /*** 视频播放相关的方法 **/
            @Override
            public View getVideoLoadingProgressView() {
                FrameLayout frameLayout = new FrameLayout(getActivity());
                frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                return frameLayout;
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
            }

            @Override
            public void onHideCustomView() {
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                Log.e("WebActivity>>>", ">>>onProgressChanged");
                // TODO 自动生成的方法存根

                if (newProgress == 100) {
                    getBinding().webViewFind.setVisibility(View.GONE);//加载完网页进度条消失
                    //getWebUrl = webview.getUrl();
                } else {
                    getBinding().webViewFind.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    getBinding().webViewFind.setProgress(newProgress);//设置进度值
                }

            }
        });

    }

}
