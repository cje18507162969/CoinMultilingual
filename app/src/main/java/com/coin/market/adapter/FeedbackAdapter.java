package com.coin.market.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.coin.market.R;
import com.coin.market.activity.showpicture.ShowPictureActivity;
import com.coin.market.adapter.shopadapter.ItemCommentPicAdapter;
import com.coin.market.util.EmptyUtil;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.util.Arrays;
import java.util.List;

import teng.wang.comment.model.FeedbackModel;
import teng.wang.comment.widget.CircleImageView;

/**
 * @author Ycc 意见反馈适配器展示
 * @version v1.0
 * @Time 2018-9-14
 */

public class FeedbackAdapter extends RecyclerArrayAdapter<FeedbackModel.DataBean> {

    public static Context mContext;
    public FeedbackAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<FeedbackModel.DataBean> {
        private TextView tv_time;
        private TextView tv_content, tv_answerContent;
        private ImageView iv_1, iv_2, iv_3;
        private LinearLayout lv_answerContent, img_layout;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_shop_details_comment_layout);
            tv_time = $(R.id.tv_time);
            tv_content = $(R.id.tv_content);
            tv_answerContent = $(R.id.tv_answerContent);
            lv_answerContent = $(R.id.lv_answerContent);
            img_layout = $(R.id.img_layout);
            iv_1 = $(R.id.iv_1);
            iv_2 = $(R.id.iv_2);
            iv_3 = $(R.id.iv_3);
        }

        @Override
        public void setData(final FeedbackModel.DataBean dataBean) {
            try {
                tv_time.setText(dataBean.getCreateDate());
                tv_content.setText(mContext.getResources().getString(R.string.tv_question)+":" + dataBean.getIssueDescription());
                if (!EmptyUtil.isEmpty(dataBean.getReplyMessage())){
                    tv_answerContent.setText(dataBean.getReplyMessage());
                    lv_answerContent.setVisibility(View.VISIBLE);
                }else {
                    lv_answerContent.setVisibility(View.GONE);
                }
                if (!EmptyUtil.isEmpty(dataBean.getPic())){
                    final List<String> list = Arrays.asList(dataBean.getPic().split(","));
                    if (list.size()>0){
                        for (int i = 0; i < list.size(); i++) {
                            if (i==0){
                                Glide.with(getContext()).load(list.get(i)).into(iv_1);
                            }
                            if (i==1){
                                Glide.with(getContext()).load(list.get(i)).into(iv_2);
                            }
                            if (i==2){
                                Glide.with(getContext()).load(list.get(i)).into(iv_3);
                            }
                        }
                    }else {
                        img_layout.setVisibility(View.GONE);
                    }
                    iv_1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (list.size()<1){
                                return;
                            }
                            ShowPictureActivity.JUMP(getContext(), list.get(0));
                        }
                    });
                    iv_2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (list.size()<2){
                                return;
                            }
                            ShowPictureActivity.JUMP(getContext(), list.get(1));
                        }
                    });
                    iv_3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (list.size()<3){
                                return;
                            }
                            ShowPictureActivity.JUMP(getContext(), list.get(2));
                        }
                    });
                }else {
                    img_layout.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
