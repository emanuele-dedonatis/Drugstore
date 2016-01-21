package it.dedonatis.emanuele.drugstore.activities;

import android.content.AsyncQueryHandler;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.data.DrugContract;
import it.dedonatis.emanuele.drugstore.fragments.AddDrugFragment;
import it.dedonatis.emanuele.drugstore.interfaces.OnMenuItemClickListener;
import it.dedonatis.emanuele.drugstore.interfaces.OnNewDrugListener;
import it.dedonatis.emanuele.drugstore.utils.Assets;
import it.dedonatis.emanuele.drugstore.utils.ColorUtils;

import static it.dedonatis.emanuele.drugstore.utils.Images.createImageFile;
import static it.dedonatis.emanuele.drugstore.utils.Images.saveToInternalSorage;
import static it.dedonatis.emanuele.drugstore.utils.Images.scaleDown;

public class AddDrugActivity extends AppCompatActivity implements OnNewDrugListener {

    private static final String LOG_TAG = AddDrugActivity.class.getSimpleName();
    ColorGenerator generator = ColorGenerator.MATERIAL;
    static final int REQUEST_TAKE_PHOTO = 1;
    private long drugId = -1;
    private Uri mPhotoUri;
    private boolean traineddataExist = false;

    private AddDrugFragment mAddDrugFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drug);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        // Copy assets for OCR
        Thread t = new Thread(new Runnable() {
            public void run() {
                traineddataExist = Assets.copyToInternalStorage(getApplication(),"tessdata", "ita.traineddata");
            }
        });
        t.start();

        // Fab
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        // Header selection
        Intent intent = getIntent();
        drugId = intent.getLongExtra(DrugDetailActivity.MESSAGE_DRUG_ID, -1);
        if(drugId >= 0) {
            // Existing drug
            String drugName = intent.getStringExtra(DrugDetailActivity.MESSAGE_DRUG_NAME);
            String drugApi = intent.getStringExtra(DrugDetailActivity.MESSAGE_DRUG_API);
            TextView tvName = (TextView) findViewById(R.id.drug_name);
            tvName.setText(drugName);
            TextView tvApi = (TextView) findViewById(R.id.drug_api);
            tvApi.setText(drugApi);
            int color = generator.getColor(drugName);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
            getWindow().setStatusBarColor(ColorUtils.getDarkerColor(color));

        }else{
            // New drug
            ViewSwitcher viewSwitcher =   (ViewSwitcher)findViewById(R.id.toolbar_switcher);
            viewSwitcher.showNext();

            ViewGroup.MarginLayoutParams fabParam = (ViewGroup.MarginLayoutParams) fab.getLayoutParams();
            fabParam.setMargins(fabParam.leftMargin, fabParam.topMargin + 60, fabParam.rightMargin, fabParam.bottomMargin);
            fab.setLayoutParams(fabParam);
        }




        mAddDrugFragment = AddDrugFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.activity_new_drug_container, mAddDrugFragment).commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
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
                ((OnMenuItemClickListener)mAddDrugFragment).onDone();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), mPhotoUri);
                        Bitmap thumb = scaleDown(bitmap, 600, true);
                        saveToInternalSorage(thumb, mPhotoUri);
                        ImageView image = (ImageView) findViewById(R.id.package_image);
                        image.setImageURI(mPhotoUri);
                        if (traineddataExist) {
                            TessBaseAPI baseAPI = new TessBaseAPI();
                            baseAPI.init(getExternalFilesDir(null).getPath(), "ita");
                            baseAPI.setImage(thumb);
                            String recognizedText = baseAPI.getUTF8Text();
                            baseAPI.end();
                            Log.v(LOG_TAG, recognizedText);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

        }
    }


    @Override
    public void addDrug(String description, int units, int isPercentage, int exp_date, byte[] image) {
        if (drugId < 0){
            ContentValues drug = new ContentValues();
            drug.put(DrugContract.DrugEntry.COLUMN_NAME, ((EditText) findViewById(R.id.drug_name_et)).getText().toString());
            drug.put(DrugContract.DrugEntry.COLUMN_API, ((EditText) findViewById(R.id.drug_api_et)).getText().toString());
            drug.put(DrugContract.DrugEntry.COLUMN_NEED_PRESCRIPTION, 0);
            Uri uri = getContentResolver().insert(
                    DrugContract.DrugEntry.CONTENT_URI,
                    drug
            );
            drugId = ContentUris.parseId(uri);
            Log.v(LOG_TAG, "New drug id " + drugId);
        }

        if(drugId>=0) {
            ContentValues pkg = new ContentValues();
            pkg.put(DrugContract.PackageEntry.COLUMN_DRUG, drugId);
            pkg.put(DrugContract.PackageEntry.COLUMN_DESCRIPTION, description);
            pkg.put(DrugContract.PackageEntry.COLUMN_UNITS, units);
            pkg.put(DrugContract.PackageEntry.COLUMN_IS_PERCENTAGE, isPercentage);
            pkg.put(DrugContract.PackageEntry.COLUMN_EXPIRATION_DATE, exp_date);
            pkg.put(DrugContract.PackageEntry.COLUMN_IMAGE, mPhotoUri.toString());
            Log.v(LOG_TAG, "New pkg");
            AsyncQueryHandler queryHandler = new AsyncQueryHandler(getContentResolver()) {};

            queryHandler.startInsert(0, null,
                    DrugContract.PackageEntry.CONTENT_URI,
                    pkg
            );
        }
        finish();
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            mPhotoUri = null;
            try {
                mPhotoUri = createImageFile(this);
            } catch (IOException ex) {
                Log.e(LOG_TAG, ex.toString());
            }
            if (mPhotoUri != null) {
                Log.v(LOG_TAG, "before " + mPhotoUri.toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }



}
