package com.android.hcbd.blesoft.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by la on 2018/12/25.
 */

public class ProgressDialogUtils {
    private static ProgressDialog dialog;

    /**
     * 显示等待框
     * @param context
     */
    public static void showLoading(Context context){
        if(dialog != null && dialog.isShowing()){
            dismissLoading();
        }
        dialog = new ProgressDialog(context);
        dialog.setMessage("请稍后...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();
    }

    /**
     * 显示等待框
     * @param context
     */
    public static void showLoading(Context context,String title,String msg){
        if(dialog != null && dialog.isShowing()){
            dismissLoading();
        }
        dialog = new ProgressDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();
    }

    public static void dismissLoading(){
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
            dialog = null;
        }
    }

}
