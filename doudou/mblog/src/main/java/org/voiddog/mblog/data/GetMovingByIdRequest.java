package org.voiddog.mblog.data;

import org.voiddog.lib.http.DHttpRequestBase;
import org.voiddog.mblog.Const;

/**
 * 获得某人的动态列表
 * Created by Dog on 2015/5/30.
 */
public class GetMovingByIdRequest extends DHttpRequestBase{
    public String id;
    public String email;
    public int page = 0;
    public int num = 10;

    @Override
    public String getHost() {
        return Const.HOST + "Moving/get_movings_by_id";
    }
}
