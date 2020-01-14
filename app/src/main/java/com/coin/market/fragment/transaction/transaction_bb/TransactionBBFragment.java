package com.coin.market.fragment.transaction.transaction_bb;

import android.databinding.DataBindingUtil;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.activity.mine.coinorder.CoinOrderActivity;
import com.coin.market.activity.mine.coinrecharge.CoinRechargeActivity;
import com.coin.market.activity.mine.transfer.TransferActivity;
import com.coin.market.activity.quotation.QuotationActivity;
import com.coin.market.databinding.FragmentTransactionBbLayoutBinding;
import com.coin.market.util.AnimationUtils;
import com.coin.market.util.ArithTulis;
import com.coin.market.util.EmptyUtil;
import com.coin.market.util.StringUtils;
import com.coin.market.wight.CusSeekBar;
import com.coin.market.wight.popupwindow.TransactionPopWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import teng.wang.comment.base.BaseFragment;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.CoinEntity;
import teng.wang.comment.model.EntrustInfo;
import teng.wang.comment.utils.log.Log;
import teng.wang.comment.widget.ActionSheetDialog;

/**
 * @author 交易Fragment  币币
 * @version v1.0
 * @Time 2018-9-3
 */
public class TransactionBBFragment extends BaseFragment implements View.OnClickListener {

    private TransactionBBViewModel transactionBBViewModel;
    private FragmentTransactionBbLayoutBinding bbLayoutBinding;
    private TransactionPopWindow popWindow;
    public CoinEntity coinEntity;
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

