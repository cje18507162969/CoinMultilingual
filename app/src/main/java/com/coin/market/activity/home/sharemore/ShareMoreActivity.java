package com.coin.market.activity.home.sharemore;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.coin.market.R;
import com.coin.market.databinding.ActivityShareMoreLayoutBinding;
import com.coin.market.util.DateUtils;
import com.coin.market.util.EmptyUtil;
import com.coin.market.wight.dialog.AssetRecordsScreenDialog;
import com.coin.market.wight.dialog.ExchangeDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ExchangeDialogModel;

/**
 * @author: cjh
 * @date: 2019/10/18
 * @info: 返佣记录
 */
public class ShareMoreActivity extends BaseActivity implements View.OnClickListener {

    private int tabType = 0;
    private ActivityShareMoreLayoutBinding binding;
    private ShareMoreViewModel model;
    private TimePickerView pvCustomLunar;

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, ShareMoreActivity.class));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_share_more_layout);
        model = new ShareMoreViewModel(this, binding);
        initLunarPicker();
        if (2==FaceApplication.getLevel()){
            binding.coinOrderRecords.setVisibility(View.VISIBLE);
        }else{
            binding.coinOrderRecords.setVisibility(View.GONE);
        }

    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
        binding.titleBar.mImgScan.setVisibility(View.GONE);
        binding.titleBar.mImgScan.setImageResource(R.drawable.mine_filter);

    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        binding.titleBar.mImgScan.setOnClickListener(this);
        binding.coinOrderAll.setOnClickListener(this);
        binding.coinOrderRecords.setOnClickListener(this);
    }

    public void setTabButton(int type){
        tabType = type;
        binding.coinOrderAll.setTextColor(this.getResources().getColor(R.color.color_999999));
        binding.coinOrderRecords.setTextColor(this.getResources().getColor(R.color.color_999999));
        binding.coinOrderAll.setTextSize(14);
        binding.coinOrderRecords.setTextSize(14);
        if (type==0){
            binding.coinOrderAll.setTextColor(this.getResources().getColor(R.color.app_home_text));
            binding.coinOrderAll.setTextSize(18);

        }else {
            binding.coinOrderRecords.setTextColor(this.getResources().getColor(R.color.app_home_text));
            binding.coinOrderRecords.setTextSize(18);
        }
    }

    /**
     *   邀请记录筛选
     */
    private void showconfirmDialog() {
        List<String> titles = Arrays.asList("全部","生效中","已失效");
        List<ExchangeDialogModel> models = new ArrayList<ExchangeDialogModel>();
        for (int i = 0; i < titles.size(); i++) {
            ExchangeDialogModel bean = new ExchangeDialogModel();
            bean.setTitle(titles.get(i));
            models.add(bean);
        }
        AssetRecordsScreenDialog dialog = new AssetRecordsScreenDialog(this, models);
        dialog.builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .setOnClickListener(new ExchangeDialog.OnItemClickListener() {
                    @Override
                    public void onClick(String title) {
                        switch (title) {
                            case "全部":
                                model.fragment.setType("1");
                                break;
                            case "生效中":
                                model.fragment.setType("2");
                                break;
                            case "已失效":
                                model.fragment.setType("3");
                                break;
                            default:
                                break;
                        }
                    }
                });
        dialog.show();
    }

    /**
     * 农历时间已扩展至 ： 1900 - 2100年
     */
    private void initLunarPicker() {
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
        startDate.set(2014, 1, 23);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2069, 2, 28);
        //时间选择器 ，自定义布局
        pvCustomLunar = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                try {
                    if (EmptyUtil.isEmpty(date)){
                        return;
                    }
                    model.fragment2.setType(DateUtils.getTime(date).replace("-", "_"));
                    Toast.makeText(mContext, "" + DateUtils.getTime(date), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        })
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.pickerview_custom_lunar, new CustomListener() {

                    @Override
                    public void customLayout(final View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        TextView ivCancel = (TextView) v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomLunar.returnData();
                                pvCustomLunar.dismiss();
                            }
                        });
                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomLunar.dismiss();
                            }
                        });
                    }
                })
                .setType(new boolean[]{true, true, false, false, false, false})
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(Color.parseColor("#999999"))
                .build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_Return:
                finish();
                break;
            case R.id.mImg_Scan: // 筛选的弹窗
                if (tabType==0){
                    showconfirmDialog();
                }else {
                    pvCustomLunar.show();
                }
                break;
            case R.id.coin_order_all:
                model.setAdapterItem(0);
                break;
            case R.id.coin_order_records:
                model.setAdapterItem(1);
                break;
            default:
                break;
        }
    }

}
