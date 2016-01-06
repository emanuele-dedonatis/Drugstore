package it.dedonatis.emanuele.drugstore.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.models.Drug;


public class DrugDetailFragment extends DialogFragment {
    private static final String ARG_DRUG_ID = "id";
    private static final String LOG_TAG = DrugDetailFragment.class.getSimpleName();

    private String drugId;

    public DrugDetailFragment() {}

    public static DrugDetailFragment newInstance(long id) {
        DrugDetailFragment fragment = new DrugDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DRUG_ID, Long.toString(id));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            drugId = getArguments().getString(ARG_DRUG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_drug_detail, container, false);
        TextView tv = (TextView) fragmentView.findViewById(R.id.detail_fragment_tv);
        tv.setText(drugId);

        return  fragmentView;
    }
}
