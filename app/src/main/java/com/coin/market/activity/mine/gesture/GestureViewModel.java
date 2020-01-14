package com.coin.market.activity.mine.gesture;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.activity.main.MainActivity;
import com.coin.market.databinding.ActivityGestureLayoutBinding;
import com.coin.market.util.androidUtils;
import com.coin.market.wight.gesture.GestureLockLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import teng.wang.comment.SPConstants;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.utils.DataKeeper;

/**
 * @author: Lenovo
 * @date: 2019/8/6
 * @info: 手势解锁 ViewModel
 */
public class GestureViewModel extends BaseActivityViewModel<GestureActivity, ActivityGestureLayoutBinding> {

    private List<Integer> oneGesture = new ArrayList<Integer>();
    private int numb;
    private String pass = "";

    //private int oneGesture;

    public GestureViewModel(GestureActivity activity, ActivityGestureLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        if (TextUtils.isEmpty(getActivity().verify)){
            getBinding().titleBar.imgReturn.setVisibility(View.GONE);
        }
        try {
            initGesture();
            initEvents();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initGesture() {
        getBinding().lGestureLockView.setDotCount(3);
        getBinding().lGestureLockView.setTryTimes(100);
        //getBinding().lGestureLockView.setAnswer(1, 4, 7);
        getBinding().lGestureLockView.setPathWidth(1);
        if (TextUtils.isEmpty(getActivity().verify)){
            getBinding().lGestureLockView.setMode(GestureLockLayout.RESET_MODE);
        }else {
            getBinding().lGestureLockView.setMode(GestureLockLayout.VERIFY_MODE);
            pass = DataKeeper.get(getActivity(), SPConstants.MBR_GESTURRE,"");
            List<Integer> aaa = new ArrayList<Integer>();
            for (int i = 0; i < pass.length(); i++) {
                aaa.add(Integer.parseInt(pass.substring(i, i+1)));
            }
            getBinding().lGestureLockView.setAnswer(aaa);
        }
    }

    private void initEvents() {
        //设置密码的回调
        getBinding().lGestureLockView.setOnLockResetListener(new GestureLockLayout.OnLockResetListener() {
            @Override
            public void onConnectCountUnmatched(int connectCount, int minCount) {

            }

            @Override
            public void onFirstPasswordFinished(List<Integer> answerList) {
                numb = 1;
                //重置
                getBinding().lGestureLockView.resetGesture();
                // 第一次设置手势 保存
                //getBinding().lGestureLockView.setAnswer(answerList);
                // 清空密码
                //oneGesture.clear();
                // 更换为验证密码模式
                //getBinding().lGestureLockView.setMode(GestureLockLayout.VERIFY_MODE);
                Toast.makeText(getActivity(), "请再次设置手势！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSetPasswordFinished(boolean isMatched, List<Integer> answerList) {
                Toast.makeText(getActivity(), "设置密码完成！", Toast.LENGTH_SHORT).show();
                getBinding().lGestureLockView.resetGesture();
                if (TextUtils.isEmpty(pass)){
                    pass = "";
                }
                for (int i = 0; i < answerList.size(); i++) {
                    pass += answerList.get(i)+"";
                }
                DataKeeper.put(getActivity(), SPConstants.MBR_GESTURRE, pass + "");
                String aa =  DataKeeper.get(getActivity(), SPConstants.MBR_GESTURRE, answerList + "");
                EventBus.getDefault().post("GestureSuccess"); // 表示手势设置已打开
                getActivity().finish();
            }
        });

        //验证密码的回调
        getBinding().lGestureLockView.setOnLockVerifyListener(new GestureLockLayout.OnLockVerifyListener() {
            @Override
            public void onGestureSelected(int id) {
                //Toast.makeText(getActivity(), "id:" + id, Toast.LENGTH_SHORT).show();
                androidUtils.vibrator(getActivity(), 70);
                oneGesture.add(id);
            }

            @Override
            public void onGestureFinished(boolean isMatched) { // 验证模式 输入结束
                //Toast.makeText(getActivity(), "输入结束！", Toast.LENGTH_SHORT).show();
                clearGesture(isMatched, oneGesture);
            }

            @Override
            public void onGestureTryTimesBoundary() {
                Toast.makeText(getActivity(), "onGestureTryTimesBoundary", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearGesture(final boolean isMatched, final List<Integer> oneGesture) {
        new Handler().post(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(2000);
                    getBinding().lGestureLockView.resetGesture(); //重置
                    if (isMatched) {
                        Toast.makeText(getActivity(), "欢迎回到" + getActivity().getResources().getString(R.string.app_name), Toast.LENGTH_SHORT).show();
//                        DataKeeper.put(getActivity(), SPConstants.MBR_GESTURRE, oneGesture + "");
                        EventBus.getDefault().post("VerifySuccess");
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), "密码不正确", Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
