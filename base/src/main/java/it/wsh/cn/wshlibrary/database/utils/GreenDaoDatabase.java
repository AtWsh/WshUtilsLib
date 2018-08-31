package it.wsh.cn.wshlibrary.database.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;



import org.greenrobot.greendao.database.Database;

import it.wsh.cn.wshlibrary.database.greendao.DaoMaster;
import it.wsh.cn.wshlibrary.database.greendao.DaoSession;

/**
 * Created by wenshenghui on 2018/8/31.
 *
 * 数据库 greendao.db 初始化操作类
 */

public class GreenDaoDatabase {

    private static volatile GreenDaoDatabase sInstance;

    private DaoSession mDaoSession;

    private static final String GREEN_DAO_DB_NAME = "greendao.db";
    private DaoMaster mDaoMaster;
    private GreenDaoOpenHelper mGreenDaoOpenHelper;
    private Context mContext;

    private GreenDaoDatabase() {
    }

    public static GreenDaoDatabase getInstance() {
        if(sInstance == null) {
            synchronized (GreenDaoDatabase.class) {
                if (sInstance == null) {
                    sInstance = new GreenDaoDatabase();
                }
            }
        }
        return sInstance;
    }

    /**
     * 配置数据库
     */
    public synchronized void initDatabase(Context context) {
        if (context == null) {
            return;
        }

        mContext = context.getApplicationContext();

        //创建数据库GREEN_DAO_DB_NAME"     GreenDaoOpenHelper：创建SQLite数据库的SQLiteOpenHelper的具体实现
        if(mGreenDaoOpenHelper == null){
           mGreenDaoOpenHelper = new GreenDaoOpenHelper(context, GREEN_DAO_DB_NAME);
        }
        //获取可写数据库
        SQLiteDatabase db = mGreenDaoOpenHelper.getWritableDatabase();
        //获取数据库对象   DaoMaster为 GreenDao的顶级对象，作为数据库对象、用于创建表和删除表
        if(mDaoMaster == null){
            mDaoMaster = new DaoMaster(db);
        }
        //获取Dao对象管理者   DaoSession：管理所有的Dao对象，Dao对象中存在着增删改查等API
        mDaoSession = mDaoMaster.newSession();
    }

    public synchronized DaoSession getDaoSession() {
        if (mDaoSession == null) {
            initDatabase(mContext);
        }
        return mDaoSession;
    }

    public synchronized void dropAllTables(SQLiteDatabase db) {
        /**
         * 必须要这么处理, 降级处理的时候   initDatabase()还没完全执行完 mDaoMaster还= null
         */
        if(mDaoMaster == null){
            mDaoMaster = new DaoMaster(db);
        }

        if(mDaoMaster != null){
            mDaoMaster.dropAllTables(mDaoMaster.getDatabase(), true);
        }
    }

    public synchronized void createAllTables(SQLiteDatabase db) {
        if(mDaoMaster == null){
            mDaoMaster = new DaoMaster(db);
        }
        if(mDaoMaster != null){
            mDaoMaster.createAllTables(mDaoMaster.getDatabase(), true);
        }
    }

    public synchronized void createAllTables(Database db) {
        if(mDaoMaster == null){
            mDaoMaster = new DaoMaster(db);
        }
        if(mDaoMaster != null){
            mDaoMaster.createAllTables(mDaoMaster.getDatabase(), true);
        }
    }

}
