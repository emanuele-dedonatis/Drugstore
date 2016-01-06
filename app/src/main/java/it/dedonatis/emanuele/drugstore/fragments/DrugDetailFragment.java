package it.dedonatis.emanuele.drugstore.fragments;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import it.dedonatis.emanuele.drugstore.data.DrugContract.*;
import it.dedonatis.emanuele.drugstore.models.Drug;
import it.dedonatis.emanuele.drugstore.models.DrugPackage;


public class DrugDetailFragment extends DialogFragment {
    private static final String ARG_DRUG_ID = "id";
    private static final String LOG_TAG = DrugDetailFragment.class.getSimpleName();

    private static final String[] PACKAGE_COLUMNS = {
            PackageEntry.TABLE_NAME + "." + PackageEntry._ID,
            PackageEntry.COLUMN_DRUG,
            PackageEntry.COLUMN_DESCRIPTION,
            PackageEntry.COLUMN_UNITS,
            PackageEntry.COLUMN_UNITS_LEFT,
            PackageEntry.COLUMN_EXPIRATION_DATE,
    };
    public static final int COL_PACKAGE_ID = 0;
    public static final int COL_DRUG = 1;
    public static final int COL_PACKAGE_DESCRIPTION = 2;
    public static final int COL_PACKAGE_UNITS = 3;
    public static final int COL_PACKAGE_UNITS_LEFT = 4;
    public static final int COL_PACKAGE_EXPIRATION_DATE = 5;

    private long drugId;
    private List<DrugPackage> packages;

    public DrugDetailFragment() {}

    public static DrugDetailFragment newInstance(long id) {
        DrugDetailFragment fragment = new DrugDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_DRUG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            drugId = getArguments().getLong(ARG_DRUG_ID);
            Cursor cursor = getContext().getContentResolver().query(PackageEntry.buildPackagesFromDrug(drugId), PACKAGE_COLUMNS,null,null,null);
            packages = new ArrayList<DrugPackage>();
            while(cursor.moveToNext()) {
                DrugPackage pkg = new DrugPackage(cursor.getLong(COL_DRUG),
                        cursor.getString(COL_PACKAGE_DESCRIPTION),
                        cursor.getInt(COL_PACKAGE_UNITS),
                        cursor.getInt(COL_PACKAGE_UNITS_LEFT),
                        cursor.getInt(COL_PACKAGE_EXPIRATION_DATE)
                        );
                packages.add(pkg);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_drug_detail, container, false);
        //TextView tv = (TextView) fragmentView.findViewById(R.id.detail_fragment_tv);
        //tv.setText(drugId);

        RecyclerView mRecyclerView = (RecyclerView)fragmentView.findViewById(R.id.package_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        PackageRecyclerAdapter mAdapter = new PackageRecyclerAdapter(packages);
        mRecyclerView.setAdapter(mAdapter);
        return  fragmentView;
    }
}
