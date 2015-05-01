package org.voiddog.lib.http;

import com.loopj.android.http.RequestParams;

/**
 * Created by Dog on 2015/4/4.
 */
public class DHttpRequestBase {
    public static final int GET = 0;
    public static final int POST = 1;
    protected RequestParams requestParams;
    protected String action;
    protected int method;
    public DHttpRequestBase(String action){
        this.action = action;
        method = POST;
        requestParams = new RequestParams();
    }

    public int getMethod(){
        return method;
    }

    public RequestParams getParams(){
        return requestParams;
    }

    public String getAction(){
        return action;
    }
}
