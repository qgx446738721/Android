package org.voiddog.mblog.http;

import org.voiddog.lib.http.DHttpRequestBase;
import org.voiddog.mblog.Const;

/**
 * 获取用户信息
 * Created by Dog on 2015/5/30.
 */
public class GetUserInfoRequest extends DHttpRequestBase{
    public String email;

    @Override
    public String getHost() {
        return Const.HOST + "User/get_user_info";
    }
}
