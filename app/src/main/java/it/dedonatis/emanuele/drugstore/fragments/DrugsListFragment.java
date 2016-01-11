package it.dedonatis.emanuele.drugstore.fragments;

import android.content.DialogInterface;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.adapters.DrugsCursorAdapter;
import it.dedonatis.emanuele.drugstore.data.DrugContract;

public class DrugsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemLongClickListener {
    private static final String LOG_TAG = DrugsListFragment.class.getSimpleName();
    private static final int DRUG_LOADER = 0;

    private OnDrugSelectionListener mListener;
    private DrugsCursorAdapter drugsCursorAdapter;

    private static final String[] DRUG_COLUMNS = {
            DrugContract.DrugEntry.TABLE_NAME + "." + DrugContract.DrugEntry._ID,
            DrugContract.DrugEntry.COLUMN_NAME,
            DrugContract.DrugEntry.COLUMN_API
    };
    public static final int COL_DRUG_ID = 0;
    public static final int COL_DRUG_NAME = 1;
    public static final int COL_DRUG_API = 2;

    public DrugsListFragment() {}

    public static DrugsListFragment newInstance() {
        DrugsListFragment fragment = new DrugsListFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDrugSelectionListener) {
            mListener = (OnDrugSelectionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnDrugSelectionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(DRUG_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_drug_list, container, false);

        ListView drugListView = (ListView) fragmentView.findViewById(R.id.drugs_listview);
        drugsCursorAdapter = new DrugsCursorAdapter(getActivity());
        drugListView.setAdapter(drugsCursorAdapter);
        drugListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    if (mListener != null) {
                        mListener.onDrugSelected(
                                view.findViewById(R.id.item_drug_name),
                                view.findViewById(R.id.item_drug_api),
                                view.findViewById(R.id.item_drug_letter),
                                cursor.getLong(COL_DRUG_ID),
                                cursor.getString(COL_DRUG_NAME),
                                cursor.getString(COL_DRUG_API));
                    }
                }
            }
        });

        drugListView.setOnItemLongClickListener(this);


        return  fragmentView;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
        if (cursor != null) {
            final long drugId = cursor.getLong(COL_DRUG_ID);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
            builder.setMessage(R.string.delete_question);
            builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().getContentResolver().delete(DrugContract.DrugEntry.buildDrugUri(drugId), null, null);
                    getLoaderManager().restartLoader(DRUG_LOADER, null, DrugsListFragment.this);
                }
            });
            builder.setNegativeButton(R.string.cancel, null);
            builder.show();
        }
        return true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /***** CURSOR LOADER *****/
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

    /***** FRAGMENT LISTENER *****/
    public interface OnDrugSelectionListener {
        void onDrugSelected(View nameView, View apiView, View colorView, long id, String name, String api);
    }

}
