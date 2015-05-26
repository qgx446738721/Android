package org.voiddog.mblog.db.model;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.voiddog.mblog.Const;
import org.voiddog.mblog.db.helper.DBOpenHelper;

import java.sql.SQLException;

/**
 * 用户信息数据库
 * Created by Dog on 2015/5/26.
 */
@DatabaseTable(tableName = "Users")
public class UserModel {
    @DatabaseField(id = true)
    public String email;    //用户邮箱
    @DatabaseField
    public String nickname; //用户姓名
    @DatabaseField
    public String head;     //用户头像
    @DatabaseField
    public int sex = Const.MALE;     //性别
    @DatabaseField
    public int moving_num = 0;  //动态个数
    @DatabaseField
    public int update_time = 0; //上传时间

    /**
     * 对象操作类
     */
    private static Dao<UserModel, Integer> sDao = null;

    public static Dao<UserModel, Integer> Dao() {
        if (sDao == null && DBOpenHelper.getInstance() != null) {
            try {
                // 请确保DBOpenHelper创建成功
                sDao = DBOpenHelper.getInstance().getDao(UserModel.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return sDao;
    }

    public static void clearSDao(){
        sDao = null;
    }
}
