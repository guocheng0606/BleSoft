package com.android.hcbd.blesoft;

import android.app.Activity;
import android.app.Application;
import android.os.Environment;

import com.android.hcbd.blesoft.crash.CrashHandler;
import com.android.hcbd.blesoft.manager.GreenDaoManager;
import com.android.hcbd.spputils.SppUtils;
import com.blankj.utilcode.util.Utils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.model.BleDevice;

/**
 * Created by gc on 2019/01/13.
 */

public class MyApplication extends Application {

    private static MyApplication instance;
    private Set<Activity> allActivities;
    private SppUtils mSppUtils;
    private Ble<BleDevice> mBle;

    public static synchronized MyApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.instance = this;
        Utils.init(this);

        mSppUtils = new SppUtils(this);
        GreenDaoManager.getInstance();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }

    public SppUtils getmSppUtils() {
        return mSppUtils;
    }

    public void initBle(){
        mBle = Ble.options()//开启配置
                .setLogBleExceptions(true)//设置是否输出打印蓝牙日志（非正式打包请设置为true，以便于调试）
                .setThrowBleException(true)//设置是否抛出蓝牙异常
                .setAutoConnect(true)//设置是否自动连接
                .setFilterScan(true)//设置是否过滤扫描到的设备
                .setConnectFailedRetryCount(3)
                .setConnectTimeout(10 * 1000)//设置连接超时时长（默认10*1000 ms）
                .setScanPeriod(12 * 1000)//设置扫描时长（默认10*1000 ms）
                .setUuidService(UUID.fromString("0000fee9-0000-1000-8000-00805f9b34fb"))//主服务的uuid
                .setUuidWriteCha(UUID.fromString("d44bc439-abfd-45a2-b575-925416129600"))//可写特征的uuid
                .create(getApplicationContext());
    }

    public Ble<BleDevice> getmBle() {
        return mBle;
    }

    public void addActivity(Activity activity){
        if (allActivities == null) {
            allActivities = new HashSet<>();
        }
        allActivities.add(activity);
    }

    public void removeActivity(Activity activity){
        if (allActivities != null) {
            allActivities.remove(activity);
        }
    }

    public void exitApp(){
        if (allActivities != null){
            synchronized (allActivities){
                for(Activity act : allActivities){
                    act.finish();
                }
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }


    public String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if(sdCardExist){
            sdDir = Environment.getExternalStorageDirectory();
        }
        return sdDir.toString();
    }

}
