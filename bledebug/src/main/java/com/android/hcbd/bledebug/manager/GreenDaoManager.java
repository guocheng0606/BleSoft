package com.android.hcbd.bledebug.manager;

import com.android.hcbd.bledebug.app.MyApplication;
import com.android.hcbd.bledebug.db.MySQLiteOpenHelper;
import com.android.hcbd.bledebug.entity.DaoMaster;
import com.android.hcbd.bledebug.entity.DaoSession;

/**
 * Created by guocheng on 2019/1/8.
 */

public class GreenDaoManager {
    private static GreenDaoManager mInstance;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    public GreenDaoManager(){
        /*DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(MyApplication.getInstance(),"ble_order.db",null);
        mDaoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();*/

        //MigrationHelper.DEBUG = true; //如果你想查看日志信息，请将DEBUG设置为true
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(MyApplication.getInstance(), "ble_order.db",null);
        mDaoMaster = new DaoMaster(helper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
    }

    public static GreenDaoManager getInstance() {
        if (mInstance == null) {
            mInstance = new GreenDaoManager();
        }
        return mInstance;
    }

    public DaoMaster getMaster() {
        return mDaoMaster;
    }

    public DaoSession getSession() {
        return mDaoSession;
    }

    public DaoSession getNewSession() {
        mDaoSession = mDaoMaster.newSession();
        return mDaoSession;
    }

}
