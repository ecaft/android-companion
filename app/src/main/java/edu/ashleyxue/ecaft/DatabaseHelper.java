package edu.ashleyxue.ecaft;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import edu.ashleyxue.ecaft.DatabaseSchema.CompanyTable;

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
        db.execSQL("create table " + CompanyTable.NAME + "(" +
                        CompanyTable.Cols.ID + ", " +
                        CompanyTable.Cols.COMPANY_NAME + ", " +
                        CompanyTable.Cols.VISITED + "," +
                        CompanyTable.Cols.NOTE + ")"
        );

      //  db.execSQL("create index " + INDEX_1 + " on " + CompanyTable.NAME +
      //          "" +
      //          "(" + CompanyTable.Cols.COMPANY_NAME + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
