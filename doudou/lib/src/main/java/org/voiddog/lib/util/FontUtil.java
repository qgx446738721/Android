package org.voiddog.lib.util;

import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

/**
 * 字体缓存工具类
 * Created by Dog on 2015/5/26.
 */
public class FontUtil {
    //字体缓存
    public static Map<String, Typeface> fontCache;

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
