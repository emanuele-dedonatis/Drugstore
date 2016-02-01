package it.dedonatis.emanuele.drugstore.fragments;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
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
import it.dedonatis.emanuele.drugstore.adapters.PackageRecyclerAdapter;
import it.dedonatis.emanuele.drugstore.data.DataContract;
import it.dedonatis.emanuele.drugstore.data.DataContract.*;
import it.dedonatis.emanuele.drugstore.models.DrugPackage;


public class PackagesListFragment extends DialogFragment  implements LoaderManager.LoaderCallbacks<Cursor>, PackageRecyclerAdapter.PackageClickListener{
    private static final String ARG_DRUG_ID = "id";
    private static final String ARG_DRUG_COLOR = "color";
    private static final int PACKAGE_LOADER = 1;
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
    public static final int COL_DRUG_ID = 1;
    public static final int COL_PACKAGE_DESCRIPTION = 2;
    public static final int COL_PACKAGE_DOSES = 3;
    public static final int COL_PACKAGE_IMAGE_URI = 4;

    private long mDrugId;
    private int mDrugColor;
    private List<DrugPackage> mPackages = new ArrayList<DrugPackage>();
    private PackageRecyclerAdapter mAdapter;

    public PackagesListFragment() {}

    public static PackagesListFragment newInstance(long id, int drugColor) {
        PackagesListFragment fragment = new PackagesListFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_DRUG_ID, id);
        args.putInt(ARG_DRUG_COLOR, drugColor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDrugId = getArguments().getLong(ARG_DRUG_ID);
        mDrugColor = getArguments().getInt(ARG_DRUG_COLOR);
        getLoaderManager().initLoader(PACKAGE_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_drug_detail, container, false);
        RecyclerView mRecyclerView = (RecyclerView)fragmentView.findViewById(R.id.package_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new PackageRecyclerAdapter(getActivity(), mPackages, this);
        mRecyclerView.setAdapter(mAdapter);
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
        return new CursorLoader(getActivity(), PackageEntry.buildPackagesFromDrug(mDrugId), PACKAGE_COLUMNS, null, null, PackageEntry.COLUMN_DESCRIPTION + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPackages.clear();
        while(data.moveToNext()) {
            DrugPackage pkg = new DrugPackage(
                    data.getLong(COL_PACKAGE_ID),
                    data.getLong(COL_DRUG_ID),
                    data.getString(COL_PACKAGE_DESCRIPTION),
                    data.getInt(COL_PACKAGE_DOSES),
                    data.getString(COL_PACKAGE_IMAGE_URI),
                    mDrugColor
            );
            mPackages.add(pkg);
        }
        mAdapter.notifyDataSetChanged();

        if(data.getCount() == 0)  {
            getActivity().findViewById(R.id.empty_pack_list).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.package_list).setVisibility(View.GONE);
        }else {
            getActivity().findViewById(R.id.empty_pack_list).setVisibility(View.GONE);
            getActivity().findViewById(R.id.package_list).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    /***** PackageRecyclerAdapter METHODS *****/
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
    }
}
