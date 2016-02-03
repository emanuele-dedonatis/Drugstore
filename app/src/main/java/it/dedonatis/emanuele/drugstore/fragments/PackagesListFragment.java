package it.dedonatis.emanuele.drugstore.fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.holders.AddSubpackageTreeHolder;
import it.dedonatis.emanuele.drugstore.holders.PackageTreeHolder;
import it.dedonatis.emanuele.drugstore.holders.SubpackageTreeHolder;
import it.dedonatis.emanuele.drugstore.data.DataContract;
import it.dedonatis.emanuele.drugstore.models.DrugPackage;
import it.dedonatis.emanuele.drugstore.models.DrugSubpackage;
import it.dedonatis.emanuele.drugstore.utils.DateUtils;


public class PackagesListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SubpackageTreeHolder.OnSubpackageClickListener, AddSubpackageTreeHolder.OnAddSubpackageClickListener {
    private static final String ARG_DRUG_ID = "id";
    private static final String ARG_DRUG_NAME = "name";
    private static final String ARG_DRUG_API = "api";
    private static final String ARG_DRUG_COLOR = "color";
    private static final String LOG_TAG = PackagesListFragment.class.getSimpleName();
    private static final int PACKAGE_LOADER = 1;

    /*****
     * CONTENT PROVIDER PROJECTION
     *****/
    private static final String[] PACKAGE_COLUMNS = {
            DataContract.PackageEntry.TABLE_NAME + "." + DataContract.PackageEntry._ID,
            DataContract.PackageEntry.COLUMN_DRUG_ID,
            DataContract.PackageEntry.COLUMN_DESCRIPTION,
            DataContract.PackageEntry.COLUMN_DOSES,
            DataContract.PackageEntry.COLUMN_IMAGE_URI
    };

    public static final int COL_PACKAGE_ID = 0;
    public static final int COL_PACKAGE_DRUG_ID = 1;
    public static final int COL_PACKAGE_DESCRIPTION = 2;
    public static final int COL_PACKAGE_DOSES = 3;
    public static final int COL_PACKAGE_IMAGE_URI = 4;

