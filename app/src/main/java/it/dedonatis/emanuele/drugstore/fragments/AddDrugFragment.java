package it.dedonatis.emanuele.drugstore.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.interfaces.OnMenuItemClickListener;
import it.dedonatis.emanuele.drugstore.interfaces.OnNewDrugListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddDrugFragment extends Fragment implements OnMenuItemClickListener {

    private OnNewDrugListener newDrugListener;
    private EditText mDescriptionEt;
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

        mDescriptionEt = (EditText) fragmentView.findViewById(R.id.description_et);
        mUnitsEt = (EditText) fragmentView.findViewById(R.id.units_et);
        mExpDateEt = (EditText) fragmentView.findViewById(R.id.expiration_date_et);

        Button cameraBtn = (Button) fragmentView.findViewById(R.id.camera_btn);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newDrugListener != null) {
                    newDrugListener.dispatchTakePictureIntent();
                }
            }
        });

        return fragmentView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNewDrugListener) {
            newDrugListener = (OnNewDrugListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        newDrugListener = null;
    }

    @Override
    public void onDone() {
        String description = mDescriptionEt.getText().toString();
        int units = Integer.parseInt(mUnitsEt.getText().toString());
        int exp_date = Integer.parseInt(mExpDateEt.getText().toString());

        newDrugListener.addDrug(description, units, 0, exp_date, null);
    }

}
