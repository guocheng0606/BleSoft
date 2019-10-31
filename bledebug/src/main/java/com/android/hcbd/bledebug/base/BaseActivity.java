package com.android.hcbd.bledebug.base;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.hcbd.bledebug.app.MyApplication;
import com.android.hcbd.bledebug.event.MessageEvent;
import com.blankj.utilcode.util.KeyboardUtils;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;

/**
 * Created by la on 2018/12/24.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected String TAG = getClass().getSimpleName();
    protected Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        mContext = this;
        EventBus.getDefault().register(this);
        StatusBarUtil.setColorNoTranslucent(this,0xFF008577);
        MyApplication.getInstance().addActivity(this);
        ButterKnife.bind(this);
        //StatusBarUtil.setColorNoTranslucent(this,0xFF3EADEE);
        initEventAndData();
    }


    protected void setToolBar(Toolbar toolbar, String title){
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishActivity();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event) {

    }


    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config=new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config,res.getDisplayMetrics() );
        return res;
    }

    public void finishActivity() {
        if (KeyboardUtils.isSoftInputVisible(this))
            KeyboardUtils.toggleSoftInput();
        finish();
    }

    protected void hideSoftInput(){
        if (KeyboardUtils.isSoftInputVisible(this))
            KeyboardUtils.toggleSoftInput();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().removeActivity(this);
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    protected abstract int getLayout();
    protected abstract void initEventAndData();


}
