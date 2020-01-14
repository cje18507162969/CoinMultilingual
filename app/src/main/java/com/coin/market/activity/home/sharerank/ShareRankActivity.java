package com.coin.market.activity.home.sharerank;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.coin.market.R;
import com.coin.market.databinding.ActivityShareRankLayoutBinding;
import com.coin.market.util.DateUtils;
import com.coin.market.util.EmptyUtil;

import java.util.Calendar;
import java.util.Date;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;

/**
 * @author: cjh
 * @date: 2019/10/18
 * @info: 邀请榜单
 */
public class ShareRankActivity extends BaseActivity implements View.OnClickListener {

    private ActivityShareRankLayoutBinding binding;
    private ShareRankViewModel model;
    private TimePickerView pvCustomLunar;
    public String time = "";
    public int page = 1;

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, ShareRankActivity.class));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_share_rank_layout);
        model = new ShareRankViewModel(this, binding);
        initLunarPicker();
    }

    @Override
    protected void setListener() {
        binding.shareRankBack.setOnClickListener(this);
        binding.rankScreen.setOnClickListener(this);
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
                    time = DateUtils.getTime(date).replace("-","_");
                    page = 1;
                    model.getShareRank(FaceApplication.getToken(), page, time,1);
                    binding.tvTime.setVisibility(View.VISIBLE);
                    binding.tvTime.setText(time.replaceAll("_","/"));
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
            case R.id.share_rank_back:
                finish();
                break;
            case R.id.rank_screen: // 筛选的弹窗
                pvCustomLunar.show();
                break;
            default:
                break;
        }
    }

}
