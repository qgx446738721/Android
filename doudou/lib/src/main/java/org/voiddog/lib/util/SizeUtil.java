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
        return getLocalDisplayMetrics(BaseApplication.getInstance());
    }

    public static DisplayMetrics getLocalDisplayMetrics(Context context){
        if(displayMetrics != null){
            return displayMetrics;
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics;
    }

    /**
     * 输入dp 转换为 px
     * @param context 上下文
     * @param dp 要转换的dp的数值
     * @return dp 转换成 px 后的数值
     */
    public static int dp2px(Context context, float dp){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
