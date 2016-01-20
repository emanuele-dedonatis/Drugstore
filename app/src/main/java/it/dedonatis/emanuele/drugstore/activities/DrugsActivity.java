package it.dedonatis.emanuele.drugstore.activities;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.data.DrugContract;
import it.dedonatis.emanuele.drugstore.fragments.DrugsListFragment;

import static it.dedonatis.emanuele.drugstore.utils.Images.createImageFile;
import static it.dedonatis.emanuele.drugstore.utils.Images.saveToInternalSorage;
import static it.dedonatis.emanuele.drugstore.utils.Images.scaleDown;

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

        // Populate database if empty
        Cursor cursor = getContentResolver().query(DrugContract.DrugEntry.CONTENT_URI,null,null,null,null);
        if(cursor.getCount() == 0)
            populateDb();

        // Fab
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DrugsActivity.this, AddDrugActivity.class);
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

    private void populateDb() {
        // TACHIPIRINA
        long id_drug1 = 1;
        ContentValues drug1 = new ContentValues();
        drug1.put(DrugContract.DrugEntry._ID, id_drug1);
        drug1.put(DrugContract.DrugEntry.COLUMN_NAME, "Tachipirina");
        drug1.put(DrugContract.DrugEntry.COLUMN_API, "Paracetamolo");
        drug1.put(DrugContract.DrugEntry.COLUMN_NEED_PRESCRIPTION, 0);
        getContentResolver().insert(
                DrugContract.DrugEntry.CONTENT_URI,
                drug1
        );

        // TACHIPIRINA - Granulato
        long id_pkg1 = 1;
        ContentValues pkg1 = new ContentValues();
        pkg1.put(DrugContract.PackageEntry._ID, id_pkg1);
        pkg1.put(DrugContract.PackageEntry.COLUMN_DRUG, id_drug1);
        pkg1.put(DrugContract.PackageEntry.COLUMN_DESCRIPTION, "Granulato effervescente");
        pkg1.put(DrugContract.PackageEntry.COLUMN_UNITS, 20);
        pkg1.put(DrugContract.PackageEntry.COLUMN_IS_PERCENTAGE, false);
        pkg1.put(DrugContract.PackageEntry.COLUMN_EXPIRATION_DATE, 20160303);
        try {
            Uri photoUri = createImageFile(this);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tachipirina_granulato);
            Bitmap thumb = scaleDown(bitmap, 600, true);
            saveToInternalSorage(thumb, photoUri);
            pkg1.put(DrugContract.PackageEntry.COLUMN_IMAGE, photoUri.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        AsyncQueryHandler queryHandler = new AsyncQueryHandler(getContentResolver()) {
        };
        queryHandler.startInsert(0, null,
                DrugContract.PackageEntry.CONTENT_URI,
                pkg1
        );

        // TACHIPIRINA - Compresse
        long id_pkg2 = 2;
        ContentValues pkg2 = new ContentValues();
        pkg2.put(DrugContract.PackageEntry._ID, id_pkg2);
        pkg2.put(DrugContract.PackageEntry.COLUMN_DRUG, id_drug1);
        pkg2.put(DrugContract.PackageEntry.COLUMN_DESCRIPTION, "Compresse");
        pkg2.put(DrugContract.PackageEntry.COLUMN_UNITS, 20);
        pkg2.put(DrugContract.PackageEntry.COLUMN_IS_PERCENTAGE, false);
        pkg2.put(DrugContract.PackageEntry.COLUMN_EXPIRATION_DATE, 20160412);
        try {
            Uri photoUri = createImageFile(this);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tachipirina_compresse);
            Bitmap thumb = scaleDown(bitmap, 600, true);
            saveToInternalSorage(thumb, photoUri);
            pkg2.put(DrugContract.PackageEntry.COLUMN_IMAGE, photoUri.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        queryHandler.startInsert(0, null,
                DrugContract.PackageEntry.CONTENT_URI,
                pkg2
        );

        // OKI
        long id_drug2 = 2;
        ContentValues drug2 = new ContentValues();
        drug2.put(DrugContract.DrugEntry._ID, id_drug2);
        drug2.put(DrugContract.DrugEntry.COLUMN_NAME, "OKi");
        drug2.put(DrugContract.DrugEntry.COLUMN_API, "Ketoprofene");
        drug2.put(DrugContract.DrugEntry.COLUMN_NEED_PRESCRIPTION, 0);
        getContentResolver().insert(
                DrugContract.DrugEntry.CONTENT_URI,
                drug2
        );

        // OKi - Granulato
        long id_pkg3 = 3;
        ContentValues pkg3 = new ContentValues();
        pkg3.put(DrugContract.PackageEntry._ID, id_pkg3);
        pkg3.put(DrugContract.PackageEntry.COLUMN_DRUG, id_drug2);
        pkg3.put(DrugContract.PackageEntry.COLUMN_DESCRIPTION, "Granulato per soluzione orale");
        pkg3.put(DrugContract.PackageEntry.COLUMN_UNITS, 30);
        pkg3.put(DrugContract.PackageEntry.COLUMN_IS_PERCENTAGE, false);
        pkg3.put(DrugContract.PackageEntry.COLUMN_EXPIRATION_DATE, 20160518);
        try {
            Uri photoUri = createImageFile(this);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.oki);
            Bitmap thumb = scaleDown(bitmap, 600, true);
            saveToInternalSorage(thumb, photoUri);
            pkg3.put(DrugContract.PackageEntry.COLUMN_IMAGE, photoUri.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        queryHandler.startInsert(0, null,
                DrugContract.PackageEntry.CONTENT_URI,
                pkg3
        );

        // VIVINC
        long id_drug3 = 3;
        ContentValues drug3 = new ContentValues();
        drug3.put(DrugContract.DrugEntry._ID, id_drug3);
        drug3.put(DrugContract.DrugEntry.COLUMN_NAME, "VIVIN C");
        drug3.put(DrugContract.DrugEntry.COLUMN_API, "Acido Acetilsalicilico + Acido Ascorbico");
        drug3.put(DrugContract.DrugEntry.COLUMN_NEED_PRESCRIPTION, 0);
        getContentResolver().insert(
                DrugContract.DrugEntry.CONTENT_URI,
                drug3
        );

        // OKi - Granulato
        long id_pkg4 = 4;
        ContentValues pkg4 = new ContentValues();
        pkg4.put(DrugContract.PackageEntry._ID, id_pkg4);
        pkg4.put(DrugContract.PackageEntry.COLUMN_DRUG, id_drug3);
        pkg4.put(DrugContract.PackageEntry.COLUMN_DESCRIPTION, "Comrpesse effervescenti");
        pkg4.put(DrugContract.PackageEntry.COLUMN_UNITS, 20);
        pkg4.put(DrugContract.PackageEntry.COLUMN_IS_PERCENTAGE, false);
        pkg4.put(DrugContract.PackageEntry.COLUMN_EXPIRATION_DATE, 20160129);
        try {
            Uri photoUri = createImageFile(this);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.vivinc);
            Bitmap thumb = scaleDown(bitmap, 600, true);
            saveToInternalSorage(thumb, photoUri);
            pkg4.put(DrugContract.PackageEntry.COLUMN_IMAGE, photoUri.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        queryHandler.startInsert(0, null,
                DrugContract.PackageEntry.CONTENT_URI,
                pkg4
        );
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
