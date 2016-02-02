package it.dedonatis.emanuele.drugstore.adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.models.DrugPackage;
import it.dedonatis.emanuele.drugstore.models.DrugSubpackage;
import it.dedonatis.emanuele.drugstore.utils.ImageUtils;

public class PackageTreeHolder extends TreeNode.BaseNodeViewHolder<DrugPackage> {

    public PackageTreeHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, DrugPackage pkg) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.item_package_treeview, null, false);
        TextView tvDescription = (TextView) view.findViewById(R.id.item_package_description);
        tvDescription.setText(pkg.getDescription());
        TextView tvDoses = (TextView) view.findViewById(R.id.item_package_total_doses);
        int doses = 0;
        for (DrugSubpackage subpackage: pkg.getSubpackages() ) {
            doses += subpackage.getDosesLeft();
        }
        tvDoses.setText(doses + " " + ((doses == 1) ? context.getString(R.string.dose).toLowerCase() : context.getString(R.string.doses).toLowerCase()));
        ImageView letter = (ImageView) view.findViewById(R.id.item_package_letter);
        letter.setImageDrawable(ImageUtils.generateRoundLetter(pkg.getDescription(), pkg.getDrugColor()));

        return view;
    }
}
