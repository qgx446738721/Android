package org.voiddog.mblog.data;

import org.voiddog.mblog.Const;

/**
 * 评论数据
 * Created by Dog on 2015/5/31.
 */
public class CommentData {
    public int moid;
    public int mid;
    public String email = "";
    public String content;
    public int type = Const.COMMENT;
    public long create_time;
    public String nickname;
    public String head;
}
