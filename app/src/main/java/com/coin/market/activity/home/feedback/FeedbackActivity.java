package com.coin.market.activity.home.feedback;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.coin.market.R;
import com.coin.market.databinding.ActivityFeedbackFeedbackLayoutBinding;

import teng.wang.comment.base.BaseActivity;

/**
 * @author 意见反馈
 * @version v1.0
 * @Time 2018-9-14
 */

public class FeedbackActivity extends BaseActivity implements View.OnClickListener {

    private FeedbackViewModel mFeedbackFeedbackModel;
    private ActivityFeedbackFeedbackLayoutBinding mFeedbackFeedbackLayoutBinding;

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, FeedbackActivity.class));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mFeedbackFeedbackLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_feedback_feedback_layout);
        mFeedbackFeedbackModel = new FeedbackViewModel(this, mFeedbackFeedbackLayoutBinding);
    }

    @Override
    protected void initTitleData() {
        mFeedbackFeedbackLayoutBinding.titleBar.txtTitle.setText(getResources().getString(R.string.home_feedback));
    }

    @Override
    protected void setListener() {
        mFeedbackFeedbackLayoutBinding.titleBar.imgReturn.setOnClickListener(this);
        mFeedbackFeedbackLayoutBinding.titleBar.mTvPreservation.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_Return:
                finish();
                break;
            default:
                break;
        }
    }

}
