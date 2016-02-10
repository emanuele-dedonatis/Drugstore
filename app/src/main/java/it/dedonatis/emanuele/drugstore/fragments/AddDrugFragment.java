package it.dedonatis.emanuele.drugstore.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.activities.AddDrugActivity;
import it.dedonatis.emanuele.drugstore.data.DataContract;
import it.dedonatis.emanuele.drugstore.utils.Assets;
import it.dedonatis.emanuele.drugstore.utils.ColorUtils;
import it.dedonatis.emanuele.drugstore.utils.DateUtils;

import static it.dedonatis.emanuele.drugstore.utils.ImageUtils.createImageFile;
import static it.dedonatis.emanuele.drugstore.utils.ImageUtils.saveToInternalSorage;
import static it.dedonatis.emanuele.drugstore.utils.ImageUtils.scaleDown;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddDrugFragment extends Fragment implements AddDrugActivity.OnMenuItemClickListener {
    private static final String LOG_TAG = AddDrugFragment.class.getSimpleName();
    private static final String ARG_DRUG_ID = "id";
    private static final String ARG_DRUG_NAME = "name";
    private static final String ARG_DRUG_API = "api";
    private static final String ARG_DRUG_COLOR = "color";
    private static final int REQUEST_TAKE_PHOTO = 1;

    private ImageView mPackageImage;
    private AutoCompleteTextView mDrugNameEt;
    private AutoCompleteTextView mDrugApiEt;
    private AutoCompleteTextView mPackageDescriptionEt;
    private EditText mPackageExpDateEt;
    private EditText mPackageDosesEt;

    private boolean traineddataExist = false;
    private boolean mOcrEnabled = false;
    private Uri mPhotoUri;

    private long mDrugId;
    private int mDrugColor;
    private String mDrugName;
    private String mDrugApi;

    public AddDrugFragment() {
    }

    public static AddDrugFragment newInstance(long id, String name, String api, int drugColor) {
        AddDrugFragment fragment = new AddDrugFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_DRUG_ID, id);
        args.putString(ARG_DRUG_NAME, name);
        args.putString(ARG_DRUG_API, api);
        args.putInt(ARG_DRUG_COLOR, drugColor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mOcrEnabled = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getBoolean(getString(R.string.pref_OCR), false);

        if (mOcrEnabled) {
            Log.v(LOG_TAG, "OCR enabled");
            // Copy assets for OCR
            new Thread(new Runnable() {
                public void run() {
                    traineddataExist = Assets.copyToInternalStorage(getActivity(), "tessdata", "ita.traineddata");
                }
            }).start();
        } else
            Log.v(LOG_TAG, "OCR not enabled");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDrugId = getArguments().getLong(ARG_DRUG_ID);
        mDrugColor = getArguments().getInt(ARG_DRUG_COLOR);
        mDrugName = getArguments().getString(ARG_DRUG_NAME);
        mDrugApi = getArguments().getString(ARG_DRUG_API);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_add_drug, container, false);

        // Get all view elements
        mDrugNameEt = (AutoCompleteTextView) fragmentView.findViewById(R.id.add_drug_name);
        mDrugApiEt = (AutoCompleteTextView) fragmentView.findViewById(R.id.add_drug_api);
        mPackageDescriptionEt = (AutoCompleteTextView) fragmentView.findViewById(R.id.add_package_description);
        mPackageExpDateEt = (EditText) fragmentView.findViewById(R.id.add_package_exp_date);
        mPackageDosesEt = (EditText) fragmentView.findViewById(R.id.add_package_doses);
        mPackageImage = (ImageView) fragmentView.findViewById(R.id.package_image);

        // Set EXP DATE edit text
        mPackageExpDateEt.setInputType(InputType.TYPE_NULL);
        mPackageExpDateEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                                       @Override
                                                       public void onFocusChange(View v, boolean hasFocus) {
                                                           if (hasFocus) {
                                                               showDateDialog();
                                                           }
                                                       }
                                                   }
        );
        mPackageExpDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        // If drug already exists
        if (mDrugId >= 0) {
            mDrugNameEt.setText(mDrugName);
            mDrugNameEt.setEnabled(false);
            mDrugApiEt.setText(mDrugApi);
            mDrugApiEt.setEnabled(false);
        }

        // Fab
        FloatingActionButton fab = (FloatingActionButton) fragmentView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        return fragmentView;
    }

    private void showDateDialog() {
        DatePickerDialog datePicker = new DatePickerDialog(
                getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mPackageExpDateEt.setText(DateUtils.fromPickerToEurString(year, monthOfYear, dayOfMonth));
                    }
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );

        datePicker.setCancelable(true);
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePicker.setTitle(getString(R.string.select_exp_date));
        datePicker.show();
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            mPhotoUri = null;
            try {
                mPhotoUri = createImageFile(getActivity());
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == getActivity().RESULT_OK) {
            final ProgressDialog ringProgressDialog = ProgressDialog.show(getActivity(), getString(R.string.please_wait), getString(R.string.processing_image), true);
            ringProgressDialog.setCancelable(true);

            new Thread(new Runnable() {
                public void run() {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mPhotoUri);
                        final Bitmap thumb = scaleDown(bitmap, 512, true);
                        saveToInternalSorage(thumb, mPhotoUri);

                        final List<String> lines = new ArrayList<String>();
                        if (mOcrEnabled) {
                            if (traineddataExist) {
                                TessBaseAPI baseAPI = new TessBaseAPI();
                                baseAPI.init(getActivity().getExternalFilesDir(null).getPath(), "ita");
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

                        mDrugNameEt.post(new Runnable() {
                            @Override
                            public void run() {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                        android.R.layout.simple_dropdown_item_1line, lines.toArray(new String[lines.size()]));
                                mDrugNameEt.setAdapter(adapter);
                                mDrugApiEt.setAdapter(adapter);
                                mPackageDescriptionEt.setAdapter(adapter);
                                mPackageImage.setImageURI(mPhotoUri);
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
    public void onSave() {
        // TODO Check if no empty fields and no existing values
        if (mDrugId < 0) {
            // ADD DRUG + PACKAGE
            String drugName = mDrugNameEt.getText().toString();
            String drugApi = mDrugApiEt.getText().toString();

            ContentValues drugValues = new ContentValues();
            drugValues.put(DataContract.DrugEntry.COLUMN_NAME, drugName);
            drugValues.put(DataContract.DrugEntry.COLUMN_API, drugApi);
            Uri uri = getActivity().getContentResolver().insert(
                    DataContract.DrugEntry.CONTENT_URI,
                    drugValues
            );
            mDrugId = ContentUris.parseId(uri);
            Log.v(LOG_TAG, "Insert new drug id = " + mDrugId);
        }

        String packDescription = mPackageDescriptionEt.getText().toString();
        String expDate = DateUtils.fromEurStringToDbString(mPackageExpDateEt.getText().toString());
        String doses = mPackageDosesEt.getText().toString();

        ContentValues pkg = new ContentValues();
        pkg.put(DataContract.PackageEntry.COLUMN_DRUG_ID, mDrugId);
        pkg.put(DataContract.PackageEntry.COLUMN_DESCRIPTION, packDescription);
        pkg.put(DataContract.PackageEntry.COLUMN_DOSES, doses);
        if(mPhotoUri != null) {
            pkg.put(DataContract.PackageEntry.COLUMN_IMAGE_URI, mPhotoUri.toString());
        }
        Uri uri = getActivity().getContentResolver().insert(
                DataContract.PackageEntry.CONTENT_URI,
                pkg
        );
        long packId = ContentUris.parseId(uri);
        Log.d(LOG_TAG, "Insert new package id = " + packId);

        ContentValues subpack = new ContentValues();
        subpack.put(DataContract.SubpackageEntry.COLUMN_DRUG_ID, mDrugId);
        subpack.put(DataContract.SubpackageEntry.COLUMN_PACKAGE_ID, packId);
        subpack.put(DataContract.SubpackageEntry.COLUMN_DOSES_LEFT, doses);
        subpack.put(DataContract.SubpackageEntry.COLUMN_EXP_DATE, expDate);
        uri = getActivity().getContentResolver().insert(
                DataContract.SubpackageEntry.CONTENT_URI,
                subpack
        );
        Log.d(LOG_TAG, subpack.toString());
        long subId = ContentUris.parseId(uri);
        Log.v(LOG_TAG, "Insert new subpackage id = " + subId);
        getActivity().finish();
    }

    @Override
    public void onCancel() {
        // TODO delete temp image
        if(mPhotoUri != null)
            new File(mPhotoUri.toString()).delete();
        getActivity().finish();
    }
}
