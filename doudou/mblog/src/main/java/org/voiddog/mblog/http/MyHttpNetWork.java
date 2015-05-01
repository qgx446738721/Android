package org.voiddog.mblog.http;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import org.voiddog.lib.http.DHttpRequestBase;
import org.voiddog.lib.http.DJsonObjectResponse;
import org.voiddog.lib.http.HttpNetWork;
import org.voiddog.lib.util.ToastUtil;
import org.voiddog.mblog.MyApplication;

/**
 * Created by Dog on 2015/4/14.
 */
public class MyHttpNetWork extends HttpNetWork {

    public static MyHttpNetWork getInstance(){
        if(instance == null){
            instance = new MyHttpNetWork();
        }
        return (MyHttpNetWork)instance;
    }

    @Override
    public void request(final DHttpRequestBase requestBase, final AsyncHttpResponseHandler responseHandler) {
        if(requestBase.getMethod() == DHttpRequestBase.POST) {
            MyHttpRequest.GetToken getToken = new MyHttpRequest.GetToken();
            super.request(getToken, new DJsonObjectResponse() {
                @Override
                public void onSuccess(int statusCode, DResponse response) {
                    requestBase.getParams().put("_token", response.data);
                    asyncHttpClient.post(requestBase.getAction(), requestBase.getParams(), responseHandler);
                }

                @Override
                public void onFailure(int statusCode, Throwable throwable) {
                    ToastUtil.showToast("网络不可用 错误码: " + statusCode);
                    responseHandler.onFailure(statusCode, null, null, throwable);
                }
            });
        }
        else{
            super.request(requestBase, responseHandler);
        }
    }
}
