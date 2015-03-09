package test.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoheri on 3/5/15.
 */
public class MyDB {

    // Database fields
    private SQLiteDatabase database;
    private MyDBHelper dbHelper;
    private String[] allColumns = {
            MyDBHelper.COLUMN_ID,
            MyDBHelper.COLUMN_START_TIME,
            MyDBHelper.COLUMN_END_TIME,
            MyDBHelper.COLUMN_VTYPE
    };

    public MyDB(Context context) {
        dbHelper = new MyDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(Rules rule) {
        ContentValues values = new ContentValues();
        values.put(MyDBHelper.COLUMN_START_TIME, rule.start_time);
        values.put(MyDBHelper.COLUMN_END_TIME, rule.end_time);
        values.put(MyDBHelper.COLUMN_VTYPE, rule.vtype);
        long insertId = database.insert(MyDBHelper.TABLE_NAME, null, values);
    }

    public List<Rules> select() {
        List<Rules> rules = new ArrayList<Rules>();

        Cursor cursor = database.query(MyDBHelper.TABLE_NAME, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Rules rule = cursorToRule(cursor);
            rules.add(rule);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return rules;
    }

    private Rules cursorToRule(Cursor cursor) {
        Rules rule = new Rules(cursor.getString(1), cursor.getString(2), cursor.getString(3));
        return rule;
    }
}
