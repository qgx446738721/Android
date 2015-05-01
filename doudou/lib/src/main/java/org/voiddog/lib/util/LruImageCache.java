package org.voiddog.lib.util;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dog on 2015/4/4.
 */
public class LruImageCache implements ImageLoader.ImageCache{
    private LruCache<String, Bitmap> lruCache;
    private static LruImageCache instance;
    private List<String> keys = new ArrayList<String>();

    public static LruImageCache getInstance() {
        if (instance == null) {
            instance = new LruImageCache();
        }
        return instance;
    }

    private LruImageCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 4;

        lruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };
    }

    @Override
    public Bitmap getBitmap(String url) {
        return lruCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        keys.add(url);
        lruCache.put(url, bitmap);
    }

    public void removeBitmap(String url) {
        lruCache.remove(url);
    }
}
