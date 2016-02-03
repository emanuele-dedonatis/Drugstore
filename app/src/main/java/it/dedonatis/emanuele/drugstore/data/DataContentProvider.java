package it.dedonatis.emanuele.drugstore.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;

import it.dedonatis.emanuele.drugstore.data.DataContract.*;

public class DataContentProvider extends ContentProvider{
    private static final String LOG_TAG = DataContentProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DataDbHelper mOpenHelper;

    static final int DRUGS = 100;                   //  /drugs
    static final int DRUGS_ID = 101;                //  /drugs/#
    static final int DRUGS_PACKAGES = 102;          //  /drugs/#/packages
    static final int DRUGS_SUBPACKAGES = 103;       //  /drugs/#/subpackages

    static final int PACKAGES = 200;                //  /packages
    static final int PACKAGES_ID = 201;             //  /packages/#
    static final int PACKAGES_SUBPACKAGES = 202;    //  /packages/#/subpackages
    static final int PACKAGES_THERAPIES = 203;      //  /packages/#/therapies

    static final int SUBPACKAGES = 300;             //  /subpackages
    static final int SUBPACKAGES_ID = 301;          //  /subpackages/#

    static final int THERAPIES = 400;               //  /therapies
    static final int THERAPIES_ID = 401;            //  /therapies/#
    static final int THERAPIES_ALARMS = 402;        //  /therpies/#/alarms

    static final int ALARMS = 500;                  //  /alarms
    static final int ALARMS_ID = 501;               //  /alarms/#


