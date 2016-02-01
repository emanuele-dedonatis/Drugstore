package it.dedonatis.emanuele.drugstore.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.activities.AddDrugActivity;
import it.dedonatis.emanuele.drugstore.adapters.PackageRecyclerAdapter;
import it.dedonatis.emanuele.drugstore.data.DataContract.*;
import it.dedonatis.emanuele.drugstore.models.DrugPackage;
import it.dedonatis.emanuele.drugstore.models.DrugSubpackage;
import it.dedonatis.emanuele.drugstore.utils.DateUtils;


public class PackagesListFragment extends DialogFragment  implements LoaderManager.LoaderCallbacks<Cursor>, PackageRecyclerAdapter.PackageClickListener{
    private static final String ARG_DRUG_ID = "id";
    private static final String ARG_DRUG_NAME = "name";
    private static final String ARG_DRUG_API = "api";
    private static final String ARG_DRUG_COLOR = "color";
    private static final int PACKAGE_LOADER = 1;
    private static final int SUBPACKAGE_LOADER = 2;
    private static final String LOG_TAG = PackagesListFragment.class.getSimpleName();

    /***** CONTENT PROVIDER PROJECTION *****/
    private static final String[] PACKAGE_COLUMNS = {
            PackageEntry.TABLE_NAME + "." + PackageEntry._ID,
            PackageEntry.COLUMN_DRUG_ID,
            PackageEntry.COLUMN_DESCRIPTION,
            PackageEntry.COLUMN_DOSES,
            PackageEntry.COLUMN_IMAGE_URI
    };

    public static final int COL_PACKAGE_ID = 0;
    public static final int COL_PACKAGE_DRUG_ID = 1;
    public static final int COL_PACKAGE_DESCRIPTION = 2;
    public static final int COL_PACKAGE_DOSES = 3;
    public static final int COL_PACKAGE_IMAGE_URI = 4;

    private static final String[] SUBPACKAGE_COLUMNS = {
            SubpackageEntry.TABLE_NAME + "." + SubpackageEntry._ID,
            SubpackageEntry.COLUMN_DRUG_ID,
            SubpackageEntry.COLUMN_PACKAGE_ID,
            SubpackageEntry.COLUMN_DOSES_LEFT,
            SubpackageEntry.COLUMN_EXP_DATE
    };

    public static final int COL_SUBPACKAGE_ID = 0;
    public static final int COL_SUBPACKAGE_DRUG_ID = 1;
    public static final int COL_SUBPACKAGE_PACKAGE_ID = 2;
    public static final int COL_SUBPACKAGE_DOSES_LEFT = 3;
    public static final int COL_SUBPACKAGE_EXP_DATE = 4;

    private long mDrugId;
    private int mDrugColor;
    private String mDrugName;
    private String mDrugApi;
    private List<DrugPackage> mPackages = new ArrayList<DrugPackage>();
    private PackageRecyclerAdapter mAdapter;
    private Cursor cursorPackage = null;
    private Cursor cursorSubpackage = null;

    public PackagesListFragment() {}

