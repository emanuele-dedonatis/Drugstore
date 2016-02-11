package it.dedonatis.emanuele.drugstore.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.data.DataContract;
import it.dedonatis.emanuele.drugstore.data.DataDbHelper;
import it.dedonatis.emanuele.drugstore.fragments.DrugsListFragment;
import it.dedonatis.emanuele.drugstore.fragments.PharmaciesFragment;
import it.dedonatis.emanuele.drugstore.fragments.PrescriptionFragment;
import it.dedonatis.emanuele.drugstore.services.NotifyExpDate;
import it.dedonatis.emanuele.drugstore.utils.DateUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final static String LOG_TAG = MainActivity.class.getSimpleName();
    final static int GET_FILE = 0;
    NavigationView mNavigationView;

    PharmaciesFragment mPharmaciesFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //populateDb();

        // Navigation Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        // Main Fragment
        getSupportActionBar().setTitle(getString(R.string.drugs));
        DrugsListFragment drugsListFragment = DrugsListFragment.newInstance();
        getFragmentManager().beginTransaction().replace(R.id.activity_drugs_container, drugsListFragment).commit();

        // Create ALARM to check exp date
        scheduleNotification();

    }

    private void scheduleNotification() {
        Intent notificationIntent = new Intent(this, NotifyExpDate.class);
        notificationIntent.putExtra(NotifyExpDate.NOTIFICATION_ID, 1);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int hourOfDay = Integer.valueOf(preferences.getString(getString(R.string.pref_hour_of_day_notify), "9"));
        int dayOfWeek = Integer.valueOf(preferences.getString(getString(R.string.pref_days_notify), "0"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        if (weekday != Calendar.MONDAY)
        {
            // set next monday
            int days = (Calendar.SATURDAY - weekday + 2) % 7;
            calendar.add(Calendar.DAY_OF_YEAR, days);
        }
        calendar.add(Calendar.DAY_OF_YEAR, dayOfWeek - 7);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        boolean isDebug = preferences.getBoolean(getString(R.string.pref_debug), false);
        if(isDebug)
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 30000, alarmIntent);
        else
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, alarmIntent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /***** NAVIGATION DRAWER *****/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_drugs:
                getSupportActionBar().setTitle(getString(R.string.drugs));
                DrugsListFragment drugsListFragment = DrugsListFragment.newInstance();
                getFragmentManager().beginTransaction().replace(R.id.activity_drugs_container, drugsListFragment).commit();
                break;

            case R.id.nav_pharmacies:
                getSupportActionBar().setTitle(getString(R.string.pharmacies));
                mPharmaciesFragment = PharmaciesFragment.newInstance();
                getFragmentManager().beginTransaction().replace(R.id.activity_drugs_container, mPharmaciesFragment).commit();
                break;

            /*
            case R.id.nav_prescriptions:
                getSupportActionBar().setTitle(getString(R.string.prescriptions));
                PrescriptionFragment prescriptionFragment = PrescriptionFragment.newInstance();
                getFragmentManager().beginTransaction().replace(R.id.activity_drugs_container, prescriptionFragment).commit();
                break;
            */
            case R.id.nav_settings:
                /*
                title.setText(getString(R.string.settings));
                getFragmentManager().beginTransaction().replace(R.id.activity_drugs_container, new SettingsFragment()).commit();
        */
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_export_db:
                DataDbHelper dbHelper = new DataDbHelper(this);
                try {
                    Date today = new Date();
                    File filePath = dbHelper.backupDatabase("drugstore_" + DateUtils.fromDateToDbString(today) +
                            "_" + DateUtils.fromDateToDbHourString(today) + ".db");
                    if(filePath!=null) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(filePath));
                        sendIntent.setType("*/*");
                        startActivity(sendIntent);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.nav_import_db:
                // This always works
                Intent i = new Intent(this, FilePickerActivity.class);
                // This works if you defined the intent filter
                // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

                // Set these depending on your use case. These are the defaults.
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

                // Configure initial directory by specifying a String.
                // You could specify a String like "/storage/emulated/0/", but that can
                // dangerous. Always use Android's API calls to get paths to the SD-card or
                // internal memory.
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

                startActivityForResult(i, GET_FILE);
                break;
            default:
                break;
        }

        getSupportFragmentManager().executePendingTransactions();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    /***** FRAGMENTS METHODS *****/
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case PharmaciesFragment.REQUEST_LOCATION_SERVICES:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.v(LOG_TAG, "User agreed to make required location settings changes.");
                        mPharmaciesFragment.startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.v(LOG_TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
            case GET_FILE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Uri uri = intent.getData();
                        DataDbHelper dataDbHelper = new DataDbHelper(this);
                        try {
                            dataDbHelper.importDatabase(uri.getPath());
                        } catch (IOException e) {
                            Toast.makeText(this, getString(R.string.alert_file_error), Toast.LENGTH_SHORT).show();
                            return;
                        } catch (ParseException e) {
                            Toast.makeText(this, getString(R.string.alert_file_error), Toast.LENGTH_SHORT).show();
                            return;
                        }

                }
        }
    }

    private void populateDb() {
        Cursor data = getContentResolver().query(
                DataContract.AlarmEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        if(data.getCount()==0) {

            ContentValues drug = new ContentValues();
            drug.put(DataContract.DrugEntry.COLUMN_NAME, "Montelukast Teva");
            drug.put(DataContract.DrugEntry.COLUMN_API, "Montelukast");
            Uri uri = getContentResolver().insert(
                    DataContract.DrugEntry.CONTENT_URI,
                    drug
            );
            long drugId = ContentUris.parseId(uri);
            Log.v(LOG_TAG, "New drug id " + drugId);

            ContentValues pkg = new ContentValues();
            pkg.put(DataContract.PackageEntry.COLUMN_DRUG_ID, drugId);
            pkg.put(DataContract.PackageEntry.COLUMN_DESCRIPTION, "Compresse");
            pkg.put(DataContract.PackageEntry.COLUMN_DOSES, "30");
            uri = getContentResolver().insert(
                    DataContract.PackageEntry.CONTENT_URI,
                    pkg
            );
            long packId = ContentUris.parseId(uri);
            Log.v(LOG_TAG, "New package id " + packId);

            ContentValues subpack = new ContentValues();
            subpack.put(DataContract.SubpackageEntry.COLUMN_DRUG_ID, drugId);
            subpack.put(DataContract.SubpackageEntry.COLUMN_PACKAGE_ID, packId);
            subpack.put(DataContract.SubpackageEntry.COLUMN_DOSES_LEFT, 22);
            subpack.put(DataContract.SubpackageEntry.COLUMN_EXP_DATE, 20161102);
            uri = getContentResolver().insert(
                    DataContract.SubpackageEntry.CONTENT_URI,
                    subpack
            );
            long subId = ContentUris.parseId(uri);
            Log.v(LOG_TAG, "New subpackage id " + subId);

            ContentValues theraphy = new ContentValues();
            theraphy.put(DataContract.TherapyEntry.COLUMN_DOSE, 1);
            theraphy.put(DataContract.TherapyEntry.COLUMN_PACKAGE_ID, packId);
            theraphy.put(DataContract.TherapyEntry.COLUMN_EXP_DATE, 20160302);
            uri = getContentResolver().insert(
                    DataContract.TherapyEntry.CONTENT_URI,
                    theraphy
            );
            long therId = ContentUris.parseId(uri);
            Log.v(LOG_TAG, "New therapy id " + therId);

            ContentValues allarm = new ContentValues();
            allarm.put(DataContract.AlarmEntry.COLUMN_THERAPY_ID, therId);
            allarm.put(DataContract.AlarmEntry.COLUMN_DAY_OF_WEEK, Calendar.TUESDAY);
            allarm.put(DataContract.AlarmEntry.COLUMN_HOUR_OF_DAY, 16);
            allarm.put(DataContract.AlarmEntry.COLUMN_MINUTE, 25);
            uri = getContentResolver().insert(
                    DataContract.AlarmEntry.CONTENT_URI,
                    allarm
            );
            long allarId = ContentUris.parseId(uri);
            Log.v(LOG_TAG, "New allarm id " + allarId);

            // second package
            pkg = new ContentValues();
            pkg.put(DataContract.PackageEntry.COLUMN_DRUG_ID, drugId);
            pkg.put(DataContract.PackageEntry.COLUMN_DESCRIPTION, "Bustine effervescenti");
            pkg.put(DataContract.PackageEntry.COLUMN_DOSES, "30");
            uri = getContentResolver().insert(
                    DataContract.PackageEntry.CONTENT_URI,
                    pkg
            );
            packId = ContentUris.parseId(uri);
            Log.v(LOG_TAG, "New package id " + packId);

            subpack = new ContentValues();
            subpack.put(DataContract.SubpackageEntry.COLUMN_DRUG_ID, drugId);
            subpack.put(DataContract.SubpackageEntry.COLUMN_PACKAGE_ID, packId);
            subpack.put(DataContract.SubpackageEntry.COLUMN_DOSES_LEFT, 10);
            subpack.put(DataContract.SubpackageEntry.COLUMN_EXP_DATE, 20160811);
            uri = getContentResolver().insert(
                    DataContract.SubpackageEntry.CONTENT_URI,
                    subpack
            );
            subId = ContentUris.parseId(uri);
            Log.v(LOG_TAG, "New subpackage id " + subId);

            subpack = new ContentValues();
            subpack.put(DataContract.SubpackageEntry.COLUMN_DRUG_ID, drugId);
            subpack.put(DataContract.SubpackageEntry.COLUMN_PACKAGE_ID, packId);
            subpack.put(DataContract.SubpackageEntry.COLUMN_DOSES_LEFT, 28);
            subpack.put(DataContract.SubpackageEntry.COLUMN_EXP_DATE, 20160316);
            uri = getContentResolver().insert(
                    DataContract.SubpackageEntry.CONTENT_URI,
                    subpack
            );
            subId = ContentUris.parseId(uri);
            Log.v(LOG_TAG, "New subpackage id " + subId);
        }

    }
}
