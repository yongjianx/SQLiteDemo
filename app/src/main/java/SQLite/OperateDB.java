package SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by skyworthclub on 2018/7/14.
 */

public class OperateDB {

    private final static String TAG = "OperateDB";
    private Context context;
    private Database mDatabase;

    public OperateDB(Context context){
        this.context = context;
        mDatabase = new Database(context);
    }

    /**
     * 判断表中是否有数据
     * @return
     */
    public boolean isDataExist(){
        int count = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try{
            /*当数据库不可写入的时候（如磁盘空间已满）getReadableDatabase()方法返回的对象将以只读的方式去打开数据库，
            而getWritableDatabase()方法则将出现异常
             */
            db = mDatabase.getReadableDatabase();
//            cursor = db.query(Database.TABLE_NAME, new String[]{"COUNT(Id)"},
//                    null, null, null, null, null);
            cursor = db.rawQuery("select * from "+Database.TABLE_NAME, null);
            count = cursor.getCount();
            if (count > 0)
                return true;

        }
        catch(Exception e){
            Log.e(TAG, "isDataExist() fail!");
            e.printStackTrace();
        }
        finally {
            if (cursor != null)
                cursor.close();
            if (db != null)
                db.close();
        }

        return false;
    }

    /**
     * 初始化表
     */
    public void initTable(){
        SQLiteDatabase db = null;

        try{
            db = mDatabase.getWritableDatabase();
            db.beginTransaction();

            db.execSQL("insert into "+Database.TABLE_NAME
                    + " (Id, CustomName, OrderPrice, Country) values (1, 'Arc', 100, 'China')");
            db.execSQL("insert into " + Database.TABLE_NAME
                    + " (Id, CustomName, OrderPrice, Country) values (2, 'Bor', 200, 'USA')");
            db.execSQL("insert into " + Database.TABLE_NAME
                    + " (Id, CustomName, OrderPrice, Country) values (3, 'Cut', 500, 'Japan')");
            db.execSQL("insert into " + Database.TABLE_NAME
                    + " (Id, CustomName, OrderPrice, Country) values (4, 'Bor', 300, 'USA')");
            db.execSQL("insert into " + Database.TABLE_NAME
                    + " (Id, CustomName, OrderPrice, Country) values (5, 'Arc', 600, 'China')");
            db.execSQL("insert into " + Database.TABLE_NAME
                    + " (Id, CustomName, OrderPrice, Country) values (6, 'Doom', 200, 'China')");

            db.setTransactionSuccessful();
        }
        catch(Exception e){
            Log.e(TAG,"initTable() fail!");
            e.printStackTrace();
        }
        finally {
            if (db != null){
                db.endTransaction();
                db.close();
            }
        }
    }

    /**
     * 获取所有的数据
     * @return
     */
    public List<Order> getAllData(){
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try{
            db = mDatabase.getReadableDatabase();
            cursor = db.rawQuery("select * from "+Database.TABLE_NAME, null);

            if (cursor.getCount() > 0){
                List<Order> orderList = new ArrayList<>(cursor.getCount());

                while (cursor.moveToNext())
                    orderList.add(parseOrder(cursor));
                return orderList;
            }
        }
        catch(Exception e){
            Log.e(TAG, "getAllData() fail!");
        }
        finally {
            if (cursor != null)
                cursor.close();
            if (db != null)
                db.close();
        }

        return null;
    }

