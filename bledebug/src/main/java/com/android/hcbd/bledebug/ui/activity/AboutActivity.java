package com.android.hcbd.bledebug.ui.activity;

import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.android.hcbd.bledebug.R;
import com.android.hcbd.bledebug.base.BaseActivity;
import com.blankj.utilcode.util.AppUtils;

import butterknife.BindView;

public class AboutActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_info)
    TextView tv_info;

    @Override
    protected int getLayout() {
        return R.layout.activity_about;
    }

    @Override
    protected void initEventAndData() {
        setToolBar(toolbar, "关于");

        tv_info.setText(getString(R.string.app_name) + "   V" + AppUtils.getAppVersionName());
    }


}
