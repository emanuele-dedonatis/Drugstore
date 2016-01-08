package it.dedonatis.emanuele.drugstore.fragments;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import it.dedonatis.emanuele.drugstore.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class NewDrugFragment extends Fragment {

    private OnNewDrugListener newDrugListener;
    public NewDrugFragment() {
    }

    public static NewDrugFragment newInstance() {
        NewDrugFragment fragment = new NewDrugFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_new_drug, container, false);
        final EditText descriptionEt = (EditText) fragmentView.findViewById(R.id.description_et);
        final EditText unitsEt = (EditText) fragmentView.findViewById(R.id.units_et);
        final EditText expDateEt = (EditText) fragmentView.findViewById(R.id.expiration_date_et);
        Button cameraBtn = (Button) fragmentView.findViewById(R.id.camera_btn);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newDrugListener != null) {
                    newDrugListener.dispatchTakePictureIntent();
                }
            }
        });

        Button doneBtn = (Button) fragmentView.findViewById(R.id.done_btn);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newDrugListener != null) {
                    newDrugListener.addDrug(descriptionEt.getText().toString(), Integer.parseInt(unitsEt.getText().toString()), 0, Integer.parseInt(expDateEt.getText().toString()), null);
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
    public interface OnNewDrugListener {
        public void addDrug(String description, int units, int isPercentage, int exp_date, byte[] image);

        public void dispatchTakePictureIntent();
    }

}
