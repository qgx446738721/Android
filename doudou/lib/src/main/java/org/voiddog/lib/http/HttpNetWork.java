package org.voiddog.lib.http;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import org.voiddog.lib.BaseApplication;

/**
 * Created by Dog on 2015/4/4.
 */
public class HttpNetWork {
    protected static HttpNetWork instance;
    protected AsyncHttpClient asyncHttpClient;
    protected PersistentCookieStore persistentCookieStore;

    protected HttpNetWork(){
        asyncHttpClient = new AsyncHttpClient();
        persistentCookieStore = new PersistentCookieStore(BaseApplication.getInstance());
        asyncHttpClient.setCookieStore(persistentCookieStore);
    }

    public static HttpNetWork getInstance(){
        if(instance == null){
            instance = new HttpNetWork();
        }
        return instance;
    }

    public void request(DHttpRequestBase requestBase, AsyncHttpResponseHandler responseHandler){
        if(requestBase.getMethod() == DHttpRequestBase.GET){
            asyncHttpClient.get(requestBase.getAction(), requestBase.getParams(), responseHandler);
        }
        else{
            asyncHttpClient.post(requestBase.getAction(), requestBase.getParams(), responseHandler);
        }
    }
}
