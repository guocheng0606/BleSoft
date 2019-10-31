package com.android.hcbd.blesoft.holder;

import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.hcbd.blesoft.MyApplication;
import com.android.hcbd.blesoft.R;
import com.android.hcbd.blesoft.event.MessageEvent;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import org.greenrobot.eventbus.EventBus;
import java.util.Set;

public class BleSearchViewHolder extends BaseViewHolder<BluetoothDevice> {

    TextView tv_header;
    TextView tv_name;
    TextView tv_address;
    LinearLayout item_layout;

    public BleSearchViewHolder(ViewGroup parent) {
        super(parent, R.layout.ble_item);
        tv_header = $(R.id.tv_header);
        tv_name = $(R.id.tv_name);
        tv_address = $(R.id.tv_address);
        item_layout = $(R.id.item_layout);
    }

    @Override
    public void setData(final BluetoothDevice data) {
        super.setData(data);
        tv_name.setText(data.getName());
        tv_address.setText(data.getAddress());

        // 获取已经配对的设备
        Set<BluetoothDevice> pairedDevices = MyApplication.getInstance().getmSppUtils().getBluetoothAdapter().getBondedDevices();
        if (pairedDevices.size() > 0){
            if (getAdapterPosition() == 0){
                tv_header.setVisibility(View.VISIBLE);
                tv_header.setText("已匹配");
            } else if(getAdapterPosition() == pairedDevices.size()){
                tv_header.setVisibility(View.VISIBLE);
                tv_header.setText("未匹配");
            } else {
                tv_header.setVisibility(View.GONE);
            }
        } else {
            if (getAdapterPosition() == 0){
                tv_header.setVisibility(View.VISIBLE);
                tv_header.setText("未匹配");
            } else {
                tv_header.setVisibility(View.GONE);
            }
        }

        item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setEventId(MessageEvent.EVENT_CONNECT_BLE);
                messageEvent.setObj(data.getAddress());
                EventBus.getDefault().post(messageEvent);
            }
        });

    }
}
