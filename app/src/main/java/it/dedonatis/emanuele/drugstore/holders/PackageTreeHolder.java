package it.dedonatis.emanuele.drugstore.holders;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.GridHolder;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.unnamed.b.atv.model.TreeNode;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.adapters.DialogImageAdapter;
import it.dedonatis.emanuele.drugstore.models.DrugPackage;
import it.dedonatis.emanuele.drugstore.models.DrugSubpackage;
import it.dedonatis.emanuele.drugstore.utils.ImageUtils;

public class PackageTreeHolder extends TreeNode.BaseNodeViewHolder<DrugPackage> {

    private DrugPackage mPkg;
    private ImageView mArrow;
    private TextView mTvDoses;

    public PackageTreeHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, final DrugPackage pkg) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.item_package_treeview, null, false);
        this.mPkg = pkg;
        TextView tvDescription = (TextView) view.findViewById(R.id.item_package_description);
        tvDescription.setText(pkg.getDescription());
        mTvDoses = (TextView) view.findViewById(R.id.item_package_total_doses_number);
        int doses = 0;
        for (DrugSubpackage subpackage : pkg.getSubpackages()) {
            doses += subpackage.getDosesLeft();
        }
        mTvDoses.setText(doses + "");
        TextView tvDosesString = (TextView) view.findViewById(R.id.item_package_total_doses_string);
        tvDosesString.setText((doses == 1) ? context.getString(R.string.dose).toLowerCase() : context.getString(R.string.doses).toLowerCase());

        ImageView letter = (ImageView) view.findViewById(R.id.item_package_letter);
        letter.setImageDrawable(ImageUtils.generateRoundLetter(pkg.getDescription(), pkg.getDrugColor()));

        ImageView image = (ImageView) view.findViewById(R.id.item_package_image);
        if (pkg.getImageUri() != null) {
            Log.v("PACKAGE TREE HOLDER", "set image uri " + pkg.getImageUri().toString());
            image.setImageURI(Uri.parse(pkg.getImageUri().toString()));
            image.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {

                                             Uri imageUri = pkg.getImageUri();
                                             if (imageUri != null) {
                                                 DialogPlus dialog = DialogPlus.newDialog(context)
                                                         .setContentHolder(new GridHolder(1))
                                                         .setCancelable(true)
                                                         .setAdapter(new DialogImageAdapter(context, pkg.getImageUri()))
                                                         .setGravity(Gravity.CENTER)
                                                         .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                         .setOnItemClickListener(new OnItemClickListener() {
                                                             @Override
                                                             public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                                                 dialog.dismiss();
                                                             }
                                                         })
                                                         .create();
                                                 dialog.show();
                                             }
                                         }
                                     }

                );
            }else{
                Log.v("PACKAGE TREE HOLDER", "image uri null");
                image.setVisibility(View.GONE);
            }
            mArrow = (ImageView) view.findViewById(R.id.item_package_arrow);
            return view;
        }


        @Override
        public void toggle ( boolean active){
            if (active)
                mArrow.animate().rotation(180).start();
            else
                mArrow.animate().rotation(0).start();
        }

    public DrugPackage getPackage() {
        return mPkg;
    }

    public void updateDosesLeft() {
        mTvDoses.setText(mPkg.getAllDosesLeft() + "");
    }

    public void removeSubpackage(TreeNode node) {
        DrugSubpackage subpackage = ((SubpackageTreeHolder) node.getViewHolder()).getSubpackage();
        mPkg.getSubpackages().remove(subpackage);
        getTreeView().removeNode(node);
        updateDosesLeft();


    }

}
