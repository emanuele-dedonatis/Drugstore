package it.dedonatis.emanuele.drugstore.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.sql.SQLException;

import it.dedonatis.emanuele.drugstore.data.DrugContract.*;
import it.dedonatis.emanuele.drugstore.models.Drug;

public class DrugProvider extends ContentProvider{
    private static final String LOG_TAG = DrugProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DrugDbHelper mOpenHelper;

    static final int DRUGS = 100;
    static final int DRUG = 101;
    static final int DRUG_PACKAGES = 102;
    static final int DRUG_PRESCTIPIONS = 103;
    static final int PACKAGES = 200;
    static final int PACKAGE = 201;
    static final int PRESCRIPTIONS = 300;
    static final int PRESCRIPTION = 301;


    @Override
    public boolean onCreate() {
        mOpenHelper = new DrugDbHelper(getContext());
        return true;
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DrugContract.CONTENT_AUTHORITY;

        // content://it.drugstore.app/drug
        matcher.addURI(authority, DrugContract.PATH_DRUG, DRUGS);
        // content://it.drugstore.app/drug/#
        matcher.addURI(authority, DrugContract.PATH_DRUG + "/#", DRUG);
        // content://it.drugstore.app/drug/#/package
        matcher.addURI(authority, DrugContract.PATH_DRUG + "/#/" + DrugContract.PATH_PACKAGE, DRUG_PACKAGES);
        // content://it.drugstore.app/drug/#/prescription
        matcher.addURI(authority, DrugContract.PATH_DRUG + "/#/" + DrugContract.PATH_PRESCRIPTION, DRUG_PRESCTIPIONS);

        // content://it.drugstore.app/package
        matcher.addURI(authority, DrugContract.PATH_PACKAGE, PACKAGES);
        // content://it.drugstore.app/package/#
        matcher.addURI(authority, DrugContract.PATH_PACKAGE + "/#", PACKAGE);

        // content://it.drugstore.app/prescription
        matcher.addURI(authority, DrugContract.PATH_PRESCRIPTION, PRESCRIPTIONS);
        // content://it.drugstore.app/prescription/#
        matcher.addURI(authority, DrugContract.PATH_PRESCRIPTION + "/#", PRESCRIPTION);

        return matcher;
    }


    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case DRUGS:
                return DrugEntry.CONTENT_TYPE;
            case DRUG:
                return DrugEntry.CONTENT_ITEM_TYPE;
            case DRUG_PACKAGES:
                return PackageEntry.CONTENT_TYPE;
            case DRUG_PRESCTIPIONS:
                return PrescriptionEntry.CONTENT_TYPE;
            case PACKAGES:
                return PackageEntry.CONTENT_TYPE;
            case PACKAGE:
                return PackageEntry.CONTENT_ITEM_TYPE;
            case PRESCRIPTIONS:
                return PrescriptionEntry.CONTENT_TYPE;
            case PRESCRIPTION:
                return PrescriptionEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "drug"
            // "drug?name=drugname"
            // "drug?name_like=name"
            // "drug?api_like=name"
            // "drug?name_like=name"
            case DRUGS:
            {
                Log.v(LOG_TAG, uri.toString());
                String name = uri.getQueryParameter(DrugEntry.COLUMN_NAME);
                if(name != null) {
                    selection = DrugEntry.TABLE_NAME + "." + DrugEntry.COLUMN_NAME + " = ?";
                    selectionArgs = new String[]{name};
                }else {
                    String name_like = uri.getQueryParameter(DrugEntry.FILTER_NAME_LIKE);
                    String api_like = uri.getQueryParameter(DrugEntry.FILTER_API_LIKE);

                    if(name_like != null && api_like != null) {
                        selection = DrugEntry.TABLE_NAME + "." + DrugEntry.COLUMN_NAME + " LIKE ? OR " + DrugEntry.TABLE_NAME + "." + DrugEntry.COLUMN_API + " LIKE ?";
                        selectionArgs = new String[]{"%" + name_like + "%", "%" + api_like + "%"};
                    }else{
                        if (name_like != null) {
                            selection = DrugEntry.TABLE_NAME + "." + DrugEntry.COLUMN_NAME + " LIKE ?";
                            selectionArgs = new String[]{"%" + name_like + "%"};
                        }

                        if (api_like != null) {
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
            // "drug/#"
            case DRUG: {
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
            // "drug/#/package"
            case DRUG_PACKAGES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PackageEntry.TABLE_NAME,
                        projection,
                        PackageEntry.TABLE_NAME + "." + PackageEntry.COLUMN_DRUG + " = ?",
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "drug/#/prescription"
            case DRUG_PRESCTIPIONS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PrescriptionEntry.TABLE_NAME,
                        projection,
                        PrescriptionEntry.TABLE_NAME + "." + PrescriptionEntry.COLUMN_DRUG + " = ?",
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "package"
            case PACKAGES: {
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
            // "package/#"
            case PACKAGE: {
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
            // "prescription"
            case PRESCRIPTIONS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PrescriptionEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "prescription/#"
            case PRESCRIPTION: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PrescriptionEntry.TABLE_NAME,
                        projection,
                        PackageEntry.TABLE_NAME + "." + PackageEntry._ID + " = ?",
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
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case DRUGS: {
                long _id = db.insertWithOnConflict(DrugEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                if(_id < 0) {
                    String selection = DrugEntry.TABLE_NAME + "." + DrugEntry.COLUMN_NAME + " = ?";
                    String[] selectionArgs = new String[]{values.getAsString(DrugContract.DrugEntry.COLUMN_NAME)};

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
                returnUri = PackageEntry.buildPackageUri(_id);
                break;
            }
            case PRESCRIPTIONS: {
                long _id = db.insertWithOnConflict(PrescriptionEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                returnUri = PrescriptionEntry.buildPrescriptionUri(_id);
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
        Log.v(LOG_TAG, "Delete request " + uri.toString());

        if ( null == selection ) selection = "1";
        switch (match) {
            // "drug/#"
            case DRUG:
                rowsDeleted = db.delete(DrugEntry.TABLE_NAME,
                        DrugEntry.TABLE_NAME + "." + DrugEntry._ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)}
                );
                break;
            // "package/#"
            case PACKAGE:
                Cursor cursor = query(uri, new String[] {PackageEntry.COLUMN_IMAGE},null,null,null);
                Uri imageUri;
                while (cursor.moveToNext() && cursor.getString(0) != null) {
                    imageUri = Uri.parse(cursor.getString(0));
                    File imageFile = new File(imageUri.getPath());
                    imageFile.delete();
                }
                rowsDeleted = db.delete(PackageEntry.TABLE_NAME,
                        PackageEntry.TABLE_NAME + "." + PackageEntry._ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)}
                );
                break;
            // "package/#"
            case PRESCRIPTION:
                rowsDeleted = db.delete(PrescriptionEntry.TABLE_NAME,
                        PrescriptionEntry.TABLE_NAME + "." + PrescriptionEntry._ID + " = ?",
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
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case DRUGS:
                rowsUpdated = db.update(DrugEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case DRUG:
                rowsUpdated = db.update(DrugEntry.TABLE_NAME, values,
                        DrugEntry.TABLE_NAME + "." + DrugEntry._ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)});
                break;
            case PACKAGES:
                rowsUpdated = db.update(PackageEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case PACKAGE:
                rowsUpdated = db.update(PackageEntry.TABLE_NAME, values,
                        PackageEntry.TABLE_NAME + "." + PackageEntry._ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)});
                break;
            case PRESCRIPTIONS:
                rowsUpdated = db.update(PrescriptionEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case PRESCRIPTION:
                rowsUpdated = db.update(PrescriptionEntry.TABLE_NAME, values,
                        PrescriptionEntry.TABLE_NAME + "." + PrescriptionEntry._ID + " = ?",
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