    @Override
    public boolean onCreate() {
        mOpenHelper = new DataDbHelper(getContext());
        return true;
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.CONTENT_AUTHORITY;

        // content://it.drugstore.app/drugs
        matcher.addURI(authority, DataContract.PATH_DRUGS, DRUGS);
        // content://it.drugstore.app/drugs/#
        matcher.addURI(authority, DataContract.PATH_DRUGS + "/#", DRUGS_ID);
        // content://it.drugstore.app/drugs/#/packages
        matcher.addURI(authority, DataContract.PATH_DRUGS + "/#/" + DataContract.PATH_PACKAGES, DRUGS_PACKAGES);
        // content://it.drugstore.app/drugs/#/subpackages
        matcher.addURI(authority, DataContract.PATH_DRUGS + "/#/" + DataContract.PATH_SUBPACKAGES, DRUGS_SUBPACKAGES);

        // content://it.drugstore.app/packages
        matcher.addURI(authority, DataContract.PATH_PACKAGES, PACKAGES);
        // content://it.drugstore.app/packages/#
        matcher.addURI(authority, DataContract.PATH_PACKAGES + "/#", PACKAGES_ID);
        // content://it.drugstore.app/packages/#/subpackages
        matcher.addURI(authority, DataContract.PATH_PACKAGES + "/#/" + DataContract.PATH_SUBPACKAGES, PACKAGES_SUBPACKAGES);
        // content://it.drugstore.app/packages/#/therapies
        matcher.addURI(authority, DataContract.PATH_PACKAGES + "/#/" + DataContract.PATH_THERAPIES , PACKAGES_THERAPIES);

        // content://it.drugstore.app/subpackages
        matcher.addURI(authority, DataContract.PATH_SUBPACKAGES, SUBPACKAGES);
        // content://it.drugstore.app/subpackages/#
        matcher.addURI(authority, DataContract.PATH_SUBPACKAGES + "/#", SUBPACKAGES_ID);

        // content://it.drugstore.app/therapies
        matcher.addURI(authority, DataContract.PATH_THERAPIES, THERAPIES);
        // content://it.drugstore.app/therapies/#
        matcher.addURI(authority, DataContract.PATH_THERAPIES + "/#", THERAPIES_ID);
        // content://it.drugstore.app/therapies/#/alarms
        matcher.addURI(authority, DataContract.PATH_THERAPIES + "/#/" + DataContract.PATH_ALARMS , THERAPIES_ALARMS);

        // content://it.drugstore.app/alarms
        matcher.addURI(authority, DataContract.PATH_ALARMS, ALARMS);
        // content://it.drugstore.app/alarms/#
        matcher.addURI(authority, DataContract.PATH_ALARMS + "/#", ALARMS_ID);

        return matcher;
    }


    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case DRUGS:                   //  /drugs
                return DrugEntry.CONTENT_TYPE;
            case DRUGS_ID:                //  /drugs/#
                return DrugEntry.CONTENT_ITEM_TYPE;
            case DRUGS_PACKAGES:          //  /drugs/#/packages
                return PackageEntry.CONTENT_TYPE;
            case DRUGS_SUBPACKAGES:          //  /drugs/#/subpackages
                return PackageEntry.CONTENT_TYPE;
            case PACKAGES:                //  /packages
                return PackageEntry.CONTENT_TYPE;
            case PACKAGES_ID:             //  /packages/#
                return PackageEntry.CONTENT_ITEM_TYPE;
            case PACKAGES_SUBPACKAGES:    //  /packages/#/subpackages
                return SubpackageEntry.CONTENT_TYPE;
            case PACKAGES_THERAPIES:      //  /packages/#/therapies
                return TherapyEntry.CONTENT_TYPE;
            case SUBPACKAGES:             //  /subpackages
                return SubpackageEntry.CONTENT_TYPE;
            case SUBPACKAGES_ID:          //  /subpackages/#
                return SubpackageEntry.CONTENT_ITEM_TYPE;
            case THERAPIES:               //  /therapies
                return TherapyEntry.CONTENT_TYPE;
            case THERAPIES_ID:            //  /therapies/#
                return TherapyEntry.CONTENT_ITEM_TYPE;
            case THERAPIES_ALARMS:        //  /therpies/#/alarms
                return AlarmEntry.CONTENT_TYPE;
            case ALARMS:                  //  /alarms
                return AlarmEntry.CONTENT_TYPE;
            case ALARMS_ID:               //  /alarms/#
                return AlarmEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        Log.v(LOG_TAG, "query: " + uri.toString());
        switch (sUriMatcher.match(uri)) {
            /*
                content://it.drugstore.app/drugs
                ?name ?name_like ?api ?api_like
            */
            case DRUGS:
            {
                // ?name
                String name = uri.getQueryParameter(DrugEntry.PARAM_NAME);
                if(name != null) {
                    selection = DrugEntry.TABLE_NAME + "." + DrugEntry.COLUMN_NAME + " = ?";
                    selectionArgs = new String[]{name};
                }else {
                    String name_like = uri.getQueryParameter(DrugEntry.PARAM_NAME_LIKE);
                    String api_like = uri.getQueryParameter(DrugEntry.PARAM_API_LIKE);

                    if(name_like != null && api_like != null) {
                        // ?name_like & ?api_like
                        selection = DrugEntry.TABLE_NAME + "." + DrugEntry.COLUMN_NAME + " LIKE ? OR " + DrugEntry.TABLE_NAME + "." + DrugEntry.COLUMN_API + " LIKE ?";
                        selectionArgs = new String[]{"%" + name_like + "%", "%" + api_like + "%"};
                    }else{
                        if (name_like != null) {
                            // ?name_like
                            selection = DrugEntry.TABLE_NAME + "." + DrugEntry.COLUMN_NAME + " LIKE ?";
                            selectionArgs = new String[]{"%" + name_like + "%"};
                        }

                        if (api_like != null) {
                            //?api_like
                            selection = DrugEntry.TABLE_NAME + "." + DrugEntry.COLUMN_API + " LIKE ?";
                            selectionArgs = new String[]{"%" + api_like + "%"};
                        }
                    }
                }


                retCursor = mOpenHelper.getReadableDatabase().query(
                        DrugEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            /*
                content://it.drugstore.app/drugs/#
            */
            case DRUGS_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DrugEntry.TABLE_NAME,
                        projection,
                        DrugEntry.TABLE_NAME + "." + DrugEntry._ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            /*
                content://it.drugstore.app/drugs/#/packages
            */
            case DRUGS_PACKAGES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PackageEntry.TABLE_NAME,
                        projection,
                        PackageEntry.TABLE_NAME + "." + PackageEntry.COLUMN_DRUG_ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            /*
                content://it.drugstore.app/drugs/#/subpackages
            */
            case DRUGS_SUBPACKAGES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SubpackageEntry.TABLE_NAME,
                        projection,
                        SubpackageEntry.TABLE_NAME + "." + SubpackageEntry.COLUMN_DRUG_ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }


        /*
            content://it.drugstore.app/packages
            ?description ?description_like
        */
            case PACKAGES: {
                // ?description
                String description = uri.getQueryParameter(PackageEntry.PARAM_DESCRIPTION);
                if(description != null) {
                    selection = PackageEntry.TABLE_NAME + "." + PackageEntry.COLUMN_DESCRIPTION + " = ?";
                    selectionArgs = new String[]{description};
                }else {
                    String description_like = uri.getQueryParameter(PackageEntry.PARAM_DESCRIPTION_LIKE);
                        if (description_like != null) {
                            //?description_like
                            selection = PackageEntry.TABLE_NAME + "." + PackageEntry.COLUMN_DESCRIPTION + " LIKE ?";
                            selectionArgs = new String[]{"%" + description_like + "%"};
                        }
                }


                retCursor = mOpenHelper.getReadableDatabase().query(
                        PackageEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
        /*
            content://it.drugstore.app/packages/#
        */
            case PACKAGES_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PackageEntry.TABLE_NAME,
                        projection,
                        PackageEntry.TABLE_NAME + "." + PackageEntry._ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
        /*
            content://it.drugstore.app/packages/#/subpackages
        */
            case PACKAGES_SUBPACKAGES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SubpackageEntry.TABLE_NAME,
                        projection,
                        SubpackageEntry.TABLE_NAME + "." + SubpackageEntry.COLUMN_PACKAGE_ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
        /*
            content://it.drugstore.app/packages/#/therapies
        */
            case PACKAGES_THERAPIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TherapyEntry.TABLE_NAME,
                        projection,
                        TherapyEntry.TABLE_NAME + "." + TherapyEntry.COLUMN_PACKAGE_ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }



        /*
            content://it.drugstore.app/subpackages
            ?dose_left_under ?exp_before
        */
            case SUBPACKAGES: {
                    String dose_left_under = uri.getQueryParameter(SubpackageEntry.PARAM_DOSE_LEFT_UNDER);
                    String exp_before = uri.getQueryParameter(SubpackageEntry.PARAM_EXP_BEFORE);

                    if(dose_left_under != null && exp_before != null) {
                        // ?dose_left_under & ?exp_before
                        selection = SubpackageEntry.TABLE_NAME + "." + SubpackageEntry.COLUMN_DOSES_LEFT + " <= ? AND "
                                + SubpackageEntry.TABLE_NAME + "." + SubpackageEntry.COLUMN_EXP_DATE + " <= ?";
                        selectionArgs = new String[]{"%" + dose_left_under + "%", "%" + exp_before + "%"};
                    }else{
                        if (dose_left_under != null) {
                            // ?dose_left_under
                            selection = SubpackageEntry.TABLE_NAME + "." + SubpackageEntry.COLUMN_DOSES_LEFT + " <= ?";
                            selectionArgs = new String[]{"%" + dose_left_under + "%"};
                        }

                        if (exp_before != null) {
                            //?api_like
                            selection = SubpackageEntry.TABLE_NAME + "." + SubpackageEntry.COLUMN_EXP_DATE + " <= ?";
                            selectionArgs = new String[]{"%" + exp_before + "%"};
                        }
                    }

                retCursor = mOpenHelper.getReadableDatabase().query(
                        SubpackageEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

        /*
            content://it.drugstore.app/subpackages/#
        */
            case SUBPACKAGES_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SubpackageEntry.TABLE_NAME,
                        projection,
                        SubpackageEntry.TABLE_NAME + "." + SubpackageEntry._ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }

        /*
            content://it.drugstore.app/therapies
        */
            case THERAPIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TherapyEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
        /*
            content://it.drugstore.app/therapies/#
        */
            case THERAPIES_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TherapyEntry.TABLE_NAME,
                        projection,
                        TherapyEntry.TABLE_NAME + "." + TherapyEntry._ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
        /*
            content://it.drugstore.app/therapies/#/alarms
        */
            case THERAPIES_ALARMS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AlarmEntry.TABLE_NAME,
                        projection,
                        AlarmEntry.TABLE_NAME + "." + AlarmEntry.COLUMN_THERAPY_ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }

        /*
            content://it.drugstore.app/alarms
        */
            case ALARMS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AlarmEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

        /*
            content://it.drugstore.app/alarms/#
        */
            case ALARMS_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AlarmEntry.TABLE_NAME,
                        projection,
                        AlarmEntry.TABLE_NAME + "." + AlarmEntry.COLUMN_THERAPY_ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.v(LOG_TAG, "insert: " + uri.toString());
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case DRUGS: {
                long _id = db.insertWithOnConflict(DrugEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                if(_id < 0) {
                    String selection = DrugEntry.TABLE_NAME + "." + DrugEntry.COLUMN_NAME + " = ?";
                    String[] selectionArgs = new String[]{values.getAsString(DataContract.DrugEntry.COLUMN_NAME)};

                    Cursor cursor = mOpenHelper.getReadableDatabase().query(
                            DrugEntry.TABLE_NAME,
                            null,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            null
                    );

                    if (cursor.moveToFirst())
                        _id = cursor.getLong(0);

                    cursor.close();
                }
                returnUri = DrugEntry.buildDrugUri(_id);
                break;
            }
            case PACKAGES: {
                long _id = db.insertWithOnConflict(PackageEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                if(_id < 0) {
                    String selection = PackageEntry.TABLE_NAME + "." + PackageEntry.COLUMN_DESCRIPTION + " = ?";
                    String[] selectionArgs = new String[]{values.getAsString(PackageEntry.COLUMN_DESCRIPTION)};

                    Cursor cursor = mOpenHelper.getReadableDatabase().query(
                            PackageEntry.TABLE_NAME,
                            null,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            null
                    );

                    if (cursor.moveToFirst())
                        _id = cursor.getLong(0);

                    cursor.close();
                }
                returnUri = PackageEntry.buildPackageUri(_id);
                break;
            }
            case SUBPACKAGES: {
                long _id = db.insertWithOnConflict(SubpackageEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                returnUri = SubpackageEntry.buildSubpackageUri(_id);
                break;
            }
            case THERAPIES: {
                long _id = db.insertWithOnConflict(TherapyEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                returnUri = TherapyEntry.buildTherapyUri(_id);
                break;
            }
            case ALARMS: {
                long _id = db.insertWithOnConflict(AlarmEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                returnUri = AlarmEntry.buildAlarmUri(_id);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        Log.v(LOG_TAG, "Delete: " + uri.toString());

        if ( null == selection ) selection = "1";
        switch (match) {
            // "drug/#"
            case DRUGS_ID:
                rowsDeleted = db.delete(DrugEntry.TABLE_NAME,
                        DrugEntry.TABLE_NAME + "." + DrugEntry._ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)}
                );
                break;
            // "package/#"
            case PACKAGES_ID:
                Cursor cursor = query(uri, new String[] {PackageEntry.COLUMN_IMAGE_URI},null,null,null);
                Uri imageUri;
                while (cursor.moveToNext() && cursor.getString(0) != null) {
                    imageUri = Uri.parse(cursor.getString(0));
                    File imageFile = new File(imageUri.getPath());
                    imageFile.delete();
                    Log.v(LOG_TAG, "Deleting image file " + imageUri.getPath());
                }
                rowsDeleted = db.delete(PackageEntry.TABLE_NAME,
                        PackageEntry.TABLE_NAME + "." + PackageEntry._ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)}
                );
                break;
            // "subpackages/#"
            case SUBPACKAGES_ID:
                rowsDeleted = db.delete(SubpackageEntry.TABLE_NAME,
                        SubpackageEntry.TABLE_NAME + "." + SubpackageEntry._ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)}
                );
                break;
            // "therapies/#"
            case THERAPIES_ID:
                rowsDeleted = db.delete(TherapyEntry.TABLE_NAME,
                        TherapyEntry.TABLE_NAME + "." + TherapyEntry._ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)}
                );
                break;
            // "alarms/#"
            case ALARMS_ID:
                rowsDeleted = db.delete(AlarmEntry.TABLE_NAME,
                        AlarmEntry.TABLE_NAME + "." + AlarmEntry._ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)}
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.v(LOG_TAG, "Update: " + uri.toString());
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case DRUGS:
                rowsUpdated = db.update(DrugEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case DRUGS_ID:
                rowsUpdated = db.update(DrugEntry.TABLE_NAME, values,
                        DrugEntry.TABLE_NAME + "." + DrugEntry._ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)});
                break;
            case PACKAGES:
                rowsUpdated = db.update(PackageEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case PACKAGES_ID:
                rowsUpdated = db.update(PackageEntry.TABLE_NAME, values,
                        PackageEntry.TABLE_NAME + "." + PackageEntry._ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)});
                break;
            case SUBPACKAGES:
                rowsUpdated = db.update(SubpackageEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case SUBPACKAGES_ID:
                rowsUpdated = db.update(SubpackageEntry.TABLE_NAME, values,
                        SubpackageEntry.TABLE_NAME + "." + SubpackageEntry._ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)});
                Log.v(LOG_TAG, "Update subpackage " + uri.getPathSegments().get(1) + " rows updated " + rowsUpdated );
                break;
            case THERAPIES:
                rowsUpdated = db.update(TherapyEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case THERAPIES_ID:
                rowsUpdated = db.update(TherapyEntry.TABLE_NAME, values,
                        TherapyEntry.TABLE_NAME + "." + TherapyEntry._ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)});
                break;
            case ALARMS:
                rowsUpdated = db.update(AlarmEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case ALARMS_ID:
                rowsUpdated = db.update(AlarmEntry.TABLE_NAME, values,
                        AlarmEntry.TABLE_NAME + "." + AlarmEntry._ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;    }
}

