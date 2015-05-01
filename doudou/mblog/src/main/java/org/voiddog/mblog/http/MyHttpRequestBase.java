package org.voiddog.mblog.http;

import org.voiddog.lib.http.DHttpRequestBase;
import org.voiddog.mblog.MyApplication;

/**
 *
 * Created by Dog on 2015/4/5.
 */
public class MyHttpRequestBase extends DHttpRequestBase {

    public MyHttpRequestBase(String action) {
        super(MyApplication.HOST + action);
    }
}