    /**
     * 执行自定义SQL语句
     * @param sql
     */
    public void execSQL(String sql) {
        SQLiteDatabase db = null;

        try {
            if (sql.contains("select")){
                Toast.makeText(context, "不可以执行自定义select语句", Toast.LENGTH_SHORT).show();
            }else if (sql.contains("insert") || sql.contains("update") || sql.contains("delete")){
                db = mDatabase.getWritableDatabase();
                db.beginTransaction();

                db.execSQL(sql);
                db.setTransactionSuccessful();
                Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "不可以执行自定义SQL语句", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    /**
     * @param sql
     * @param selectionArgs
     * @return
     */
    public List<Order> rawQuery(String sql, String[] selectionArgs){

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try{
            db = mDatabase.getReadableDatabase();
            cursor = db.rawQuery(sql, selectionArgs);

            if (cursor.getCount() > 0){
                List<Order> orderList = new ArrayList<>(cursor.getCount());

                while (cursor.moveToNext())
                    orderList.add(parseOrder(cursor));

                return orderList;
            }
        }
        catch(Exception e){
            Log.e(TAG, "rawQuery() fail!");
            e.printStackTrace();
        }
        finally {
            if (cursor != null)
                cursor.close();
            if (db != null)
                db.close();
        }

        return null;
    }

    /**
     * 自定义查询表中数据
     * @param id
     * @param customName
     * @param orderPrice
     * @param country
     * @return
     */
    public List<Order> rawQuery(String id, String customName, String orderPrice, String country){

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try{
            db = mDatabase.getReadableDatabase();
            String sql;
            String[] selectionArgs;

            if (!id.equals("")) {
                sql = "select * from " + Database.TABLE_NAME + " where Id=?";
                selectionArgs = new String[]{id};
            }else if (!customName.equals("")){
                sql = "select * from " + Database.TABLE_NAME + " where CustomName=?";
                selectionArgs = new String[]{customName};
            }else if (!orderPrice.equals("")){
                sql = "select * from " + Database.TABLE_NAME + " where OrderPrice=?";
                selectionArgs = new String[]{orderPrice};
            }else if (!country.equals("")){
                sql = "select * from " + Database.TABLE_NAME + " where Country=?";
                selectionArgs = new String[]{country};
            }else {
                Toast.makeText(context, "查询时不能全为空", Toast.LENGTH_SHORT).show();
                return null;
            }
            cursor = db.rawQuery(sql, selectionArgs);

            if (cursor.getCount() > 0){
                List<Order> orderList = new ArrayList<>(cursor.getCount());

                while (cursor.moveToNext())
                    orderList.add(parseOrder(cursor));

                return orderList;
            }
        }
        catch(Exception e){
            Log.e(TAG, "rawQuery() fail!");
            e.printStackTrace();
        }
        finally {
            if (cursor != null)
                cursor.close();
            if (db != null)
                db.close();
        }

        return null;
    }

    /**
     * 插入一条数据
     * @param id
     * @param customName
     * @param orderPrice
     * @param country
     * @return
     */
    public boolean insertData(String id, String customName, String orderPrice, String country){
        //四项数据都要完整才能插入
        if (id.equals("") || customName.equals("") || orderPrice.equals("") || country.equals("")){
            Toast.makeText(context, "请填完整数据再插入", Toast.LENGTH_SHORT).show();
            return false;
        }
        SQLiteDatabase db = null;

        try{
            db = mDatabase.getWritableDatabase();
            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put("Id", id);
            values.put("CustomName", customName);
            values.put("OrderPrice", orderPrice);
            values.put("Country", country);
            db.insertOrThrow(Database.TABLE_NAME, null, values);

            db.setTransactionSuccessful();
            return true;
        }
        catch(SQLiteConstraintException e){
            Toast.makeText(context, "主键id重复", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        finally {
            if (db != null){
                db.endTransaction();
                db.close();
            }
        }

        return false;
    }

    /**
     * 根据id删除一条记录
     * @param id
     * @param customName
     * @param orderPrice
     * @param country
     * @return
     */
    public int deleteOrder(String id, String customName, String orderPrice, String country){

        SQLiteDatabase db = null;
        int res = 0;

        try{
            db = mDatabase.getWritableDatabase();
            db.beginTransaction();

            //依次根据id,CustomName,OrderPrice,Country删除数据
            if (!id.equals(""))
                res = db.delete(Database.TABLE_NAME, "Id=?", new String[]{id});
            else if (!customName.equals(""))
                res = db.delete(Database.TABLE_NAME, "CustomName=?", new String[]{customName});
            else if (!orderPrice.equals(""))
                res = db.delete(Database.TABLE_NAME, "OrderPrice=?", new String[]{orderPrice});
            else if (!country.equals(""))
                res = db.delete(Database.TABLE_NAME, "Country=?", new String[]{country});
            else{
                Toast.makeText(context, "删除时不能全为空", Toast.LENGTH_SHORT).show();
                return 0;
            }

            db.setTransactionSuccessful();
            if (res == 0)
                Toast.makeText(context, "删除时表中不存在这样的记录", Toast.LENGTH_SHORT).show();

            return res;
        }
        catch (Exception e){
            Log.e(TAG, "deleteOrder fail:id不存在！");
            e.printStackTrace();
        }
        finally {
            if (db != null){
                db.endTransaction();
                db.close();
            }

        }

        return 0;
    }

    /**
     * 更新一条/多条数据
     * @param id    根据id更新数据（不能更新id）
     * @param customName
     * @param orderPrice
     * @param country
     * @return
     */
    public int updateOrder(String id,String customName, String orderPrice, String country){
        if (id.equals("")){
            Toast.makeText(context, "更新时id不能为空", Toast.LENGTH_SHORT).show();
            return 0;
        }
        SQLiteDatabase db = null;

        try{
            db = mDatabase.getWritableDatabase();
            db.beginTransaction();

            ContentValues values = new ContentValues();
            if (!customName.equals(""))
                values.put("CustomName", customName);
            else if (!orderPrice.equals(""))
                values.put("OrderPrice", orderPrice);
            else if (!country.equals(""))
                values.put("Country", country);
            else {
                Toast.makeText(context, "更新时不能全为空", Toast.LENGTH_SHORT).show();
                return 0;
            }

            int res = db.update(Database.TABLE_NAME, values, "Id=?", new String[]{id});

            db.setTransactionSuccessful();
            if (res == 0)
                Toast.makeText(context, "更新时表中不存在这样的id记录", Toast.LENGTH_SHORT).show();

            return res;
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (db != null){
                db.endTransaction();
                db.close();
            }
        }

        return 0;
    }


    /**
     * 将查询到的数据转换为Order格式
     * @param cursor
     * @return
     */
    private Order parseOrder(Cursor cursor){
        Order order = new Order();
        order.id = cursor.getInt(cursor.getColumnIndex("Id"));
        order.customName = (cursor.getString(cursor.getColumnIndex("CustomName")));
        order.orderPrice = (cursor.getInt(cursor.getColumnIndex("OrderPrice")));
        order.country = (cursor.getString(cursor.getColumnIndex("Country")));

        return order;
    }

}
