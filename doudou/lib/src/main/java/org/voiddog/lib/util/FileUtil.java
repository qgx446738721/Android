package org.voiddog.lib.util;

import android.os.Environment;
import android.os.StatFs;

import org.voiddog.lib.BaseApplication;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Dog on 2015/4/4.
 */
public class FileUtil {

    /**
     * 判断SD卡是否存在
     *
     * @return boolean
     */
    public static boolean ExistSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

    /**
     * 判断SD卡剩余容量，单位MB
     *
     * @return long
     */
    public static long getSDFreeSize() {
        // 取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSizeLong();
        // 空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocksLong();
        // 返回SD卡空闲大小
        return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    public static String getDiskCachePath(String uniqueName) {

        final String cachePath = BaseApplication.getInstance().getCacheDir().getPath();
        return cachePath + File.separator + uniqueName;
    }

    public static String getFilePath(String uniqueName){
        final String cachePath = BaseApplication.getInstance().getFilesDir().getPath();
        return cachePath + File.separator + uniqueName;
    }
}
