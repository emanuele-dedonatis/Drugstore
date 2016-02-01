package it.dedonatis.emanuele.drugstore.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Date;

import it.dedonatis.emanuele.drugstore.utils.DateUtils;

public class DataContract {

    /*
        content://it.drugstore.app/
    */
    public static final String CONTENT_AUTHORITY = "it.drugstore.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_DRUGS = "drugs";
    public static final String PATH_PACKAGES = "packages";
    public static final String PATH_SUBPACKAGES = "subpackages";
    public static final String PATH_THERAPIES = "therapies";
    public static final String PATH_ALARMS = "alarms";

    public static final String PARAM_DRUG_ID = "drug_id";
    public static final String PARAM_PACKAGE_ID = "package_id";
    public static final String PARAM_SUBPACKAGE_ID = "subpackage_id";
    public static final String PARAM_THERAPIES_ID = "therapies_id";
    public static final String PARAM_ALARMS_ID = "alarms_id";

    /*      ----------------
            |   DRUGS       |
            ----------------
            |   long id     |
            |   String name |
            |   String api  |
            ----------------
     */
    public static final class DrugEntry implements BaseColumns {
        public static final String TABLE_NAME = PATH_DRUGS;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_API = "api";

        public static final String PARAM_NAME = "name";
        public static final String PARAM_API = "api";
        public static final String PARAM_NAME_LIKE = "name_like";
        public static final String PARAM_API_LIKE = "api_like";


        /*
            content://it.drugstore.app/drugs
            content://it.drugstore.app/drugs/#
            ?name ?name_like ?api ?api_like
        */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DRUGS).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DRUGS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DRUGS;

        /*
            content://it.drugstore.app/drugs/<id>
        */
        public static Uri buildDrugUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildAlarmFromPackage(long packageId) {
            return  PackageEntry.CONTENT_URI.buildUpon().appendPath(Long.toString(packageId)).appendPath(PATH_ALARMS).build();
        }

        /*
            content://it.drugstore.app/drugs?name_like=<name>
        */
        public static Uri buildDrugLikeName(String name) {
            return  CONTENT_URI.buildUpon().appendQueryParameter(PARAM_NAME_LIKE, name).build();
        }

        /*
            content://it.drugstore.app/drugs?name=<name>
        */
        public static Uri buildDrugFromName(String name) {
            return  CONTENT_URI.buildUpon().appendQueryParameter(PARAM_NAME, name).build();
        }

        /*
            content://it.drugstore.app/drugs?api=<name>
        */
        public static Uri buildDrugFromApi(String api) {
            return  CONTENT_URI.buildUpon().appendQueryParameter(PARAM_API, api).build();
        }

        /*
            content://it.drugstore.app/drugs?api_like=<name>
        */
        public static Uri buildDrugLikeApi(String api) {
            return  CONTENT_URI.buildUpon().appendQueryParameter(PARAM_API_LIKE, api).build();
        }

