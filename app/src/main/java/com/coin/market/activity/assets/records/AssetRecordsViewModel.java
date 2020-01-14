package com.coin.market.activity.assets.records;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;

import com.coin.market.R;
import com.coin.market.adapter.AssetRecordsAdapter;
import com.coin.market.databinding.ActivityAssetRecordsLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import teng.wang.comment.api.FaceApiMbr;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.ExchangeDialogModel;
import teng.wang.comment.model.FB_BBEntity;
import teng.wang.comment.model.FinancialLogs;
import teng.wang.comment.model.InAndOutEntity;
import teng.wang.comment.model.InAndOutModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.log.Log;

/**
 * @author: Lenovo
 * @date: 2019/8/5
 * @info: 资产 财务记录 单个币种的列表 ViewModel
 */
public class AssetRecordsViewModel extends BaseActivityViewModel <AssetRecordsActivity, ActivityAssetRecordsLayoutBinding>{

    private AssetRecordsAdapter adapter;
    private List<InAndOutEntity> entities = new ArrayList<InAndOutEntity>();

    public AssetRecordsViewModel(AssetRecordsActivity activity, ActivityAssetRecordsLayoutBinding binding) {
        super(activity, binding);
    }


    public int page = 1;
    public int limit = 10;
    @Override
    protected void initView() {
        setCoinAdapter();
        page = 1;
        limit = 10;
        transferRecord(FaceApplication.getToken(),getActivity().coinId, page,""); // 刷新币的财务记录列表
//        if (getActivity().type.equals("fb")){
//            transferRecord(FaceApplication.getToken(), getActivity().coinId, ""); // 刷新币的财务记录列表
//        }else {
//            getCoinList(FaceApplication.getToken(), getActivity().coinId, 1); // 刷新币的财务记录列表
//        }
    }

