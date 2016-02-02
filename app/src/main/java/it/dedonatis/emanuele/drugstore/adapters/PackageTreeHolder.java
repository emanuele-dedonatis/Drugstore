package it.dedonatis.emanuele.drugstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.models.DrugPackage;

public class PackageTreeHolder extends TreeNode.BaseNodeViewHolder<DrugPackage> {

    public PackageTreeHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, DrugPackage pkg) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.item_package_treeview, null, false);
        TextView tvValue = (TextView) view.findViewById(R.id.item_package_description);
        tvValue.setText(pkg.getDescription());

        return view;
    }
}
