package it.dedonatis.emanuele.drugstore.activities;

import android.content.AsyncQueryHandler;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.data.DrugContract;
import it.dedonatis.emanuele.drugstore.fragments.NewDrugFragment;
import it.dedonatis.emanuele.drugstore.utils.ColorUtils;

public class NewDrugActivity extends AppCompatActivity implements NewDrugFragment.OnNewDrugListener {

    private static final String LOG_TAG = NewDrugActivity.class.getSimpleName();
    ColorGenerator generator = ColorGenerator.MATERIAL;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;

    private long drugId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_drug);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        Intent intent = getIntent();

        drugId = intent.getLongExtra(DrugDetailActivity.MESSAGE_DRUG_ID, -1);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Log.v(LOG_TAG, "Image taken");
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
            Log.v(LOG_TAG, "Insered new drug id " + drugId);
        }

        if(drugId>=0) {
            ContentValues pkg = new ContentValues();
            pkg.put(DrugContract.PackageEntry.COLUMN_DRUG, drugId);
            pkg.put(DrugContract.PackageEntry.COLUMN_DESCRIPTION, description);
            pkg.put(DrugContract.PackageEntry.COLUMN_UNITS, units);
            pkg.put(DrugContract.PackageEntry.COLUMN_IS_PERCENTAGE, isPercentage);
            pkg.put(DrugContract.PackageEntry.COLUMN_EXPIRATION_DATE, exp_date);/*
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bitMapData = stream.toByteArray(); */
            pkg.put(DrugContract.PackageEntry.COLUMN_IMAGE, mCurrentPhotoPath);
            Log.v(LOG_TAG, "insert new pkg");
            AsyncQueryHandler queryHandler = new AsyncQueryHandler(getContentResolver()) {};

            queryHandler.startInsert(0, null,
                    DrugContract.PackageEntry.CONTENT_URI,
                    pkg
            );
        }
        finish();
    }

    @Override
    public void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(LOG_TAG, ex.toString());
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = "file:" + image.getAbsolutePath();

        Log.v(LOG_TAG, "Create image file @ " +mCurrentPhotoPath + " with uri "+ Uri.fromFile(image));
        return image;
    }
}
