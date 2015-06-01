package org.voiddog.mblog.http;

import org.voiddog.lib.http.DHttpRequestBase;
import org.voiddog.mblog.Const;

/**
 * 评论或点赞
 * Created by Dog on 2015/6/1.
 */
public class CommentRequest extends DHttpRequestBase{
    public int mid;
    public String email;
    public int type = Const.COMMENT;
    public String content = "";

    @Override
    public String getHost() {
        return Const.HOST + "Moving/add_moving_option";
    }
}
