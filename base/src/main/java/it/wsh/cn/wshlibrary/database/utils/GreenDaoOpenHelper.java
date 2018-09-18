package it.wsh.cn.wshlibrary.database.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;



import org.greenrobot.greendao.database.Database;

import it.wsh.cn.wshlibrary.database.greendao.DaoMaster;
import it.wsh.cn.wshlibrary.database.greendao.DownloadInfoDao;
import it.wsh.cn.wshlibrary.http.download.DownloadInfo;

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
        //依据每个版本的更新需求，只需要将需要升级的实体数据库进行如下操作：便可保留原有数据，然后根据新的bean类创建新表

       /* if(newVersion >= 3) {
            MigrationHelper.migrate(db, DownloadInfoDao.class);
        }*/

        GreenDaoDatabase.getInstance().createAllTables(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       GreenDaoDatabase.getInstance().dropAllTables(db);
       GreenDaoDatabase.getInstance().createAllTables(db);
    }
}
