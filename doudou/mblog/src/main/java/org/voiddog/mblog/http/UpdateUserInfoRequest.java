package org.voiddog.mblog.http;

import org.voiddog.lib.http.DHttpRequestBase;
import org.voiddog.mblog.Const;
import org.voiddog.mblog.db.model.UserModel;

/**
 * 更新用户信息
 * Created by Dog on 2015/6/1.
 */
public class UpdateUserInfoRequest extends DHttpRequestBase{
    public String email;
    public UserModel info;

    @Override
    public String getHost() {
        return Const.HOST + "User/set_user_info";
    }
}
