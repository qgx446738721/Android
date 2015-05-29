package org.voiddog.mblog.db.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.voiddog.mblog.MyApplication;
import org.voiddog.mblog.db.model.UserModel;

import java.sql.SQLException;

public class DBOpenHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = "cxzapp";

    private static DBOpenHelper instance;
    public static DBOpenHelper getInstance() {
        if(instance == null && MyApplication.getInstance() != null){
            SharedPreferences sp = MyApplication.getInstance().getApplicationContext()
                    .getSharedPreferences("Config", 0);
            String dbname = sp.getString("mobile", null);
            if(dbname != null){
                new DBOpenHelper(MyApplication.getInstance().getApplicationContext(), dbname);
            }
        }
        return instance;
    }

    public DBOpenHelper(Context context, String dbname) {
        super(context, dbname, null, 3);
        onCreate(getReadableDatabase(), connectionSource);
        instance = this;
    }

    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, UserModel.class);
        } catch (SQLException e) {
            Log.d(TAG, "unable to create database ", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVer, int newVer) {
        try {
            TableUtils.dropTable(connectionSource, UserModel.class, true);
            onCreate(sqliteDatabase, connectionSource);
        } catch (SQLException e) {
            Log.e(TAG, "Unable to upgrade database from version " + oldVer
                    + " to new " + newVer, e);
        }
    }

    @Override
    public void close() {
        super.close();
        UserModel.clearSDao();
    }
}
