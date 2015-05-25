package org.voiddog.mblog.http;

import org.voiddog.lib.http.DHttpRequestBase;
import org.voiddog.mblog.Const;

/**
 * 登陆操作
 * Created by Dog on 2015/5/24.
 */
public class LoginRequest extends DHttpRequestBase{
    public String email;
    public String password;

    @Override
    public String getHost() {
        return Const.HOST + "user/login/";
    }
}
