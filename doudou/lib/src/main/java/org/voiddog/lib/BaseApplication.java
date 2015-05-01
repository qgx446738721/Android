package org.voiddog.lib;

import android.app.Application;

/**
 * Created by Dog on 2015/4/3.
 */
public class BaseApplication extends Application{
    private static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static BaseApplication getInstance(){
        return instance;
    }
}
