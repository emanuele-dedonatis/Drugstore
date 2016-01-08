package it.dedonatis.emanuele.drugstore.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.dedonatis.emanuele.drugstore.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class NewDrugFragment extends Fragment {

    public NewDrugFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_drug, container, false);
    }
}
