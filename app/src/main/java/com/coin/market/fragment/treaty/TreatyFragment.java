package com.coin.market.fragment.treaty;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.activity.mine.coinorder.CoinOrderActivity;
import com.coin.market.activity.mine.coinrecharge.CoinRechargeActivity;
import com.coin.market.activity.mine.transfer.TransferActivity;
import com.coin.market.activity.quotation.QuotationActivity;
import com.coin.market.databinding.FragmentTreatyLayoutBinding;
import com.coin.market.util.AnimationUtils;
import com.coin.market.util.ArithTulis;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.PopUtil;
import com.coin.market.util.StringUtils;
import com.coin.market.wight.CusSeekBar;
import com.coin.market.wight.popupwindow.TransactionPopWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import teng.wang.comment.base.BaseFragment;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.listener.NoMoreClickListener;
import teng.wang.comment.model.CoinEntity;
import teng.wang.comment.model.ContraceTradeDTO;
import teng.wang.comment.model.EntrustInfo;
import teng.wang.comment.model.MarketsDTO;
import teng.wang.comment.utils.StatusBarUtil;
import teng.wang.comment.utils.log.Log;
import teng.wang.comment.widget.ActionSheetDialog;

/**
 * @author 交易Fragment  合约
 * @version v1.0
 * @Time 2018-9-3
 */
public class TreatyFragment extends BaseFragment implements View.OnClickListener {

    private TreatyViewModel treatyViewModel;
    private FragmentTreatyLayoutBinding treatyLayoutBinding;
    private TransactionPopWindow popWindow;
    public MarketsDTO coinEntity;
    public String CoinId = "";
    public String marketId = "";
    public String CoinName = "";
    public String PayName = "";
    public int buyOrSell = 0;
    private int Progress = 0;
    public double pay_balance; // 可支付余额
    public double coin_balance; // 币余额
    public double price; // 单价
    public double Cnumb = 0; // 输入框的值 输入框币的数量
    public double price_cny = 0; // price_cny为 人民币兑换美元的汇率
    public double allNumber; // 进度条拉满 所有的

    public int priceScale;
    public int amountScale;
    public int priceCnyScale;

    /**
     *  创建合买相关参数
     */
    public int PoloBarnum = 25;  //杠杆倍数 25x 50x
    public int selMoney = 300;  //本金  100 300 500


    /**
     *  创建合约参数
     * @return
     */
    public String buysType = "rise";  // 购买类型（rise：涨 drop：跌）
    public String contractName = "BTC_USDT";// 合约名称
    public String money = "100";  //本金
    public String pryBar = "25";  //杠杆
    public String mloss = "1";  //亏损比例  100 1  90 0.9
    public String profit = "1";  //盈利比例

    public double available = 0;
    public double allmoney = 1;


    public List<ContraceTradeDTO> list = new ArrayList<>();


