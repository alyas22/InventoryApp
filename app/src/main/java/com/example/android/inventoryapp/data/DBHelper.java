package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.inventoryapp.data.Contract.InventoryEntry;

public class DBHelper extends SQLiteOpenHelper {


    public static final String LOG_TAG = DBHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "inventory.db";

    private static final int DATABASE_VERSION = 4;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " +
                InventoryEntry.TABLE_NAME + "(" +
                InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                InventoryEntry.COLUMN_NAME + " TEXT NOT NULL," +
                InventoryEntry.COLUMN_PRICE + " INTEGER NOT NULL DEFAULT 0," +
                InventoryEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0," +
                InventoryEntry.COLUMN_IMAGE + " TEXT NOT NULL" + ");";

        Log.v(LOG_TAG, SQL_CREATE_INVENTORY_TABLE);
        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + InventoryEntry.TABLE_NAME);
        onCreate(db);

    }
}
