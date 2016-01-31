package it.dedonatis.emanuele.drugstore.activities;

import android.app.ProgressDialog;
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
import android.preference.PreferenceManager;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.data.DrugContract;
import it.dedonatis.emanuele.drugstore.fragments.AddDrugFragment;
import it.dedonatis.emanuele.drugstore.interfaces.OnMenuItemClickListener;
import it.dedonatis.emanuele.drugstore.interfaces.OnNewDrugListener;
import it.dedonatis.emanuele.drugstore.utils.Assets;
import it.dedonatis.emanuele.drugstore.utils.ColorUtils;
import it.dedonatis.emanuele.drugstore.utils.Images;

import static it.dedonatis.emanuele.drugstore.utils.Images.createImageFile;
import static it.dedonatis.emanuele.drugstore.utils.Images.saveToInternalSorage;
import static it.dedonatis.emanuele.drugstore.utils.Images.scaleDown;

public class AddDrugActivity extends AppCompatActivity implements OnNewDrugListener, AddDrugFragment.OnChooseFotoListener {

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
        new Thread(new Runnable() {
            public void run() {
                traineddataExist = Assets.copyToInternalStorage(getApplication(),"tessdata", "ita.traineddata");
            }
        }).start();


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
            int color = ColorUtils.getDrugColor(drugName, drugApi);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
            getWindow().setStatusBarColor(ColorUtils.getDarkerColor(color));

        }else{
            // New drug
            ViewSwitcher viewSwitcher =   (ViewSwitcher)findViewById(R.id.toolbar_switcher);
            viewSwitcher.showNext();
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
            final ProgressDialog ringProgressDialog = ProgressDialog.show(AddDrugActivity.this, getString(R.string.please_wait),getString(R.string.processing_image), true);
            ringProgressDialog.setCancelable(true);

            final ImageView image = (ImageView) findViewById(R.id.package_image);
            final AutoCompleteTextView drugNameEditText = (AutoCompleteTextView) findViewById(R.id.drug_name_et);
            final AutoCompleteTextView drugApiEditText = (AutoCompleteTextView) findViewById(R.id.drug_api_et);

            new Thread(new Runnable() {
                public void run() {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), mPhotoUri);
                        final Bitmap thumb = scaleDown(bitmap, 512, true);
                        saveToInternalSorage(thumb, mPhotoUri);

                        boolean ocrEnabled = PreferenceManager.getDefaultSharedPreferences(getBaseContext())
                                .getBoolean(getString(R.string.pref_OCR), false);
                        final List<String> lines = new ArrayList<String>();
                        if(ocrEnabled) {
                            if (traineddataExist) {
                                TessBaseAPI baseAPI = new TessBaseAPI();
                                baseAPI.init(getExternalFilesDir(null).getPath(), "ita");
                                baseAPI.setImage(thumb);
                                Scanner scanner = new Scanner(baseAPI.getUTF8Text());
                                baseAPI.end();
                                while (scanner.hasNextLine()) {
                                    String line = scanner.nextLine().replaceAll("[^A-Za-z\\s]+", "");
                                    line = line.trim().replaceAll(" +", " ");
                                    if (line.length() > 8)
                                        line = Character.toUpperCase(line.charAt(0)) + line.substring(1).toLowerCase();
                                    lines.add(line);
                                    Log.v(LOG_TAG, line + " -> " + line);
                                }
                            }
                        }
                            ringProgressDialog.dismiss();

                            drugNameEditText.post(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplication(),
                                            android.R.layout.simple_dropdown_item_1line, lines.toArray(new String[lines.size()]));
                                    drugNameEditText.setAdapter(adapter);
                                    drugApiEditText.setAdapter(adapter);
                                    mAddDrugFragment.getDescriptionEt().setAdapter(adapter);
                                    image.setImageURI(mPhotoUri);
                                }
                            });
                    } catch (IOException e) {
                        e.printStackTrace();
                }
                }
            }).start();

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
            pkg.put(DrugContract.PackageEntry.COLUMN_IMAGE, (mPhotoUri!=null) ? mPhotoUri.toString() : null);
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
