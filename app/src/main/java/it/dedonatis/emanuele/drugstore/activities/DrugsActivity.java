package it.dedonatis.emanuele.drugstore.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.fragments.DrugDetailFragment;
import it.dedonatis.emanuele.drugstore.fragments.DrugsListFragment;

public class DrugsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DrugsListFragment.OnDrugSelectionListener {
    final static String LOG_TAG = DrugsActivity.class.getSimpleName();
    final static String API_BASE_URL = "http://opendatasalutedata.cloudapp.net";

    public final static String MESSAGE_DRUG_ID = DrugsActivity.class.getSimpleName() + ".DRUG_ID";

    boolean mDualPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drugs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setupDrugsView();

        //setupPharmaciesView();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (savedInstanceState != null) {
            return;
        } else {
            DrugsListFragment drugsListFragment = DrugsListFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.activity_drugs_container, drugsListFragment).commit();
        }

    }

    /*
    private void setupDrugsView() {
        final List<Drug> drugs = new ArrayList<Drug>();
        drugs.add(new Drug("Aspirina"));
        drugs.add(new Drug("Tachipirina"));
        drugs.add(new Drug("Montelukast"));
        drugs.add(new Drug("Revinty"));

        ListView drugsListView = (ListView)findViewById(R.id.drugs_listView);

        drugsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Drug drug = drugs.get(position);

                AlertDialog.Builder alertDialogBuilder =
                        new AlertDialog.Builder(DrugsActivity.this);

                CharSequence message = Html.fromHtml(
                        String.format(getResources().getString(R.string.click_on_movie),
                                drug.getName(),
                                drug.getPackages().size() + ""));

                alertDialogBuilder
                        .setTitle(R.string.hello_user)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();

            }
        });
        DrugArrayAdapters drugArrayAdapters = new DrugArrayAdapters(this, drugs);
        drugsListView.setAdapter(drugArrayAdapters);
    }


    private void setupPharmaciesView() {

        final ProgressDialog progressDialog =
                ProgressDialog.show(this,
                        getResources().getString(R.string.wait),
                        getResources().getString(R.string.loading_pharmacies), true, false);

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();


        PharmacyRestService restService = retrofit.create(PharmacyRestService.class);

        String descrizionecomune = "CHIETI";
        restService.getPharmacies("descrizionecomune eq '"+ descrizionecomune + "'","json").enqueue(
                new Callback<Pharmacies>() {

                        @Override
                        public void onResponse(Response<Pharmacies> response,
                                               Retrofit retrofit) {
                            if(response.isSuccess()) {
                                Pharmacies pharmacies = response.body();
                                Log.v(LOG_TAG, pharmacies.size() + "");
                                for(int i = 0; i < pharmacies.size(); i++)
                                    Log.v(LOG_TAG, i + ": " + pharmacies.get(i).toString());

                                progressDialog.dismiss();
                            }else {
                                Log.e(LOG_TAG, response.message());
                            }

                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.v(LOG_TAG, "FAIL: " + t.toString());
                        }
                    });

    }
    */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_drugs) {
            // Handle the camera action
        } else if (id == R.id.nav_pharmacies) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDrugSelected(long id) {
        Intent intent = new Intent(this, DrugDetailActivity.class);
        intent.putExtra(MESSAGE_DRUG_ID, id);
        startActivity(intent);
    }
}