    public static PackagesListFragment newInstance(long id, String name, String api, int drugColor) {
        PackagesListFragment fragment = new PackagesListFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_DRUG_ID, id);
        args.putString(ARG_DRUG_NAME, name);
        args.putString(ARG_DRUG_API, api);
        args.putInt(ARG_DRUG_COLOR, drugColor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDrugId = getArguments().getLong(ARG_DRUG_ID);
        mDrugColor = getArguments().getInt(ARG_DRUG_COLOR);
        mDrugName = getArguments().getString(ARG_DRUG_NAME);
        mDrugApi = getArguments().getString(ARG_DRUG_API);
        getLoaderManager().initLoader(PACKAGE_LOADER, null, this);
        getLoaderManager().initLoader(SUBPACKAGE_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_drug_packages, container, false);
        RecyclerView mRecyclerView = (RecyclerView)fragmentView.findViewById(R.id.package_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new PackageRecyclerAdapter(getActivity(), mPackages, this);
        mRecyclerView.setAdapter(mAdapter);


// Fab
        FloatingActionButton fab = (FloatingActionButton) fragmentView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddDrugActivity.class);
                intent.putExtra(AddDrugActivity.MESSAGE_DRUG_ID, mDrugId);
                intent.putExtra(AddDrugActivity.MESSAGE_DRUG_NAME, mDrugName);
                intent.putExtra(AddDrugActivity.MESSAGE_DRUG_API, mDrugApi);
                startActivity(intent);
            }
        });
        return  fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(PACKAGE_LOADER, null, this);
    }

    /***** LOADER CALLBACKS *****/
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case PACKAGE_LOADER:
                return new CursorLoader(getActivity(),
                        PackageEntry.buildPackagesFromDrug(mDrugId),
                        PACKAGE_COLUMNS,
                        null,
                        null,
                        PackageEntry._ID + " ASC");
            case SUBPACKAGE_LOADER:
                return new CursorLoader(getActivity(),
                        SubpackageEntry.buildSubackagesFromDrug(mDrugId),
                        SUBPACKAGE_COLUMNS,
                        null,
                        null,
                        SubpackageEntry.COLUMN_PACKAGE_ID + " ASC");
            default: return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case PACKAGE_LOADER:
                cursorPackage = data;
                joinCursors();
            case SUBPACKAGE_LOADER:
                cursorSubpackage = data;
                joinCursors();
                default: return;
        }
    }

    private void joinCursors() {
        if(cursorPackage != null && cursorSubpackage != null) {
            List<DrugSubpackage> subpackages = new ArrayList<DrugSubpackage>();
            while(cursorSubpackage.moveToNext()) {
                subpackages.add(new DrugSubpackage(
                        cursorSubpackage.getLong(COL_SUBPACKAGE_ID),
                        cursorSubpackage.getLong(COL_SUBPACKAGE_DRUG_ID),
                        cursorSubpackage.getLong(COL_SUBPACKAGE_PACKAGE_ID),
                        cursorSubpackage.getInt(COL_SUBPACKAGE_DOSES_LEFT),
                        DateUtils.fromDbStringToDate(cursorSubpackage.getString(COL_SUBPACKAGE_EXP_DATE))
                ));
            }

            mPackages.clear();
            while(cursorPackage.moveToNext()) {
                long packageId = cursorPackage.getLong(COL_PACKAGE_ID);
                List<DrugSubpackage> packageSubpackages = new ArrayList<DrugSubpackage>();
                for(DrugSubpackage subpackage : subpackages) {
                    if(subpackage.getPackageId() == packageId)
                        packageSubpackages.add(subpackage);
                }
                DrugPackage pkg = new DrugPackage(
                        cursorPackage.getLong(COL_PACKAGE_ID),
                        cursorPackage.getLong(COL_PACKAGE_DRUG_ID),
                        cursorPackage.getString(COL_PACKAGE_DESCRIPTION),
                        cursorPackage.getInt(COL_PACKAGE_DOSES),
                        cursorPackage.getString(COL_PACKAGE_IMAGE_URI),
                        mDrugColor,
                        packageSubpackages
                );
                mPackages.add(pkg);
            }
            mAdapter.notifyDataSetChanged();

            if(mPackages.size() == 0)  {
                getActivity().findViewById(R.id.empty_pack_list).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.package_list).setVisibility(View.GONE);
            }else {
                getActivity().findViewById(R.id.empty_pack_list).setVisibility(View.GONE);
                getActivity().findViewById(R.id.package_list).setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    /***** PackageRecyclerAdapter METHODS ****
    @Override
    public void onClickPackageUse(long packageId, int units) {
        int newUnits = units-1;
        if(newUnits >= 0) {
            ContentValues values = new ContentValues();
            values.put(PackageEntry.COLUMN_UNITS, newUnits);
            getActivity().getContentResolver().update(DataContract.PackageEntry.buildPackageUri(packageId), values, null, null);
            getLoaderManager().restartLoader(PACKAGE_LOADER, null, this);
        }
    }

    @Override
    public void onClickPackageDelete(final long packageId) {
        Log.v(LOG_TAG, "Request deleting package " + packageId);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setMessage(R.string.delete_question);
                builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().getContentResolver().delete(PackageEntry.buildPackageUri(packageId), null, null);
                        Log.v(LOG_TAG, "Package " + packageId + " deleted");
                        getLoaderManager().restartLoader(PACKAGE_LOADER, null, PackagesListFragment.this);
                    }
                });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }*/
}
