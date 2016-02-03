package it.dedonatis.emanuele.drugstore.holders;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

import java.util.List;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.models.Drug;
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
    public View createNodeView(TreeNode node, DrugPackage pkg) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.item_package_treeview, null, false);
        this.mPkg = pkg;
        TextView tvDescription = (TextView) view.findViewById(R.id.item_package_description);
        tvDescription.setText(pkg.getDescription());
        mTvDoses = (TextView) view.findViewById(R.id.item_package_total_doses_number);
        int doses = 0;
        for (DrugSubpackage subpackage: pkg.getSubpackages() ) {
            doses += subpackage.getDosesLeft();
        }
        mTvDoses.setText(doses + "");
        TextView tvDosesString = (TextView) view.findViewById(R.id.item_package_total_doses_string);
        tvDosesString.setText((doses == 1) ? context.getString(R.string.dose).toLowerCase() : context.getString(R.string.doses).toLowerCase());

        ImageView letter = (ImageView) view.findViewById(R.id.item_package_letter);
        letter.setImageDrawable(ImageUtils.generateRoundLetter(pkg.getDescription(), pkg.getDrugColor()));

        ImageView image = (ImageView) view.findViewById(R.id.item_package_image);
        if(pkg.getImageUri() != null) {
            image.setImageURI(Uri.parse(pkg.getImageUri().toString()));
        }else {
            image.setVisibility(View.GONE);
        }

        mArrow = (ImageView) view.findViewById(R.id.item_package_arrow);

        return view;
    }

    @Override
    public void toggle(boolean active) {
        if(active)
            mArrow.animate().rotation(180).start();
        else
            mArrow.animate().rotation(0).start();
    }

    public void removeDosesLeft(int dosesToRemove) {
        int doses = Integer.valueOf(mTvDoses.getText().toString());
        doses -= dosesToRemove;
        mTvDoses.setText(doses + "");
    }

    public DrugPackage getPackage() {
        return mPkg;
    }

    public void updateDosesLeft() {
        mTvDoses.setText(mPkg.getAllDosesLeft() + "");
    }
}