    /**
     *   币币 获取单个币下面的 财务记录
     */
    public void getCoinList(String token, String coinId,int showType) {
        FaceApiTest.getV1ApiServiceTest().getCoinRecordList(token, coinId)
                .compose(RxSchedulers.<ApiModel<InAndOutModel>>io_main())
                .subscribe(new RxObserver<InAndOutModel>(getActivity(), getActivity().Tag, showType == 1 ? true : false) {
                    @Override
                    public void onSuccess(InAndOutModel list) {
                        try {
                            Log.e("cjh>>>","财务记录：" + new Gson().toJson(list));
                            List<InAndOutEntity> models = new ArrayList<InAndOutEntity>();
                            for (int i = 0; i < list.getFinancialLogs().size(); i++) {
                                InAndOutEntity entity = new InAndOutEntity();
                                entity = list.getFinancialLogs().get(i);
                                if (entity.getType().equals("OTC转入")){
                                    entity.setTag("ZR");
                                }else if (entity.getType().equals("OTC转出")){
                                    entity.setTag("ZC");
                                }
                                models.add(entity);
                            }

                            for (int i = 0; i < list.getExportsDTOS().size(); i++) {
                                InAndOutEntity entity = new InAndOutEntity();
                                entity = list.getExportsDTOS().get(i);
                                entity.setType("提币");
                                entity.setTag("TB");
                                models.add(entity);
                            }

                            for (int i = 0; i < list.getImportsDTOS().size(); i++) {
                                InAndOutEntity entity = new InAndOutEntity();
                                entity = list.getImportsDTOS().get(i);
                                entity.setType("充币");
                                entity.setTag("CB");
                                models.add(entity);
                            }

                            if (null != models && models.size() > 0) {
                                entities = models;
                                adapter.clear();
                                adapter.addAll(models);
                            } else {
                                //占位图
                                getBinding().assetRecordsRecycler.setEmptyView(R.layout.comment_view_seat_layout);
                                getBinding().assetRecordsRecycler.showEmpty();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     *   财务记录  资金类型列表
     */
    public void getTypeList() {
        FaceApiTest.getV1ApiServiceTest().typeList()
                .compose(RxSchedulers.<ApiModel<ExchangeDialogModel>>io_main())
                .subscribe(new RxObserver<ExchangeDialogModel>(getActivity(), getActivity().Tag, true) {
                    @Override
                    public void onSuccess(ExchangeDialogModel model) {
                        try {
                            Log.e("cjh>>>","资金类型列表：" + new Gson().toJson(model));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     *  获取历史记录
     */
    public void transferRecord(String token,String coinId,int mpage,String type) {
        page = mpage;
        if (TextUtils.isEmpty(type)){
            getActivity().type = type;
            FaceApiTest.getV1ApiServiceTest().transferRecordss(token,coinId, page, limit)
                    .compose(RxSchedulers.<ApiModel<FinancialLogs>>io_main())
                    .subscribe(new RxObserver<FinancialLogs>(getActivity(), getActivity().Tag, true) {
                        @Override
                        public void onSuccess(FinancialLogs model) {
                            try {
                                Log.e("cjh>>>","财务记录：" + new Gson().toJson(model));

                                if (page == 1 || page == 0) {
                                    if (null != model && model.getList().size() > 0) {
                                        List<InAndOutEntity> models = new ArrayList<InAndOutEntity>();
                                        for (int i = 0; i < model.getList().size(); i++) {
                                            InAndOutEntity entity = new InAndOutEntity();
                                            entity = model.getList().get(i);
                                            entity.setTag(entity.getType());
                                            models.add(entity);
                                        }
                                        entities = models;
                                        adapter.clear();
                                        adapter.addAll(models);
                                    } else {
                                        //占位图
                                        getBinding().assetRecordsRecycler.setEmptyView(R.layout.comment_view_seat_layout);
                                        getBinding().assetRecordsRecycler.showEmpty();
                                    }
                                } else {
                                    if (model.getList().size() <= 0) {
                                        adapter.stopMore();
                                    } else {

                                        List<InAndOutEntity> models = new ArrayList<InAndOutEntity>();
                                        for (int i = 0; i < model.getList().size(); i++) {
                                            InAndOutEntity entity = new InAndOutEntity();
                                            entity = model.getList().get(i);
                                            entity.setTag(entity.getType());
                                            models.add(entity);
                                        }
                                        entities.addAll(models);
                                        adapter.addAll(models);
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }else{
            getActivity().type = type;
            FaceApiTest.getV1ApiServiceTest().transferRecords(token,coinId, page, limit,type)
                    .compose(RxSchedulers.<ApiModel<FinancialLogs>>io_main())
                    .subscribe(new RxObserver<FinancialLogs>(getActivity(), getActivity().Tag, true) {
                        @Override
                        public void onSuccess(FinancialLogs model) {
                            try {
                                Log.e("cjh>>>","财务记录：" + new Gson().toJson(model));

                                if (page == 1 || page == 0) {
                                    if (null != model && model.getList().size() > 0) {
                                        List<InAndOutEntity> models = new ArrayList<InAndOutEntity>();
                                        for (int i = 0; i < model.getList().size(); i++) {
                                            InAndOutEntity entity = new InAndOutEntity();
                                            entity = model.getList().get(i);
                                            entity.setTag(entity.getType());
                                            models.add(entity);
                                        }
                                        entities = models;
                                        adapter.clear();
                                        adapter.addAll(models);
                                    } else {
                                        //占位图
                                        getBinding().assetRecordsRecycler.setEmptyView(R.layout.comment_view_seat_layout);
                                        getBinding().assetRecordsRecycler.showEmpty();
                                    }
                                } else {
                                    if (model.getList().size() <= 0) {
                                        adapter.stopMore();
                                    } else {

                                        List<InAndOutEntity> models = new ArrayList<InAndOutEntity>();
                                        for (int i = 0; i < model.getList().size(); i++) {
                                            InAndOutEntity entity = new InAndOutEntity();
                                            entity = model.getList().get(i);
                                            entity.setTag(entity.getType());
                                            models.add(entity);
                                        }
                                        entities.addAll(models);
                                        adapter.addAll(models);
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }


    }

    /**
     *   获取余额
     */
    /**
     *    获取法币资产数据
     */
    public void getFBAssetsData(String token, int isShow) {
        FaceApiMbr.getV1ApiServiceMbr().getOTCCoins(token)
                .compose(RxSchedulers.<ApiModel<FB_BBEntity>>io_main())
                .subscribe(new RxObserver<FB_BBEntity>(getActivity(), getActivity().Tag, isShow==1? true:false) {
                    @Override
                    public void onSuccess(FB_BBEntity entity) {
                        try {
                            Log.e("cjh>>>", "划转成功刷新币种可用" + new Gson().toJson(entity));
                            if (!EmptyUtil.isEmpty(entity)){
                                for (int i = 0; i < entity.getListdetail().size(); i++) {
                                    if (entity.getListdetail().get(i).getCoin_id().equals(getActivity().coinId)){
                                        if (getActivity().type.equals("bb")){
                                            getBinding().assetRecordsTitle.setText(entity.getListdetail().get(i).getCoin_name());
                                            getBinding().assetsAccountAvailable.setText(StringUtils.double2String(Double.parseDouble(entity.getListdetail().get(i).getAvailable()), 8));
                                            getBinding().assetsAccountFrozen.setText(StringUtils.double2String(Double.parseDouble(entity.getListdetail().get(i).getDisabled()), 8));
                                            getBinding().assetsAccountConvert.setText(StringUtils.double2String(Double.parseDouble(entity.getListdetail().get(i).getAvailable())*entity.getUsdtPrice().doubleValue(), 2));
                                        }else if (getActivity().type.equals("fb")){
                                            getBinding().assetRecordsTitle.setText(entity.getListdetail().get(i).getCoin_name());
                                            getBinding().assetsAccountAvailable.setText(StringUtils.double2String(Double.parseDouble(entity.getListdetail().get(i).getOtcAavailable()), 8));
                                            getBinding().assetsAccountFrozen.setText(StringUtils.double2String(Double.parseDouble(entity.getListdetail().get(i).getOtcfreeze()), 8));
                                            getBinding().assetsAccountConvert.setText(StringUtils.double2String(Double.parseDouble(entity.getListdetail().get(i).getOtcAavailable())*entity.getUsdtPrice().doubleValue(), 2));
                                        }
                                    }
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void selectType(String type){
        if (EmptyUtil.isEmpty(entities)){
            return;
        }
        List<InAndOutEntity> inAndOutEntities = new ArrayList<InAndOutEntity>();
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).getTag().equals(type)){
                inAndOutEntities.add(entities.get(i));
            }
        }
        adapter.clear();
        adapter.addAll(inAndOutEntities);
    }

    /**
     * 初始化Adapter
     */
    public void setCoinAdapter() {
        adapter = new AssetRecordsAdapter(getActivity());
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                GetCoinRecordInfoModel infoModel = new GetCoinRecordInfoModel();
//                infoModel.setNumber(adapter.getItem(position).getNumber()+"");
//                infoModel.setCoin_name(adapter.getItem(position).getCoin_name());
//                infoModel.setAddress(adapter.getItem(position).getAddress());
//                infoModel.setFee(adapter.getItem(position).getFee()+"");
//                infoModel.setTxid(adapter.getItem(position).getTxid());
//                if (!EmptyUtil.isEmpty(adapter.getItem(position).getCoinName())){
//                    infoModel.setCoin_name(adapter.getItem(position).getCoinName());
//                }else {
//                    infoModel.setCoin_name(adapter.getItem(position).getCoin_name());
//                }
//                if (!EmptyUtil.isEmpty(adapter.getItem(position).getStatus())){
//                    infoModel.setStatus(adapter.getItem(position).getStatus());
//                }
//                if (!EmptyUtil.isEmpty(adapter.getItem(position).getCreated_at())){
//                    infoModel.setCreated_at(adapter.getItem(position).getCreated_at());
//                }else {
//                    infoModel.setCreated_at(adapter.getItem(position).getCreatedAt());
//                }
//                if (!EmptyUtil.isEmpty(adapter.getItem(position).getType())){
//                    infoModel.setType(adapter.getItem(position).getType());
//                }
//
//               CoinInfoActivity.JUMP(getActivity(), adapter.getItem(position).getTag(), infoModel);
            }
        });
        getBinding().assetRecordsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        getBinding().assetRecordsRecycler.setAdapter(adapter);
        getBinding().assetRecordsRecycler.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBinding().assetRecordsRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page = 1;
                        transferRecord(FaceApplication.getToken(),getActivity().coinId,page,getActivity().type);
//                        if (getActivity().type.equals("fb")){
//                            transferRecord(FaceApplication.getToken(), getActivity().coinId, ""); // 刷新币的财务记录列表
//                        }else {
//                            getCoinList(FaceApplication.getToken(), getActivity().coinId, 0); // 刷新币的财务记录列表
//                        }
                    }
                }, 1000);
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


        adapter.setMore(R.layout.view_more, new RecyclerArrayAdapter.OnMoreListener() {
            @Override
            public void onMoreShow() {
                getBinding().assetRecordsRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page++;
                        transferRecord(FaceApplication.getToken(),getActivity().coinId,page,getActivity().type);
                    }
                }, 1000);
            }

            @Override
            public void onMoreClick() {

            }
        });
        adapter.setNoMore(R.layout.view_nomore);
    }
}
