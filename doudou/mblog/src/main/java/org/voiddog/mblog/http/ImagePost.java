package org.voiddog.mblog.http;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.voiddog.lib.http.DJsonObjectResponse;
import org.voiddog.mblog.Const;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 图片上传
 * Created by Dog on 2015/5/26.
 */
public class ImagePost {

    static AsyncHttpClient client;
    static ImagePost instance;

    ImagePost(){
        client = new AsyncHttpClient();
    }

    public static ImagePost getInstance(){
        if(instance == null){
            instance = new ImagePost();
        }
        return instance;
    }

    public void postImage(String filePath, DJsonObjectResponse response){
        File postFile = new File(filePath);
        RequestParams params = new RequestParams();
        try{
            params.put("postImg", postFile);
            client.post(Const.HOST + "App/upload", params, response);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
}
