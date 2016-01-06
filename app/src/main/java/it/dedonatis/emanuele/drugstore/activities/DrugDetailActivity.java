package it.dedonatis.emanuele.drugstore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.fragments.DrugDetailFragment;
import it.dedonatis.emanuele.drugstore.fragments.DrugsListFragment;

public class DrugDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DrugDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Intent intent = getIntent();
        long drugId = intent.getLongExtra(DrugsActivity.MESSAGE_DRUG_ID, -1);
        getSupportActionBar().setTitle(Long.toString(drugId));

        if (savedInstanceState != null) {
            return;
        } else {
            DrugDetailFragment drugDetailFragment = DrugDetailFragment.newInstance(drugId);
            getSupportFragmentManager().beginTransaction().add(R.id.activity_drug_detail_container, drugDetailFragment).commit();
        }
    }

}
