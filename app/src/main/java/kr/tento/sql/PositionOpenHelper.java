package kr.tento.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kr.tento.model.Position;

public class PositionOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "positions";
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (x INT, y INT, music_id INT);";


    public PositionOpenHelper(Context context) {
        super(context, "tento-android.db", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

    public void addPosition(HashMap<String, String> m) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Iterator<Map.Entry<String, String>> entries = m.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            values.put(entry.getKey(), entry.getValue());
        }
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<Position> getAllPosition() {
        ArrayList<Position> l = new ArrayList<Position>();
        try {
            l.clear();

            String selectQuery = "SELECT * FROM " + TABLE_NAME;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    Position position = new Position(
                        Integer.parseInt(cursor.getString(0)),
                        Integer.parseInt(cursor.getString(1)),
                        Integer.parseInt(cursor.getString(2))
                    );
                    l.add(position);
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
            return l;
        } catch (Exception e) {
            Log.e("sql.PositionOpenHelper", e.getMessage());
        }

        return l;
    }
}
