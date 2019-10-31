package com.android.hcbd.bledebug.ui.activity;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.hcbd.bledebug.R;
import com.android.hcbd.bledebug.adapter.BleMsgAdapter;
import com.android.hcbd.bledebug.app.MyApplication;
import com.android.hcbd.bledebug.base.BaseActivity;
import com.android.hcbd.bledebug.db.CustomDbTool;
import com.android.hcbd.bledebug.entity.Custom;
import com.android.hcbd.bledebug.entity.Instructions;
import com.android.hcbd.bledebug.entity.Msg;
import com.android.hcbd.bledebug.event.MessageEvent;
import com.android.hcbd.bledebug.utils.LogUtils;
import com.android.hcbd.bledebug.utils.ProgressDialogUtils;
import com.android.hcbd.bledebug.utils.ToastUtils;
import com.android.hcbd.bledebug.weight.LinearDividerDecoration;
import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import cn.com.heaton.blelibrary.ble.BleStates;
import cn.com.heaton.blelibrary.ble.L;
import cn.com.heaton.blelibrary.ble.callback.BleConnectCallback;
import cn.com.heaton.blelibrary.ble.callback.BleMtuCallback;
import cn.com.heaton.blelibrary.ble.callback.BleNotiftCallback;
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;

public class BleMainActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecycler;
    @BindView(R.id.input_text)
    EditText inputText;
    @BindView(R.id.send)
    Button send;
    @BindView(R.id.iv_custom)
    ImageView ivCustom;

    private BleMsgAdapter mAdapter;
    private BleDevice mBleDevice;
    private int selected = 0;

    @Override
    protected int getLayout() {
        return R.layout.activity_ble_main;
    }

    @Override
    protected void initEventAndData() {
        mBleDevice = (BleDevice) getIntent().getSerializableExtra("ble_device");
        toolbar.setTitle(TextUtils.isEmpty(mBleDevice.getBleName()) ? "N/A" : mBleDevice.getBleName());
        toolbar.setSubtitle(mBleDevice.getBleAddress());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBleDevice.isConnected())
                    showExitDialog();
                else
                    finishActivity();
            }
        });

        if (!mBleDevice.isConnected()) {
            ProgressDialogUtils.showMessageLoading(mContext,"正在连接蓝牙...");
            MyApplication.getInstance().getBle().connect(mBleDevice, connectCallback);
        }
        initAdapter();

        initListener();
    }

    private void initListener() {
        send.setOnClickListener(this);
        ivCustom.setOnClickListener(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event){
        switch (event.getEventId()) {
            case MessageEvent.EVENT_SEND:
                if(!mBleDevice.isConnected()){
                    ToastUtils.showShortToast(MyApplication.getInstance(),"蓝牙未连接");
                    return;
                }
                final List<String> list = (List<String>) event.getObj();
                threadSend(list);
                break;
            case MessageEvent.EVENT_MSG_ADD:
                Msg msg = (Msg) event.getObj();
                writeData(msg.getContent());
                break;
            case MessageEvent.EVENT_MSG_DEL:
                int position = (int) event.getObj();
                mAdapter.remove(position);
                break;
        }
    }

    private void initAdapter() {
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.addItemDecoration(new LinearDividerDecoration(this));
        mAdapter = new BleMsgAdapter(R.layout.msg_item);
        mRecycler.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
    }

    /**
     * 连接的回调
     */
    private BleConnectCallback<BleDevice> connectCallback = new BleConnectCallback<BleDevice>() {
        @Override
        public void onConnectionChanged(BleDevice device) {
            Log.e(TAG, "onConnectionChanged: " + device.getConnectionState());
            if (device.getConnectionState() == BleStates.BleStatus.CONNECTED){
                /*连接成功后，设置通知*/
                MyApplication.getInstance().getBle().startNotify(device, bleNotiftCallback);
                ProgressDialogUtils.dismissLoading();
                ToastUtils.showShortToast(mContext,"蓝牙连接成功！");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestMtu();
                    }
                },3000);
            }else if (device.getConnectionState() == BleStates.BleStatus.DISCONNECT){
                ProgressDialogUtils.dismissLoading();
            }
            invalidateOptionsMenu();
        }

        @Override
        public void onConnectException(BleDevice device, int errorCode) {
            super.onConnectException(device, errorCode);
            ToastUtils.showShortToast(mContext,"连接异常，异常状态码:" + errorCode);
            ProgressDialogUtils.dismissLoading();

        }

        @Override
        public void onConnectTimeOut(BleDevice device) {
            super.onConnectTimeOut(device);
            Log.e(TAG, "onConnectTimeOut: " + device.getBleAddress());
            ToastUtils.showShortToast(mContext,"连接超时:" + (TextUtils.isEmpty(device.getBleName()) ? "" : device.getBleName()));
            ProgressDialogUtils.dismissLoading();
        }
    };

    /**
     * 设置通知的回调
     */
    private BleNotiftCallback<BleDevice> bleNotiftCallback = new BleNotiftCallback<BleDevice>() {
        @Override
        public void onChanged(BleDevice device, BluetoothGattCharacteristic characteristic) {
            UUID uuid = characteristic.getUuid();
            L.e(TAG, "onChanged==uuid:" + uuid.toString());
            L.e(TAG, "onChanged==address:" + device.getBleAddress());
            L.e(TAG, "onChanged==data:" + Arrays.toString(characteristic.getValue()));
            String message = characteristic.getStringValue(0);
            L.e(TAG, "onChanged==data2:" + message);
            final Msg msg = new Msg(message,Msg.TYPE_RECEIVED);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.addData(msg);
                    mRecycler.scrollToPosition(mAdapter.getData().size()-1);
                }
            });

        }

        @Override
        public void onReady(BleDevice device) {
            Log.e(TAG, "onReady: ");
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt) {
            Log.e(TAG, "onServicesDiscovered is success ");
        }

        @Override
        public void onNotifySuccess(BluetoothGatt gatt) {
            Log.e(TAG, "onNotifySuccess is success ");
        }
    };


    /**
     * 设置请求MTU
     */
    private void requestMtu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //此处第二个参数  不是特定的   比如你也可以设置500   但是如果设备不支持500个字节则会返回最大支持数
            MyApplication.getInstance().getBle().setMTU(MyApplication.getInstance().getBle().getConnetedDevices().get(0).getBleAddress(), 517, new BleMtuCallback<BleDevice>() {
                @Override
                public void onMtuChanged(BleDevice device, int mtu, int status) {
                    LogUtils.LogShow("最大支持MTU：" + mtu);
                    super.onMtuChanged(device, mtu, status);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_ble, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mBleDevice.isConnected()) {
            menu.findItem(R.id.action_connect).setTitle("断开");
        } else {
            menu.findItem(R.id.action_connect).setTitle("连接");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_connect:
                if (mBleDevice.isConnected()) {
                    ProgressDialogUtils.showMessageLoading(mContext,"正在断开蓝牙...");
                    MyApplication.getInstance().getBle().disconnect(mBleDevice);
                } else {
                    ProgressDialogUtils.showMessageLoading(mContext,"正在连接蓝牙...");
                    MyApplication.getInstance().getBle().reconnect(mBleDevice);
                }
                break;
            case R.id.action_clear:
                mAdapter.setNewData(new ArrayList<Msg>());
                break;
            case R.id.action_batch:
                startActivity(new Intent(this,InstructActivity.class));
                break;
            case R.id.action_custom:
                startActivity(new Intent(this,CustomActivity.class));
                break;
            case R.id.action_current:
                writeData("ST+?");
                break;
            case R.id.action_gsm:
                writeData("ST+GSM");
                break;
            case R.id.action_bds:
                writeData("ST+BDS");
                break;
            case R.id.action_ble:
                writeData("ST+BLUETOOTH");
                break;
            case R.id.action_exit:
                writeData("ST+00");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                if (TextUtils.isEmpty(inputText.getText().toString())){
                    ToastUtils.showShortToast(MyApplication.getInstance(),"发送数据不能为空");
                    return;
                }
                writeData(inputText.getText().toString());
                break;
            case R.id.iv_custom:
                final List<Custom> list = CustomDbTool.queryAll();
                String[] strArray = new String[list.size()];
                for (int i= 0;i<list.size();i++){
                    strArray[i] = list.get(i).getName();
                }
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);//实例化builder
                builder.setTitle("请选择一项发送");//设置标题
                //设置单选列表
                builder.setSingleChoiceItems(strArray, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected = which;
                    }
                });
                //创建对话框
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                //设置确定按钮
                dialog.setButton(DialogInterface.BUTTON_POSITIVE,"确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!mBleDevice.isConnected()){
                            ToastUtils.showShortToast(MyApplication.getInstance(),"蓝牙未连接");
                            return;
                        }
                        List<Instructions> list1 = list.get(selected).getOrders();
                        List<String> sends = new ArrayList<>();
                        for (Instructions item : list1){
                            sends.add(item.getContent());
                        }
                        threadSend(sends);
                        dialog.dismiss();
                    }
                });
                dialog.show();//显示对话框
                break;
        }
    }

    /**
     * 发送数据
     */
    private void writeData(final String data) {
        if(!mBleDevice.isConnected()){
            ToastUtils.showShortToast(MyApplication.getInstance(),"蓝牙未连接");
            return;
        }
        boolean result = MyApplication.getInstance().getBle().write(mBleDevice, data.getBytes(), new BleWriteCallback<BleDevice>() {
            @Override
            public void onWriteSuccess(BluetoothGattCharacteristic characteristic) {
               // ToastUtils.showShortToast(mContext,"发送数据成功");
                Msg msg = new Msg(data,Msg.TYPE_SENT);
                mAdapter.addData(msg);
                inputText.setText("");
                mRecycler.scrollToPosition(mAdapter.getData().size()-1);
            }
        });
        if (!result) {
            ToastUtils.showShortToast(mContext,"发送数据失败!");
        }

    }

    private void threadSend(final List<String> list){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (final String item : list){
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                writeData(item);
                            }
                        });
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        if (mBleDevice.isConnected())
            showExitDialog();
        else
            finishActivity();
    }

    private void showExitDialog(){
        new AlertDialog.Builder(mContext)
                .setTitle("提示")
                .setMessage("当前处于连接状态，确定断开并退出吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyApplication.getInstance().getBle().disconnect(mBleDevice);
                        finishActivity();
                    }
                })
                .setNegativeButton("取消",null)
                .show();
    }

}
