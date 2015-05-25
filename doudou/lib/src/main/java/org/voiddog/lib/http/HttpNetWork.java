package org.voiddog.lib.http;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.voiddog.lib.BaseApplication;

/**
 * Created by Dog on 2015/4/4.
 */
public class HttpNetWork {
    protected static HttpNetWork instance;
    RequestQueue mQueue;
    Gson gson;

    protected HttpNetWork(){
        mQueue = Volley.newRequestQueue(BaseApplication.getInstance());
        gson = new Gson();
    }

    public static HttpNetWork getInstance(){
        if(instance == null){
            instance = new HttpNetWork();
        }
        return instance;
    }

    public void request(final DHttpRequestBase requestBase, final NetResponseCallback netResponseCallback, final NetErrorCallback netErrorCallback){
        String params = gson.toJson(requestBase);
        GsonRequest gsonRequest = new GsonRequest(
                requestBase.getMethod(),
                requestBase.getHost(),
                params,
                new Response.Listener<HttpResponsePacket>() {
                    @Override
                    public void onResponse(HttpResponsePacket response) {
                        netResponseCallback.onResponse(requestBase, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        netErrorCallback.onError(requestBase, error.getMessage());
                    }
                }
        );
        mQueue.add(gsonRequest);
    }

    // 返回回调接口
    public interface NetResponseCallback {
        void onResponse(DHttpRequestBase request,
                               HttpResponsePacket response);
    }

    // 访问出错的回调接口
    public interface NetErrorCallback {
        void onError(DHttpRequestBase request, String errorMsg);
    }
}
