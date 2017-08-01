package com.example.ankkitabose.dropboxintegrate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by POWERHOUSE on 7/30/2017.
 */

public class StatusDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "StatusDB.db";
    public static final String TABLE_STATUS = "Status_Table";
    public static final String COLUMN_ITEM = "Item";
    public static final String COLUMN_STATUS = "Item_Status";

    public StatusDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TABLE_STATUS +
                        "(" + COLUMN_ITEM + " text primary key, " + COLUMN_STATUS  + " text);"
        );
    }

    public boolean insertItem(String item, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_ITEM, item);
        contentValues.put(COLUMN_STATUS, value);

        Log.d("SQL","Inserting "+value);


        db.insert(TABLE_STATUS, null, contentValues);
        return true;
    }

    public Cursor getItem(String item) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + TABLE_STATUS + " where " + COLUMN_ITEM + "='" + item + "';", null);
    }

    public boolean updateItem(String item, String value) {
        Log.d("SQL","Item deleted "+deleteItem(item).toString());
        return insertItem(item, value);
    }

    public Integer deleteItem(String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_STATUS,
                COLUMN_ITEM + " = ? ",
                new String[]{item});

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
