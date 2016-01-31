package it.dedonatis.emanuele.drugstore.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Debug;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.interfaces.OnMenuItemClickListener;
import it.dedonatis.emanuele.drugstore.interfaces.OnNewDrugListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddDrugFragment extends Fragment implements OnMenuItemClickListener {

    private OnNewDrugListener newDrugListener;
    private OnChooseFotoListener chooseFotoListener;

    private AutoCompleteTextView mDescriptionEt;
    private EditText mUnitsEt;
    private EditText mExpDateEt;

    public AddDrugFragment() {
    }

    public static AddDrugFragment newInstance() {
        AddDrugFragment fragment = new AddDrugFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_add_drug, container, false);

        mDescriptionEt = (AutoCompleteTextView) fragmentView.findViewById(R.id.description_et);
        mUnitsEt = (EditText) fragmentView.findViewById(R.id.units_et);
        mExpDateEt = (EditText) fragmentView.findViewById(R.id.expiration_date_et);
        mExpDateEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                                @Override
                                                public void onFocusChange(View v, boolean hasFocus) {
                                                    if(hasFocus){
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
    public void onDone() {
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

    /***** FRAGMENT LISTENER *****/
    public interface OnChooseFotoListener {
        public void dispatchTakePictureIntent();
    }
}
