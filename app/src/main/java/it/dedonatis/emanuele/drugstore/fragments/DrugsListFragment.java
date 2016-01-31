package it.dedonatis.emanuele.drugstore.fragments;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.support.design.widget.FloatingActionButton;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.activities.AddDrugActivity;
import it.dedonatis.emanuele.drugstore.adapters.DrugsCursorAdapter;
import it.dedonatis.emanuele.drugstore.data.DrugContract;

public class DrugsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, SearchView.OnQueryTextListener {
    private static final String LOG_TAG = DrugsListFragment.class.getSimpleName();
    private static final int DRUG_LOADER = 0;

    private OnDrugSelectionListener mListener;
    private DrugsCursorAdapter drugsCursorAdapter;
    private String mCursorFilter;
    private SearchView mSearchView;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() instanceof OnDrugSelectionListener) {
            mListener = (OnDrugSelectionListener) getActivity();
        } else {
            throw new RuntimeException(getActivity().toString() + " must implement OnDrugSelectionListener");
        }

        setHasOptionsMenu(true);
        getLoaderManager().initLoader(DRUG_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_drug_list, container, false);

        ListView drugListView = (ListView) fragmentView.findViewById(R.id.drugs_listview);
        drugsCursorAdapter = new DrugsCursorAdapter(getActivity());
        drugListView.setAdapter(drugsCursorAdapter);
        drugListView.setOnItemClickListener(this);
        drugListView.setOnItemLongClickListener(this);

        // Fab
        FloatingActionButton fab = (FloatingActionButton) fragmentView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddDrugActivity.class);
                startActivity(intent);
            }
        });

        return  fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Log.v(LOG_TAG, "Click item " + position);
        Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
        if (cursor != null) {
            if (mListener != null) {
                Log.v(LOG_TAG, "Ready to call listener");
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
        Uri drugsUri;
        if(mCursorFilter != null) {
            drugsUri = DrugContract.DrugEntry.buildDrugLikeNameOrApi(mCursorFilter);
        } else {
            drugsUri = DrugContract.DrugEntry.CONTENT_URI;

        }
        String sortOrder = DrugContract.DrugEntry.COLUMN_NAME + " ASC";
        return new CursorLoader(getActivity(), drugsUri, DRUG_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        drugsCursorAdapter.swapCursor(data);
        if(data.getCount() == 0)  {
            getActivity().findViewById(R.id.empty_drug_list).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.drugs_listview).setVisibility(View.GONE);
        }else {
            getActivity().findViewById(R.id.empty_drug_list).setVisibility(View.GONE);
            getActivity().findViewById(R.id.drugs_listview).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        drugsCursorAdapter.swapCursor(null);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.v(LOG_TAG, query);
        return false;
    }

    /***** SEARCHVIEW METHODS *****/
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_drugs, menu);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
        // Don't do anything if the filter hasn't actually changed.
        // Prevents restarting the loader when restoring state.
        if (mCursorFilter == null && newFilter == null) {
            return true;
        }
        if (mCursorFilter != null && mCursorFilter.equals(newFilter)) {
            return true;
        }
        mCursorFilter = newFilter;
        getLoaderManager().restartLoader(0, null, this);
        return false;
    }

    /***** FRAGMENT LISTENER *****/
    public interface OnDrugSelectionListener {
        void onDrugSelected(View nameView, View apiView, View colorView, long id, String name, String api);
    }

}
