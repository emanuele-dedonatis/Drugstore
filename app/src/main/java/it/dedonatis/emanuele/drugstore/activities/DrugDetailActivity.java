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

    ColorGenerator generator = ColorGenerator.MATERIAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_new_package);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Intent intent = getIntent();
        long drugId = intent.getLongExtra(DrugsActivity.MESSAGE_DRUG_ID, -1);
        String drugName = intent.getStringExtra(DrugsActivity.MESSAGE_DRUG_NAME);
        String drugApi = intent.getStringExtra(DrugsActivity.MESSAGE_DRUG_API);
        TextView tvName = (TextView) findViewById(R.id.drug_name);
        tvName.setText(drugName);
        TextView tvApi = (TextView) findViewById(R.id.drug_api);
        tvApi.setText(drugApi);
        int color = generator.getColor(drugName);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
        getWindow().setStatusBarColor(ColorUtils.getDarkerColor(color));

        if (savedInstanceState != null) {
            return;
        } else {
            DrugDetailFragment drugDetailFragment = DrugDetailFragment.newInstance(drugId);
            getSupportFragmentManager().beginTransaction().add(R.id.activity_drug_detail_container, drugDetailFragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
