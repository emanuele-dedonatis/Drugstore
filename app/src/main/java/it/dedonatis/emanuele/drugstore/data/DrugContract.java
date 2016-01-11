package it.dedonatis.emanuele.drugstore.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DrugContract {

    public static final String CONTENT_AUTHORITY = "it.drugstore.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_DRUG = "drug";
    public static final String PATH_PACKAGE = "package";

    public static final class DrugEntry implements BaseColumns {
        public static final String TABLE_NAME = "drugs";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_API = "api";
        public static final String COLUMN_NEED_PRESCRIPTION = "need_prescription";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DRUG).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DRUG;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DRUG;

        public static Uri buildDrugUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildDrugFromName(String name) {
            return  CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_NAME, name).build();
        }

        public static Uri buildDrugFromApi(String api) {
            return  CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_API, api).build();
        }

    }

    public static final class PackageEntry implements BaseColumns {
        public static final String TABLE_NAME = "packages";
        public static final String COLUMN_DRUG = "drug";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_UNITS = "units";
        public static final String COLUMN_IS_PERCENTAGE = "is_percentage";
        public static final String COLUMN_EXPIRATION_DATE = "expiration_date";
        public static final String COLUMN_IMAGE = "image_uri";


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PACKAGE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PACKAGE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PACKAGE;

        public static Uri buildPackageUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildPackagesFromDrug(long drugId) {
            return  DrugEntry.CONTENT_URI.buildUpon().appendPath(Long.toString(drugId)).appendPath(PATH_PACKAGE).build();
        }
    }

}