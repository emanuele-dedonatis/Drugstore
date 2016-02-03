package it.dedonatis.emanuele.drugstore.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.fragments.AddDrugFragment;
import it.dedonatis.emanuele.drugstore.utils.ColorUtils;

public class AddDrugActivity extends AppCompatActivity {

    private static final String LOG_TAG = AddDrugActivity.class.getSimpleName();

    private AddDrugFragment mAddDrugFragment;

    public static final String MESSAGE_DRUG_ID = LOG_TAG + ".DRUG_ID";
    public static final String MESSAGE_DRUG_NAME = LOG_TAG + ".DRUG_NAME";
    public static final String MESSAGE_DRUG_API = LOG_TAG + ".DRUG_API";
    public final static String MESSAGE_DRUG_COLOR = LOG_TAG + ".DRUG_COLOR";

    private long mDrugId = -1;
    private String mDrugName;
    private String mDrugApi;
    private int mDrugColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drug);

        // Get extras from intent
        Intent intent = getIntent();
        mDrugId = intent.getLongExtra(MESSAGE_DRUG_ID, -1);
        mDrugName = intent.getStringExtra(MESSAGE_DRUG_NAME);
        mDrugApi = intent.getStringExtra(MESSAGE_DRUG_API);
        mDrugColor = intent.getIntExtra(MESSAGE_DRUG_COLOR, ColorUtils.getDrugColor(mDrugName, mDrugApi));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        // If drug already exists
        if(mDrugId >= 0) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(mDrugColor));
            getWindow().setStatusBarColor(ColorUtils.getDarkerColor(mDrugColor));
        }

        mAddDrugFragment = AddDrugFragment.newInstance(mDrugId, mDrugName, mDrugApi, mDrugColor);
        getSupportFragmentManager().beginTransaction().add(R.id.activity_new_drug_container, mAddDrugFragment).commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        ((OnMenuItemClickListener)mAddDrugFragment).onCancel();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.save:
                ((OnMenuItemClickListener)mAddDrugFragment).onSave();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public interface OnMenuItemClickListener {
        public void onSave();
        public void onCancel();
    }

}
