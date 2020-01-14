package com.coin.market.activity.mine.coinrecordinfo;

import android.view.View;

import com.coin.market.R;
import com.coin.market.databinding.ActivityCoinInfoLayoutBinding;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;

import teng.wang.comment.base.BaseActivityViewModel;
import teng.wang.comment.model.GetCoinRecordInfoModel;
import teng.wang.comment.model.ImportsDTO;

/**
 * @author: Lenovo
 * @date: 2019/7/17
 * @info:
 */
public class CoinInfoViewModel extends BaseActivityViewModel<CoinInfoActivity, ActivityCoinInfoLayoutBinding> {

    public CoinInfoViewModel(CoinInfoActivity activity, ActivityCoinInfoLayoutBinding binding) {
        super(activity, binding);
    }

    @Override
    protected void initView() {
        // getCoinInfo();
        setTypeUi();
    }

    /**
     * 获取 充币 提币历史记录 详情
     */
    public void setCoinInfo(ImportsDTO infoModel) {
        getBinding().coinInfoTitle.setText("-" + StringUtils.double2String(infoModel.getNumber().doubleValue(), 8) + " " + getActivity().coinName);//infoModel.coinName()
        getBinding().coinInfoClass.setText(getActivity().getResources().getString(R.string.mine_ordinary_withdrawal));
        getBinding().coinInfoAddress.setText(getActivity().coinName + "(" +getActivity().coinName + ")" + infoModel.getAddress());
        getBinding().coinInfoAddressCode.setText(infoModel.getAddress());
        getBinding().coinInfoMoney.setText(infoModel.getFee() + infoModel.getCoinId());
        getBinding().coinInfoTxid.setText(infoModel.getTxid());
        getBinding().coinInfoTime.setText(infoModel.getCreateDate());
        if (EmptyUtil.isEmpty(infoModel.getTxid())) {
            getBinding().txidLayout.setVisibility(View.GONE);
        }

        if (!EmptyUtil.isEmpty(infoModel.getStatus())) {
//            getBinding().coinInfoType.setText(getActivity().getResources().getString(R.string.mine_processed));
            // 状态
            switch (infoModel.getStatus()) {
                case 0:
                    getBinding().coinInfoType.setText(getActivity().getResources().getString(R.string.mine_pending));
                    break;
                case 1:
                    getBinding().coinInfoType.setText(getActivity().getResources().getString(R.string.mine_processed));
                    break;
                case 2:
                    getBinding().coinInfoType.setText(getActivity().getResources().getString(R.string.mine_revoked));
                    break;
                default:
                    break;
            }
        }

        switch (getActivity().Jump) {
            case "TB":
                getBinding().coinInfoTitle.setText("-" + StringUtils.double2String(infoModel.getNumber().doubleValue(), 8) + " " + getActivity().coinName);
                getBinding().coinInfoClass.setText(getActivity().getResources().getString(R.string.mine_ordinary_withdrawal));
                getBinding().coinInfoAddress.setText(getActivity().coinName+ "(" + getActivity().coinName + ")" + "");//infoModel.getNote()
                // 只有提币有其他状态  其他都为 已完成
                if (!EmptyUtil.isEmpty(infoModel.getStatus())) {
                    // 状态
                    switch (infoModel.getStatus()) {
                        case 0:
                            getBinding().coinInfoType.setText(getActivity().getResources().getString(R.string.mine_pending));
                            break;
                        case 1:
                            getBinding().coinInfoType.setText(getActivity().getResources().getString(R.string.mine_processed));
                            break;
                        case 2:
                            getBinding().coinInfoType.setText(getActivity().getResources().getString(R.string.mine_revoked));
                            break;
                        default:
                            break;
                    }
                }
                break;
            case "CB": // 充币
                getBinding().coinInfoTitle.setText("+" + StringUtils.double2String(infoModel.getNumber().doubleValue(), 8) + " " + infoModel.coinName);
                getBinding().coinInfoClass.setText(getActivity().getResources().getString(R.string.mine_ordinary_deposit));
                getBinding().coinInfoType.setText(getActivity().getResources().getString(R.string.mine_completed));
                getBinding().coinInfoAddressLayout.setVisibility(View.GONE);
                getBinding().coinInfoMoneyLayout.setVisibility(View.GONE);
                getBinding().coinInfoTitle.setText("+" + StringUtils.double2String(infoModel.getNumber().doubleValue(), 8) + " " + infoModel.coinName);
                break;
            case "ZR":
                getBinding().coinInfoTitle.setText("+" + StringUtils.double2String(infoModel.getNumber().doubleValue(), 8) + " " + infoModel.coinName);
                getBinding().coinInfoClass.setText("转入");
                getBinding().coinInfoType.setText("已完成");
                getBinding().coinInfoAddressLayout.setVisibility(View.GONE);
                getBinding().coinInfoMoneyLayout.setVisibility(View.GONE);
                getBinding().txidLayout.setVisibility(View.GONE);
//                if (!EmptyUtil.isEmpty(infoModel.getType())) {
//                    getBinding().coinInfoType2.setText(infoModel.getType());
//                    getBinding().coinInfoType2.setVisibility(View.VISIBLE);
//                }
                break;
            case "ZC": //转出
                getBinding().coinInfoTitle.setText("-" + StringUtils.double2String(infoModel.getNumber().doubleValue(), 8) + " " + infoModel.coinName);
                getBinding().coinInfoClass.setText("转出");
                getBinding().coinInfoType.setText("已完成");
                getBinding().coinInfoAddressLayout.setVisibility(View.GONE);
                getBinding().coinInfoMoneyLayout.setVisibility(View.GONE);
                getBinding().txidLayout.setVisibility(View.GONE);
//                if (!EmptyUtil.isEmpty(infoModel.getType())) {
//                    getBinding().coinInfoType2.setText(infoModel.getType());
//                    getBinding().coinInfoType2.setVisibility(View.VISIBLE);
//                }
                break;
            case "fb": // 法币
//                switch (infoModel.getType()) {
//                    case "币币账户转到法币账户":
//                        getBinding().coinInfoTitle.setText("+" + StringUtils.double2String(Double.parseDouble(infoModel.getNumber()), 8) + " " + infoModel.coinName());
//                        getBinding().coinInfoClass.setText("转入");
//                        break;
//                    case "法币账户转到币币账户":
//                        getBinding().coinInfoTitle.setText("-" + StringUtils.double2String(Double.parseDouble(infoModel.getNumber()), 8) + " " + infoModel.coinName());
//                        getBinding().coinInfoClass.setText("转出");
//                        break;
//                    case "申请商家入驻押金":
//                        getBinding().coinInfoTitle.setText("-" + StringUtils.double2String(Double.parseDouble(infoModel.getNumber()), 8) + " " + infoModel.coinName());
//                        getBinding().coinInfoClass.setText("转出");
//                        break;
//                    case "买入":
//                        getBinding().coinInfoTitle.setText("+" + StringUtils.double2String(Double.parseDouble(infoModel.getNumber()), 8)+infoModel.coinName());
//                        getBinding().coinInfoClass.setText("法币买入");
//                        break;
//                    case "卖出":
//                        getBinding().coinInfoTitle.setText("-" + StringUtils.double2String(Double.parseDouble(infoModel.getNumber()), 8) + infoModel.coinName());
//                        getBinding().coinInfoClass.setText("法币卖出");
//                        break;
//                    default:
//                        break;
//                }
//                getBinding().coinInfoTime.setText(infoModel.getCreated_at());
//                getBinding().coinInfoType.setText("已完成");
//                getBinding().coinInfoAddressLayout.setVisibility(View.GONE);
//                getBinding().coinInfoMoneyLayout.setVisibility(View.GONE);
//                getBinding().txidLayout.setVisibility(View.GONE);
//                if (!EmptyUtil.isEmpty(infoModel.getType())) {
//                    getBinding().coinInfoType2.setText(infoModel.getType());
//                    getBinding().coinInfoType2.setVisibility(View.VISIBLE);
//                    if (infoModel.getType().equals("买入") || infoModel.getType().equals("卖出")){
//                        getBinding().coinInfoType2.setText(infoModel.getType()+infoModel.coinName());
//                    }
//                }
                break;
            default:
                break;
        }

    }

    /**
     * 根据从不同的 界面跳转过来 展示不同的布局  BT是提币  CB是充币   这边提币显示地址和手续费
     */
    private void setTypeUi() {
        if (!EmptyUtil.isEmpty(getActivity().getIntent().getStringExtra("Jump"))) {
            if (getActivity().getIntent().getStringExtra("Jump").equals("CB")) {
                getBinding().coinInfoAddressLayout.setVisibility(View.GONE);
                getBinding().coinInfoMoneyLayout.setVisibility(View.GONE);
            }
        }
    }

}
