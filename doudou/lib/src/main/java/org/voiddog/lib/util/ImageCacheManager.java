package org.voiddog.lib.util;

import android.graphics.Bitmap;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import org.voiddog.lib.BaseApplication;

/**
 * Created by Dog on 2015/4/4.
 */
public class ImageCacheManager {

    public enum CacheType{
        DISK, MEMORY
    }

    private static ImageCacheManager mInstacne;

    private ImageLoader mImageLoader;

    private ImageLoader.ImageCache mImageCache;

    private RequestQueue mQueue;

    public static ImageCacheManager getInstacne(){
        if(mInstacne == null){
            mInstacne = new ImageCacheManager();
            mInstacne.init(FileUtil.getDiskCachePath("img-tmp/"), 100*1024*1024, Bitmap.CompressFormat.JPEG, 90, CacheType.DISK);
        }
        return mInstacne;
    }

    /**
     *
     * @param dir file path
     * @param cacheSize size of cache
     * @param compressFormat the compress format of file
     * @param quality qulity
     * @param type type
     */
    public void init(String dir, int cacheSize, Bitmap.CompressFormat compressFormat, int quality, CacheType type) {
        switch (type) {
            case DISK:
                mImageCache = new DiskLruImageCache(dir, cacheSize, compressFormat, quality);
                break;
            case MEMORY:
                mImageCache = new BitmapLruImageCache(cacheSize);
            default:
                mImageCache = new BitmapLruImageCache(cacheSize);
                break;
        }
    }

    public Bitmap getBitmap(String url) {
        try {
            return mImageCache.getBitmap(createKey(url));
        } catch (NullPointerException e) {
            throw new IllegalStateException("Disk Cache Not initialized");
        }
    }

    public void putBitmap(String url, Bitmap bitmap) {
        try {
            mImageCache.putBitmap(createKey(url), bitmap);
        } catch (NullPointerException e) {
            throw new IllegalStateException("Disk Cache Not initialized");
        }
    }

    /**
     * Executes and image load
     *
     * @param url
     *            location of image
     * @param listener
     *            Listener for completion
     */
    public void getImage(String url, ImageLoader.ImageListener listener) {
        mImageLoader.get(url, listener);
    }

    /**
     * @return instance of the image loader
     */
    public ImageLoader getImageLoader() {
        if(mQueue == null){
            mQueue = Volley.newRequestQueue(BaseApplication.getInstance(), null);
        }
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(mQueue, mImageCache);
        }
        return mImageLoader;
    }

    public static String getImageLoaderCacheKey(String url, int maxWidth, int maxHeight) {
        return "#W" + maxWidth + "#H" + maxHeight + url;
    }

    /**
     * @return the instance of request queue
     */
    public RequestQueue getRequestQueue(){
        if(mQueue == null){
            mQueue = Volley.newRequestQueue(BaseApplication.getInstance(), null);
        }
        return mQueue;
    }

    /**
     * @return the instance of image cache
     */
    public ImageLoader.ImageCache getmImageCache(){
        return mImageCache;
    }

    /**
     * Creates a unique cache key based on a url value
     *
     * @param url
     *            url to be used in key creation
     * @return cache key value
     */
    private String createKey(String url) {
        return String.valueOf(url.hashCode());
    }
}
