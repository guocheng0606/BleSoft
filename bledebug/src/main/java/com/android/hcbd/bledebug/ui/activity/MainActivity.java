package com.android.hcbd.bledebug.ui.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.hcbd.bledebug.R;
import com.android.hcbd.bledebug.adapter.BleDeviceAdapter;
import com.android.hcbd.bledebug.app.MyApplication;
import com.android.hcbd.bledebug.base.BaseActivity;
import com.android.hcbd.bledebug.utils.ToastUtils;
import com.android.hcbd.bledebug.weight.LinearDividerDecoration;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jaeger.library.StatusBarUtil;

import butterknife.BindView;
import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.L;
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener ,NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.rv_ble)
    RecyclerView mRecycler;
    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout swipeLayout;

    private BleDeviceAdapter mAdapter;

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initEventAndData() {
        StatusBarUtil.setColorNoTranslucentForDrawerLayout(this, mDrawerLayout, 0xFF008577);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        initAdapter();
        mAdapter.setEmptyView(R.layout.view_ble_empty,(ViewGroup) mRecycler.getParent());
        checkBluetoothStatus();
        swipeLayout.setOnRefreshListener(this);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    //检查蓝牙是否支持及打开
    private void checkBluetoothStatus() {
        // 检查设备是否支持BLE4.0
        if (!MyApplication.getInstance().getBle().isSupportBle(mContext)) {
            ToastUtils.showLongToast(mContext,getString(R.string.ble_not_supported));
            //finish();
        }
        if (!MyApplication.getInstance().getBle().isBleEnable()) {
            //4、若未打开，则请求打开蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Ble.REQUEST_ENABLE_BT);
        } else {
            //5、若已打开，则进行扫描
            startScan();
        }
    }

    private void initAdapter() {
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.addItemDecoration(new LinearDividerDecoration(this));
        mAdapter = new BleDeviceAdapter(R.layout.item_ble);
        mRecycler.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
    }

    private void startScan() {
        setProgressBarIndeterminateVisibility(true);
        if (!swipeLayout.isRefreshing())
            swipeLayout.setRefreshing(true);
        //5、若已打开，则进行扫描
        if (MyApplication.getInstance().getBle() != null && !(MyApplication.getInstance().getBle().isScanning())) {
            mAdapter.clearData();
            mAdapter.setEmptyView(R.layout.view_ble_empty,(ViewGroup) mRecycler.getParent());
            MyApplication.getInstance().getBle().startScan(scanCallback);
        }
    }

    /**
     * 重新扫描
     */
    private void reScan() {
        if (MyApplication.getInstance().getBle() != null && !(MyApplication.getInstance().getBle().isScanning())) {
            mAdapter.clearData();
            MyApplication.getInstance().getBle().startScan(scanCallback);
        }
    }

    /**
     * 扫描的回调
     */
    private BleScanCallback<BleDevice> scanCallback = new BleScanCallback<BleDevice>() {

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onLeScan(final BleDevice device, final int rssi, byte[] scanRecord) {
            Log.e(TAG, "onLeScan: " + device.toString());
            Log.e(TAG, "rssi: " + rssi);

            synchronized (MyApplication.getInstance().getBle().getLocker()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addDevice(device,rssi);
                    }
                });

            }
        }

        @Override
        public void onStop() {
            super.onStop();
            L.e(TAG, "onStop: ");
            swipeLayout.setRefreshing(false);
        }

        /*@Override
        public void onParsedData(BleDevice device, ScanRecord scanRecord) {
            super.onParsedData(device, scanRecord);
            byte[] data = scanRecord.getManufacturerSpecificData(65520);//参数为厂商id
            if (data != null) {
                Log.e(TAG, "onParsedData: " + ByteUtils.BinaryToHexString(data));
            }
        }*/
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Ble.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            if (swipeLayout.isRefreshing())
                swipeLayout.setRefreshing(false);
        } else if (requestCode == Ble.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            //6、若打开，则进行扫描
            startScan();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRefresh() {
        checkBluetoothStatus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_settings:

                break;
            case R.id.nav_adout:
                startActivity(new Intent(mContext,AboutActivity.class));
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MyApplication.getInstance().getBle() != null) {
            MyApplication.getInstance().getBle().destory(getApplicationContext());
        }
    }

    private long exitTime = 0;
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                MyApplication.getInstance().exitApp();
            }
        }
    }
}
