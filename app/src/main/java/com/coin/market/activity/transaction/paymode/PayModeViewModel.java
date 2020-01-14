package com.coin.market.activity.transaction.paymode;

import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.adapter.PayModeAdapter;
import com.coin.market.databinding.ActivityPayModeLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.google.gson.Gson;

import java.util.List;

import okhttp3.RequestBody;
import teng.wang.comment.api.FaceApiMbr;
import teng.wang.comment.api.RequestBodyUtils;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.PayMethodsBean;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.log.Log;
import teng.wang.comment.widget.ActionSheetDialog;

/**
 * @author: Lenovo
 * @date: 2019/8/2
 * @info: 收款方式 ViewModel
 */
public class PayModeViewModel extends BaseActivityViewModel <PayModeActivity, ActivityPayModeLayoutBinding>{

    private PayModeAdapter adapter;

    public PayModeViewModel(PayModeActivity activity, ActivityPayModeLayoutBinding binding) {
        super(activity, binding);
    }


    @Override
    protected void initView() {
        initAdapter(); // 初始化 币种列表 adapter
        getPayMentMethod(FaceApplication.getToken(), 1); // 获取币种列表数据
    }

    /**
     *  初始化 货币行情分类  Navigation adapter
     */
    private void initAdapter() {
        adapter = new PayModeAdapter(getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        getBinding().payModeRecycler.setLayoutManager(layoutManager);
        getBinding().payModeRecycler.setAdapter(adapter);
        adapter.setListener(new PayModeAdapter.mOnClickCallback() {
            @Override
            public void onSelect(int item) {
                // 删除收款方式
                showconfirmDialog("是否删除收款方式？", adapter.getItem(item).getId());
            }
        });
    }

    /**
     * 确定 兑换
     */
    private void showconfirmDialog(final String Title, final String id) {
        new ActionSheetDialog(getActivity())
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .setTitle(Title)
                .setOnDismissListener(new ActionSheetDialog.DialogOnDismissListener() {
                    @Override
                    public void onDialogDismiss(ActionSheetDialog dialog) {

                    }
                })
                .addSheetItem("确定", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        try {
                            deletePayMentMethod(FaceApplication.getToken(), id, 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).show();
    }

    /**
     *    获取收款方式
     */
    public void getPayMentMethod(String token, int isShow) {
        FaceApiMbr.getV1ApiServiceMbr().getPayMentMethod(token)
                .compose(RxSchedulers.<ApiModel<List<PayMethodsBean>>>io_main())
                .subscribe(new RxObserver<List<PayMethodsBean>>(getActivity(), getActivity().Tag, isShow==1? true:false) {
                    @Override
                    public void onSuccess(List<PayMethodsBean> list) {
                        try {
                            Log.e("cjh>>>", "PayMethodsBean" + new Gson().toJson(list));
                            if (!EmptyUtil.isEmpty(adapter) && !EmptyUtil.isEmpty(list)) {
                                adapter.clear();
                                adapter.addAll(list);
                            }else {
                                //占位图
                                getBinding().payModeRecycler.setEmptyView(R.layout.comment_view_seat_layout);
                                getBinding().payModeRecycler.showEmpty();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     *    删除收款方式
     */
    public void deletePayMentMethod(String token, String id, int isShow) {
        RequestBody body = new RequestBodyUtils.Builder()
                .addParam("id", id)
                .builder();
        FaceApiMbr.getV1ApiServiceMbr().deleteUseMethodPay(token, body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, isShow==1? true:false) {
                    @Override
                    public void onSuccess(String str) {
                        try {
                            Toast.makeText(getActivity(), "删除成功！", Toast.LENGTH_SHORT).show();
                            getPayMentMethod(FaceApplication.getToken(), 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
