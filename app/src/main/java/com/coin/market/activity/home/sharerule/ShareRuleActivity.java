package com.coin.market.activity.home.sharerule;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.coin.market.R;
import com.coin.market.databinding.ActivityShareRuleLayoutBinding;
import com.google.gson.Gson;

import teng.wang.comment.api.FaceApiTest;
import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.model.ApiModel;
import teng.wang.comment.model.RuleModel;
import teng.wang.comment.retrofit.RxObserver;
import teng.wang.comment.retrofit.RxSchedulers;
import teng.wang.comment.utils.log.Log;

/**
 * @author: cjh
 * @date: 2019/10/18
 * @info: 分享规则
 */
public class ShareRuleActivity extends BaseActivity implements View.OnClickListener {

    private ActivityShareRuleLayoutBinding binding;

    public static void JUMP(Context context) {
        context.startActivity(new Intent(context, ShareRuleActivity.class));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_share_rule_layout);
        getRule(FaceApplication.getToken());
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setText("规则");
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_Return:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     *   分享  返佣记录
     */
    public void getRule(String token) {
        FaceApiTest.getV1ApiServiceTest().getRule(token, "1")
                .compose(RxSchedulers.<ApiModel<RuleModel>>io_main())
                .subscribe(new RxObserver<RuleModel>(this, this.Tag, true) {
                    @Override
                    public void onSuccess(RuleModel model) {
                        try {
                            Log.e("cjh>>>", "返佣规则" + new Gson().toJson(model));
                            binding.shareRule.setText(model.getRule());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
