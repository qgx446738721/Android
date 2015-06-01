package org.voiddog.mblog.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 更新当前用户信息的广播
 * Created by Dog on 2015/6/1.
 */
public abstract class UpdateUserInfoReceiver extends BroadcastReceiver {
    public static final String UPDATE_USER_INFO = "org.voiddog.mblog.update_user_info";
    public static final int NEED_UPDATE = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(UPDATE_USER_INFO)){
            int command = intent.getIntExtra("COMMAND", -1);
            if(command == NEED_UPDATE){
                onUpdate();
            }
        }
    }

    protected abstract void onUpdate();
}
