package edu.ecaft;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pdarb on 1/31/2018.
 */

public class PicDatabaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "picDatabase.db";
    private static final String CREATE_TABLE = String.format("CREATE TABLE %s(%s TEXT, %s TEXT)",
            PicDatabaseSchema.CompanyTable.NAME, PicDatabaseSchema.CompanyTable.COMPANY_NAME,
            PicDatabaseSchema.CompanyTable.PICFILES);

    public PicDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + PicDatabaseSchema.CompanyTable.NAME + " (" +
                PicDatabaseSchema.CompanyTable._ID + " INTEGER PRIMARY KEY," +
                PicDatabaseSchema.CompanyTable.COMPANY_NAME + " TEXT," +
                PicDatabaseSchema.CompanyTable.PICFILES + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
