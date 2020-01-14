package com.coin.market.activity.mine.publish;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.adapter.MyPublishAdapter;
import com.coin.market.databinding.ActivityMyPublishLayoutBinding;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import org.greenrobot.eventbus.EventBus;

import okhttp3.RequestBody;
import teng.wang.comment.api.FaceApiMbr;
import teng.wang.comment.api.RequestBodyUtils;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.OTCTradeBean;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.log.Log;
import teng.wang.comment.widget.ActionSheetDialog;

/**
 * @author: Lenovo
 * @date: 2019/8/5
 * @info: 我发布的广告 列表 ViewModel
 */
public class MyPublishViewModel extends BaseActivityViewModel <MyPublishActivity, ActivityMyPublishLayoutBinding>{

    private int page;
    private MyPublishAdapter adapter;

    public MyPublishViewModel(MyPublishActivity activity, ActivityMyPublishLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        setCoinAdapter();
        page = 1;
        getCoinList(FaceApplication.getToken(), "order_all", page, 1);
    }

    /**
     *   初始化Adapter
     */
    public void setCoinAdapter() {
        adapter = new MyPublishAdapter(getActivity());
        getBinding().myPublishRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        getBinding().myPublishRecycler.setAdapter(adapter);
        getBinding().myPublishRecycler.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBinding().myPublishRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page = 1;
                        getCoinList(FaceApplication.getToken(), "order_all", page, 0);
                    }
                }, 1000);
            }
        });
        adapter.setMore(R.layout.view_more, new RecyclerArrayAdapter.OnMoreListener() {
            @Override
            public void onMoreShow() {
                getBinding().myPublishRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page++;
                        getCoinList(FaceApplication.getToken(), "order_all", page, 0);
                    }
                }, 1000);
            }

            @Override
            public void onMoreClick() {

            }
        });
        adapter.setError(R.layout.view_error_foot, new RecyclerArrayAdapter.OnErrorListener() {
            @Override
            public void onErrorShow() {

            }

            @Override
            public void onErrorClick() {
                adapter.resumeMore();
            }
        });
        adapter.setNoMore(R.layout.view_nomore);

        adapter.setListener(new MyPublishAdapter.mOnClickCallback() {
            @Override
            public void cancelOnClick(String otcTradeId) {
                showDialog(otcTradeId);
            }
        });
    }

    /**
     * 获取广告记录
     */
    public void getCoinList(String token, String otcStatus, int index, int showType) {
        FaceApiMbr.getV1ApiServiceMbr().otcUserOrderAllList(token, otcStatus,index)
                .compose(RxSchedulers.<ApiModel<OTCTradeBean>>io_main())
                .subscribe(new RxObserver<OTCTradeBean>(getActivity(), getActivity().Tag, showType == 1 ? true : false) {
                    @Override
                    public void onSuccess(OTCTradeBean entity) {
                        try {
                            Log.e("cjh>>>获取广告记录:","OTCTradeBean:"+ new Gson().toJson(entity));
                            if (page == 1) {
                                if (null != entity && entity.getList().size() > 0) {
                                    adapter.clear();
                                    adapter.addAll(entity.getList());
                                } else {
                                    //占位图
                                    getBinding().myPublishRecycler.setEmptyView(R.layout.comment_view_seat_layout);
                                    getBinding().myPublishRecycler.showEmpty();
                                }
                            } else {
                                if (entity.getList().size() <= 0) {
                                    adapter.stopMore();
                                } else {
                                    adapter.addAll(entity.getList());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     *  撤销广告
     */
    public void cancelItemOrder(String token, String otcTradeId, int showType) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("otcTradeId", otcTradeId)  //广告id
                .builder();
        FaceApiMbr.getV1ApiServiceMbr().cancelItemOrder(token, body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, showType == 1 ? true : false) {
                    @Override
                    public void onSuccess(String entity) {
                        try {
                            Toast.makeText(getActivity(), "撤销成功！", Toast.LENGTH_SHORT).show();
                            page = 1;
                            getCoinList(FaceApplication.getToken(), "order_all", page, 0);
                            EventBus.getDefault().post("publish");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     *  撤销提示
     */
    private void showDialog(final String otcTradeId) {
        new ActionSheetDialog(getActivity())
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .setTitle("是否撤销？")
                .setOnDismissListener(new ActionSheetDialog.DialogOnDismissListener() {
                    @Override
                    public void onDialogDismiss(ActionSheetDialog dialog) {

                    }
                })
                .addSheetItem("撤销", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        cancelItemOrder(FaceApplication.getToken(), otcTradeId, 1);
                    }
                }).show();
    }

}
