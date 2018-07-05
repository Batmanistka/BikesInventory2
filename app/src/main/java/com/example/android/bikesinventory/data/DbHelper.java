package com.example.android.bikesinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.bikesinventory.data.Contract.BikeEntry;

public class DbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = DbHelper.class.getSimpleName();

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "bs11.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_BIKES_TABLE =
                "CREATE TABLE " + BikeEntry.TABLE_NAME + " (" +
                        BikeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        BikeEntry.COLUMN_PRODUCT_NAME + " TEXT, " +
                        BikeEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, " +
                        BikeEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
                        BikeEntry.COLUMN_PRICE + " INTEGER NOT NULL DEFAULT 0, " +
                        BikeEntry.COLUMN_SUPPLIER_PHONE + " INTEGER NOT NULL DEFAULT 0 " + ");";
        db.execSQL(SQL_CREATE_BIKES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}