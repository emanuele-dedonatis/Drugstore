package it.dedonatis.emanuele.drugstore.holders;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.models.Drug;
import it.dedonatis.emanuele.drugstore.models.DrugSubpackage;
import it.dedonatis.emanuele.drugstore.services.NotifyExpDate;
import it.dedonatis.emanuele.drugstore.utils.DateUtils;

public class SubpackageTreeHolder extends TreeNode.BaseNodeViewHolder<DrugSubpackage> {

    private final OnSubpackageClickListener mListener;
    private DrugSubpackage mSubpackage;
    private TreeNode mNode;
    private TextView mTvDoses;
    private TextView mTvExp;
    private Button mBtnUse;

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
        mTvExp = (TextView) view.findViewById(R.id.item_subpackage_exp);
        mTvDoses = (TextView) view.findViewById(R.id.item_subpackage_doses);
        mBtnUse = (Button) view.findViewById(R.id.item_subpackage_button_use);
        mBtnUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickButtonUse(mNode, mSubpackage);
            }
        });

        update();

        return view;
    }


    public void updateDosesLeft() {
        int doses = mSubpackage.getDosesLeft();
        mTvDoses.setText(doses + " " + ((doses == 1) ? context.getString(R.string.dose).toLowerCase() : context.getString(R.string.doses).toLowerCase()));

        if(doses < 3)
            mTvDoses.setTextColor(Color.parseColor("#B71C1C"));

        if (doses <= 0) {
            mBtnUse.setEnabled(false);
        }
    }

    public void updateExpDate() {
        Date expDate = mSubpackage.getExpirationDate();
        mTvExp.setText(context.getString(R.string.exp) + " " + DateUtils.fromDateToEurString(expDate));

        Calendar nextDays = Calendar.getInstance();
        nextDays.setTime(new Date());

        Calendar expCalendar = Calendar.getInstance();
        expCalendar.setTime(expDate);

        nextDays.add(Calendar.DATE, NotifyExpDate.NEXT_DAYS);
        if (expCalendar.before(nextDays)) {
            mTvExp.setTextColor(Color.parseColor("#B71C1C"));
        }
    }

    public void updateParentDosesLeft() {
        PackageTreeHolder parentHolder = (PackageTreeHolder) mNode.getParent().getViewHolder();
        if(parentHolder != null)
            parentHolder.updateDosesLeft();
    }

    public void update() {
        updateDosesLeft();
        updateExpDate();
    }

    public void updateAll() {
        update();
        updateParentDosesLeft();
    }

    public DrugSubpackage getSubpackage() {
        return this.mSubpackage;
    }

    public interface OnSubpackageClickListener {
        public void onClickButtonUse(TreeNode node, DrugSubpackage subpackageId);
    }
}
