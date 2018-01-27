package edu.ashleyxue.ecaft;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import edu.ashleyxue.ecaft.DatabaseSchema.CompanyTable;

import android.database.Cursor;
import android.util.Log;


/**
 * Created by Ashley on 1/16/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "companyBase.db";
    private static final String INDEX_1 = "company_names";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("123456789", countTables() + ": num tables");
        db.execSQL("create table " + CompanyTable.NAME + "(" +
                        CompanyTable.Cols.ID + ", " +
                        CompanyTable.Cols.COMPANY_NAME + ", " +
                        CompanyTable.Cols.VISITED + "," +
                        CompanyTable.Cols.NOTE + ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int countTables() {
        int count = 0;
        String SQL_GET_ALL_TABLES = "SELECT * FROM sqlite_master WHERE type='table'";
        Cursor cursor = getReadableDatabase()
                .rawQuery(SQL_GET_ALL_TABLES, null);
        cursor.moveToFirst();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            count++;
            getReadableDatabase().close();

        }
        cursor.close();
        return count;
    }
}
