package com.android.hcbd.blesoft.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Anroid6.0权限申请助手
 * Created by gc on 2018/12/25.
 */

public class PermissionHelper {
    private static final String TAG = "PermissionHelper";

    private final static int READ_PHONE_STATE_CODE = 101;
    private final static int WRITE_EXTERNAL_STORAGE_CODE = 102;
    private final static int REQUEST_OPEN_APPLICATION_SETTINGS_CODE = 12345;

    /**
     * 所需要向用户申请的权限列表
     */
    private PermissionModel[] mPermissionModels = new PermissionModel[]{
            new PermissionModel(
                    "存储空间",
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    "我们需要您允许我们读写你的存储卡，以方便我们临时保存一些数据",
                    WRITE_EXTERNAL_STORAGE_CODE
            ),
            new PermissionModel("电话",Manifest.permission.READ_PHONE_STATE,"我们需要读写手机信息来标识您的身份",READ_PHONE_STATE_CODE)
    };

    private Activity mActivity;
    private OnApplyPermissionListener mOnApplyPermissionListener;

    public PermissionHelper(Activity activity){
        mActivity = activity;
    }

    public void setmOnApplyPermissionListener(OnApplyPermissionListener onApplyPermissionListener){
        mOnApplyPermissionListener = onApplyPermissionListener;
    }

    /**
     *演示如何在Android6.0+上运行时申请权限
     */
    public void applyPermissions(){
        try {
            for (final PermissionModel model : mPermissionModels){
                if(PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mActivity,model.permission)){
                    ActivityCompat.requestPermissions(mActivity,new String[]{model.permission},model.requestCode);
                    return;
                }
            }
            if(mOnApplyPermissionListener != null){
                mOnApplyPermissionListener.onAfterApplyAllPermission();
            }
        } catch (Throwable e) {
            Log.e(TAG,"",e);
        }
    }

    /**
     * 对应Activity的{@code onRequestPermissionsResult(...)} 方法
     * @param requestCode
     * @param permission
     * @param grantResults
     */
    public void onRequestPermissionResult(int requestCode,String[] permission ,int[] grantResults){
        switch (requestCode){
            case READ_PHONE_STATE_CODE:
            case WRITE_EXTERNAL_STORAGE_CODE:
                //如果用户不允许，我们视情况发起二次请求或者引导用户到应用页面手动打开
                if(PackageManager.PERMISSION_GRANTED != grantResults[0]) {

                    //二次请求，表现为：以前请求过这个权限，但是用户拒绝了
                    //子啊二次请求的时候，会有一个“不再提示”的选择框
                    //因此这里需要给用户解释一下我们为什么需要这个权限，否则用户会永久不在激活这个申请
                    //方便用户离我们为什么需要这个权限
                    if(ActivityCompat.shouldShowRequestPermissionRationale(mActivity,permission[0])){
                        new AlertDialog.Builder(mActivity)
                                .setTitle("权限申请")
                                .setMessage(findPermissionExplain(permission[0]))
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        applyPermissions();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }
                    //到这里就表示已经是第3+次请求，而且此时用户已经永久拒绝了，这个时候，我们引导用户到应用权限页面，让用户自己手动打开
                    else {
                        new AlertDialog.Builder(mActivity)
                                .setTitle("权限申请")
                                .setMessage("请在打开的窗口的权限中开启"+
                                        findPermissionExplain(permission[0])+"权限，以正常使用本应用")
                                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        openApplicationSettings(REQUEST_OPEN_APPLICATION_SETTINGS_CODE);
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }
                    return;
                }
                //到这里就表示用户允许了本次操作，我们继续检査是否还有待申请的权限没有申请
                if(isAllRequestedPermissionGranted()){
                    if (mOnApplyPermissionListener != null){
                        mOnApplyPermissionListener.onAfterApplyAllPermission();
                    }
                } else {
                    applyPermissions();
                }
                break;
        }
    }

    /**
     * 对应activity的{@code onActivityResult(...)}方法
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode,int resultCode, Intent data){
        switch (requestCode){
            case REQUEST_OPEN_APPLICATION_SETTINGS_CODE:
                if (isAllRequestedPermissionGranted()) {
                    if (mOnApplyPermissionListener != null) {
                        mOnApplyPermissionListener.onAfterApplyAllPermission();
                    }
                } else {
                    mActivity.finish();
                }
                break;
        }
    }

    /**
     * 判断是否所有的权限都被授权了
     * @return
     */
    public boolean isAllRequestedPermissionGranted(){
        for (PermissionModel model : mPermissionModels) {
            if(PackageManager.PERMISSION_GRANTED!= ContextCompat.checkSelfPermission(mActivity,model.permission)){
                return false;
            }
        }
        return true;
    }

    /**
     * 打开应用设置界面
     * @param requestCode 请求码
     * @return
     */
    private boolean openApplicationSettings(int requestCode){
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    , Uri.parse("package:"+mActivity.getPackageName()));
            intent.addCategory(Intent.CATEGORY_DEFAULT);

            //android L之后的Activity的启动模式发生了一些变化
            //如果用下面的Intent.FLAG_ACTIVITY_NEW_TASK,并且startActivityForResult
            //那么会在打开新的activity的时候就会立即回调 onActivityResult
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mActivity.startActivityForResult(intent,requestCode);
            return true;
        } catch (Throwable e) {
            Log.e(TAG,"",e);
        }
        return false;
    }

    /**
     * 査找申请权限的解释短语
     * @param permission
     * @return
     */
    private String findPermissionExplain(String permission){
        if (mPermissionModels != null) {
            for (PermissionModel model : mPermissionModels) {
                if (model != null && model.permission != null && model.permission.equals(permission)){
                    return model.explain;
                }
            }
        }
        return null;
    }

    /**
     * 査找申请权限的名称
     * @param permission
     * @return
     */
    private String findPermissionName(String permission){
        if (mPermissionModels != null) {
            for (PermissionModel model : mPermissionModels) {
                if (model != null && model.permission != null && model.permission.equals(permission)){
                    return model.name;
                }
            }
        }
        return null;
    }

    private static class PermissionModel{
        //权限名称
        public String name;
        //请求的权限
        public String permission;
        //解析为什么请求这个权限
        public String explain;
        //请求代码
        public int requestCode;

        public PermissionModel(String name, String permission, String explain, int requestCode) {
            this.name = name;
            this.permission = permission;
            this.explain = explain;
            this.requestCode = requestCode;
        }
    }

    public interface OnApplyPermissionListener{
        /**
         * 申请所有权限之后的逻辑
         */
        void onAfterApplyAllPermission();
    }

}