    private static final String[] SUBPACKAGE_COLUMNS = {
            DataContract.SubpackageEntry.TABLE_NAME + "." + DataContract.SubpackageEntry._ID,
            DataContract.SubpackageEntry.COLUMN_DRUG_ID,
            DataContract.SubpackageEntry.COLUMN_PACKAGE_ID,
            DataContract.SubpackageEntry.COLUMN_DOSES_LEFT,
            DataContract.SubpackageEntry.COLUMN_EXP_DATE
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

    ViewGroup mContainerView;

    public PackagesListFragment() {
    }

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(PACKAGE_LOADER, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDrugId = getArguments().getLong(ARG_DRUG_ID);
        mDrugColor = getArguments().getInt(ARG_DRUG_COLOR);
        mDrugName = getArguments().getString(ARG_DRUG_NAME);
        mDrugApi = getArguments().getString(ARG_DRUG_API);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_drug_packages, container, false);
        mContainerView = (ViewGroup) fragmentView.findViewById(R.id.drug_packages_list);

        return fragmentView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case PACKAGE_LOADER:
                return new CursorLoader(getActivity(),
                        DataContract.PackageEntry.buildPackagesFromDrug(mDrugId),
                        PACKAGE_COLUMNS,
                        null,
                        null,
                        DataContract.PackageEntry.COLUMN_DESCRIPTION + " ASC");
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case PACKAGE_LOADER:
                createTree(data);
            default:
                return;
        }
    }


    private void createTree(final Cursor packageCursor) {
        // Create root node
        final TreeNode root = TreeNode.root();

        // Get all packages
        final List<DrugPackage> packages = new ArrayList<DrugPackage>();
        while (packageCursor.moveToNext()) {
            long packageId = packageCursor.getLong(COL_PACKAGE_ID);


            List<DrugSubpackage> packageSubpackages = new ArrayList<DrugSubpackage>();
            List<TreeNode> subpackageNodes = new ArrayList<TreeNode>();

            DrugPackage pkg = new DrugPackage(
                    packageCursor.getLong(COL_PACKAGE_ID),
                    packageCursor.getLong(COL_PACKAGE_DRUG_ID),
                    packageCursor.getString(COL_PACKAGE_DESCRIPTION),
                    packageCursor.getInt(COL_PACKAGE_DOSES),
                    packageCursor.getString(COL_PACKAGE_IMAGE_URI),
                    mDrugColor,
                    packageSubpackages
            );

            TreeNode pkgNode = new TreeNode(pkg).setViewHolder(new PackageTreeHolder(getActivity()));

            Cursor subpackageCursor = getActivity().getContentResolver().query(
                    DataContract.SubpackageEntry.buildSubackagesFromPackage(packageId),
                    SUBPACKAGE_COLUMNS,
                    null,
                    null,
                    DataContract.SubpackageEntry.COLUMN_EXP_DATE + " ASC"
            );
            while (subpackageCursor.moveToNext()) {
                DrugSubpackage subpackage = new DrugSubpackage(
                        subpackageCursor.getLong(COL_SUBPACKAGE_ID),
                        subpackageCursor.getLong(COL_SUBPACKAGE_DRUG_ID),
                        subpackageCursor.getLong(COL_SUBPACKAGE_PACKAGE_ID),
                        subpackageCursor.getInt(COL_SUBPACKAGE_DOSES_LEFT),
                        DateUtils.fromDbStringToDate(subpackageCursor.getString(COL_SUBPACKAGE_EXP_DATE))
                );
                subpackageNodes.add(new TreeNode(subpackage).setViewHolder(new SubpackageTreeHolder(getActivity(), this, pkgNode)));
                packageSubpackages.add(subpackage);
            }
            pkgNode.addChildren(subpackageNodes);
            pkgNode.addChild(new TreeNode(null).setViewHolder(new AddSubpackageTreeHolder(getActivity(), this)));
            root.addChild(pkgNode);
            packages.add(pkg);
        }

        // Add treeview to view
        AndroidTreeView tView = new AndroidTreeView(getActivity(), root);
        tView.setDefaultAnimation(true);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleDivided, true);
        mContainerView.addView(tView.getView());


        // Destroy loaders
        getLoaderManager().destroyLoader(PACKAGE_LOADER);


            /* If empty show empty image
            if (packages.size() == 0) {
                findViewById(R.id.empty_pack_list).setVisibility(View.VISIBLE);
                findViewById(R.id.packages_recycleview).setVisibility(View.GONE);
            } else {
                findViewById(R.id.empty_pack_list).setVisibility(View.GONE);
                findViewById(R.id.packages_recycleview).setVisibility(View.VISIBLE);
            }*/
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClickButtonUse(TreeNode node, DrugSubpackage subpackage) {
        int dosesToRemove = 1;
        int newDoses = subpackage.getDosesLeft() - dosesToRemove;
        if (newDoses >= 0) {
            ContentValues values = new ContentValues();
            values.put(DataContract.SubpackageEntry.COLUMN_DOSES_LEFT, newDoses);
            getActivity().getContentResolver().update(
                    DataContract.SubpackageEntry.buildSubpackageUri(subpackage.getId()),
                    values,
                    null,
                    null
            );
            subpackage.setDosesLeft(newDoses);
            ((SubpackageTreeHolder) node.getViewHolder()).updateDosesLeft();
            ((PackageTreeHolder) node.getParent().getViewHolder()).updateDosesLeft();
        }
    }

    @Override
    public void onClickButtonAddSubpackage(final TreeNode packageNode, final TreeNode btnNode) {
        final PackageTreeHolder packageTreeHolder = (PackageTreeHolder) packageNode.getViewHolder();
        final DrugPackage drugPackage = packageTreeHolder.getPackage();
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        final SubpackageTreeHolder.OnSubpackageClickListener listener = this;
        final AndroidTreeView treeView = packageNode.getViewHolder().getTreeView();
        DatePickerDialog datePicker = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        DrugSubpackage subpackage = new DrugSubpackage(
                                0,
                                drugPackage.getDrugID(),
                                drugPackage.getId(),
                                drugPackage.getDefaultDoses(),
                                DateUtils.fromDbStringToDate(year + String.format("%2d", monthOfYear) + dayOfMonth)
                        );

                        ContentValues subpack = new ContentValues();
                        subpack.put(DataContract.SubpackageEntry.COLUMN_DRUG_ID, subpackage.getDrugID());
                        subpack.put(DataContract.SubpackageEntry.COLUMN_PACKAGE_ID, subpackage.getPackageId());
                        subpack.put(DataContract.SubpackageEntry.COLUMN_DOSES_LEFT, subpackage.getDosesLeft());
                        subpack.put(DataContract.SubpackageEntry.COLUMN_EXP_DATE, DateUtils.fromDateToDbString(subpackage.getExpirationDate()));

                        getActivity().getContentResolver().insert(
                                DataContract.SubpackageEntry.CONTENT_URI,
                                subpack
                        );

                        treeView.removeNode(btnNode);
                        treeView.addNode(packageNode, new TreeNode(subpackage).setViewHolder(new SubpackageTreeHolder(getActivity(), listener, packageNode)));
                        treeView.addNode(packageNode, btnNode);
                        drugPackage.getSubpackages().add(subpackage);
                        packageTreeHolder.updateDosesLeft();
                    }
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH) + 1);
        datePicker.setCancelable(true);
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePicker.setTitle(getString(R.string.select_exp_date));
        datePicker.show();
    }

    /***** PackageRecyclerAdapter METHODS ****
     @Override public void onClickPackageUse(long packageId, int units) {
     int newUnits = units-1;
     if(newUnits >= 0) {
     ContentValues values = new ContentValues();
     values.put(PackageEntry.COLUMN_UNITS, newUnits);
     getActivity().getContentResolver().update(DataContract.PackageEntry.buildPackageUri(packageId), values, null, null);
     getLoaderManager().restartLoader(PACKAGE_LOADER, null, this);
     }
     }

     @Override public void onClickPackageDelete(final long packageId) {
     Log.v(LOG_TAG, "Request deleting package " + packageId);
     AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
     builder.setMessage(R.string.delete_question);
     builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
     @Override public void onClick(DialogInterface dialog, int which) {
     getActivity().getContentResolver().delete(PackageEntry.buildPackageUri(packageId), null, null);
     Log.v(LOG_TAG, "Package " + packageId + " deleted");
     getLoaderManager().restartLoader(PACKAGE_LOADER, null, PackagesListFragment.this);
     }
     });
     builder.setNegativeButton(R.string.cancel, null);
     builder.show();
     }*/


}
