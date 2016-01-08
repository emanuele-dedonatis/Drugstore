package it.dedonatis.emanuele.drugstore.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.amulyakhare.textdrawable.util.ColorGenerator;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.fragments.NewDrugFragment;
import it.dedonatis.emanuele.drugstore.utils.ColorUtils;

public class NewDrugActivity extends AppCompatActivity implements NewDrugFragment.OnNewDrugListener {

    private static final String LOG_TAG = NewDrugActivity.class.getSimpleName();
    ColorGenerator generator = ColorGenerator.MATERIAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_drug);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        Intent intent = getIntent();

        final long drugId = intent.getLongExtra(DrugDetailActivity.MESSAGE_DRUG_ID, -1);
        if(drugId >= 0) {
            final String drugName = intent.getStringExtra(DrugDetailActivity.MESSAGE_DRUG_NAME);
            final String drugApi = intent.getStringExtra(DrugDetailActivity.MESSAGE_DRUG_API);
            TextView tvName = (TextView) findViewById(R.id.drug_name_tv);
            tvName.setText(drugName);
            TextView tvApi = (TextView) findViewById(R.id.drug_api_tv);
            tvApi.setText(drugApi);
            int color = generator.getColor(drugName);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
            getWindow().setStatusBarColor(ColorUtils.getDarkerColor(color));
        }else{
            ViewSwitcher viewSwitcher =   (ViewSwitcher)findViewById(R.id.toolbar_switcher);
            viewSwitcher.showNext();

        }

        if (savedInstanceState != null) {
            return;
        } else {
            NewDrugFragment newDrugFragment = NewDrugFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.activity_new_drug_container, newDrugFragment).commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    @Override
    public void addDrug(String description, int units, int units_left, int exp_date) {
        Log.v(LOG_TAG, description + "" + units_left + "/" + units + " " + exp_date);
        finish();
    }
}
