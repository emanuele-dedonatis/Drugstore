package it.dedonatis.emanuele.drugstore.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.fragments.DrugsListFragment;
import it.dedonatis.emanuele.drugstore.fragments.PharmaciesFragment;
import it.dedonatis.emanuele.drugstore.fragments.PrescriptionFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final static String LOG_TAG = MainActivity.class.getSimpleName();


    NavigationView mNavigationView;

    PharmaciesFragment mPharmaciesFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //populateDb();

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // Navigation Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        // Main Fragment
        DrugsListFragment drugsListFragment = DrugsListFragment.newInstance();
        getFragmentManager().beginTransaction().replace(R.id.activity_drugs_container, drugsListFragment).commit();
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
                getActionBar().setTitle(getString(R.string.drugs));
                DrugsListFragment drugsListFragment = DrugsListFragment.newInstance();
                getFragmentManager().beginTransaction().replace(R.id.activity_drugs_container, drugsListFragment).commit();
                break;

            case R.id.nav_pharmacies:
                getActionBar().setTitle(getString(R.string.pharmacies));
                mPharmaciesFragment = PharmaciesFragment.newInstance();
                getFragmentManager().beginTransaction().replace(R.id.activity_drugs_container, mPharmaciesFragment).commit();
                break;

            case R.id.nav_prescriptions:
                getActionBar().setTitle(getString(R.string.prescriptions));
                PrescriptionFragment prescriptionFragment = PrescriptionFragment.newInstance();
                getFragmentManager().beginTransaction().replace(R.id.activity_drugs_container, prescriptionFragment).commit();
                break;

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
                if (id == R.id.nav_drugs) {} else if (id == R.id.nav_pharmacies) {
} else if (id == R.id.nav_prescriptions) {
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

}
