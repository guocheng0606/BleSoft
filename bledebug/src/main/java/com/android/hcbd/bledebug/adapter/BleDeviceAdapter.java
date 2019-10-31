package com.android.hcbd.bledebug.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.android.hcbd.bledebug.R;
import com.android.hcbd.bledebug.ui.activity.BleMainActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import cn.com.heaton.blelibrary.ble.model.BleDevice;

public class BleDeviceAdapter extends BaseQuickAdapter<BleDevice, BaseViewHolder> {

    private List<Integer> rssiList = new ArrayList<>();

    public BleDeviceAdapter(int layoutResId, @Nullable List<BleDevice> data) {
        super(layoutResId, data);
    }

    public BleDeviceAdapter(@Nullable List<BleDevice> data) {
        super(data);
    }

    public BleDeviceAdapter(int layoutResId) {
        super(layoutResId);
    }

    public void addDevice(BleDevice device,int rssi){
        for (BleDevice d : getData()){
            if(d.getBleAddress().equals(device.getBleAddress())){
                return;
            }
        }
        addData(device);
        rssiList.add(rssi);
    }

    public void clearData(){
        getData().clear();
        notifyDataSetChanged();
        rssiList.clear();
    }

    @Override
    protected void convert(@NonNull final BaseViewHolder helper, final BleDevice item) {
        helper.setText(R.id.tv_name,TextUtils.isEmpty(item.getBleName()) ? "N/A" : item.getBleName());
        helper.setText(R.id.tv_address,item.getBleAddress());
        helper.getView(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BleMainActivity.class);
                intent.putExtra("ble_device",item);
                mContext.startActivity(intent);
            }
        });
        helper.setText(R.id.tv_rssi,rssiList.get(helper.getAdapterPosition())+"dBm");


    }
}
