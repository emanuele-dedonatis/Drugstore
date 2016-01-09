package it.dedonatis.emanuele.drugstore.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.fragments.DrugDetailFragment;
import it.dedonatis.emanuele.drugstore.fragments.DrugsListFragment;
import it.dedonatis.emanuele.drugstore.utils.ColorUtils;

public class DrugDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DrugDetailActivity.class.getSimpleName();
    public final static String MESSAGE_DRUG_ID = DrugDetailActivity.class.getSimpleName() + ".DRUG_ID";
    public final static String MESSAGE_DRUG_NAME = DrugDetailActivity.class.getSimpleName() + ".DRUG_NAME";
    public final static String MESSAGE_DRUG_API = DrugDetailActivity.class.getSimpleName() + ".DRUG_API";
    ColorGenerator generator = ColorGenerator.MATERIAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        Intent intent = getIntent();
        final long drugId = intent.getLongExtra(DrugsActivity.MESSAGE_DRUG_ID, -1);
        final String drugName = intent.getStringExtra(DrugsActivity.MESSAGE_DRUG_NAME);
        final String drugApi = intent.getStringExtra(DrugsActivity.MESSAGE_DRUG_API);
        TextView tvName = (TextView) findViewById(R.id.drug_name);
        tvName.setText(drugName);
        TextView tvApi = (TextView) findViewById(R.id.drug_api);
        tvApi.setText(drugApi);
        int color = generator.getColor(drugName);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));


        DrugDetailFragment drugDetailFragment = DrugDetailFragment.newInstance(drugId);
        getSupportFragmentManager().beginTransaction().add(R.id.activity_drug_detail_container, drugDetailFragment).commit();


        getWindow().setStatusBarColor(ColorUtils.getDarkerColor(color));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_new_package);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DrugDetailActivity.this, NewDrugActivity.class);
                intent.putExtra(MESSAGE_DRUG_ID, drugId);
                intent.putExtra(MESSAGE_DRUG_NAME, drugName);
                intent.putExtra(MESSAGE_DRUG_API, drugApi);
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
