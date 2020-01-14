package com.coin.market.activity.home.share;

import com.coin.market.databinding.ActivityShareLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;
import com.google.gson.Gson;

import java.util.List;

import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.ShareModel;
import teng.wang.comment.model.ShareProfitModel;
import teng.wang.comment.model.ShareRankModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.log.Log;

/**
 * @author: cjh
 * @date: 2019/10/18
 * @info: 分享 ViewModel
 */
public class ShareViewModel extends BaseActivityViewModel <ShareActivity, ActivityShareLayoutBinding>{

    public ShareViewModel(ShareActivity activity, ActivityShareLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        getShare(0, FaceApplication.getToken());
//        getShareRank(FaceApplication.getToken(), 1, 1);
        getShareMyProfit(FaceApplication.getToken(), 1);
        getBinding().tvCode.setText(FaceApplication.getInviteCode());
    }

    /**
     *  获取 我的链接 我的邀请码
     */
    public void getShare(int showType, String token) {
        FaceApiTest.getV1ApiServiceTest().getShareCode(token)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, showType == 1 ? true : false) {
                    @Override
                    public void onSuccess(String entity) {
                        Log.e("cjh>>>>","我的邀请码 "+entity);
                        try {
                            getActivity().shareUrl = entity;
                            getBinding().tvUrl.setText(entity);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     *  获取 我的收益
     */
    public void getShareMyProfit(String token, int showType) {
        FaceApiTest.getV1ApiServiceTest().getMyProfit(token)
                .compose(RxSchedulers.<ApiModel<ShareProfitModel>>io_main())
                .subscribe(new RxObserver<ShareProfitModel>(getActivity(), getActivity().Tag, showType == 1 ? true : false) {
                    @Override
                    public void onSuccess(ShareProfitModel entity) {
                        try {
                            Log.e("cjh>>>", "分享 我的收益:" + new Gson().toJson(entity));
                            getBinding().sharePerson.setText(entity.getInvitetotal()+"");
                            getBinding().shareMoney.setText(StringUtils.double2String(Double.parseDouble(entity.getIncome()), 6));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     *  分享 排行榜列表
     */
    public void getShareRank(String token, int page, int showType) {
        FaceApiTest.getV1ApiServiceTest(). getShareRank(token, page, "")
                .compose(RxSchedulers.<ApiModel<List<ShareRankModel>>>io_main())
                .subscribe(new RxObserver<List<ShareRankModel>>(getActivity(), getActivity().Tag, showType==1?true:false) {
                    @Override
                    public void onSuccess(List<ShareRankModel> model) {
                        try {
                            Log.e("cjh>>>", "分享 排行榜列表:" + new Gson().toJson(model));
                            if (!EmptyUtil.isEmpty(model.get(0))){
                                getBinding().tvName1.setText(StringUtils.splitPhone(model.get(0).getMobile()));
                                getBinding().tvProfit1.setText(StringUtils.double2String(Double.parseDouble(model.get(0).getResultLeaderBoardNumber()), 6));
                            }
                            if (!EmptyUtil.isEmpty(model.get(1))){
                                getBinding().tvName2.setText(StringUtils.splitPhone(model.get(1).getMobile()));
                                getBinding().tvProfit2.setText(StringUtils.double2String(Double.parseDouble(model.get(1).getResultLeaderBoardNumber()), 6));
                            }
                            if (!EmptyUtil.isEmpty(model.get(2))){
                                getBinding().tvName3.setText(StringUtils.splitPhone(model.get(2).getMobile()));
                                getBinding().tvProfit3.setText(StringUtils.double2String(Double.parseDouble(model.get(2).getResultLeaderBoardNumber()), 6));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


}
