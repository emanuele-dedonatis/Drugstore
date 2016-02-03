package it.dedonatis.emanuele.drugstore.holders;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.models.DrugPackage;
import it.dedonatis.emanuele.drugstore.models.DrugSubpackage;
import it.dedonatis.emanuele.drugstore.utils.ImageUtils;

public class AddSubpackageTreeHolder extends TreeNode.BaseNodeViewHolder<Void> {

    private OnAddSubpackageClickListener mListener;

    public AddSubpackageTreeHolder(Context context, OnAddSubpackageClickListener listener) {
        super(context);
        this.mListener = listener;
    }

    @Override
    public View createNodeView(final TreeNode node, Void data) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.item_add_subpackage_treeview, null, false);

        ImageView btnAdd = (ImageView) view.findViewById(R.id.item_subpackage_button_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickButtonAddSubpackage(node.getParent(), node);
            }
        });

        return view;
    }

    public interface OnAddSubpackageClickListener {
        public void onClickButtonAddSubpackage(TreeNode packageNode, TreeNode node);
    }
}
