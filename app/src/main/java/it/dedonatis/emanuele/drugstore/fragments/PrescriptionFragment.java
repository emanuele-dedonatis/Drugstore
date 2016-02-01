package it.dedonatis.emanuele.drugstore.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.adapters.PrescriptionsRecyclerAdapter;
import it.dedonatis.emanuele.drugstore.data.DataContract;
import it.dedonatis.emanuele.drugstore.models.Prescription;

public class PrescriptionFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,PrescriptionsRecyclerAdapter.PrescriptionClickListener {
    private static final int PRESC_LOADER = 1;
    private static final String LOG_TAG = PrescriptionFragment.class.getSimpleName();

    /***** CONTENT PROVIDER PROJECTION *****/
    private static final String[] PRESC_COLUMNS = {
            DataContract.PrescriptionEntry.TABLE_NAME + "." + DataContract.PrescriptionEntry._ID,
            DataContract.PrescriptionEntry.COLUMN_DRUG,
            DataContract.PrescriptionEntry.COLUMN_PACKAGE,
            DataContract.PrescriptionEntry.COLUMN_HOW_MUCH,
            DataContract.PrescriptionEntry.COLUMN_EVERY,
            DataContract.PrescriptionEntry.COLUMN_HOUR,
            DataContract.PrescriptionEntry.COLUMN_UNTIL
    };
    public static final int COL_PRESC_ID = 0;
    public static final int COL_DRUG_ID = 1;
    public static final int COL_PACKAGE_ID = 2;
    public static final int COL_PRESC_HOW_MUCH = 3;
    public static final int COL_PRESC_EVERY = 4;
    public static final int COL_PRESC_HOUR = 5;
    public static final int COL_PRESC_UNTIL = 6;

    private List<Prescription> mPrescriptions = new ArrayList<Prescription>();
    private PrescriptionsRecyclerAdapter mAdapter;
    public PrescriptionFragment() {}


    public static PrescriptionFragment newInstance() {
        PrescriptionFragment fragment = new PrescriptionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(PRESC_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_prescription, container, false);
        RecyclerView mRecyclerView = (RecyclerView)fragmentView.findViewById(R.id.presc_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new PrescriptionsRecyclerAdapter(getActivity(), mPrescriptions, this);
        mRecyclerView.setAdapter(mAdapter);
        return  fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(PRESC_LOADER, null, this);
    }

    /** PACKAGE LISTNER **/
    @Override
    public void onClickPackageDelete(long prescId) {
        Log.v(LOG_TAG, "Delete " + prescId);
    }

    @Override
    public void onClickPackageEdit(long prescId) {
        Log.v(LOG_TAG, "Edit " + prescId);

    }

    /** CURSOR LOADER **/
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), DataContract.PrescriptionEntry.CONTENT_URI, PRESC_COLUMNS, null, null, DataContract.PrescriptionEntry.COLUMN_UNTIL + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPrescriptions.clear();
        while(data.moveToNext()) {
            Prescription prescription = new Prescription(
                data.getLong(COL_PRESC_ID),
                    data.getLong(COL_DRUG_ID),
                    "NAME",
                    "API",
                    data.getLong(COL_PACKAGE_ID),
                    "DESCRIPTION",
                    data.getInt(COL_PRESC_HOW_MUCH),
                    data.getInt(COL_PRESC_EVERY),
                    data.getInt(COL_PRESC_HOUR),
                    data.getInt(COL_PRESC_UNTIL),
                    null
            );
            mPrescriptions.add(prescription);
        }
        mAdapter.notifyDataSetChanged();

        if(data.getCount() == 0)  {
            getActivity().findViewById(R.id.empty_prescriptions_list).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.presc_list).setVisibility(View.GONE);
        }else {
            getActivity().findViewById(R.id.empty_prescriptions_list).setVisibility(View.GONE);
            getActivity().findViewById(R.id.presc_list).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