    public static TransactionBBFragment getTransactionBBFragment() {
        return new TransactionBBFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup viewGroup) {
        bbLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_transaction_bb_layout, viewGroup, false);
        return bbLayoutBinding.getRoot();
    }

    @Override
    protected void onFragmentFirstVisible() {
        EventBus.getDefault().register(this);
        transactionBBViewModel = new TransactionBBViewModel(this, bbLayoutBinding);
        setSeekBar();
        transactionBBViewModel.initKeyboardStatus();
    }

    @Override
    protected void setListener() {
        bbLayoutBinding.transactionQuotation.setOnClickListener(this);
        bbLayoutBinding.transactionSelectCoin.setOnClickListener(this);
        bbLayoutBinding.transactionBbDialog.setOnClickListener(this);
        bbLayoutBinding.transactionBuyButton.setOnClickListener(this);
        bbLayoutBinding.transactionSellButton.setOnClickListener(this);
        bbLayoutBinding.transactionConfirmButton.setOnClickListener(this);
        bbLayoutBinding.addPrice.setOnClickListener(this);
        bbLayoutBinding.deletePrice.setOnClickListener(this);
        bbLayoutBinding.bbAllEntrust.setOnClickListener(this);
        bbLayoutBinding.transactionPriceEdit.addTextChangedListener(new TextWatcher() {
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
//                        bbLayoutBinding.transactionPriceEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(charSequence.length() + priceScale)});
//                    }else {
//                        bbLayoutBinding.transactionPriceEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(charSequence.length() + 4)});
//                    }
//
//                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    bbLayoutBinding.transactionPriceEdit.setSelection(bbLayoutBinding.transactionPriceEdit.getText().length());
                    if (!TextUtils.isEmpty(editable.toString())){
                        price = Double.parseDouble(editable.toString());
                        double maxNumber = pay_balance / price;
                        double number = getInputNumber(bbLayoutBinding.coinNumbEdit);
                        if (number < maxNumber){
                            int aa = ((int) (number * 100 / maxNumber));
                            bbLayoutBinding.bbSusseekbar.setProgress((int) (number * 100 / maxNumber));
                            Log.e("cjh>>>","setProgress" + aa);
                        }else {
                            bbLayoutBinding.bbSusseekbar.setProgress(100);
                            //bbLayoutBinding.coinNumbEdit.setText(StringUtils.double2String(maxNumber, 4));
                        }
                        bbLayoutBinding.bbCny.setText("≈" + StringUtils.double2String(price_cny*price, priceCnyScale));
                    }else {
                        bbLayoutBinding.bbCny.setText("≈" + StringUtils.double2String(0, priceCnyScale));
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        bbLayoutBinding.coinNumbEdit.addTextChangedListener(new TextWatcher() {
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
//                        bbLayoutBinding.coinNumbEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(charSequence.length() + amountScale)});
//                    }else {
//                        bbLayoutBinding.coinNumbEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(charSequence.length() + 4)});
//                    }
//
//                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    bbLayoutBinding.coinNumbEdit.setSelection(bbLayoutBinding.coinNumbEdit.getText().length());
                    if (!TextUtils.isEmpty(editable.toString()) && price>0){
                        double maxNumber = Double.parseDouble(StringUtils.double2String(pay_balance / price, 4));
                        double number = getInputNumber(bbLayoutBinding.coinNumbEdit);
                        if (number < maxNumber){
                            Cnumb = number;
                            int aa = ((int) (number * 100 / maxNumber));
                            bbLayoutBinding.bbSusseekbar.setProgress((int) (number * 100 / maxNumber));
                            Log.e("cjh>>>","setProgress" + aa);
                        }else {
                            Cnumb = maxNumber;
                            bbLayoutBinding.bbSusseekbar.setProgress(100);
                            //bbLayoutBinding.coinNumbEdit.setText(StringUtils.double2String(maxNumber, 4));
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

    private void setSeekBar() {
        bbLayoutBinding.coinNameText.setText(CoinName);
        bbLayoutBinding.maxMoney.setText(pay_balance + "");
        bbLayoutBinding.bbAvailableNumb.setText(
                getResources().getString(R.string.transaction_fb_transact_assets_account_available_text)+"："
                + pay_balance + "");
        bbLayoutBinding.bbSusseekbar.setListener(new CusSeekBar.OnChangeListener() {
            @Override
            public void onChange(int progress) {
                if (Progress!=progress){
                    Progress = progress;
                    setData();
                }
            }
        });
    }

    public void setData() {
        if (price <= 0) {
            return;
        }

        if (buyOrSell == 0) {
            allNumber = pay_balance * price;
            double numb = Double.parseDouble(StringUtils.double2String(pay_balance * Progress / 100 / price, 4));
            bbLayoutBinding.coinNumbEdit.setText("" + StringUtils.double2String(numb, amountScale)); // 选择交易的BTC数量
            bbLayoutBinding.maxMoney.setText("" + StringUtils.double2String(numb, 4));  // 例如 交易的BTC数量
            bbLayoutBinding.bbAllPrice.setText("" + StringUtils.double2String(numb * price, 4)); // 选择交易的BTC价格  数量*价格 = 交易额
            Cnumb = numb;
        } else {
            double numb = Double.parseDouble(StringUtils.double2String(coin_balance * Progress / 100, 4));
            bbLayoutBinding.coinNumbEdit.setText(StringUtils.double2String(numb, amountScale) + "");  // 选择交易的BTC数量
            bbLayoutBinding.maxMoney.setText("" + StringUtils.double2String(numb, 4));  // 例如 交易的BTC数量
            bbLayoutBinding.bbAllPrice.setText("" + StringUtils.double2String(numb * price, 4)); // 选择交易的BTC价格  数量*价格 = 交易额
            Cnumb = numb;
        }
    }

    private void initPopupWindow() {
        popWindow = new TransactionPopWindow(getActivity());
        popWindow.showAtBottom(bbLayoutBinding.transactionBbDialog);
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
        clearData();
        bbLayoutBinding.transactionBuyButton.setTextColor(getResources().getColor(R.color.color_666666));
        bbLayoutBinding.transactionSellButton.setTextColor(getResources().getColor(R.color.color_666666));
        if (type == 0) {
            //买
            buyOrSell = 0;
            bbLayoutBinding.bbSusseekbar.setColor(0);
            bbLayoutBinding.transactionBuyButton.setTextColor(getResources().getColor(R.color.white));
            bbLayoutBinding.transactionBuyButton.setBackground(getResources().getDrawable(R.drawable.shape_green_layout));
            bbLayoutBinding.transactionSellButton.setBackground(getResources().getDrawable(R.drawable.shape_gray_button_layout));
            bbLayoutBinding.transactionConfirmButton.setBackground(getResources().getDrawable(R.drawable.shape_green_layout));
            bbLayoutBinding.transactionConfirmButton.setText("买入" + CoinName);
            bbLayoutBinding.bbAvailableNumb.setText(
                    getResources().getString(R.string.transaction_fb_transact_assets_account_available_text)+"：" + pay_balance + PayName);
        } else {
            //卖
            buyOrSell = 1;
            bbLayoutBinding.bbSusseekbar.setColor(1);
            bbLayoutBinding.transactionSellButton.setTextColor(getResources().getColor(R.color.white));
            bbLayoutBinding.transactionBuyButton.setBackground(getResources().getDrawable(R.drawable.shape_gray_button_layout));
            bbLayoutBinding.transactionSellButton.setBackground(getResources().getDrawable(R.drawable.shape_red_layout));
            bbLayoutBinding.transactionConfirmButton.setBackground(getResources().getDrawable(R.drawable.shape_red_layout));
            bbLayoutBinding.transactionConfirmButton.setText("卖出" + CoinName);
            bbLayoutBinding.coinNameText.setText(CoinName);
            bbLayoutBinding.maxMoney.setText(pay_balance + "");
            bbLayoutBinding.bbAvailableNumb.setText(getResources().getString(R.string.transaction_fb_transact_assets_account_available_text)+"："
                    + coin_balance + CoinName);
        }
    }

    /**
     * 切换买或卖 清空操作数据
     */
    private void clearData() {
        price = 0;
        bbLayoutBinding.transactionPriceEdit.setText("");
        bbLayoutBinding.coinNumbEdit.setText("");
        bbLayoutBinding.bbCny.setText("0");
        bbLayoutBinding.bbSusseekbar.setProgress(0);
        bbLayoutBinding.bbAllPrice.setText("--");
    }

    private void addPrice(double update) {
        bbLayoutBinding.transactionPriceEdit.setText(StringUtils.double2String(ArithTulis.add(price, update), priceScale) + "");
        AnimationUtils.followAnimation(bbLayoutBinding.transactionPriceEdit, 60);
        price = ArithTulis.add(price, update);
        bbLayoutBinding.bbCny.setText("≈" + StringUtils.double2String(ArithTulis.mul(price, price_cny), priceCnyScale));
        setData();
    }

    private void deletePrice(double update) {
        bbLayoutBinding.transactionPriceEdit.setText(StringUtils.double2String(ArithTulis.sub(price, update), priceScale) + "");
        AnimationUtils.followAnimation(bbLayoutBinding.transactionPriceEdit, 60);
        price = ArithTulis.sub(price, update);
        bbLayoutBinding.bbCny.setText("≈" + StringUtils.double2String(ArithTulis.mul(price, price_cny), priceCnyScale));
        setData();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.transaction_quotation:
                if (!EmptyUtil.isEmpty(coinEntity)) {
//                    QuotationActivity.JUMP(getActivity(), coinEntity);
                }
                break;
            case R.id.transaction_select_coin:
                EventBus.getDefault().post("select_coin");
                break;
            case R.id.transaction_bb_dialog:
                initPopupWindow();
                break;
            case R.id.transaction_buy_button: // 选择买入
                buyOrSell = 0;
                setBuySell(buyOrSell);
                break;
            case R.id.transaction_sell_button: // 选择卖出
                buyOrSell = 1;
                setBuySell(buyOrSell);
                break;
            case R.id.transaction_confirm_button: // 买入 卖出 Button
                if (EmptyUtil.isEmpty(bbLayoutBinding.transactionPriceEdit.getText().toString())) {
                    Toast.makeText(getActivity(), "价格不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (EmptyUtil.isEmpty(bbLayoutBinding.coinNumbEdit.getText().toString())) {
                    Toast.makeText(getActivity(), "数量不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                showOrderDialog();
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
                        transactionBBViewModel.postOrder(FaceApplication.getToken(), bbLayoutBinding.transactionPriceEdit.getText().toString(), bbLayoutBinding.coinNumbEdit.getText().toString(), marketId, buyOrSell);
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
                        transactionBBViewModel.cancelOrder(FaceApplication.getToken(), entity);
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
                transactionBBViewModel.RefreshCoinData(marketId + "");
                bbLayoutBinding.bbCoinName.setText(model.getName() + "/" + model.getPay_name());
                bbLayoutBinding.coinNameText.setText(model.getName());
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
    public void selectBuySell(CoinEntity entity) {
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
                bbLayoutBinding.bbCoinName.setText(entity.getName() + "/" + entity.getPay_name());
                transactionBBViewModel.RefreshCoinData(entity.getMarket_id() + "");
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
                transactionBBViewModel.RefreshCoinData(marketId);
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
                transactionBBViewModel.getDepth(marketId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
