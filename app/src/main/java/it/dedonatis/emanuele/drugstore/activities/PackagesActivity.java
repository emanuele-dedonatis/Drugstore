package it.dedonatis.emanuele.drugstore.activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.data.DataContract;
import it.dedonatis.emanuele.drugstore.fragments.PackagesListFragment;
import it.dedonatis.emanuele.drugstore.models.DrugPackage;
import it.dedonatis.emanuele.drugstore.models.DrugSubpackage;
import it.dedonatis.emanuele.drugstore.utils.ColorUtils;
import it.dedonatis.emanuele.drugstore.utils.DateUtils;

public class PackagesActivity extends AppCompatActivity {
    private static final String LOG_TAG = PackagesActivity.class.getSimpleName();
    public final static String MESSAGE_DRUG_ID = LOG_TAG + ".DRUG_ID";
    public final static String MESSAGE_DRUG_NAME = LOG_TAG + ".DRUG_NAME";
    public final static String MESSAGE_DRUG_API = LOG_TAG + ".DRUG_API";
    public final static String MESSAGE_DRUG_COLOR = LOG_TAG + ".DRUG_COLOR";

    private long mDrugId;
    private String mDrugName;
    private String mDrugApi;
    private int mDrugColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packages);

        // Get extras from intent
        Intent intent = getIntent();
        mDrugId = intent.getLongExtra(MESSAGE_DRUG_ID, -1);
        mDrugName = intent.getStringExtra(MESSAGE_DRUG_NAME);
        mDrugApi = intent.getStringExtra(MESSAGE_DRUG_API);
        mDrugColor = intent.getIntExtra(MESSAGE_DRUG_COLOR, ColorUtils.getDrugColor(mDrugName, mDrugApi));

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mDrugName);
        getSupportActionBar().setSubtitle(mDrugApi);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(mDrugColor));
        getWindow().setStatusBarColor(ColorUtils.getDarkerColor(mDrugColor));


        // Fragment
        PackagesListFragment packagesListFragment = PackagesListFragment.newInstance(mDrugId, mDrugName, mDrugApi, mDrugColor);
        getFragmentManager().beginTransaction().add(R.id.activity_packages_container, packagesListFragment).commit();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_drug_packages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add:
                Intent intent = new Intent(this, AddDrugActivity.class);
                intent.putExtra(AddDrugActivity.MESSAGE_DRUG_ID, mDrugId);
                intent.putExtra(AddDrugActivity.MESSAGE_DRUG_NAME, mDrugName);
                intent.putExtra(AddDrugActivity.MESSAGE_DRUG_API, mDrugApi);
                intent.putExtra(AddDrugActivity.MESSAGE_DRUG_COLOR, mDrugColor);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
