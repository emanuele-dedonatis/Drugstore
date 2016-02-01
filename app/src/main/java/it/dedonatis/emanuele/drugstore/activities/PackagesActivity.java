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
        toolbar.setTitle(mDrugName);
        toolbar.setSubtitle(mDrugApi);

        // Fragment
        PackagesListFragment packagesListFragment = PackagesListFragment.newInstance(mDrugId, mDrugColor);
        getSupportFragmentManager().beginTransaction().add(R.id.activity_packages_container, packagesListFragment).commit();

        // Fab
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PackagesActivity.this, AddDrugActivity.class);
                intent.putExtra(MESSAGE_DRUG_ID, mDrugId);
                intent.putExtra(MESSAGE_DRUG_NAME, mDrugName);
                intent.putExtra(MESSAGE_DRUG_API, mDrugApi);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

}
