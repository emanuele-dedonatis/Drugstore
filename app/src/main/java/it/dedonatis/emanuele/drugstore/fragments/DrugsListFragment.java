package it.dedonatis.emanuele.drugstore.fragments;

import android.content.ContentUris;
import android.support.v4.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.adapters.DrugsCursorAdapter;
import it.dedonatis.emanuele.drugstore.data.DrugContract;

public class DrugsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = DrugsListFragment.class.getSimpleName();
    private static final int DRUG_LOADER = 0;

    private static final String ARG_TEXT = "arg_text";

    private String mParamText;

    private OnDrugSelectionListener mListener;
    private DrugsCursorAdapter drugsCursorAdapter;

    public static final String TABLE_NAME = "packages";
    public static final String COLUMN_DRUG = "drug";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_UNITS = "units";
    public static final String COLUMN_UNITS_LEFT = "units_left";
    public static final String COLUMN_EXPIRATION_DATE = "expiration_date";

    private static final String[] DRUG_COLUMNS = {
            DrugContract.DrugEntry.TABLE_NAME + "." + DrugContract.DrugEntry._ID,
            DrugContract.DrugEntry.COLUMN_NAME,
            DrugContract.DrugEntry.COLUMN_API
    };
    public static final int COL_DRUG_ID = 0;
    public static final int COL_DRUG_NAME = 1;
    public static final int COL_DRUG_API = 2;

    public DrugsListFragment() {
        // Required empty public constructor
    }

    public static DrugsListFragment newInstance() {
        DrugsListFragment fragment = new DrugsListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(DRUG_LOADER, null, this);
        if (getArguments() != null) {
            mParamText = getArguments().getString(ARG_TEXT);
            Log.v(LOG_TAG, mParamText);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_drug_list, container, false);

        ListView drugLv = (ListView) fragmentView.findViewById(R.id.drugs_listview);
        drugsCursorAdapter = new DrugsCursorAdapter(getActivity());
        drugLv.setAdapter(drugsCursorAdapter);
        drugLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    if (mListener != null) {
                        mListener.onDrugSelected(cursor.getLong(COL_DRUG_ID), cursor.getString(COL_DRUG_NAME), cursor.getString(COL_DRUG_API));
                    }
                }
            }
        });

        return  fragmentView;
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


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = DrugContract.DrugEntry.COLUMN_NAME + " ASC";
        Uri drugsUri = DrugContract.DrugEntry.CONTENT_URI;
        return new CursorLoader(getActivity(), drugsUri, DRUG_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        drugsCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        drugsCursorAdapter.swapCursor(null);
    }

    public interface OnDrugSelectionListener {
        void onDrugSelected(long id, String name, String api);
    }

}
