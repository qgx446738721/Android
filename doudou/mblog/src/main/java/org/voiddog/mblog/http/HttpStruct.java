package org.voiddog.mblog.http;

/**
 * 用户返回数据
 * Created by Dog on 2015/4/4.
 */
public class HttpStruct {
    public class Article{
        public int mid;
        public String email;
        public String title;
        public String sub_title;
        public String content;
        public String pic;
        public String create_time;
    }

    public class User{
        public String email;    //用户邮箱
    }
}
