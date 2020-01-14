package com.coin.market.activity.mine.bindemailconfirm;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.databinding.ActivityBindPhoneConfirmLayoutBinding;
import com.coin.market.util.EditUtils;
import com.coin.market.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import teng.wang.comment.base.BaseActivity;
import teng.wang.comment.base.FaceApplication;
import teng.wang.comment.utils.log.Log;

/**
 * @author: Lenovo
 * @date: 2019/8/12
 * @info: 绑定邮箱 确认
 */
public class BindEmailConfirmActivity extends BaseActivity implements View.OnClickListener {

    private BindEmailConfirmViewModel model;
    private ActivityBindPhoneConfirmLayoutBinding binding;
    private String email;
    private List<TextView> textList = new ArrayList<TextView>();

    public static void JUMP(Context context, String email) {
        context.startActivity(new Intent(context, BindEmailConfirmActivity.class).putExtra("email", email));
    }

    @Override
    protected void initIntentData() {
        email = getIntent().getStringExtra("email");
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSteepStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bind_phone_confirm_layout);
        model = new BindEmailConfirmViewModel(this, binding);
        textList.add(binding.bindEmailCode1);
        textList.add(binding.bindEmailCode2);
        textList.add(binding.bindEmailCode3);
        textList.add(binding.bindEmailCode4);
        textList.add(binding.bindEmailCode5);
        textList.add(binding.bindEmailCode6);
    }

    @Override
    protected void initTitleData() {
        binding.titleBar.txtTitle.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        binding.titleBar.imgReturn.setOnClickListener(this);
        EditUtils.showSoftInputFromWindow(this, binding.bindEmailEdit);
        binding.bindEmailEdit.setFocusable(true);
        binding.bindEmailEdit.setFocusableInTouchMode(true);
        binding.bindEmailEdit.requestFocus();
        binding.bindEmailEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("cjh>>>", "onTextChanged:" + charSequence.toString());
                if (i <= 6) {
                    Toast.makeText(mContext, "输入之后i：" + i, Toast.LENGTH_SHORT).show();
                    clearText();
                    for (int j = 0; j < charSequence.toString().length(); j++) {
                        setText(charSequence.toString(), j);
                    }
                }
                if (charSequence.toString().length()==6){
                    model.bindEmail(FaceApplication.getToken(), email, charSequence.toString());
                    Log.e("cjh>>>","请求：email：" + email + "   code：" +charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void clearText(){
        for (int j = 0; j < textList.size(); j++) {
            textList.get(j).setText("");
        }
    }

    private void setText(String str, int i) {
        switch (i) {
            case 0:
                binding.bindEmailCode1.setText(StringUtils.getEditPosition(str, i));
                break;
            case 1:
                binding.bindEmailCode2.setText(StringUtils.getEditPosition(str, i));
                break;
            case 2:
                binding.bindEmailCode3.setText(StringUtils.getEditPosition(str, i));
                break;
            case 3:
                binding.bindEmailCode4.setText(StringUtils.getEditPosition(str, i));
                break;
            case 4:
                binding.bindEmailCode5.setText(StringUtils.getEditPosition(str, i));
                break;
            case 5:
                binding.bindEmailCode6.setText(StringUtils.getEditPosition(str, i));
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_Return:
                finish();
                break;
            case R.id.bind_phone_confirm_edit_button:

                break;
            default:
                break;
        }
    }

}
