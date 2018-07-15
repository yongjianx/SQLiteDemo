package SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by skyworthclub on 2018/7/14.
 */

public class Database extends SQLiteOpenHelper {

    //数据库版本号
    private final static int DB_VERSION = 1;
    //数据库名称
    public final static String DB_NAME = "myDatabase.db";
    //建立的一个表名
    public final static String TABLE_NAME = "orders";

    /**
     * @param context
     */
    public Database(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists " + TABLE_NAME
                + "(Id integer primary key, CustomName text, OrderPrice integer, Country text)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS "+TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }
}
