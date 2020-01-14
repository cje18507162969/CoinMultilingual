package com.coin.market.adapter.shopadapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coin.market.R;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import teng.wang.comment.model.CbdBaseModel.AddressListModelEntity;

/**
 * @author Ycc 收货地址适配器
 * @version v1.0
 * @Time 2018-8-28
 */

public class AddressListAdapter extends RecyclerArrayAdapter<AddressListModelEntity> {
    private static mCartCallback listener;

    public void setListener(mCartCallback listener) {
        this.listener = listener;
    }

    public AddressListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PicPersonViewHolder(parent);
    }

    private static class PicPersonViewHolder extends BaseViewHolder<AddressListModelEntity> {
        private CheckBox mCheckbox;
        private TextView mTvCollectName;
        private LinearLayout mLinDetails;
        private TextView mTvCollectPhone;
        private TextView mTvDetailedAddress;
        private LinearLayout mLinUpdateAddress;
        private LinearLayout address_jump;

        public PicPersonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_address_list_layout);
            mCheckbox = $(R.id.checkbox);
            mLinDetails = $(R.id.mLin_details);
            mTvCollectName = $(R.id.mTv_Collect_name);
            mTvCollectPhone = $(R.id.mTv_Collect_phone);
            mLinUpdateAddress = $(R.id.mLin_updateAddress);
            mTvDetailedAddress = $(R.id.mTv_Detailed_address);
            address_jump = $(R.id.address_jump);
        }

        @Override
        public void setData(final AddressListModelEntity person) {
            try {
                if (person.isDefault == 1) {
                    mCheckbox.setChecked(true);
                } else {
                    mCheckbox.setChecked(false);
                }
                mTvCollectName.setText(person.fullName);
                mTvCollectPhone.setText(person.mobileNumber);
                mTvDetailedAddress.setText(person.detailAddress);
                address_jump.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != listener) {
                            listener.JUMP(person.id+"");
                        }
                    }
                });

                mCheckbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (null != listener) {
                            listener.mAddCartShop(person, mCheckbox, getDataPosition());
                        }
                    }
                });
                mLinDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (null != listener) {
                            listener.mDetailsAdd(person, getDataPosition());
                        }
                    }
                });
                mLinUpdateAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (null != listener) {
                            listener.updateAddress(person, getDataPosition());
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface mCartCallback {
        void mAddCartShop(AddressListModelEntity person, CheckBox checkBox, int position);

        void mDetailsAdd(AddressListModelEntity person, int position);

        void updateAddress(AddressListModelEntity person, int position);

        void JUMP(String id);
    }
}
