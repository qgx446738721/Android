package org.voiddog.mblog.util;

import android.net.Uri;

import org.voiddog.lib.util.StringUtil;
import org.voiddog.mblog.MyApplication;

/**
 * 字符串工具类
 * Created by Dog on 2015/5/27.
 */
public class DDStringUtil extends StringUtil {
    /**
     * 获取资源的Uri地址
     * @param resource 资源id
     * @return uri地址
     */
    public static Uri getUriFromResource(int resource){
        return Uri.parse("android.resource://"+ MyApplication.getInstance().getPackageName()+"/"+resource);
    }
}
