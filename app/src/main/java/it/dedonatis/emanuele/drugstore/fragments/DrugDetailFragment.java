package it.dedonatis.emanuele.drugstore.fragments;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.adapters.PackageRecyclerAdapter;
import it.dedonatis.emanuele.drugstore.data.DrugContract;
import it.dedonatis.emanuele.drugstore.data.DrugContract.*;
import it.dedonatis.emanuele.drugstore.models.Drug;
import it.dedonatis.emanuele.drugstore.models.DrugPackage;
import me.drakeet.materialdialog.MaterialDialog;


public class DrugDetailFragment extends DialogFragment  implements LoaderManager.LoaderCallbacks<Cursor>, PackageRecyclerAdapter.PackageClickListener{
    private static final String ARG_DRUG_ID = "id";
    private static final String ARG_DRUG_COLOR = "color";
    private static final int PACKAGE_LOADER = 1;
    private static final String LOG_TAG = DrugDetailFragment.class.getSimpleName();

    /***** CONTENT PROVIDER PROJECTION *****/
    private static final String[] PACKAGE_COLUMNS = {
            PackageEntry.TABLE_NAME + "." + PackageEntry._ID,
            PackageEntry.COLUMN_DRUG,
            PackageEntry.COLUMN_DESCRIPTION,
            PackageEntry.COLUMN_UNITS,
            PackageEntry.COLUMN_IS_PERCENTAGE,
            PackageEntry.COLUMN_EXPIRATION_DATE,
            PackageEntry.COLUMN_IMAGE
    };
    public static final int COL_PACKAGE_ID = 0;
    public static final int COL_DRUG = 1;
    public static final int COL_PACKAGE_DESCRIPTION = 2;
    public static final int COL_PACKAGE_UNITS = 3;
    public static final int COL_PACKAGE_IS_PERECENTAGE = 4;
    public static final int COL_PACKAGE_EXPIRATION_DATE = 5;
    public static final int COL_PACKAGE_IMAGE = 6;

    private long mDrugId;
    private int mDrugColor;
    private List<DrugPackage> mPackages = new ArrayList<DrugPackage>();
    private PackageRecyclerAdapter mAdapter;

    public DrugDetailFragment() {}

    public static DrugDetailFragment newInstance(long id, int drugColor) {
        DrugDetailFragment fragment = new DrugDetailFragment();
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
        return new CursorLoader(getActivity(), PackageEntry.buildPackagesFromDrug(mDrugId), PACKAGE_COLUMNS, null, null, PackageEntry.COLUMN_EXPIRATION_DATE + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPackages.clear();
        while(data.moveToNext()) {
            DrugPackage pkg = new DrugPackage(
                    data.getLong(COL_PACKAGE_ID),
                    data.getLong(COL_DRUG),
                    data.getString(COL_PACKAGE_DESCRIPTION),
                    data.getInt(COL_PACKAGE_UNITS),
                    data.getInt(COL_PACKAGE_IS_PERECENTAGE) != 0,
                    data.getInt(COL_PACKAGE_EXPIRATION_DATE),
                    data.getString(COL_PACKAGE_IMAGE),
                    mDrugColor
            );
            mPackages.add(pkg);
        }
        mAdapter.notifyDataSetChanged();
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
            getActivity().getContentResolver().update(DrugContract.PackageEntry.buildPackageUri(packageId), values, null, null);
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
                        getLoaderManager().restartLoader(PACKAGE_LOADER, null, DrugDetailFragment.this);
                    }
                });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }
}
