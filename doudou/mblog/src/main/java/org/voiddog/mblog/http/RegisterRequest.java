package org.voiddog.mblog.http;

import org.voiddog.lib.http.DHttpRequestBase;
import org.voiddog.mblog.Const;
import org.voiddog.mblog.data.UserData;

/** 注册请求
 * Created by Dog on 2015/5/26.
 */
public class RegisterRequest extends DHttpRequestBase{
    public String email;
    public String password;
    public UserData info;

    @Override
    public String getHost() {
        return Const.HOST + "User/register";
    }
}
