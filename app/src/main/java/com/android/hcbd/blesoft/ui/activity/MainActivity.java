package com.android.hcbd.blesoft.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.hcbd.blesoft.MyApplication;
import com.android.hcbd.blesoft.R;
import com.android.hcbd.blesoft.base.BaseActivity;
import com.android.hcbd.blesoft.db.CustomDbTool;
import com.android.hcbd.blesoft.entity.Custom;
import com.android.hcbd.blesoft.entity.Instructions;
import com.android.hcbd.blesoft.entity.Msg;
import com.android.hcbd.blesoft.event.MessageEvent;
import com.android.hcbd.blesoft.holder.MsgViewHolder;
import com.android.hcbd.blesoft.utils.ProgressDialogUtils;
import com.android.hcbd.blesoft.utils.ToastUtils;
import com.android.hcbd.spputils.SppState;
import com.android.hcbd.spputils.SppUtils;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;
    @BindView(R.id.input_text)
    EditText inputText;
    @BindView(R.id.send)
    Button send;

    private SppUtils mSppUtils;
    private boolean isConnect = false;
    private RecyclerArrayAdapter<Msg> adapter;

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initEventAndData() {
        toolbar.setTitle("未连接");
        setSupportActionBar(toolbar);
        mSppUtils = MyApplication.getInstance().getmSppUtils();
        mSppUtils.startService();
        if(!mSppUtils.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), "手机或者平板蓝牙不可用", Toast.LENGTH_SHORT).show();
        }

        initView();
        initListener();
    }

    private void initListener() {
        mSppUtils.setBluetoothConnectionListener(new SppUtils.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext(), "连接成功 " + name, Toast.LENGTH_SHORT).show();
                ProgressDialogUtils.dismissLoading();
                toolbar.setTitle("已连接");
                invalidateOptionsMenu();
                isConnect = true;
            }

            public void onDeviceDisconnected() {
                Toast.makeText(getApplicationContext(), "断开连接", Toast.LENGTH_SHORT).show();
                ProgressDialogUtils.dismissLoading();
                toolbar.setTitle("未连接");
                invalidateOptionsMenu();
                isConnect = false;
            }

            public void onDeviceConnectionFailed() {
                Toast.makeText(getApplicationContext(), "连接失败", Toast.LENGTH_SHORT).show();
                ProgressDialogUtils.dismissLoading();
            }
        });
        //数据的回调
        mSppUtils.setOnDataReceivedListener(new SppUtils.OnDataReceivedListener() {
            @Override
            public void onDataReceived(final byte[] data, final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Msg msg = new Msg(message,Msg.TYPE_RECEIVED);
                        adapter.add(msg);
                        recyclerView.scrollToPosition(adapter.getCount()-1);
                    }
                });
            }
        });
        send.setOnClickListener(this);
        send.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final int[] selected = {0};
                final List<Custom> list = CustomDbTool.queryAll();
                String[] strArray = new String[list.size()];
                for (int i= 0;i<list.size();i++){
                    strArray[i] = list.get(i).getName();
                }
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);//实例化builder
                builder.setIcon(R.mipmap.ic_launcher);//设置图标
                builder.setTitle("请选择一项发送");//设置标题
                //设置单选列表
                builder.setSingleChoiceItems(strArray, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected[0] = which;
                    }
                });
                //创建对话框
                AlertDialog dialog = builder.create();
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
                        if(!isConnect){
                            ToastUtils.showShortToast(MyApplication.getInstance(),"蓝牙未连接");
                            return;
                        }
                        List<Instructions> list1 = list.get(selected[0]).getOrders();
                        List<String> sends = new ArrayList<>();
                        for (Instructions item : list1){
                            sends.add(item.getContent());
                        }
                        threadSend(sends);
                        dialog.dismiss();
                    }
                });
                dialog.show();//显示对话框
                return true;
            }
        });
    }

    private void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapterWithProgress(adapter = new RecyclerArrayAdapter<Msg>(this) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new MsgViewHolder(parent);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event){
        switch (event.getEventId()) {
            case MessageEvent.EVENT_CONNECT_BLE:
                String address = (String) event.getObj();
                ProgressDialogUtils.showLoading(MainActivity.this,getString(R.string.app_name),"正在连接...");
                mSppUtils.connect(address);
                break;
            case MessageEvent.EVENT_SEND:
                if(!isConnect){
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
                adapter.remove(position);
                break;
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
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!mSppUtils.isBluetoothEnabled()) {
            //开启蓝牙
            mSppUtils.enable();
        } else {
            //开启服务
            if(!mSppUtils.isServiceAvailable()) {
                mSppUtils.setupService();
                mSppUtils.startService();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //断开蓝牙连接
        if(mSppUtils.getServiceState() == SppState.STATE_CONNECTED) {
            mSppUtils.disconnect();
        }
        //停止服务
        mSppUtils.stopService();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isConnect) {
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
                if (isConnect) {
                    mSppUtils.disconnect();
                } else {
                    startActivity(new Intent(MainActivity.this,BleActivity.class));
                }
                break;
            case R.id.action_clear:
                adapter.clear();
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
        switch (v.getId()){
            case R.id.send:
                if (TextUtils.isEmpty(inputText.getText().toString())){
                    ToastUtils.showShortToast(MyApplication.getInstance(),"发送数据不能为空");
                    return;
                }
                writeData(inputText.getText().toString());
                inputText.setText("");
                break;
        }
    }

    /**
     * 发送数据
     */
    private void writeData(String data) {
        if(!isConnect){
            ToastUtils.showShortToast(MyApplication.getInstance(),"蓝牙未连接");
            return;
        }
        mSppUtils.send(data.getBytes(), false);
        Msg msg = new Msg(data,Msg.TYPE_SENT);
        adapter.add(msg);
        recyclerView.scrollToPosition(adapter.getCount()-1);
    }
}
