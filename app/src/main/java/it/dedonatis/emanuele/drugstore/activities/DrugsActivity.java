package it.dedonatis.emanuele.drugstore.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.transition.Explode;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.fragments.DrugDetailFragment;
import it.dedonatis.emanuele.drugstore.fragments.DrugsListFragment;

public class DrugsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DrugsListFragment.OnDrugSelectionListener {

    final static String LOG_TAG = DrugsActivity.class.getSimpleName();

    public final static String MESSAGE_DRUG_ID = DrugsActivity.class.getSimpleName() + ".DRUG_ID";
    public final static String MESSAGE_DRUG_NAME = DrugsActivity.class.getSimpleName() + ".DRUG_NAME";
    public final static String MESSAGE_DRUG_API = DrugsActivity.class.getSimpleName() + ".DRUG_API";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drugs);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // Navigation Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Main Fragment
        DrugsListFragment drugsListFragment = DrugsListFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_drugs_container, drugsListFragment).commit();

        // Fab
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DrugsActivity.this, NewDrugActivity.class);
                startActivity(intent);
            }
        });

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

        if (id == R.id.nav_drugs) {
        } else if (id == R.id.nav_pharmacies) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /***** FRAGMENTS METHODS *****/
    @Override
    public void onDrugSelected(View nameView, View apiView, View colorView, long id, String name, String api) {
        Intent intent = new Intent(this, DrugDetailActivity.class);
        intent.putExtra(MESSAGE_DRUG_ID, id);
        intent.putExtra(MESSAGE_DRUG_NAME, name);
        intent.putExtra(MESSAGE_DRUG_API, api);
        startActivity(intent);
    }
}
