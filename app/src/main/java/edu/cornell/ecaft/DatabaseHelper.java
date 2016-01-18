package edu.cornell.ecaft;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import edu.cornell.ecaft.DatabaseSchema.CompanyTable;

/**
 * Created by Ashley on 1/16/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "companyBase.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CompanyTable.NAME + "(" +
                CompanyTable.Cols.UUID + ", " +
                CompanyTable.Cols.COMPANY_NAME + ", " +
                CompanyTable.Cols.VISITED + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
