package it.dedonatis.emanuele.drugstore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.ParseException;

import it.dedonatis.emanuele.drugstore.data.DataContract.*;


public class DataDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "drugs.db";
    private final String DB_FILEPATH;
    private final String FOLDER_NAME = "Drugstore";
    private final String PACKAGE_NAME;

    public DataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        PACKAGE_NAME = context.getPackageName();
        DB_FILEPATH = "/data/data/" + PACKAGE_NAME + "/databases/" + DATABASE_NAME;
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
            |   long drug_id            |
            |   long package_id         |
            |   int doses_left          |
            |   int exp_date (yyyyMMdd) |
            ----------------------------
    */
        final String SQL_CREATE_SUBPACKAGES_TABLE = "CREATE TABLE " + SubpackageEntry.TABLE_NAME + " (" +
                SubpackageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SubpackageEntry.COLUMN_DRUG_ID + " INTEGER NOT NULL, " +
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

    /**
     * Copies the database file at the specified location
     * over the current internal application database.
     * */
    public boolean importDatabase(String dbPath) throws IOException, ParseException {
        String filenameArray[] = dbPath.split("\\.");
        String extension = filenameArray[filenameArray.length-1];
        if(extension.equals("db")) {
            // Close the SQLiteOpenHelper so it will
            // commit the created empty database to internal storage.
            close();
            File newDb = new File(dbPath);
            File oldDb = new File(DB_FILEPATH);
            if (newDb.exists()) {
                copyFile(new FileInputStream(newDb), new FileOutputStream(oldDb));
                // Access the copied database so SQLiteHelper
                // will cache it and mark it as created.
                getWritableDatabase().close();
                return true;
            }
            return false;
        }else{
            throw new ParseException("not db extension", 0);
        }
    }

    private void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } finally {
                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }

    public File backupDatabase(String fileName) throws IOException {

        if (isSDCardWriteable()) {
            // Open your local db as the input stream
            String inFileName = DB_FILEPATH;
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + FOLDER_NAME);
            if (!folder.exists()) {
                boolean succes = folder.mkdir();
            }

            String outFileName = folder.getAbsolutePath() + "/" + fileName;
            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);
            // transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            // Close the streams
            output.flush();
            output.close();
            fis.close();
            return new File(outFileName);
        }else {
            return null;
        }
    }

    private boolean isSDCardWriteable() {
        boolean rc = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            rc = true;
        }
        return rc;
    }
}
