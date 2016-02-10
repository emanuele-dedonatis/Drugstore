package it.dedonatis.emanuele.drugstore.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.unnamed.b.atv.model.TreeNode;

import java.util.ArrayList;
import java.util.List;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.activities.AddDrugActivity;
import it.dedonatis.emanuele.drugstore.activities.PackagesActivity;
import it.dedonatis.emanuele.drugstore.adapters.DialogBottomAdapter;
import it.dedonatis.emanuele.drugstore.adapters.DrugRecyclerAdapter;
import it.dedonatis.emanuele.drugstore.data.DataContract;
import it.dedonatis.emanuele.drugstore.models.Drug;
import it.dedonatis.emanuele.drugstore.models.DrugPackage;
import it.dedonatis.emanuele.drugstore.models.DrugSubpackage;
import it.dedonatis.emanuele.drugstore.utils.Dialogs;
import it.dedonatis.emanuele.drugstore.utils.ShareUtils;

public class DrugsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, DrugRecyclerAdapter.DrugClickListener, SearchView.OnQueryTextListener {
    private static final String LOG_TAG = DrugsListFragment.class.getSimpleName();
    private static final int DRUG_LOADER = 0;

    /**
     * CONTENT PROVIDER PROJECTION
     **/
    private static final String[] DRUG_COLUMNS = {
            DataContract.DrugEntry.TABLE_NAME + "." + DataContract.DrugEntry._ID,
            DataContract.DrugEntry.COLUMN_NAME,
            DataContract.DrugEntry.COLUMN_API
    };
    public static final int COL_DRUG_ID = 0;
    public static final int COL_DRUG_NAME = 1;
    public static final int COL_DRUG_API = 2;





    private List<Drug> mDrugs = new ArrayList<>();
    private DrugRecyclerAdapter mAdapter;

    private String mCursorFilter;
    private SearchView mSearchView;

    public DrugsListFragment() {
    }

    public static DrugsListFragment newInstance() {
        DrugsListFragment fragment = new DrugsListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(DRUG_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_drugs_list, container, false);

        // Recycler view
        RecyclerView mRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.drugs_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new DrugRecyclerAdapter(getActivity(), mDrugs, this);
        mRecyclerView.setAdapter(mAdapter);

        // Fab
        FloatingActionButton fab = (FloatingActionButton) fragmentView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddDrugActivity.class);
                intent.putExtra(AddDrugActivity.MESSAGE_DRUG_ID, -1);
                startActivity(intent);
            }
        });
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(DRUG_LOADER, null, this);
    }

    /*****
     * CURSOR LOADER
     *****/
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri drugsUri;
        if (mCursorFilter != null) {
            drugsUri = DataContract.DrugEntry.buildDrugLikeNameOrApi(mCursorFilter);
        } else {
            drugsUri = DataContract.DrugEntry.CONTENT_URI;

        }
        String sortOrder = DataContract.DrugEntry.COLUMN_NAME + " ASC";
        return new CursorLoader(getActivity(), drugsUri, DRUG_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mDrugs.clear();
        while (data.moveToNext()) {
            Drug drug = new Drug(
                    data.getLong(COL_DRUG_ID),
                    data.getString(COL_DRUG_NAME),
                    data.getString(COL_DRUG_API)
            );
            mDrugs.add(drug);
        }
        mAdapter.notifyDataSetChanged();

        if (data.getCount() == 0) {
            getActivity().findViewById(R.id.empty_drug_list).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.drugs_recyclerview).setVisibility(View.GONE);
        } else {
            getActivity().findViewById(R.id.empty_drug_list).setVisibility(View.GONE);
            getActivity().findViewById(R.id.drugs_recyclerview).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    /*****
     * SEARCHVIEW METHODS
     *****/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_drugs, menu);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
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

    /**
     * RECYCLER LISTENER
     **/
    @Override
    public void onDrugClick(long drugId, String name, String api, int color) {
        Intent intent = new Intent(getActivity(), PackagesActivity.class);
        intent.putExtra(PackagesActivity.MESSAGE_DRUG_ID, drugId);
        intent.putExtra(PackagesActivity.MESSAGE_DRUG_NAME, name);
        intent.putExtra(PackagesActivity.MESSAGE_DRUG_API, api);
        intent.putExtra(PackagesActivity.MESSAGE_DRUG_COLOR, color);
        startActivity(intent);
    }

    @Override
    public void onDrugLongClick(final long drugId, final String name, final String api) {
        DialogPlus dialog = Dialogs.setupBottomDialog(getActivity(), new DialogBottomAdapter(getActivity()), new OnItemClickListener() {
            @Override
            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                Log.d(LOG_TAG, "Click on item " + position);
                switch (position) {
                    case DialogBottomAdapter.SHARE_POSITION:
                        ShareUtils.shareDrug(getActivity(), drugId, name, api);
                        dialog.dismiss();
                        break;
                    case DialogBottomAdapter.EDIT_POSITION:
                        showEditDialog(drugId, name, api);
                        dialog.dismiss();
                        break;
                    case DialogBottomAdapter.DELETE_POSITION:
                        requestConfirmDelete(drugId);
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }

    private void requestConfirmDelete(final long drugId) {
        new AlertDialogWrapper.Builder(getActivity())
                .setMessage(R.string.delete_question)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().getContentResolver().delete(DataContract.DrugEntry.buildDrugUri(drugId), null, null);
                        //getLoaderManager().restartLoader(DRUG_LOADER, null, DrugsListFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showEditDialog(final long drugId, final String name, final String api) {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.edit)
                .customView(R.layout.dialog_edit_drug, true)
                .positiveText(R.string.done)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    View view = dialog.getView();
                                    String newName = ((EditText) view.findViewById(R.id.dialog_edit_drug_name)).getText().toString();
                                    String newApi = ((EditText) view.findViewById(R.id.dialog_edit_drug_api)).getText().toString();
                                    if (newName.equals(name) && newApi.equals(api)) {
                                        return;
                                    } else {
                                        ContentValues values = new ContentValues();
                                        values.put(DataContract.DrugEntry.COLUMN_NAME, newName);
                                        values.put(DataContract.DrugEntry.COLUMN_API, newApi);
                                        getActivity().getContentResolver().update(DataContract.DrugEntry.buildDrugUri(drugId), values, null, null);

                                    }


                                }
                            }
                ).build();

        View view = dialog.getView();
        EditText drugName = (EditText) view.findViewById(R.id.dialog_edit_drug_name);
        EditText drugApi = (EditText) view.findViewById(R.id.dialog_edit_drug_api);
        drugName.setText(name);
        drugApi.setText(api);
        dialog.show();

    }


}

