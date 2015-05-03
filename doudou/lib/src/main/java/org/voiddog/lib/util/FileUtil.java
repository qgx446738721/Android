package org.voiddog.lib.util;

import android.os.Environment;
import android.os.StatFs;

import org.voiddog.lib.BaseApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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

    public static String getSDCardPath(String uniqueName){
        return Environment.getExternalStorageDirectory() + "/" + BaseApplication.getInstance().getPackageName() + "/"
                 + uniqueName;
    }

    /**
     * 把对象保存到本地文件里面
     * @param object 要保存的对象
     * @param path 保存位置（不是完整的绝对路径，子路径）
     */
    public static void saveObject(Object object, String path, String name){
        path = getSDCardPath(path);
        try{
            File file = new File(path);
            if(!file.exists() && !file.mkdirs()){
                return;
            }
            file = new File(path, name);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            objectOutputStream.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 从相应的文件路径中读取文件
     * @param path  要保存的文件
     * @return 读取的对象
     */
    public static Object readObject(String path){
        Object res = null;
        path = getFilePath(path);
        try{
            File file = new File(path);
            if(file.exists()){
                FileInputStream fileInputStream = new FileInputStream(file);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                res = objectInputStream.readObject();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }
}
