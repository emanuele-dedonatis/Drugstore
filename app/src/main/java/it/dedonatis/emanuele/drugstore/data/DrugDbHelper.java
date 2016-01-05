package it.dedonatis.emanuele.drugstore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import it.dedonatis.emanuele.drugstore.data.DrugContract.*;


public class DrugDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "drugs.db";

    public DrugDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_DRUG_TABLE = "CREATE TABLE " + DrugEntry.TABLE_NAME + " (" +
                DrugEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DrugEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL, " +
                DrugEntry.COLUMN_API + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_PACKAGE_TABLE = "CREATE TABLE " + PackageEntry.TABLE_NAME + " (" +
                PackageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                PackageEntry.COLUMN_DRUG + " INTEGER NOT NULL, " +

                PackageEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                PackageEntry.COLUMN_UNITS + " INTEGER NOT NULL, " +
                PackageEntry.COLUMN_UNITS_LEFT + " INTEGER NOT NULL, " +
                PackageEntry.COLUMN_EXPIRATION_DATE + " INTEGER NOT NULL," +

                " FOREIGN KEY (" + PackageEntry.COLUMN_DRUG + ") REFERENCES " +
                DrugEntry.TABLE_NAME + " (" + DrugEntry._ID + "));";

        db.execSQL(SQL_CREATE_DRUG_TABLE);
        db.execSQL(SQL_CREATE_PACKAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DrugEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PackageEntry.TABLE_NAME);
        onCreate(db);
    }
}
