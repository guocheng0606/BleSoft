package com.android.hcbd.bledebug.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.android.hcbd.bledebug.R;
import com.android.hcbd.bledebug.app.MyApplication;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;

public class SplashActivity extends AppCompatActivity {

    public static final int REQ_CODE_PERMISSION = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 移除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        requestPermission();
    }

    private void requestPermission() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.LOCATION)
                .rationale(mRationale)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        //已获得定位权限
                        SwitchToMain();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        if (AndPermission.hasAlwaysDeniedPermission(SplashActivity.this, permissions)) {
                            // 这里使用一个Dialog展示没有这些权限应用程序无法继续运行，询问用户是否去设置中授权。
                            new AlertDialog.Builder(SplashActivity.this)
                                    .setTitle("权限申请")
                                    .setMessage("是否在设置中开启定位权限，以正常使用相关功能")
                                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // 如果用户继续：
                                            AndPermission.with(SplashActivity.this)
                                                    .runtime()
                                                    .setting()
                                                    .start(REQ_CODE_PERMISSION);
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // 如果用户中断：
                                            dialog.dismiss();
                                        }
                                    }).show();
                        }
                    }
                }).start();
    }

    private void SwitchToMain() {
        MyApplication.getInstance().initBle();
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    private Rationale mRationale = new Rationale() {
        @Override
        public void showRationale(Context context, Object data, final RequestExecutor executor) {
            // 这里使用一个Dialog询问用户是否继续授权。
            new AlertDialog.Builder(SplashActivity.this)
                    .setTitle("权限申请")
                    .setMessage("APP需要获取定位权限，以正常使用相关功能")
                    .setPositiveButton("好，给你", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 如果用户继续：
                            executor.execute();
                        }
                    })
                    .setNegativeButton("我拒绝", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 如果用户中断：
                            executor.cancel();
                        }
                    }).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_PERMISSION){
            if (AndPermission.hasPermissions(SplashActivity.this,Permission.Group.LOCATION)){
                SwitchToMain();
            } else {
                new AlertDialog.Builder(SplashActivity.this)
                        .setTitle("权限申请")
                        .setMessage("是否在设置中开启定位权限，以正常使用相关功能")
                        .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 如果用户继续：
                                AndPermission.with(SplashActivity.this)
                                        .runtime()
                                        .setting()
                                        .start(REQ_CODE_PERMISSION);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 如果用户中断：
                                dialog.dismiss();
                            }
                        }).show();
            }
        }
    }
}
