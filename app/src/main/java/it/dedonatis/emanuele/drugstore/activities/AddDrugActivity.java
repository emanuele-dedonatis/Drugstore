package it.dedonatis.emanuele.drugstore.activities;

import android.content.AsyncQueryHandler;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.data.DrugContract;
import it.dedonatis.emanuele.drugstore.fragments.AddDrugFragment;
import it.dedonatis.emanuele.drugstore.interfaces.OnMenuItemClickListener;
import it.dedonatis.emanuele.drugstore.interfaces.OnNewDrugListener;
import it.dedonatis.emanuele.drugstore.utils.ColorUtils;

public class AddDrugActivity extends AppCompatActivity implements OnNewDrugListener {

    private static final String LOG_TAG = AddDrugActivity.class.getSimpleName();
    ColorGenerator generator = ColorGenerator.MATERIAL;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;
    private long drugId = -1;
    private AddDrugFragment mAddDrugFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drug);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

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
            Uri uri = Uri.parse(mCurrentPhotoPath);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                Bitmap thumb = scaleDown(bitmap, 600, true);
                saveToInternalSorage(thumb);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.v(LOG_TAG, uri.toString());

        }
    }

    private void saveToInternalSorage(Bitmap bitmapImage) throws IOException {
        File mypath = new File(Uri.parse(mCurrentPhotoPath).getPath());
        Log.v(LOG_TAG, "Resizing file " + mypath.toString());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
            ImageView image = (ImageView) findViewById(R.id.package_image);
            image.setImageURI(Uri.parse(mCurrentPhotoPath));
            image.setVisibility(View.VISIBLE);
        }
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
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

        return image;
    }

}
