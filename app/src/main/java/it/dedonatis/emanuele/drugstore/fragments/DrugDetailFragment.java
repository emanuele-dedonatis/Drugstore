package it.dedonatis.emanuele.drugstore.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import it.dedonatis.emanuele.drugstore.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DrugDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DrugDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DrugDetailFragment extends Fragment {
    private static final String ARG_ID = "id";

    private String mParamId;

    public DrugDetailFragment() {}

    public static DrugDetailFragment newInstance(String id) {
        DrugDetailFragment fragment = new DrugDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamId = getArguments().getString(ARG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_drug_detail, container, false);
        TextView tv = (TextView) fragmentView.findViewById(R.id.detail_fragment_tv);
        tv.setText(mParamId);
        return  fragmentView;
    }
}
