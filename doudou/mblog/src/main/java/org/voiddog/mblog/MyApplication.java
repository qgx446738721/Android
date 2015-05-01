package org.voiddog.mblog;

import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;

import org.androidannotations.annotations.EApplication;
import org.voiddog.lib.BaseApplication;

/**
 * 主app
 * Created by Dog on 2015/4/6.
 */
@EApplication
public class MyApplication extends BaseApplication {
    public static String HOST = "http://voiddog.vicp.cc:8008/blog/public/mobile/";
    public static String IMG_HOST = "http://voiddog.vicp.cc:8008/blog/public/photo/";

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //初始化Fresco图片框架
        Fresco.initialize(getApplicationContext());
    }

    public static MyApplication getInstance(){
        return instance;
    }

    /**
     * 获取图片的链接
     */
    public static String getImageHost(String url){
        return IMG_HOST + url;
    }

    /**
     * 获取图片Uri
     */
    public static Uri getImageHostUri(String url){
        return Uri.parse(getImageHost(url));
    }
}
