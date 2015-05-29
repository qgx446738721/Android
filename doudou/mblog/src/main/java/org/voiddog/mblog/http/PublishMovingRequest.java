package org.voiddog.mblog.http;

import org.voiddog.lib.http.DHttpRequestBase;
import org.voiddog.mblog.Const;

/**
 * 发布动态
 * Created by Dog on 2015/5/30.
 */
public class PublishMovingRequest extends DHttpRequestBase{
    public String email;
    public String title;
    public String sub_title;
    public String content;
    public String pic;

    @Override
    public String getHost() {
        return Const.HOST + "Moving/add_moving";
    }
}
