package com.issac.mystepcounter.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.issac.mystepcounter.pojo.StepHour;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import java.util.List;

/**
 * Created by xf on 2016/1/31.
 */
public class DbUtils {

    public static String DB_NAME;
    public static LiteOrm liteOrm;

    public static void createDb(Context _activity, String DB_NAME) {
        DB_NAME = DB_NAME + ".db";
        if (liteOrm == null) {
            liteOrm = LiteOrm.newCascadeInstance(_activity, DB_NAME);
            liteOrm.setDebugged(true);
            for (int i=0;i<25;i++){
                StepHour stepHour = new StepHour();
                stepHour.setHour(i+"");
                stepHour.setStep(500+"");
                liteOrm.insert(stepHour);

            }
        }
    }

    public static void createDb(Context _activity,boolean SD_CARD,String DB_NAME){
        if (SD_CARD && liteOrm ==null){
            DB_NAME = Environment.getExternalStorageDirectory().getAbsolutePath()+"/database/"+DB_NAME+".db";
            Log.i("main","path = "+DB_NAME);
            liteOrm = LiteOrm.newCascadeInstance(_activity,DB_NAME);
            liteOrm.setDebugged(true);
        }
    }

    public static LiteOrm getLiteOrm() {
        return liteOrm;
    }

    /**
     * 插入一条记录
     *
     * @param t
     */
    public static <T> void insert(T t) {
        liteOrm.save(t);
    }

    /**
     * 插入所有记录
     *
     * @param list
     */
    public static <T> void insertAll(List<T> list) {
        liteOrm.save(list);
    }

    /**
     * 查询所有
     *
     * @param cla
     * @return
     */
    public static <T> List<T> getQueryAll(Class<T> cla) {
        return liteOrm.query(cla);
    }

    /**
     * 查询  某字段 等于 Value的值
     *
     * @param cla
     * @param field
     * @param value
     * @return
     */
    public static <T> List<T> getQueryByWhere(Class<T> cla, String field, String[] value) {
        return liteOrm.<T>query(new QueryBuilder(cla).where(field + "=?", value));
    }

    /**
     * 查询  某字段 等于 Value的值  可以指定从1-20，就是分页
     *
     * @param cla
     * @param field
     * @param value
     * @param start
     * @param length
     * @return
     */
    public static <T> List<T> getQueryByWhereLength(Class<T> cla, String field, String[] value, int start, int length) {
        return liteOrm.<T>query(new QueryBuilder(cla).where(field + "=?", value).limit(start, length));
    }

    /**
     * 删除所有 某字段等于 Vlaue的值
     * @param cla
     * @param field
     * @param value
     */
//        public static <T> void deleteWhere(Class<T> cla,String field,String [] value){
//            liteOrm.delete(cla, WhereBuilder.create().where(field + "=?", value));
//        }

    /**
     * 删除所有
     *
     * @param cla
     */
    public static <T> void deleteAll(Class<T> cla) {
        liteOrm.deleteAll(cla);
    }

    /**
     * 仅在以存在时更新
     *
     * @param t
     */
    public static <T> void update(T t) {
        liteOrm.update(t, ConflictAlgorithm.Replace);
    }


    public static <T> void updateALL(List<T> list) {
        liteOrm.update(list);
    }

    public static void closeDb(){
        liteOrm.close();
    }

}
