package com.issac.mystepcounter.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.issac.mystepcounter.AppContext;
import com.issac.mystepcounter.pojo.StepDay;
import com.issac.mystepcounter.pojo.StepHour;
import com.issac.mystepcounter.pojo.StepValues;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
                /*StepHour stepHour = new StepHour();
                stepHour.setHour(i);
                stepHour.setStep(500+"");
                liteOrm.insert(stepHour);*/

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
        Log.i(AppContext.Tag,QueryBuilder.create(t.getClass()).whereEquals(StepHour.STEP,0)
                .whereAppendAnd()
                .whereEquals(StepHour.TIME,1)
                .createStatement().sql+"");
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
    public static <T extends StepValues> void update(T t) {
        //liteOrm.update(t, ConflictAlgorithm.Replace);
        WhereBuilder builder = WhereBuilder.create(t.getClass());
        builder.where("time = ?",t.getTime());
        Map<String, Object > kv = new HashMap<>();
        kv.put("step",t.getStep());
        ColumnsValue value = new ColumnsValue(kv);
        liteOrm.update(builder,value,null);
    }


    public static <T> void updateALL(List<T> list) {
        liteOrm.update(list);
    }

    public static void closeDb(){
        liteOrm.close();
    }

    public static <T> long count(Class<T> claxx){
        return liteOrm.queryCount(claxx);
    }

    public static <T extends StepValues> int sumStep(Class<T> claxx, Long head, Long tail){
        int result=0;
        List<T> list = query(claxx,head,tail);

        for (T t:list){
            result += t.getStep();
        }

        return result;

    }

    public static <T extends StepValues> void insertOrUpdate(T t){
        long count = liteOrm.queryCount(QueryBuilder.create(t.getClass())
                .whereEquals(T.TIME,t.getTime())
                .whereAppendAnd()
                .whereEquals(T.STEP,t.getStep())
                .appendOrderAscBy(T.TIME)
        );
        if (count >=0){
            Map<String, Object> kv = new HashMap<>();
            kv.put(T.STEP,t.getStep());
            liteOrm.update(WhereBuilder.create(t.getClass()).equals(T.TIME,t.getTime()),
                    new ColumnsValue(kv),null);
        }else{
            liteOrm.insert(t);
        }
    }


    public static <T extends StepValues> List<T> query(Class<T> claxx,Long head,Long tail){
        WhereBuilder whereBuilder = WhereBuilder.create(claxx);
        whereBuilder.where("time between ? and ?",new Long[]{head,tail});
        QueryBuilder queryBuilder = QueryBuilder.create(claxx).where(whereBuilder).appendOrderAscBy(StepValues.TIME);
        Log.i("Main",queryBuilder.createStatement().sql);
        return liteOrm.query(queryBuilder);

    }

}
