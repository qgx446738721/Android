package org.voiddog.mblog.http;

import org.voiddog.mblog.Const;

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
        public String nickname; //用户姓名
        public String head;     //游湖头像
        public int sex = Const.MALE;     //性别
        public int moving_num = 0;  //动态个数
        public int update_time = 0; //上传时间
    }
}
