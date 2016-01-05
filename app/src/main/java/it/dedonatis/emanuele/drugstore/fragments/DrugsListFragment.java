package it.dedonatis.emanuele.drugstore.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.models.Drug;

public class DrugsListFragment extends Fragment {
    private static final String LOG_TAG = DrugsListFragment.class.getSimpleName();
    private static final String ARG_TEXT = "arg_text";

    private String mParamText;

    private OnDrugSelectionListener mListener;

    public DrugsListFragment() {
        // Required empty public constructor
    }

    public static DrugsListFragment newInstance(String paramText) {
        DrugsListFragment fragment = new DrugsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, paramText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamText = getArguments().getString(ARG_TEXT);
            Log.v(LOG_TAG, mParamText);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_drug_list, container, false);

        TextView tv = (TextView) fragmentView.findViewById(R.id.drug_list_tv);
        tv.setText(mParamText);

        Button btn1 = (Button) fragmentView.findViewById(R.id.button1);
        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mListener.onDrugSelected("BUTTON 1");
            }
        });

        Button btn2 = (Button) fragmentView.findViewById(R.id.button2);
        btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mListener.onDrugSelected("BUTTON 2");
            }
        });


        FloatingActionButton fab = (FloatingActionButton) fragmentView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        return  fragmentView;
    }

    public void onButtonPressed(String id) {
        if (mListener != null) {
            mListener.onDrugSelected(id);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDrugSelectionListener) {
            mListener = (OnDrugSelectionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnDrugSelectionListener {
        void onDrugSelected(String id);
    }
}
