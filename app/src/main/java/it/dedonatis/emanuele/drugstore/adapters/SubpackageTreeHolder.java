package it.dedonatis.emanuele.drugstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.models.DrugSubpackage;
import it.dedonatis.emanuele.drugstore.utils.DateUtils;

public class SubpackageTreeHolder extends TreeNode.BaseNodeViewHolder<DrugSubpackage> {

    private final OnSubpackageClickListener mListener;

    public SubpackageTreeHolder(Context context, OnSubpackageClickListener listener) {
        super(context);
        this.mListener = listener;
    }

    @Override
    public View createNodeView(TreeNode node, final DrugSubpackage subpackage) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.item_subpackage_treeview, null, false);
        TextView tvExp = (TextView) view.findViewById(R.id.item_subpackage_exp);
        tvExp.setText(context.getString(R.string.exp) + " " + DateUtils.fromDateToEurString(subpackage.getExpirationDate()));
        TextView tvDoses = (TextView) view.findViewById(R.id.item_subpackage_doses);
        int doses = subpackage.getDosesLeft();
        tvDoses.setText(doses + " " + ((doses == 1) ? context.getString(R.string.dose).toLowerCase() : context.getString(R.string.doses).toLowerCase()));
        Button btn = (Button) view.findViewById(R.id.item_subpackage_button_use);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickButtonUse(subpackage.getId());
            }
        });
        return view;
    }

    public interface OnSubpackageClickListener {
        public void onClickButtonUse(long subpackageId);
    }
}
