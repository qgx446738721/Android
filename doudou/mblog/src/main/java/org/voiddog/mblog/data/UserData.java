package org.voiddog.mblog.data;

import org.voiddog.mblog.Const;

/**
 * 用户数据
 * Created by Dog on 2015/5/26.
 */
public class UserData {
    public String email;    //用户邮箱
    public String nickname; //用户姓名
    public String head;     //用户头像
    public int age = 0;     //用户年龄
    public int sex = Const.MALE;     //性别
    public int moving_num = 0;  //动态个数
    public int update_time = 0; //上传时间
}
