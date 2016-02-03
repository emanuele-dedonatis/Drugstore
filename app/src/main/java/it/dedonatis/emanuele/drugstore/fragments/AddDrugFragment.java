package it.dedonatis.emanuele.drugstore.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import it.dedonatis.emanuele.drugstore.utils.Assets;

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

    private OnNewDrugListener newDrugListener;
    private OnChooseFotoListener chooseFotoListener;

    private AutoCompleteTextView mDescriptionEt;
    private EditText mUnitsEt;
    private EditText mExpDateEt;

    private boolean traineddataExist = false;
    private boolean mOcrEnabled = false;

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

        if(mOcrEnabled) {
            Log.v(LOG_TAG, "OCR enabled");
            // Copy assets for OCR
            new Thread(new Runnable() {
                public void run() {
                    traineddataExist = Assets.copyToInternalStorage(getActivity(), "tessdata", "ita.traineddata");
                }
            }).start();
        }else
            Log.v(LOG_TAG, "OCR not enabled");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_add_drug, container, false);

        mDescriptionEt = (AutoCompleteTextView) fragmentView.findViewById(R.id.add_package_description);
        mUnitsEt = (EditText) fragmentView.findViewById(R.id.add_package_doses);

        mExpDateEt = (EditText) fragmentView.findViewById(R.id.add_package_exp_date);
        mExpDateEt.setInputType(InputType.TYPE_NULL);

        mExpDateEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                                @Override
                                                public void onFocusChange(View v, boolean hasFocus) {
                                                    if (hasFocus) {
                                                        new DatePickerDialog(
                                                                getActivity(),
                                                                new DatePickerDialog.OnDateSetListener() {
                                                                    @Override
                                                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                                                        String month = "" + (monthOfYear + 1);
                                                                        month = month.length() < 2 ? "0" + month : month;

                                                                        mExpDateEt.setText(dayOfMonth + "/" + month + "/" + year);
                                                                    }
                                                                },
                                                                Calendar.getInstance().get(Calendar.YEAR),
                                                                Calendar.getInstance().get(Calendar.MONTH),
                                                                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                                                        ).show();
                                                    }
                                                }
                                            }
        );
        mExpDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(
                        getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String month = "" + (monthOfYear + 1);
                                month = month.length() < 2 ? "0" + month : month;

                                mExpDateEt.setText(dayOfMonth + "/" + month + "/" + year);
                            }
                        },
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        // Fab
        FloatingActionButton fab = (FloatingActionButton) fragmentView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFotoListener.dispatchTakePictureIntent();
            }
        });

        return fragmentView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof OnNewDrugListener && getActivity() instanceof OnChooseFotoListener) {
            newDrugListener = (OnNewDrugListener) getActivity();
            chooseFotoListener = (OnChooseFotoListener) getActivity();
        } else {
            throw new RuntimeException(getActivity().toString()
                    + " must implement OnNewDrugListener && OnChooseFotoListener");
        }
    }

    @Override
    public void onSave() {
        String description = mDescriptionEt.getText().toString();
        int units = Integer.parseInt(mUnitsEt.getText().toString());

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = inputFormat.parse(mExpDateEt.getText().toString());

            SimpleDateFormat databaseFormat = new SimpleDateFormat("yyyyMMdd");
            newDrugListener.addDrug(description, units, 0, Integer.parseInt(databaseFormat.format(date).toString()), null);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public AutoCompleteTextView getDescriptionEt() {
        return mDescriptionEt;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            final ProgressDialog ringProgressDialog = ProgressDialog.show(AddDrugActivity.this, getString(R.string.please_wait),getString(R.string.processing_image), true);
            ringProgressDialog.setCancelable(true);

            final ImageView image = (ImageView) findViewById(R.id.package_image);
            final AutoCompleteTextView drugNameEditText = (AutoCompleteTextView) findViewById(R.id.add_drug_name);
            final AutoCompleteTextView drugApiEditText = (AutoCompleteTextView) findViewById(R.id.add_drug_api);

            new Thread(new Runnable() {
                public void run() {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), mPhotoUri);
                        final Bitmap thumb = scaleDown(bitmap, 512, true);
                        saveToInternalSorage(thumb, mPhotoUri);

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
        /*
        if (drugId < 0){
            ContentValues drug = new ContentValues();
            drug.put(DataContract.DrugEntry.COLUMN_NAME, ((EditText) findViewById(R.id.drug_name_et)).getText().toString());
            drug.put(DataContract.DrugEntry.COLUMN_API, ((EditText) findViewById(R.id.drug_api_et)).getText().toString());
            drug.put(DataContract.DrugEntry.COLUMN_NEED_PRESCRIPTION, 0);
            Uri uri = getContentResolver().insert(
                    DataContract.DrugEntry.CONTENT_URI,
                    drug
            );
            drugId = ContentUris.parseId(uri);
            Log.v(LOG_TAG, "New drug id " + drugId);
        }

        if(drugId>=0) {
            ContentValues pkg = new ContentValues();
            pkg.put(DataContract.PackageEntry.COLUMN_DRUG, drugId);
            pkg.put(DataContract.PackageEntry.COLUMN_DESCRIPTION, description);
            pkg.put(DataContract.PackageEntry.COLUMN_UNITS, units);
            pkg.put(DataContract.PackageEntry.COLUMN_IS_PERCENTAGE, isPercentage);
            pkg.put(DataContract.PackageEntry.COLUMN_EXPIRATION_DATE, exp_date);
            pkg.put(DataContract.PackageEntry.COLUMN_IMAGE, (mPhotoUri!=null) ? mPhotoUri.toString() : null);
            Log.v(LOG_TAG, "New pkg");
            AsyncQueryHandler queryHandler = new AsyncQueryHandler(getContentResolver()) {};

            queryHandler.startInsert(0, null,
                    DataContract.PackageEntry.CONTENT_URI,
                    pkg
            );
        }
        finish();*/
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


    @Override
    public void onCancel() {

    }
}
