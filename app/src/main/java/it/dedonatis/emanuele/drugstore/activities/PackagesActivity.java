package it.dedonatis.emanuele.drugstore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.fragments.PackagesListFragment;
import it.dedonatis.emanuele.drugstore.utils.ColorUtils;

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

        // Fragment
        PackagesListFragment packagesListFragment = PackagesListFragment.newInstance(mDrugId, mDrugName, mDrugApi, mDrugColor);
        getSupportFragmentManager().beginTransaction().add(R.id.activity_packages_container, packagesListFragment).commit();



    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

}
