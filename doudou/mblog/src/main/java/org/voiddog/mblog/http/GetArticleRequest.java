package org.voiddog.mblog.http;

import org.voiddog.lib.http.DHttpRequestBase;
import org.voiddog.mblog.Const;

/**
 * 获取文章详情
 * Created by Dog on 2015/5/30.
 */
public class GetArticleRequest extends DHttpRequestBase{
    public int mid;
    public String email;

    @Override
    public String getHost() {
        return Const.HOST + "Moving/get_moving";
    }
}
