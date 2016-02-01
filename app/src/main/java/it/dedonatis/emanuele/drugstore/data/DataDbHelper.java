package it.dedonatis.emanuele.drugstore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import it.dedonatis.emanuele.drugstore.data.DataContract.*;


public class DataDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "drugs.db";

    public DataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    /*      ----------------
            |   DRUGS       |
            ----------------
            |   long id     |
            |   String name |
            |   String api  |
            ----------------
     */
        final String SQL_CREATE_DRUGS_TABLE = "CREATE TABLE " + DrugEntry.TABLE_NAME + " (" +
                DrugEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DrugEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL, " +
                DrugEntry.COLUMN_API + " TEXT NOT NULL" +
                " );";

    /*      ------------------------------------
            |   PACKAGES                        |
            ------------------------------------
            |   long id                         |
            |   long drug_id                    |
            |   (UNIQUE) String description     |
            |   int doses                       |
            |   Uri image                       |
            ------------------------------------
    */
        final String SQL_CREATE_PACKAGES_TABLE = "CREATE TABLE " + PackageEntry.TABLE_NAME + " (" +
                PackageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PackageEntry.COLUMN_DRUG_ID + " INTEGER NOT NULL, " +
                PackageEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL UNIQUE, " +
                PackageEntry.COLUMN_DOSES + " INTEGER NOT NULL, " +
                PackageEntry.COLUMN_IMAGE_URI + " STRING," +

                " FOREIGN KEY (" + PackageEntry.COLUMN_DRUG_ID + ") REFERENCES " +
                DrugEntry.TABLE_NAME + " (" + DrugEntry._ID + ") ON DELETE CASCADE);";

    /*      ----------------------------
            |   SUBPACKAGES             |
            ----------------------------
            |   long id                 |
            |   long package_id         |
            |   int doses_left          |
            |   int exp_date (yyyyMMdd) |
            ----------------------------
    */
        final String SQL_CREATE_SUBPACKAGES_TABLE = "CREATE TABLE " + SubpackageEntry.TABLE_NAME + " (" +
                SubpackageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SubpackageEntry.COLUMN_PACKAGE_ID + " INTEGER NOT NULL, " +
                SubpackageEntry.COLUMN_DOSES_LEFT + " INTEGER NOT NULL, " +
                SubpackageEntry.COLUMN_EXP_DATE + " INTEGER NOT NULL, " +

                " FOREIGN KEY (" + SubpackageEntry.COLUMN_PACKAGE_ID + ") REFERENCES " +
                PackageEntry.TABLE_NAME + " (" + PackageEntry._ID + ") ON DELETE CASCADE);";

    /*    ------------------------------
          |   THERAPIES                 |
          ------------------------------
          |   long id                   |
          |   long package_id           |
          |   int doses                 |
          |   int exp_date (yyyyMMdd)   |
          ------------------------------
    */
        final String SQL_CREATE_THERAPIES_TABLE = "CREATE TABLE " + TherapyEntry.TABLE_NAME + " (" +
                TherapyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TherapyEntry.COLUMN_PACKAGE_ID + " INTEGER NOT NULL, " +
                TherapyEntry.COLUMN_DOSE + " INTEGER NOT NULL, " +
                TherapyEntry.COLUMN_EXP_DATE + " INTEGER NOT NULL, " +

                " FOREIGN KEY (" + TherapyEntry.COLUMN_PACKAGE_ID + ") REFERENCES " +
                PackageEntry.TABLE_NAME + " (" + PackageEntry._ID + ") ON DELETE CASCADE);";


    /*     -----------------------------------------------------
          |   ALARMS                                            |
          ------------------------------------------------------
          |   long id                                           |
          |   long therapy_id                                   |
          |   int day_of_week (java.util.Calendar.DAY_OF_WEEK)  |
          |   int hour_of_day (java.util.Calendar.HOUR_OF_DAY)  |
          |   int minute (java.util.Calendar.MINUTE)            |
          ------------------------------------------------------
    */
        final String SQL_CREATE_ALARMS_TABLE = "CREATE TABLE " + AlarmEntry.TABLE_NAME + " (" +
                AlarmEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AlarmEntry.COLUMN_THERAPY_ID + " INTEGER NOT NULL, " +
                AlarmEntry.COLUMN_DAY_OF_WEEK + " INTEGER NOT NULL, " +
                AlarmEntry.COLUMN_HOUR_OF_DAY + " INTEGER NOT NULL, " +
                AlarmEntry.COLUMN_MINUTE + " INTEGER NOT NULL, " +

                " FOREIGN KEY (" + AlarmEntry.COLUMN_THERAPY_ID + ") REFERENCES " +
                TherapyEntry.TABLE_NAME + " (" + TherapyEntry._ID + ") ON DELETE CASCADE);";

        db.execSQL(SQL_CREATE_DRUGS_TABLE);
        db.execSQL(SQL_CREATE_PACKAGES_TABLE);
        db.execSQL(SQL_CREATE_SUBPACKAGES_TABLE);
        db.execSQL(SQL_CREATE_THERAPIES_TABLE);
        db.execSQL(SQL_CREATE_ALARMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DrugEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PackageEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SubpackageEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TherapyEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AlarmEntry.TABLE_NAME);
        onCreate(db);
    }
}