        /*
            content://it.drugstore.app/drugs?name_like=<name>&api_like=<api>
        */
        public static Uri buildDrugLikeNameOrApi(String nameOrApi) {
            return  CONTENT_URI.buildUpon().appendQueryParameter(PARAM_NAME_LIKE, nameOrApi).appendQueryParameter(PARAM_API_LIKE, nameOrApi).build();
        }

    }

    /*      ------------------------
            |   PACKAGES            |
            ------------------------
            |   long id             |
            |   long drug_id        |
            |   String description  |
            |   int doses           |
            |   Uri image           |
            ------------------------
    */
    public static final class PackageEntry implements BaseColumns {
        public static final String TABLE_NAME = PATH_PACKAGES;
        public static final String COLUMN_DRUG_ID = PARAM_DRUG_ID;
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_DOSES = "doses";
        public static final String COLUMN_IMAGE_URI = "image_uri";

        public static final String PARAM_DESCRIPTION = "description";
        public static final String PARAM_DESCRIPTION_LIKE = "description_like";
        /*
            content://it.drugstore.app/packages
            content://it.drugstore.app/packages/#
            content://it.drugstore.app/drugs/#/packages
            ?description ?description_like
        */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PACKAGES).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PACKAGES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PACKAGES;


        /*
            content://it.drugstore.app/packages/<id>
        */
        public static Uri buildPackageUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /*
            content://it.drugstore.app/packages?description=<description>
        */
        public static Uri buildPackageFromDescription(String description) {
            return  CONTENT_URI.buildUpon().appendQueryParameter(PARAM_DESCRIPTION, description).build();
        }

        /*
            content://it.drugstore.app/packages?description_like=<description>
        */
        public static Uri buildDrugLikeName(String description) {
            return  CONTENT_URI.buildUpon().appendQueryParameter(PARAM_DESCRIPTION_LIKE, description).build();
        }

        /*
            content://it.drugstore.app/drugs/#/packages
        */
        public static Uri buildPackagesFromDrug(long drugId) {
            return  DrugEntry.CONTENT_URI.buildUpon().appendPath(Long.toString(drugId)).appendPath(PATH_PACKAGES).build();
        }
    }

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
    public static final class SubpackageEntry implements BaseColumns {
        public static final String TABLE_NAME = PATH_SUBPACKAGES;
        public static final String COLUMN_DRUG_ID = PARAM_DRUG_ID;
        public static final String COLUMN_PACKAGE_ID = PARAM_PACKAGE_ID;
        public static final String COLUMN_DOSES_LEFT = "doses_left";
        public static final String COLUMN_EXP_DATE = "exp_date";

        public static final String PARAM_DOSE_LEFT_UNDER = "dose_left_under";
        public static final String PARAM_EXP_BEFORE = "exp_before";


        /*
            content://it.drugstore.app/subpackages
            content://it.drugstore.app/subpackages/#
            content://it.drugstore.app/drugs/#/subpackages
            content://it.drugstore.app/packages/#/subpackages
            ?dose_left_under ?exp_before

        */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUBPACKAGES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBPACKAGES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBPACKAGES;

        /*
            content://it.drugstore.app/subpackages/<id>
        */
        public static Uri buildSubpackageUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /*
          content://it.drugstore.app/subpackages?dose_left_under=<maxDoseLeft>
        */
        public static Uri buildSubpackagesWithDoseLeftUnder(int maxDoseLeft) {
            return  CONTENT_URI.buildUpon().appendQueryParameter(PARAM_DOSE_LEFT_UNDER, Integer.toString(maxDoseLeft)).build();
        }

        /*
            content://it.drugstore.app/subpackages?exp_before=<yyyyMMdd>
        */
        public static Uri buildSubpackagesWithExpBefore(Date maxDate) {
            return  CONTENT_URI.buildUpon().appendQueryParameter(PARAM_EXP_BEFORE, DateUtils.fromDateToDbString(maxDate)).build();
        }

        /*
            content://it.drugstore.app/drugs/#/subpackages
        */
        public static Uri buildSubackagesFromDrug(long drugId) {
            return  DrugEntry.CONTENT_URI.buildUpon().appendPath(Long.toString(drugId)).appendPath(PATH_SUBPACKAGES).build();
        }

        /*
            content://it.drugstore.app/packages/#/subpackages
        */
        public static Uri buildSubackagesFromPackage(long packageId) {
            return  PackageEntry.CONTENT_URI.buildUpon().appendPath(Long.toString(packageId)).appendPath(PATH_SUBPACKAGES).build();
        }
    }

    /*    ------------------------------
          |   THERAPIES                 |
          ------------------------------
          |   long id                   |
          |   long package_id           |
          |   int doses                 |
          |   int exp_date (yyyyMMdd)   |
          ------------------------------
    */
    public static final class TherapyEntry implements BaseColumns {
        public static final String TABLE_NAME = PATH_THERAPIES;
        public static final String COLUMN_PACKAGE_ID = "package_id";
        public static final String COLUMN_DOSE = "dose";
        public static final String COLUMN_EXP_DATE = "exp_date";

        /*
            content://it.drugstore.app/therapies
            content://it.drugstore.app/therapies/#
            content://it.drugstore.app/packages/#/therapies
        */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_THERAPIES).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_THERAPIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_THERAPIES;

        /*
            content://it.drugstore.app/therapies/<id>
        */
        public static Uri buildTherapyUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /*
            content://it.drugstore.app/packages/#/therapies
        */
        public static Uri buildTherapiesFromPackage(long packageId) {
            return  PackageEntry.CONTENT_URI.buildUpon().appendPath(Long.toString(packageId)).appendPath(PATH_THERAPIES).build();
        }
    }

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
    public static final class AlarmEntry implements BaseColumns {
        public static final String TABLE_NAME = PATH_ALARMS;
        public static final String COLUMN_THERAPY_ID = "therapy_id";
        public static final String COLUMN_DAY_OF_WEEK = "day_of_week";
        public static final String COLUMN_HOUR_OF_DAY = "hour_of_day";
        public static final String COLUMN_MINUTE = "minute";


        /*
            content://it.drugstore.app/alarms
            content://it.drugstore.app/alarms/#
            content://it.drugstore.app/therapies/#/alarms
        */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ALARMS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ALARMS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ALARMS;


        /*
            content://it.drugstore.app/alarms/#
        */
        public static Uri buildAlarmUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /*
            content://it.drugstore.app/therapies/#/alarms
        */
        public static Uri buildAlarmsFromTherapy(long therapyId) {
            return  TherapyEntry.CONTENT_URI.buildUpon().appendPath(Long.toString(therapyId)).appendPath(PATH_ALARMS).build();
        }
    }

}