package it.wsh.cn.common_http.http.database.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;

import it.wsh.cn.common_http.http.database.greendao.DaoMaster;
import it.wsh.cn.common_http.http.database.greendao.DownloadInfoDao;


/**
 * Created by wenshenghui on 2018/8/31.
 * 数据库升级操作类
 */

public class GreenDaoOpenHelper extends DaoMaster.OpenHelper {

    public GreenDaoOpenHelper(Context context, String name) {
        super(context, name);
    }

    /**
     * 数据库升级
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        //操作数据库的更新
        //依据每个版本的更新需求，只需要将需要升级的实体数据库进行如下操作：便可保留原有数据，
        //传入的Dao类对应的表数据将被复制到新表，旧表将删除

        if(newVersion >= 3) {
            MigrationHelper.migrate(db, DownloadInfoDao.class);
        }
        //最后检测未被创建的新表，如果存在不做处理，如果不存在，将被统一创建出来
        GreenDaoDatabase.getInstance().createAllTables(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       GreenDaoDatabase.getInstance().dropAllTables(db);
       GreenDaoDatabase.getInstance().createAllTables(db);
    }
}
