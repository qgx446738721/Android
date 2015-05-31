package org.voiddog.mblog.http;

import org.voiddog.lib.http.DHttpRequestBase;
import org.voiddog.mblog.Const;

/**
 * 获取关注的人的动态列表
 * Created by Dog on 2015/5/31.
 */
public class GetAttentionMovingRequest extends DHttpRequestBase{
    public String email;
    public int page = 1;
    public int num = 10;

    @Override
    public String getHost() {
        return Const.HOST + "Moving/get_attention_movings";
    }
}
