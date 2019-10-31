package com.android.hcbd.blesoft.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by guocheng
 */

public class ToastUtils {
    private static Toast toast;

    public static void showLongToast(Context context,String str){
        if(toast != null){
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(context,str,Toast.LENGTH_LONG);
        toast.show();
    }
    public static void showShortToast(Context context,String str){
        if(toast != null){
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(context,str,Toast.LENGTH_SHORT);
        toast.show();
    }

}
