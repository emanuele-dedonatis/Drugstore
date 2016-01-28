package it.dedonatis.emanuele.drugstore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import it.dedonatis.emanuele.drugstore.data.DrugContract.*;


public class DrugDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;

    static final String DATABASE_NAME = "drugs.db";

    public DrugDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_DRUG_TABLE = "CREATE TABLE " + DrugEntry.TABLE_NAME + " (" +
                DrugEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DrugEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL, " +
                DrugEntry.COLUMN_API + " TEXT NOT NULL, " +
                DrugEntry.COLUMN_NEED_PRESCRIPTION + " INTEGER NOT NULL " +
                " );";

        final String SQL_CREATE_PACKAGE_TABLE = "CREATE TABLE " + PackageEntry.TABLE_NAME + " (" +
                PackageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                PackageEntry.COLUMN_DRUG + " INTEGER NOT NULL, " +

                PackageEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                PackageEntry.COLUMN_UNITS + " INTEGER NOT NULL, " +
                PackageEntry.COLUMN_IS_PERCENTAGE + " INTEGER NOT NULL, " +
                PackageEntry.COLUMN_EXPIRATION_DATE + " INTEGER NOT NULL," +
                PackageEntry.COLUMN_IMAGE + " STRING," +

                " FOREIGN KEY (" + PackageEntry.COLUMN_DRUG + ") REFERENCES " +
                DrugEntry.TABLE_NAME + " (" + DrugEntry._ID + ") ON DELETE CASCADE);";

        final String SQL_CREATE_PRESCRIPTION_TABLE = "CREATE TABLE " + PrescriptionEntry.TABLE_NAME + " (" +
                PrescriptionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PrescriptionEntry.COLUMN_DRUG + " INTEGER NOT NULL, " +
                PrescriptionEntry.COLUMN_PACKAGE + " INTEGER, " +
                PrescriptionEntry.COLUMN_HOW_MUCH + " INTEGER NOT NULL, " +
                PrescriptionEntry.COLUMN_EVERY + " INTEGER NOT NULL, " +
                PrescriptionEntry.COLUMN_HOUR + " INTEGER NOT NULL" +
                PrescriptionEntry.COLUMN_UNTIL + " INTEGER, " +

                " FOREIGN KEY (" + PrescriptionEntry.COLUMN_DRUG + ") REFERENCES " +
                DrugEntry.TABLE_NAME + " (" + DrugEntry._ID + ") ON DELETE CASCADE), " +


                " FOREIGN KEY (" + PrescriptionEntry.COLUMN_PACKAGE + ") REFERENCES " +
                PackageEntry.TABLE_NAME + " (" + PackageEntry._ID + ") ON DELETE SET NULL);";
        db.execSQL(SQL_CREATE_DRUG_TABLE);
        db.execSQL(SQL_CREATE_PACKAGE_TABLE);
        db.execSQL(SQL_CREATE_PRESCRIPTION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DrugEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PackageEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PrescriptionEntry.TABLE_NAME);
        onCreate(db);
    }
}
