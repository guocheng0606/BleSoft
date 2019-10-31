package com.android.hcbd.blesoft.ui.activity;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import com.android.hcbd.blesoft.MyApplication;
import com.android.hcbd.blesoft.R;
import com.android.hcbd.blesoft.base.BaseActivity;
import com.android.hcbd.blesoft.event.MessageEvent;
import com.android.hcbd.blesoft.holder.BleSearchViewHolder;
import com.android.hcbd.spputils.SppUtils;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;

public class BleActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;

    private SppUtils mSppUtils;
    private List<BluetoothDevice> mBlueList = new ArrayList<>();
    private RecyclerArrayAdapter<BluetoothDevice> adapter;

    @Override
    protected int getLayout() {
        return R.layout.activity_ble;
    }

    @Override
    protected void initEventAndData() {
        setToolBar(toolbar,"选择一个设备进行连接");
        mSppUtils = MyApplication.getInstance().getmSppUtils();
        initView();

        // 获取已经配对的设备
        Set<BluetoothDevice> pairedDevices = mSppUtils.getBluetoothAdapter().getBondedDevices();

        // 判断是否有配对过的设备
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mBlueList.add(device);
            }
            adapter.addAll(mBlueList);
        }


        //开启搜索
        mSppUtils.startDiscovery();
        //搜索的回调
        mSppUtils.setOnDeviceCallBack(new SppUtils.OnDeviceCallBack() {
            @Override
            public void onDeviceCallBack(BluetoothDevice bluetoothDevice) {
                if (adapter.getAllData().contains(bluetoothDevice))
                    return;
                // 判断是否配对过
                if (bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if (adapter.getAllData().size() > 0){
                        adapter.add(bluetoothDevice);
                    } else {
                        mBlueList.add(bluetoothDevice);
                        adapter.addAll(mBlueList);
                    }
                }

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event){
        switch (event.getEventId()) {
            case MessageEvent.EVENT_CONNECT_BLE:
                finishActivity();
                break;
        }
    }


    private void initView() {
        recyclerView.setEmptyView(R.layout.view_empty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerDecoration itemDecoration = new DividerDecoration(0xFFEDEDED, 1, 0, 0);
        itemDecoration.setDrawLastItem(true);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapterWithProgress(adapter = new RecyclerArrayAdapter<BluetoothDevice>(this) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new BleSearchViewHolder(parent);
            }
        });
        adapter.setError(R.layout.view_error, new RecyclerArrayAdapter.OnErrorListener() {
            @Override
            public void onErrorShow() {
                adapter.resumeMore();
            }

            @Override
            public void onErrorClick() {
                adapter.resumeMore();
            }
        });
        // StickyHeader
        /*StickyHeaderDecoration decoration = new StickyHeaderDecoration(new StickyHeaderAdapter(this));
        decoration.setIncludeHeader(false);
        recyclerView.addItemDecoration(decoration);*/
    }


}
