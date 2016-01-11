package it.dedonatis.emanuele.drugstore.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.fragments.DrugDetailFragment;
import it.dedonatis.emanuele.drugstore.utils.ColorUtils;

public class DrugDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DrugDetailActivity.class.getSimpleName();
    public final static String MESSAGE_DRUG_ID = DrugDetailActivity.class.getSimpleName() + ".DRUG_ID";
    public final static String MESSAGE_DRUG_NAME = DrugDetailActivity.class.getSimpleName() + ".DRUG_NAME";
    public final static String MESSAGE_DRUG_API = DrugDetailActivity.class.getSimpleName() + ".DRUG_API";

    private long mDrugId;
    private String mDrugName;
    private String mDrugApi;
    private int mDrugColor;
    ColorGenerator generator = ColorGenerator.MATERIAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_detail);

        // Get extras from intent
        Intent intent = getIntent();
        mDrugId = intent.getLongExtra(DrugsActivity.MESSAGE_DRUG_ID, -1);
        mDrugName = intent.getStringExtra(DrugsActivity.MESSAGE_DRUG_NAME);
        mDrugApi = intent.getStringExtra(DrugsActivity.MESSAGE_DRUG_API);
        mDrugColor = generator.getColor(mDrugName);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
;
        // Header
        TextView tvName = (TextView) findViewById(R.id.drug_name);
        tvName.setText(mDrugName);
        TextView tvApi = (TextView) findViewById(R.id.drug_api);
        tvApi.setText(mDrugApi);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(mDrugColor));
        getWindow().setStatusBarColor(ColorUtils.getDarkerColor(mDrugColor));

        // Fragment
        DrugDetailFragment drugDetailFragment = DrugDetailFragment.newInstance(mDrugId, mDrugColor);
        getSupportFragmentManager().beginTransaction().add(R.id.activity_drug_detail_container, drugDetailFragment).commit();

        // Fab
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DrugDetailActivity.this, NewDrugActivity.class);
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
