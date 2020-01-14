package com.coin.market.activity.mine.settings;

import android.text.TextUtils;

import com.coin.market.databinding.ActivitySettingsLayoutBinding;

import org.greenrobot.eventbus.EventBus;

import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.DataKeeper;

/**
 * @author: Lenovo
 * @date: 2019/7/29
 * @info: 设置ViewModel
 */
public class SettingsViewModel extends BaseActivityViewModel <SettingsActivity, ActivitySettingsLayoutBinding>{

    public SettingsViewModel(SettingsActivity activity, ActivitySettingsLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {

    }

    /**
     *  退出App
     */
    public void loginOut(String token) {
        if (!TextUtils.isEmpty(token)){
            FaceApiTest.getV1ApiServiceTest().loginOut(token)
                    .compose(RxSchedulers.<ApiModel<String>>io_main())
                    .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, true) {
                        @Override
                        public void onSuccess(String str) {
                            try {

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
        getActivity().loginOut();
    }



}
