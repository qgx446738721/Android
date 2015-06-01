package org.voiddog.mblog.http;

import org.voiddog.lib.http.DHttpRequestBase;
import org.voiddog.mblog.Const;

/**
 * 获取评论列表
 * Created by Dog on 2015/6/1.
 */
public class GetMovingReplyRequest extends DHttpRequestBase{
    public int mid;
    int page = 0;
    int num = 100;

    @Override
    public String getHost() {
        return Const.HOST + "Moving/get_moving_replys";
    }
}
