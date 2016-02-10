package it.dedonatis.emanuele.drugstore.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.activities.PackagesActivity;
import it.dedonatis.emanuele.drugstore.data.DataContract;
import it.dedonatis.emanuele.drugstore.utils.ColorUtils;
import it.dedonatis.emanuele.drugstore.utils.DateUtils;

public class NotifyExpDate extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    private static final int NEXT_DAYS = 7;

    private static final String[] DRUG_COLUMNS = {
            DataContract.DrugEntry.TABLE_NAME + "." + DataContract.DrugEntry._ID,
            DataContract.DrugEntry.COLUMN_NAME,
            DataContract.DrugEntry.COLUMN_API
    };
    public static final int COL_DRUG_ID = 0;
    public static final int COL_DRUG_NAME = 1;
    public static final int COL_DRUG_API = 2;

    private static final String[] PACKAGE_COLUMNS = {
            DataContract.PackageEntry.TABLE_NAME + "." + DataContract.PackageEntry._ID,
            DataContract.PackageEntry.COLUMN_DESCRIPTION
    };
    public static final int COL_PACKAGE_ID = 0;
    public static final int COL_PACKAGE_DESCRIPTION = 1;

    private static final String[] SUBPACKAGE_COLUMNS = {
            DataContract.SubpackageEntry.TABLE_NAME + "." + DataContract.SubpackageEntry._ID,
            DataContract.SubpackageEntry.COLUMN_DRUG_ID,
            DataContract.SubpackageEntry.COLUMN_PACKAGE_ID,
            DataContract.SubpackageEntry.COLUMN_EXP_DATE
    };

    public static final int COL_SUBPACKAGE_ID = 0;
    public static final int COL_SUBPACKAGE_DRUG_ID = 1;
    public static final int COL_SUBPACKAGE_PACKAGE_ID = 2;
    public static final int COL_SUBPACKAGE_EXP_DATE = 3;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("MUMU", "Ho ricevuto l'alarm");
        Cursor subpackageCursor = context.getContentResolver().query(DataContract.SubpackageEntry.CONTENT_URI,
                SUBPACKAGE_COLUMNS,
                null,
                null,
                DataContract.SubpackageEntry.COLUMN_EXP_DATE + " ASC"
        );

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Calendar today = Calendar.getInstance();
        today.setTime(new Date());

        Calendar nextDays = Calendar.getInstance();
        nextDays.setTime(new Date());
        nextDays.add(Calendar.DATE, NEXT_DAYS);

        while (subpackageCursor.moveToNext()) {
            long subpackageId = subpackageCursor.getLong(COL_SUBPACKAGE_ID);
            long packageId = subpackageCursor.getLong(COL_SUBPACKAGE_PACKAGE_ID);
            long drugId = subpackageCursor.getLong(COL_SUBPACKAGE_DRUG_ID);

            String dbExpDate = subpackageCursor.getString(COL_SUBPACKAGE_EXP_DATE);
            Calendar expDate = Calendar.getInstance();
            expDate.setTime(DateUtils.fromDbStringToDate(dbExpDate));
            if(expDate.before(nextDays) && expDate.after(today)) {
                // Get package name
                Cursor packageCursor = context.getContentResolver().query(DataContract.PackageEntry.buildPackageUri(packageId),
                        PACKAGE_COLUMNS,
                        null,
                        null,
                        null);
                String packageName = "";
                while(packageCursor.moveToNext())
                    packageName = packageCursor.getString(COL_PACKAGE_DESCRIPTION);

                // Get drug name and api
                Cursor drugCursor = context.getContentResolver().query(DataContract.DrugEntry.buildDrugUri(drugId),
                        DRUG_COLUMNS,
                        null,
                        null,
                        null);

                String drugName = "";
                String drugApi = "";
                while(drugCursor.moveToNext()) {
                    drugName = drugCursor.getString(COL_DRUG_NAME);
                    drugApi = drugCursor.getString(COL_DRUG_API);
                }

                Intent drugIntent = new Intent(context, PackagesActivity.class);
                drugIntent.putExtra(PackagesActivity.MESSAGE_DRUG_ID, drugId);
                drugIntent.putExtra(PackagesActivity.MESSAGE_DRUG_NAME, drugName);
                drugIntent.putExtra(PackagesActivity.MESSAGE_DRUG_API, drugApi);

                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                context,
                                (int)subpackageId,
                                drugIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                Notification.Builder builder = new Notification.Builder(context);
                builder.setContentTitle(drugName + " " + context.getString(R.string.is_expiring));
                builder.setContentText(packageName + " [" + DateUtils.fromDbStringToEurString(dbExpDate) + "]");
                builder.setSmallIcon(R.drawable.ic_stat_medical15);
                builder.setContentIntent(resultPendingIntent);
                builder.setAutoCancel(true);
                Notification notification = builder.build();
                notificationManager.notify((int)subpackageId, notification);


                packageCursor.close();
                drugCursor.close();
            }
        }
    }
}
