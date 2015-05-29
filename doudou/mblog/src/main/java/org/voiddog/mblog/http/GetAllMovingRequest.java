package org.voiddog.mblog.http;

import org.voiddog.lib.http.DHttpRequestBase;
import org.voiddog.mblog.Const;

/**
 * 后去全部动态列表
 * Created by Dog on 2015/5/29.
 */
public class GetAllMovingRequest extends DHttpRequestBase{
    public String email;
    public int page = 0;
    public int num = 10;

    @Override
    public String getHost() {
        return Const.HOST + "Moving/get_movings";
    }
}
