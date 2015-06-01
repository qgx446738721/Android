package org.voiddog.lib.util;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * Created by Dog on 2015/5/2.
 */
public class StringUtil {

    //字体缓存
    public static Map<String, Typeface> fontCache;

    /**
     * 检测邮箱地址是否合法
     * @param email
     * @return true合法 false不合法
     */
    public static boolean isEmail(String email){
        if (null==email || "".equals(email)) return false;
        Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 根据文件路径获取Uri
     * @param filePath 文件路径
     * @return 文件路径的uri
     */
    public static Uri getUriFromFilePath(String filePath){
        return Uri.parse(String.format("file://%s", filePath));
    }

    public static boolean isEmpty(String s){
        return s == null || s.length() == 0;
    }

    public static int isAge(String age){
        try{
            int res = Integer.parseInt(age);
            if(res > 0 && res < 120){
                return res;
            }
        }
        catch (Exception ignore){
        }
        return -1;
    }

    public static Typeface getFontFace(AssetManager assetManager, String name){
        try{
            if(fontCache == null){
                fontCache = new HashMap<>();
            }
            if(fontCache.containsKey(name)){
                return fontCache.get(name);
            }
            else {
                Typeface typeface = Typeface.createFromAsset(assetManager, name);
                fontCache.put(name, typeface);
                return typeface;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
