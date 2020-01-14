package com.coin.market.fragment.assetsaccount;

import android.support.v7.widget.LinearLayoutManager;

import com.coin.market.activity.assets.records.AssetRecordsActivity;
import com.coin.market.adapter.AssetsAccountAdapter;
import com.coin.market.databinding.FragmentAssetsAccountLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import teng.wang.comment.api.FaceApiMbr;
import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseFragmentViewModel;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.CoinRecordEntity;
import teng.wang.comment.model.CoinWorthModel;
import teng.wang.comment.model.FB_BBEntity;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.log.Log;

/**
 * @author: Lenovo
 * @date: 2019/8/5
 * @info: 资产 币币账户 法币账户 ViewModel
 */
public class AssetsAccountViewModel extends BaseFragmentViewModel <AssetsAccountFragment, FragmentAssetsAccountLayoutBinding>{

    private AssetsAccountAdapter adapter;
    private List<CoinRecordEntity> entityList;
    private FB_BBEntity fb_bbEntitie;

    public AssetsAccountViewModel(AssetsAccountFragment fragment, FragmentAssetsAccountLayoutBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initView() {
        if (getFragments().type.equals("bb")){
            getBinding().assetsAccountTitle.setText("币币总资产折合(USDT)");
        }else {
            getBinding().assetsAccountTitle.setText("法币总资产折合(USDT)");
        }

        getBinding().assetsAccountPrice.setText("0.00000000");
        getBinding().assetsAccountPriceCny.setText("≈0.00CNY");

        setCoinAdapter();
        setData();
        if (getFragments().type.equals("fb")){ // 法币
            getFBAssetsData(FaceApplication.getToken(), 1);
        }
    }

    /**
     *   set资产UI
     */
    public void setData() {
        if (EmptyUtil.isEmpty(getFragments().entity)){
            return;
        }
        if (getFragments().type.equals("bb") && !EmptyUtil.isEmpty(getFragments().entity)){   //币币
            getBinding().assetsAccountPrice.setText(StringUtils.double2String(getFragments().entity.getTotalPrice().doubleValue(),8));
            getBinding().assetsAccountPriceCny.setText("≈"+StringUtils.double2String(getFragments().entity.getTotalPrice().doubleValue()*getFragments().entity.getPrice_cny().doubleValue(), 2)+"CNY");
            entityList = new ArrayList<CoinRecordEntity>();
            for (int i = 0; i < getFragments().entity.getAsset().size(); i++) {
                CoinRecordEntity entity = new CoinRecordEntity();
                entity.setTitle(getFragments().entity.getAsset().get(i).getCoin_name());
                entity.setNumb(getFragments().entity.getAsset().get(i).getAvailable()+"");
                entity.setDeal(getFragments().entity.getAsset().get(i).getDisabled()+"");
                entity.setCoinId(getFragments().entity.getAsset().get(i).getCoin_id()+"");
                if (getFragments().entity.isHide()){
                    entity.setType("1");
                }else {
                    entity.setType("0");
                }
                entity.setPrice((StringUtils.double2String(getFragments().entity.getAsset().get(i).getWorth()*getFragments().entity.getPrice_cny().doubleValue(), 2))+"");
                entityList.add(entity);
            }
        }else if (getFragments().type.equals("fb") && !EmptyUtil.isEmpty(fb_bbEntitie)){ //法币
            getBinding().assetsAccountPrice.setText(StringUtils.double2String(fb_bbEntitie.getAvailable().doubleValue(),8));
            getBinding().assetsAccountPriceCny.setText("≈"+StringUtils.double2String(fb_bbEntitie.getAvailable().doubleValue()*fb_bbEntitie.getUsdtPrice().doubleValue(), 2)+"CNY");
            entityList = new ArrayList<CoinRecordEntity>();
            for (int i = 0; i < fb_bbEntitie.getListdetail().size(); i++) {
                if (!EmptyUtil.isEmpty(fb_bbEntitie.getListdetail().get(i).getOtcAavailable())){
                    CoinRecordEntity entity = new CoinRecordEntity();
                    entity.setTitle(fb_bbEntitie.getListdetail().get(i).getCoin_name());
                    entity.setNumb(Double.parseDouble(fb_bbEntitie.getListdetail().get(i).getOtcAavailable())+"");
                    entity.setDeal(Double.parseDouble(fb_bbEntitie.getListdetail().get(i).getDisabled())+"");
                    entity.setCoinId(fb_bbEntitie.getListdetail().get(i).getCoin_id()+"");
                    if (getFragments().entity.isHide()){
                        entity.setType("1");
                    }else {
                        entity.setType("0");
                    }
                    entity.setPrice((StringUtils.double2String(Double.parseDouble(fb_bbEntitie.getListdetail().get(i).getOtcAavailable())*fb_bbEntitie.getUsdtPrice().doubleValue(), 2))+"");
                    entityList.add(entity);
                }
            }
        }
        if (getFragments().entity.isHide()){
            getBinding().assetsAccountPrice.setText("*****");
            getBinding().assetsAccountPriceCny.setText("*****");
        }
        adapter.clear();
        adapter.addAll(entityList);
    }

    /**
     *   初始化Adapter
     */
    public void setCoinAdapter() {
        adapter = new AssetsAccountAdapter(getContexts());
        getBinding().assetsAccountRecycler.setLayoutManager(new LinearLayoutManager(getContexts()));
        getBinding().assetsAccountRecycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                AssetRecordsActivity.JUMP(getContexts(), adapter.getItem(position), getFragments().type);
            }
        });
    }

    /** 获取币币资产数据 */
    public void getBBAssetsData(String token){
//        FaceApiTest.getV1ApiServiceTest().getCoinWorth(token)
//                .compose(RxSchedulers.<ApiModel<CoinWorthModel>>io_main())
//                .subscribe(new RxObserver<CoinWorthModel>(getContexts(), getFragments().getTag(),true) {
//                    @Override
//                    public void onSuccess(CoinWorthModel model) {
//                        Log.e("cjh>>>", "币币资产币AssetsAccount" + new Gson().toJson(model));
//                        model.setHide(getFragments().entity.isHide());
//                        getFragments().entity = model;
//                        setData();
//                    }
//                });
    }

    /**
     *    获取 币币 法币资产数据
     */
    public void getFBAssetsData(String token, int isShow) {
        FaceApiMbr.getV1ApiServiceMbr().getOTCCoins(token)
                .compose(RxSchedulers.<ApiModel<FB_BBEntity>>io_main())
                .subscribe(new RxObserver<FB_BBEntity>(getContexts(), getFragments().getTag(), isShow==1? true:false) {
                    @Override
                    public void onSuccess(FB_BBEntity entity) {
                        try {
                            Log.e("cjh>>>", "法币资产AssetsAccount:" + new Gson().toJson(entity));
                            Log.e("cjh>>>", "法币总资产AssetsAccount" + entity.getAvailable().doubleValue());
                            if (!EmptyUtil.isEmpty(entity)){
                                fb_bbEntitie = entity;
                                setData();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
