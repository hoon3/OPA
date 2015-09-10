package ncrc.nise.ajou.ac.kr.opa.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
 * Create DB and Table
 */
public class DBHelper extends SQLiteOpenHelper {

    static final String TAG = "DbHelper";
    static final String DB_NAME = "opa.db";
    static final int DB_VERSION = 1;
    static final String TABLE = "user";
    Context context;
    SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    // called when database is created
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("create table user (" +
                    "_id integer primary key autoincrement, " +
                    "height real, weight real, age integer, sex integer);");
            Log.i("DB", "onCreate!");
            this.db = db;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* When DB Version changes, it is called. */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Typically do ALTER TABLE statements, but...we're just in development,
        // so:
        db.execSQL("drop table if exists " + TABLE); // drops the old database
        Log.d(TAG, "onUpdated");
        onCreate(db); // run onCreate to get new database
    }

    // insert data
    public void insert(double height, double weight, int age, int sex) {
        ContentValues values = new ContentValues();
        values.put("height", height);
        values.put("weight",weight);
        values.put("age", age);
        values.put("sex", sex);

        db = getWritableDatabase();
        db.insert("user", null, values);
    }
}
