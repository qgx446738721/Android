package org.voiddog.lib.util;

import android.widget.Toast;

import org.voiddog.lib.BaseApplication;

/**
 * Created by Dog on 2015/4/4.
 */
public class ToastUtil {
    public static void showToast(String message){
        Toast.makeText(BaseApplication.getInstance(), message, Toast.LENGTH_SHORT).show();
    }
}
