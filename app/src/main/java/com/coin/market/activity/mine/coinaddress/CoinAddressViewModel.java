package com.coin.market.activity.mine.coinaddress;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;

import com.coin.market.R;
import com.coin.market.adapter.CoinAddressAdapter;
import com.coin.market.databinding.ActivityCoinAddressLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.api.RequestBodyUtils;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.AddressDelParams;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.CoinAddressEntity;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.widget.ActionSheetDialog;

/**
 * @author: Lenovo
 * @date: 2019/7/17
 * @info:  提币地址 ViewModel
 */
public class CoinAddressViewModel extends BaseActivityViewModel<CoinAddressActivity, ActivityCoinAddressLayoutBinding> {

    private CoinAddressAdapter adapter;

    public CoinAddressViewModel(CoinAddressActivity activity, ActivityCoinAddressLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        setCoinAdapter();
        getCoinList(FaceApplication.getToken(), getActivity().CoinId);
    }


    /**
     * 初始化奖金钱包适配器
     */
    public void setCoinAdapter() {
        adapter = new CoinAddressAdapter(getActivity());
        getBinding().coinAddressRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        getBinding().coinAddressRecycler.setAdapter(adapter);
        getBinding().coinAddressRecycler.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBinding().coinAddressRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getCoinList(FaceApplication.getToken(), getActivity().CoinId);
                    }
                }, 1000);
            }
        });

        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                EventBus.getDefault().post(adapter.getItem(position));
                getActivity().finish();
                //CoinInfoActivity.JUMP(getActivity(), adapter.getItem(position).getTitle(), getActivity().getIntent().getStringExtra("Jump"));
            }
        });
        adapter.setListener(new CoinAddressAdapter.itemClick() {
            @Override
            public void Click(String addressId) {
                showDeleteDialog(addressId);
            }
        });
    }

    /**
     *  获取地址列表数据
     */
    public void getCoinList(String token, String coin_id) {
        FaceApiTest.getV1ApiServiceTest().getCoinAddress(token)
                .compose(RxSchedulers.<ApiModel<List<CoinAddressEntity>>>io_main())
                .subscribe(new RxObserver<List<CoinAddressEntity>>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(List<CoinAddressEntity> list) {
                        try {
                            if (!EmptyUtil.isEmpty(list)){
                                adapter.clear();
                                adapter.addAll(list);
                            }else {
                                //占位图
                                getBinding().coinAddressRecycler.setEmptyView(R.layout.comment_view_seat_layout);
                                getBinding().coinAddressRecycler.showEmpty();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     *  删除地址
     */
    List<String> ids = null;
    public void deleteAddress(String token, String address_id) {
        Gson gson = new Gson();
        ids = new ArrayList<>();
        ids.add(address_id);
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json;charset=UTF-8"),
                gson.toJson(ids) );
        FaceApiTest.getV1ApiServiceTest().deleteAddress(token, body)
                .compose(RxSchedulers.<ApiModel<String>>io_main())
                .subscribe(new RxObserver<String>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(String str) {
                        try {
                            getCoinList(FaceApplication.getToken(), getActivity().CoinId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     *  是否删除该条 币种地址
     */
    private void showDeleteDialog(final String addressId) {
        new ActionSheetDialog(getActivity())
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .setTitle("是否删除")
                .setOnDismissListener(new ActionSheetDialog.DialogOnDismissListener() {
                    @Override
                    public void onDialogDismiss(ActionSheetDialog dialog) {

                    }
                })
                .addSheetItem("确定", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        deleteAddress(FaceApplication.getToken(), addressId);
                    }
                }).show();
    }

}
