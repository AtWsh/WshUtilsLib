package it.wsh.cn.common_http.http.database.daohelper;

import android.text.TextUtils;

import org.greenrobot.greendao.async.AsyncSession;

import java.util.ArrayList;
import java.util.List;

import it.wsh.cn.common_http.http.database.bean.OssInfo;
import it.wsh.cn.common_http.http.database.greendao.OssInfoDao;
import it.wsh.cn.common_http.http.database.utils.GreenDaoDatabase;


/**
 * author: wenshenghui
 * created on: 2018/10/12 10:13
 * description:
 */
public class OssInfoDaoHelper {

    /**
     * 添加数据，如果有重复则覆盖
     *
     * @return  < 0 为添加失败
     */
    public static long insertInfo(int key, String url, String savePath, long totalSize, String strMd5) {
        long raw = -1;

        if(TextUtils.isEmpty(url)){
            return raw;
        }

        OssInfo info = new OssInfo(key, url, savePath, totalSize, strMd5);
        try{
             raw = GreenDaoDatabase.getInstance().getDaoSession().getOssInfoDao().insertOrReplace(info);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return raw;
    }

    /**
     * 异步操作
     * @param url
     * @param savePath
     * @param totalSize
     */
    public static void insertInfoSync(int key, String url, String savePath, long totalSize, String strMd5) {

        if(TextUtils.isEmpty(url)){
            return;
        }

        OssInfo info = new OssInfo(key, url, savePath, totalSize, strMd5);
        if(info == null || TextUtils.isEmpty(info.getUrl())){
            return;
        }

        try{
             AsyncSession asyncSession = GreenDaoDatabase.getInstance().getDaoSession().startAsyncSession();
             asyncSession.insertOrReplace(info);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 添加数据，如果有重复则覆盖
     *
     * @param info
     * @return  < 0 为添加失败
     */
    public static long insertInfo(OssInfo info) {
        long raw = -1;

        if(info == null || TextUtils.isEmpty(info.getUrl())){
            return raw;
        }

        try{
             raw = GreenDaoDatabase.getInstance().getDaoSession().getOssInfoDao().insertOrReplace(info);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return raw;
    }

    public static void insertInfoSync(OssInfo info) {

        if(info == null || TextUtils.isEmpty(info.getUrl())){
            return;
        }
        try{
             AsyncSession asyncSession = GreenDaoDatabase.getInstance().getDaoSession().startAsyncSession();
             asyncSession.insertOrReplace(info);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加数据，如果有重复则覆盖
     *
     * @param list
     */
    public static void insertTasks(List<OssInfo> list) {
        if(list == null || list.size() < 1){
            return;
        }

        try{
             GreenDaoDatabase.getInstance().getDaoSession().getOssInfoDao().insertOrReplaceInTx(list);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除数据
     *
     * @param key
     * @return 是否删除成功
     */
    public static boolean deleteInfo(int key) {
        boolean delete = false;

        try{
             OssInfo info = GreenDaoDatabase.getInstance().getDaoSession().getOssInfoDao()
                     .queryBuilder().where(OssInfoDao.Properties.Key.eq(key)).unique();
             if(info != null){
                 deleteInfo(info);
                delete = true;
             }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return delete;
    }

    /**
     * 删除数据
     * @param info
     */
    public static void deleteInfo(OssInfo info) {
        if(info == null){
            return;
        }
        try{
             GreenDaoDatabase.getInstance().getDaoSession().getOssInfoDao().delete(info);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询条件为Url = url的数据
     *
     * @return
     */
    public static OssInfo queryTask(int key) {
        OssInfo info = null;

        try{
            info = GreenDaoDatabase.getInstance().getDaoSession().getOssInfoDao().
                    queryBuilder().where(OssInfoDao.Properties.Key.eq(key)).unique();

        }catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 查询count个数据
     */
    public static List<OssInfo> queryAll(int count) {
        List<OssInfo> infos = new ArrayList<>();
        try{
             List<OssInfo> list = GreenDaoDatabase.getInstance().getDaoSession().getOssInfoDao()
                     .queryBuilder().orderDesc(OssInfoDao.Properties.Key).list();
             if(list.size() > count){
                 for (int i = 0; i < count; i++) {
                    infos.add(list.get(i));
                }

             }else {
                 infos.addAll(list);
             }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return infos;
    }

    /**
     * 查询count个数据
     */
    public static List<OssInfo> queryAll() {
        List<OssInfo> infos = new ArrayList<>();
        try{
            infos = GreenDaoDatabase.getInstance().getDaoSession().getOssInfoDao()
                    .queryBuilder().orderDesc(OssInfoDao.Properties.Key).list();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return infos;
    }

    /**
     * 清除数据
     */
    public static void clear(){
        try{
             GreenDaoDatabase.getInstance().getDaoSession().getOssInfoDao().deleteAll();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除表格
     */
    public static void dropTable(){
        try{
             OssInfoDao downloadInfoDao = GreenDaoDatabase.getInstance().getDaoSession().getOssInfoDao();
             downloadInfoDao.dropTable(downloadInfoDao.getDatabase(),true);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
