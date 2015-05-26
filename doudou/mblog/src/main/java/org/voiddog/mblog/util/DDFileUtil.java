package org.voiddog.mblog.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import org.voiddog.lib.util.FileUtil;
import org.voiddog.lib.util.ImageUtil;
import org.voiddog.mblog.MyApplication;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 问价管理类
 * Created by Dog on 2015/5/27.
 */
public class DDFileUtil extends FileUtil {
    /**
     * 获取dist缓存路径
     * @param uniqueName 缓存文件名
     * @return 缓存目录
     */
    public static String getDiskCachePath(String uniqueName){
        String cachePath = MyApplication.getInstance().getCacheDir().getPath();
        return cachePath + File.separator + uniqueName;
    }

    /**
     * 获取本地文件夹路径
     * @param dir 文件夹名
     * @return 文件夹的完整路径
     */
    public static String getLocalStorePath(String dir) {
        String path;

        if (isExternalStorageExist() && getSDFreeSize() >= 10) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/DouDou/" + dir + "/";
        } else {
            path = MyApplication.getInstance().getFilesDir()
                    .getAbsolutePath()
                    + "/DouDou/" + dir + "/";
        }

        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        return path;
    }

    public static File getLocalStoreFile(String dir){
        String path;

        if (isExternalStorageExist() && getSDFreeSize() >= 10) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/DouDou/" + dir + "/";
        } else {
            path = MyApplication.getInstance().getFilesDir()
                    .getAbsolutePath()
                    + "/DouDou/" + dir + "/";
        }

        File file = new File(path);
        if (!file.exists()) {
            if(!file.mkdirs()){
                return null;
            }
            else{
                return file;
            }
        }
        return file;
    }

    public static File saveImg(Bitmap b, String outPath, int maxWidth, int maxHeight, int q){
        if(maxWidth < b.getWidth() || maxHeight < b.getHeight()){
            float simpleSize = 1;
            float scaleWidth = ((float)b.getWidth())/maxWidth;
            float scaleheight = ((float)b.getHeight())/maxHeight;
            simpleSize = scaleWidth > scaleheight ? scaleWidth : scaleheight;
            int newWidth = (int)(b.getWidth()/simpleSize);
            int newHeight = (int)(b.getHeight()/simpleSize);
            return saveImg(
                    Bitmap.createScaledBitmap(b, newWidth, newHeight, true),
                    outPath, q
            );
        }
        else{
            return saveImg(b, outPath, q);
        }
    }

    /**
     * 图片存储
     * @param b bitmap
     * @param outPath   存储的路径
     * @param q 质量
     * @return 存储的文件
     */
    public static File saveImg(Bitmap b, String outPath, int q) {
        File mediaFile = new File(outPath);
        Log.i("TAG", "width: " + b.getWidth() + ", height: " + b.getHeight());
        try {
            if (mediaFile.exists()) {
                mediaFile.delete();
            }
            if (!mediaFile.getParentFile().exists()) {
                mediaFile.getParentFile().mkdirs();
            }
            mediaFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(mediaFile);
            b.compress(Bitmap.CompressFormat.JPEG, q, fos);
            fos.flush();
            fos.close();
            b.recycle();
            b = null;
            System.gc();
            return mediaFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
