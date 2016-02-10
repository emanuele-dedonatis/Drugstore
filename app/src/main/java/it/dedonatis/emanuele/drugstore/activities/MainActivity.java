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
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.data.DataContract;
import it.dedonatis.emanuele.drugstore.fragments.DrugsListFragment;
import it.dedonatis.emanuele.drugstore.fragments.PharmaciesFragment;
import it.dedonatis.emanuele.drugstore.fragments.PrescriptionFragment;
import it.dedonatis.emanuele.drugstore.services.NotifyExpDate;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final static String LOG_TAG = MainActivity.class.getSimpleName();
    NavigationView mNavigationView;

    PharmaciesFragment mPharmaciesFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        populateDb();

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
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        int hourOfDay = 12;
        int repeatDays = 7;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * repeatDays, alarmIntent);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60000, alarmIntent);
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
