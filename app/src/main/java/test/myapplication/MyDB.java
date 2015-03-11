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

    public void insert(Rule rule) {
        ContentValues values = new ContentValues();
        values.put(MyDBHelper.COLUMN_START_TIME, rule.getStart_time());
        values.put(MyDBHelper.COLUMN_END_TIME, rule.getEnd_time());
        values.put(MyDBHelper.COLUMN_VTYPE, rule.getVolume());
        long insertId = database.insert(MyDBHelper.TABLE_NAME, null, values);
    }

    public List<Rule> select() {
        List<Rule> rules = new ArrayList<Rule>();

        Cursor cursor = database.query(MyDBHelper.TABLE_NAME, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Rule rule = cursorToRule(cursor);
            rules.add(rule);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return rules;
    }

    public void updateById(long id, String startTime, String endTime, String volume) {
        ContentValues values = new ContentValues();
        values.put(MyDBHelper.COLUMN_ID, id);
        values.put(MyDBHelper.COLUMN_START_TIME, startTime);
        values.put(MyDBHelper.COLUMN_END_TIME, endTime);
        values.put(MyDBHelper.COLUMN_VTYPE, volume);

        String selection = MyDBHelper.COLUMN_ID + " = " + id;
        database.update(MyDBHelper.TABLE_NAME, values, selection, null);
    }

    public void deleteById(long id) {
        String selection = MyDBHelper.COLUMN_ID + " = " + id;
        database.delete(MyDBHelper.TABLE_NAME, selection, null);
    }

    private Rule cursorToRule(Cursor cursor) {
        Rule rule = new Rule(cursor.getString(1), cursor.getString(2), cursor.getString(3));
        rule.setId(Long.parseLong(cursor.getString(0)));
        return rule;
    }
}
