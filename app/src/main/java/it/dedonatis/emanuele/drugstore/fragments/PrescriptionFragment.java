package it.dedonatis.emanuele.drugstore.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.dedonatis.emanuele.drugstore.R;

public class PrescriptionFragment extends Fragment {
    public PrescriptionFragment() {}


    public static PrescriptionFragment newInstance() {
        PrescriptionFragment fragment = new PrescriptionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_prescription, container, false);
    }

}
