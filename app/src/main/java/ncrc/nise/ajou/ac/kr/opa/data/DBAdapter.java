package ncrc.nise.ajou.ac.kr.opa.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class DBAdapter {
    static final String user_dbname = "user";

    private Context context;
    private SQLiteDatabase db;

    public DBAdapter(Context context) {
        this.context = context;
        this.open();
    }

    /* Open DB */
    private void open() {
        try {
            db = (new DBHelper(context).getWritableDatabase());
        } catch(SQLiteException e) {
            db = (new DBHelper(context).getReadableDatabase());
        }
    }

    /* Close DB */
    public void Close() {
        db.close();
    }

    /* Insert User Data */
    public void insertUser(double height, double weight, int age, int sex) {
        try {
            ContentValues values = new ContentValues();
            values.put("height", height);
            values.put("weight",weight);
            values.put("age", age);
            values.put("sex", sex);
            db.insert("user", null, values);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /* Read User Info */
    public Cursor read_user_info() {
        Cursor c = db.query("user", //table name
                new String[] {"_id","height","weight", "age", "sex"}, //colum 명세
                null, //where
                null, //where 절에 전달할 데이터
                null, //group by
                null, //having
                "_id" + " DESC" //order by
        );

        return c;
    }
}
