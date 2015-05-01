package org.voiddog.lib.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import org.voiddog.lib.BaseApplication;

/**
 * Created by Dog on 2015/4/3.
 */
public class SizeUtil {

    private static DisplayMetrics displayMetrics = null;

    public static int getScreenWidth(){
        if(BaseApplication.getInstance() == null){
            return 0;
        }
        if(displayMetrics == null) {
            WindowManager wm = (WindowManager) BaseApplication.getInstance()
                    .getSystemService(Context.WINDOW_SERVICE);
            displayMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight(){
        if(BaseApplication.getInstance() == null){
            return 0;
        }
        if(displayMetrics == null) {
            WindowManager wm = (WindowManager) BaseApplication.getInstance()
                    .getSystemService(Context.WINDOW_SERVICE);
            displayMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics.heightPixels;
    }

    public static DisplayMetrics getLocalDisplayMetrics(){
        WindowManager wm = (WindowManager) BaseApplication.getInstance()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics;
    }
}
