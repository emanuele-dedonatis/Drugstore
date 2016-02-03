package it.dedonatis.emanuele.drugstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

import org.w3c.dom.Text;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.models.Drug;
import it.dedonatis.emanuele.drugstore.models.DrugSubpackage;
import it.dedonatis.emanuele.drugstore.utils.DateUtils;

public class SubpackageTreeHolder extends TreeNode.BaseNodeViewHolder<DrugSubpackage> {

    private final OnSubpackageClickListener mListener;
    private DrugSubpackage mSubpackage;
    private TreeNode mNode;
    private TextView mTvDoses;

    public SubpackageTreeHolder(Context context, OnSubpackageClickListener listener, TreeNode parent) {
        super(context);
        this.mListener = listener;
    }

    @Override
    public View createNodeView(final TreeNode node, final DrugSubpackage subpackage) {
        this.mNode = node;
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.item_subpackage_treeview, null, false);

        this.mSubpackage = subpackage;
        TextView tvExp = (TextView) view.findViewById(R.id.item_subpackage_exp);
        tvExp.setText(context.getString(R.string.exp) + " " + DateUtils.fromDateToEurString(subpackage.getExpirationDate()));
        mTvDoses = (TextView) view.findViewById(R.id.item_subpackage_doses);
        int doses = subpackage.getDosesLeft();
        mTvDoses.setText(doses + " " + ((doses == 1) ? context.getString(R.string.dose).toLowerCase() : context.getString(R.string.doses).toLowerCase()));
        Button btn = (Button) view.findViewById(R.id.item_subpackage_button_use);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickButtonUse(mNode, mSubpackage);
            }
        });        return view;
    }


    public void removeDosesLeft(int dosesToRemove) {
        int doses = mSubpackage.removeDosesLeft(dosesToRemove);
        mTvDoses.setText(doses + " " + ((doses == 1) ? context.getString(R.string.dose).toLowerCase() : context.getString(R.string.doses).toLowerCase()));
}

    public interface OnSubpackageClickListener {
        public void onClickButtonUse(TreeNode node, DrugSubpackage subpackageId);
    }
}
