package it.dedonatis.emanuele.drugstore.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import it.dedonatis.emanuele.drugstore.data.DataContract;
import it.dedonatis.emanuele.drugstore.holders.PackageTreeHolder;
import it.dedonatis.emanuele.drugstore.models.DrugPackage;
import it.dedonatis.emanuele.drugstore.models.DrugSubpackage;

public class ShareUtils {

    private static final String[] PACKAGE_COLUMNS = {
            DataContract.PackageEntry.TABLE_NAME + "." + DataContract.PackageEntry._ID,
            DataContract.PackageEntry.COLUMN_DESCRIPTION
    };
    public static final int COL_PACKAGE_ID = 0;
    public static final int COL_PACKAGE_DESCRIPTION = 1;


    private static final String[] SUBPACKAGE_COLUMNS = {
            DataContract.SubpackageEntry.TABLE_NAME + "." + DataContract.SubpackageEntry._ID,
            DataContract.SubpackageEntry.COLUMN_DOSES_LEFT
    };

    public static final int COL_SUBPACKAGE_DOSES_LEFT = 1;

    public static void shareDrug(Context context, long drugId, String name, String api) {
        String textToSend = name + " (" + api + ") ";

        Cursor packageCursor = context.getContentResolver().query(
                DataContract.PackageEntry.buildPackagesFromDrug(drugId),
                PACKAGE_COLUMNS,
                null,
                null,
                DataContract.PackageEntry.COLUMN_DESCRIPTION + " ASC");

        while (packageCursor.moveToNext()) {
            long packageId = packageCursor.getLong(COL_PACKAGE_ID);

            Cursor subpackageCursor = context.getContentResolver().query(
                    DataContract.SubpackageEntry.buildSubackagesFromPackage(packageId),
                    SUBPACKAGE_COLUMNS,
                    null,
                    null,
                    null
            );

            int dosesLeft = 0;

            while(subpackageCursor.moveToNext()) {
                dosesLeft += subpackageCursor.getInt(COL_SUBPACKAGE_DOSES_LEFT);
            }


            String line = "- " + dosesLeft + " x " + packageCursor.getString(COL_PACKAGE_DESCRIPTION);
            textToSend += "\n" + line;
        }
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textToSend);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    public static void shareDrugPackage(Context context, String drugName, String drugApi, DrugPackage drugPackage) {
        String textToSend = drugName + " (" + drugApi + ") ";
        String packageDescription = drugPackage.getDescription();
        for (DrugSubpackage subpackage: drugPackage.getSubpackages()) {
            textToSend += "\n- " + subpackage.getDosesLeft() + " x " + packageDescription + " [" + DateUtils.fromDateToEurString(subpackage.getExpirationDate()) + "]";
        }
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textToSend);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    public static void shareDrugSubpackage(Context context, String drugName, String drugApi, DrugPackage drugPackage, DrugSubpackage subpackage) {
        String textToSend = drugName + " (" + drugApi + ") ";
        String packageDescription = drugPackage.getDescription();
        textToSend += "\n- " + subpackage.getDosesLeft() + " x " + packageDescription + " [" + DateUtils.fromDateToEurString(subpackage.getExpirationDate()) + "]";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textToSend);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

}