    public static TreatyFragment getTreatyFragment() {
        return new TreatyFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup viewGroup) {
        treatyLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_treaty_layout, viewGroup, false);
        return treatyLayoutBinding.getRoot();
    }

    @Override
    protected void onFragmentVisible() {
        super.onFragmentVisible();

        StatusBarUtil.setColor(getActivity(), getResources().getColor(R.color.white), 0);
    }
    @Override
    protected void onFragmentFirstVisible() {
        EventBus.getDefault().register(this);
        treatyViewModel = new TreatyViewModel(this, treatyLayoutBinding);
//        setSeekBar();
        treatyViewModel.initKeyboardStatus();


    }

    public double lossnum = 0;
    @SuppressLint("StringFormatInvalid")
    @Override
    protected void setListener() {
        treatyLayoutBinding.ll.setOnClickListener(this);
        treatyLayoutBinding.transactionSelectCoin.setOnClickListener(this);
        treatyLayoutBinding.transactionBbDialog.setOnClickListener(this);
        treatyLayoutBinding.transactionBuyButton.setOnClickListener(this);
        treatyLayoutBinding.transactionSellButton.setOnClickListener(this);
        treatyLayoutBinding.addPrice.setOnClickListener(this);
        treatyLayoutBinding.deletePrice.setOnClickListener(this);
        treatyLayoutBinding.bbAllEntrust.setOnClickListener(this);
        treatyLayoutBinding.tvpolebarOne.setOnClickListener(this);
        treatyLayoutBinding.tvpolebarTwo.setOnClickListener(this);
        treatyLayoutBinding.llOrder.setOnClickListener(this);
        treatyLayoutBinding.transactionConfirmButton.setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {

                if (4!=FaceApplication.getAuthentication()){
                    Toast.makeText(getContext(), getResources().getString(R.string.toast_authentication), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (1!=FaceApplication.getLevel()){
                    Toast.makeText(getContext(),getResources().getString(R.string.toast_no_permission),Toast.LENGTH_SHORT).show();
                    return;
                }
                if (available<allmoney){
                    Toast.makeText(getContext(),getResources().getString(R.string.toast_recharge),Toast.LENGTH_SHORT).show();
                    return;
                }
                PopUtil.lossnum = 1;
                PopUtil.gainnum = 1;
                PopUtil.gainloss = selMoney;
                PopUtil.ShowPopTs(getContext(),
                        getResources().getString(R.string.mbr_treaty_sf_create),
                        getResources().getString(R.string.ok),
                        getResources().getString(R.string.cancel),
                        treatyLayoutBinding.transactionSelectCoin,
                        new PopUtil.PopContinueCallback() {
                            @Override
                            public void onDel(TextView loss_price_tv, TextView loss_price, int gainloss, double loss) {
                                lossnum = loss - 10;
                                double mloss = lossnum / 100f;
//                                        Log.e("cje>>>","lossnum "+loss+ "ProfitLoss "+getFragments().ProfitLoss);


                                loss_price_tv.setText((int)lossnum+"");
                                loss_price.setText(
                                        String.format(
                                                getResources().getString(
                                                        R.string.mbr_treaty_create_zk),(int)(mloss * gainloss)+""));
                            }

                            @Override
                            public void onContinue(int lossPrice,int gain, int loss) {
                                buysType = (0==buyOrSell?"rise":"drop");
                                pryBar = PoloBarnum+"";
                                money = selMoney+"";
                                contractName = "BTC_USDT";
                                float lossRate = loss / 100f;
                                mloss = lossRate+"";
                                float gainRate = gain / 100f;
                                profit = gainRate + "";
                                treatyViewModel.Contracetrade(
                                        buysType,
                                        contractName,
                                        money,
                                        pryBar,
                                        profit,
                                        mloss);
//

                            }
                        });
            }

            @Override
            protected void OnMoreErrorClick() {
                //操作过快的处理 提醒啥的
            }
        });
        treatyLayoutBinding.transactionPriceEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //正则，用来判断是否输入了小数点
//                String regex = "^\\d+.$";
//                Pattern r = Pattern.compile(regex);
//                Matcher matcher = r.matcher(charSequence);
//                if (matcher.matches()) {
//                    if (priceScale>0){
//                        treatyLayoutBinding.transactionPriceEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(charSequence.length() + priceScale)});
//                    }else {
//                        treatyLayoutBinding.transactionPriceEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(charSequence.length() + 4)});
//                    }
//
//                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    treatyLayoutBinding.transactionPriceEdit.setSelection(treatyLayoutBinding.transactionPriceEdit.getText().length());
                    if (!TextUtils.isEmpty(editable.toString())) {
                        price = Double.parseDouble(editable.toString());
                        double maxNumber = pay_balance / price;
                        double number = getInputNumber(treatyLayoutBinding.coinNumbEdit);
                        if (number < maxNumber) {
                            int aa = ((int) (number * 100 / maxNumber));
                            treatyLayoutBinding.bbSusseekbar.setProgress((int) (number * 100 / maxNumber));
                            Log.e("cjh>>>", "setProgress" + aa);
                        } else {
                            treatyLayoutBinding.bbSusseekbar.setProgress(100);
                            //treatyLayoutBinding.coinNumbEdit.setText(StringUtils.double2String(maxNumber, 4));
                        }
                        treatyLayoutBinding.bbCny.setText("≈" + StringUtils.double2String(price_cny * price, priceCnyScale));
                    } else {
                        treatyLayoutBinding.bbCny.setText("≈" + StringUtils.double2String(0, priceCnyScale));
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        treatyLayoutBinding.coinNumbEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //正则，用来判断是否输入了小数点
//                String regex = "^\\d+.$";
//                Pattern r = Pattern.compile(regex);
//                Matcher matcher = r.matcher(charSequence);
//                if (matcher.matches()) {
//                    if (amountScale>0){
//                        treatyLayoutBinding.coinNumbEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(charSequence.length() + amountScale)});
//                    }else {
//                        treatyLayoutBinding.coinNumbEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(charSequence.length() + 4)});
//                    }
//
//                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    treatyLayoutBinding.coinNumbEdit.setSelection(treatyLayoutBinding.coinNumbEdit.getText().length());
                    if (!TextUtils.isEmpty(editable.toString()) && price > 0) {
                        double maxNumber = Double.parseDouble(StringUtils.double2String(pay_balance / price, 4));
                        double number = getInputNumber(treatyLayoutBinding.coinNumbEdit);
                        if (number < maxNumber) {
                            Cnumb = number;
                            int aa = ((int) (number * 100 / maxNumber));
                            treatyLayoutBinding.bbSusseekbar.setProgress((int) (number * 100 / maxNumber));
                            Log.e("cjh>>>", "setProgress" + aa);
                        } else {
                            Cnumb = maxNumber;
                            treatyLayoutBinding.bbSusseekbar.setProgress(100);
                            //treatyLayoutBinding.coinNumbEdit.setText(StringUtils.double2String(maxNumber, 4));
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private double getInputNumber(EditText input) {
        String text = null == input ? "" : input.getText().toString();
        return TextUtils.isEmpty(text) ? 0D : Double.parseDouble(text);
    }


    public void setData() {
        if (price <= 0) {
            return;
        }

        if (buyOrSell == 0) {
            allNumber = pay_balance * price;
            double numb = Double.parseDouble(StringUtils.double2String(pay_balance * Progress / 100 / price, 4));
            treatyLayoutBinding.coinNumbEdit.setText("" + StringUtils.double2String(numb, amountScale)); // 选择交易的BTC数量
            treatyLayoutBinding.maxMoney.setText("" + StringUtils.double2String(numb, 4));  // 例如 交易的BTC数量
            treatyLayoutBinding.bbAllPrice.setText("" + StringUtils.double2String(numb * price, 4)); // 选择交易的BTC价格  数量*价格 = 交易额
            Cnumb = numb;
        } else {
            double numb = Double.parseDouble(StringUtils.double2String(coin_balance * Progress / 100, 4));
            treatyLayoutBinding.coinNumbEdit.setText(StringUtils.double2String(numb, amountScale) + "");  // 选择交易的BTC数量
            treatyLayoutBinding.maxMoney.setText("" + StringUtils.double2String(numb, 4));  // 例如 交易的BTC数量
            treatyLayoutBinding.bbAllPrice.setText("" + StringUtils.double2String(numb * price, 4)); // 选择交易的BTC价格  数量*价格 = 交易额
            Cnumb = numb;
        }
    }

    private void initPopupWindow() {
        popWindow = new TransactionPopWindow(getActivity());
        popWindow.showAtBottom(treatyLayoutBinding.transactionBbDialog);
        popWindow.setListener(new TransactionPopWindow.selectOnClick() {
            @Override
            public void OnClick(int position) {
                // PopupWindow选择item回调
                switch (position) {
                    case 0: // 充币
                        CoinRechargeActivity.JUMP(getContext(), CoinId, CoinName);
                        break;
                    case 1: // 划转
                        TransferActivity.JUMP(getActivity(), "bb_type", CoinId);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 买或卖 button 显示不同UI
     */
    private void setBuySell(int type) {
        treatyViewModel.changeBgcolor(type);
        clearData();
        treatyLayoutBinding.transactionBuyButton.setTextColor(getResources().getColor(R.color.color_666666));
        treatyLayoutBinding.transactionSellButton.setTextColor(getResources().getColor(R.color.color_666666));
        treatyLayoutBinding.tvpolebarOne.setTextColor(getResources().getColor(R.color.color_666666));
        treatyLayoutBinding.tvpolebarTwo.setTextColor(getResources().getColor(R.color.color_666666));
        if (type == 0) {
            //买
            buyOrSell = 0;
            treatyLayoutBinding.bbSusseekbar.setColor(0);
            treatyLayoutBinding.transactionBuyButton.setTextColor(getResources().getColor(R.color.white));
            treatyLayoutBinding.transactionBuyButton.setBackground(getResources().getDrawable(R.drawable.shape_green_layout));
            treatyLayoutBinding.transactionSellButton.setBackground(getResources().getDrawable(R.drawable.shape_gray_button_layout));
            treatyLayoutBinding.transactionConfirmButton.setBackground(getResources().getDrawable(R.drawable.shape_green_layout));
            treatyLayoutBinding.bbAvailableNumb.setText(
                    getResources().getString(R.string.transaction_fb_transact_assets_account_available_text)+"：" + pay_balance + PayName);

            treatyLayoutBinding.tvpolebarOne.setTextColor(getResources().getColor(R.color.white));
            if (25 == PoloBarnum) {
                treatyLayoutBinding.tvpolebarOne.setTextColor(getResources().getColor(R.color.white));
                treatyLayoutBinding.tvpolebarOne.setBackground(getResources().getDrawable(R.drawable.shape_green_layout));
                treatyLayoutBinding.tvpolebarTwo.setBackground(getResources().getDrawable(R.drawable.shape_gray_button_layout));
            } else {
                treatyLayoutBinding.tvpolebarTwo.setTextColor(getResources().getColor(R.color.white));
                treatyLayoutBinding.tvpolebarTwo.setBackground(getResources().getDrawable(R.drawable.shape_green_layout));
                treatyLayoutBinding.tvpolebarOne.setBackground(getResources().getDrawable(R.drawable.shape_gray_button_layout));
            }
        } else {
            //卖
            buyOrSell = 1;
            treatyLayoutBinding.bbSusseekbar.setColor(1);
            treatyLayoutBinding.transactionSellButton.setTextColor(getResources().getColor(R.color.white));
            treatyLayoutBinding.transactionBuyButton.setBackground(getResources().getDrawable(R.drawable.shape_gray_button_layout));
            treatyLayoutBinding.transactionSellButton.setBackground(getResources().getDrawable(R.drawable.shape_red_layout));
            treatyLayoutBinding.transactionConfirmButton.setBackground(getResources().getDrawable(R.drawable.shape_red_layout));
            treatyLayoutBinding.coinNameText.setText(CoinName);
            treatyLayoutBinding.maxMoney.setText(pay_balance + "");
            treatyLayoutBinding.bbAvailableNumb.setText(
                    getResources().getString(R.string.transaction_fb_transact_assets_account_available_text)+"："
                            + coin_balance + CoinName);
            if (25 == PoloBarnum) {
                treatyLayoutBinding.tvpolebarOne.setTextColor(getResources().getColor(R.color.white));
                treatyLayoutBinding.tvpolebarOne.setBackground(getResources().getDrawable(R.drawable.shape_red_layout));
                treatyLayoutBinding.tvpolebarTwo.setBackground(getResources().getDrawable(R.drawable.shape_gray_button_layout));
            } else {
                treatyLayoutBinding.tvpolebarTwo.setTextColor(getResources().getColor(R.color.white));
                treatyLayoutBinding.tvpolebarTwo.setBackground(getResources().getDrawable(R.drawable.shape_red_layout));
                treatyLayoutBinding.tvpolebarOne.setBackground(getResources().getDrawable(R.drawable.shape_gray_button_layout));
            }

        }
    }

    /**
     * 切换买或卖 清空操作数据
     */
    private void clearData() {
        price = 0;
        treatyLayoutBinding.transactionPriceEdit.setText("");
        treatyLayoutBinding.coinNumbEdit.setText("");
        treatyLayoutBinding.bbCny.setText("0");
        treatyLayoutBinding.bbSusseekbar.setProgress(0);
        treatyLayoutBinding.bbAllPrice.setText("--");
    }

    /**
     * 杠杆选择
     */
    private void changePoloBar(int state) {
        treatyLayoutBinding.tvpolebarOne.setTextColor(getResources().getColor(R.color.color_666666));
        treatyLayoutBinding.tvpolebarTwo.setTextColor(getResources().getColor(R.color.color_666666));
        if (0 == state) {
            PoloBarnum = 25;
            treatyLayoutBinding.tvpolebarOne.setTextColor(getResources().getColor(R.color.white));
            if (0 == buyOrSell) {
                treatyLayoutBinding.tvpolebarOne.setBackground(getResources().getDrawable(R.drawable.shape_green_layout));
                treatyLayoutBinding.tvpolebarTwo.setBackground(getResources().getDrawable(R.drawable.shape_gray_button_layout));
            } else {
                treatyLayoutBinding.tvpolebarOne.setBackground(getResources().getDrawable(R.drawable.shape_red_layout));
                treatyLayoutBinding.tvpolebarTwo.setBackground(getResources().getDrawable(R.drawable.shape_gray_button_layout));
            }
        } else {
            PoloBarnum = 50;
            treatyLayoutBinding.tvpolebarTwo.setTextColor(getResources().getColor(R.color.white));
            if (0 == buyOrSell) {
                treatyLayoutBinding.tvpolebarTwo.setBackground(getResources().getDrawable(R.drawable.shape_green_bg_layout));
                treatyLayoutBinding.tvpolebarOne.setBackground(getResources().getDrawable(R.drawable.shape_gray_button_layout));
            } else {
                treatyLayoutBinding.tvpolebarTwo.setBackground(getResources().getDrawable(R.drawable.shape_red_layout));
                treatyLayoutBinding.tvpolebarOne.setBackground(getResources().getDrawable(R.drawable.shape_gray_button_layout));
            }
        }
        treatyViewModel.changeCostView(selMoney,PoloBarnum);


    }

    private void addPrice(double update) {
        treatyLayoutBinding.transactionPriceEdit.setText(StringUtils.double2String(ArithTulis.add(price, update), priceScale) + "");
        AnimationUtils.followAnimation(treatyLayoutBinding.transactionPriceEdit, 60);
        price = ArithTulis.add(price, update);
        treatyLayoutBinding.bbCny.setText("≈" + StringUtils.double2String(ArithTulis.mul(price, price_cny), priceCnyScale));
        setData();
    }

    private void deletePrice(double update) {
        treatyLayoutBinding.transactionPriceEdit.setText(StringUtils.double2String(ArithTulis.sub(price, update), priceScale) + "");
        AnimationUtils.followAnimation(treatyLayoutBinding.transactionPriceEdit, 60);
        price = ArithTulis.sub(price, update);
        treatyLayoutBinding.bbCny.setText("≈" + StringUtils.double2String(ArithTulis.mul(price, price_cny), priceCnyScale));
        setData();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll:
                if (!EmptyUtil.isEmpty(coinEntity)) {
                    QuotationActivity.JUMP(getActivity(), coinEntity);
                }
                break;
            case R.id.transaction_select_coin:
                EventBus.getDefault().post("select_coin");
                break;
            case R.id.transaction_bb_dialog:
                EventBus.getDefault().post("goTransactionBb");
                break;
            case R.id.transaction_buy_button: // 选择买入
                buyOrSell = 0;
                setBuySell(buyOrSell);
                break;
            case R.id.transaction_sell_button: // 选择卖出
                buyOrSell = 1;
                setBuySell(buyOrSell);
                break;
            case R.id.tvpolebar_one: // 杠杆 25X
                changePoloBar(0);
                break;
            case R.id.tvpolebar_two: // 50X
                changePoloBar(1);
                break;

            case R.id.add_price: // 加价格
                addPrice(0.01);
                break;
            case R.id.delete_price: // 减价格
                deletePrice(0.01);
                break;
            case R.id.bb_all_entrust: // 跳转去全部
                CoinOrderActivity.JUMP(getActivity());
                break;
            case R.id.ll_order: // 跳转去订单
                EventBus.getDefault().post("goOrder");
                break;
            default:
                break;
        }
    }

    /**
     * 挂单 是否需要挂单
     */
    private void showOrderDialog() {
        new ActionSheetDialog(getActivity())
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .setTitle("是否挂单")
                .setOnDismissListener(new ActionSheetDialog.DialogOnDismissListener() {
                    @Override
                    public void onDialogDismiss(ActionSheetDialog dialog) {

                    }
                })
                .addSheetItem("确定", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        treatyViewModel.postOrder(FaceApplication.getToken(), treatyLayoutBinding.transactionPriceEdit.getText().toString(), treatyLayoutBinding.coinNumbEdit.getText().toString(), marketId, buyOrSell);
                    }
                }).show();
    }

    /**
     * 撤销弹窗
     */
    public void showDialog(final EntrustInfo entity) {
        new ActionSheetDialog(getContext())
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .setTitle("是否撤销")
                .setOnDismissListener(new ActionSheetDialog.DialogOnDismissListener() {
                    @Override
                    public void onDialogDismiss(ActionSheetDialog dialog) {

                    }
                })
                .addSheetItem("撤销", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        treatyViewModel.cancelOrder(FaceApplication.getToken(), entity);
                    }
                }).show();
    }


    /**
     * 选择币 刷新页面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateCoin(CoinEntity model) {
        try {
            if (!EmptyUtil.isEmpty(model)) {
                Log.e("cjh>>>", "选币回来刷新页面   id:" + model.getCoin_id());
                //Toast.makeText(getActivity(), "选币回来刷新页面   id:" + model.getCoinId(), Toast.LENGTH_SHORT).show();
                CoinId = model.getCoin_id();
                CoinName = model.getName();
                marketId = model.getMarket_id();
                treatyViewModel.RefreshCoinData(marketId + "");
                treatyLayoutBinding.bbCoinName.setText(model.getName() + "/" + model.getPay_name());
                treatyLayoutBinding.coinNameText.setText(model.getName());
                setBuySell(buyOrSell);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 选择买入 还是卖出
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void selectBuySell(MarketsDTO entity) {
        try {
            if (!EmptyUtil.isEmpty(entity)) {
                coinEntity = entity;
                if (entity.getSelect().equals("buy")) {
                    buyOrSell = 0;
                    setBuySell(buyOrSell);
                } else if (entity.getSelect().equals("sell")) {
                    buyOrSell = 1;
                    setBuySell(buyOrSell);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  下单成功 刷新界面数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void transfer(String model) {
        try {
            if (null != model && model.equals("Transfer")) {
                treatyViewModel.RefreshCoinData(marketId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 开启定时任务 刷新深度数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void stopTimer(String model) {
        try {
            if (null != model && model.equals("startTimer")) {
                treatyViewModel.getDepth(marketId);
                treatyViewModel.getCoin();
                treatyViewModel.getContracetradelogNew(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null!=treatyViewModel){
            treatyViewModel.getUserInfo(FaceApplication.getToken());
        }
    }
}

