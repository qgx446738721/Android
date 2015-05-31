package org.voiddog.mblog.data;

/**
 * 文章数据
 * Created by Dog on 2015/5/26.
 */
public class ArticleData {
    public int mid;
    public String email;
    public String title;
    public String sub_title;
    public String content;
    public String pic;
    public String head;
    public String nickname;
    public int praise_num = 0;
    public int comment_num = 0;
    public int create_time = 0;
    public boolean is_praise = false;
}