package org.voiddog.lib.http;

import com.android.volley.Request;

/**
 * Created by Dog on 2015/4/4.
 */
abstract public class DHttpRequestBase {
    /**
     * GET 0, POST 1 default POST
     * @return GET OR POST
     */
    public int getMethod(){
        return Request.Method.POST;
    }

    abstract public String getHost();
}
