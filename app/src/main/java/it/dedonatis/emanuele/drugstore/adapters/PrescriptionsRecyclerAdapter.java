package it.dedonatis.emanuele.drugstore.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.List;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.models.Prescription;
import it.dedonatis.emanuele.drugstore.utils.DateUtils;

public class PrescriptionsRecyclerAdapter extends RecyclerView.Adapter<PrescriptionsRecyclerAdapter.PrescriptionViewHolder>{

    private static final String LOG_TAG = PrescriptionsRecyclerAdapter.class.getSimpleName();
    private List<Prescription> mPrescriptions;
    private PrescriptionClickListener mPrescriptionClickListener;
    private Context mContext;

    public PrescriptionsRecyclerAdapter(Context context, List<Prescription> prescriptions, PrescriptionClickListener prescriptionClickListener) {
        this.mContext = context;
        this.mPrescriptionClickListener = prescriptionClickListener;
        this.mPrescriptions = prescriptions;
    }

    @Override
    public int getItemCount() {
        return mPrescriptions.size();
    }

    @Override
    public void onBindViewHolder(PrescriptionViewHolder prescriptionViewHolder, int i) {
        final Prescription presc = mPrescriptions.get(i);

        prescriptionViewHolder.tvDrugName.setText(presc.getDrug_name());
        prescriptionViewHolder.tvDrugApi.setText(presc.getDrug_api());
        prescriptionViewHolder.tvWhat.setText(presc.getHow_much() + " x " + presc.getPackageDescription());

        String whenString = mContext.getString(R.string.every) + " " + mContext.getString(R.string.day);
        //prescriptionViewHolder.tvWhen.setText(whenString + " @ " + DateUtils.intToHour(presc.getEveryInHour()));

       //prescriptionViewHolder.tvUntil.setText(mContext.getString(R.string.until) + " " + DateUtils.intToString(presc.getUntil()));

        if(presc.getImageUri() != null)
            prescriptionViewHolder.imageView.setImageURI(presc.getImageUri());
        else
            prescriptionViewHolder.imageView.setVisibility(View.GONE);

        String letter = "";
        try {
            letter = String.valueOf(presc.getDrug_name().charAt(0)).toUpperCase();
        }catch (StringIndexOutOfBoundsException e) {

        }
        TextDrawable drawable = TextDrawable.builder().buildRound(letter, presc.getDrugColor());
        prescriptionViewHolder.roundLetter.setImageDrawable(drawable);

        prescriptionViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrescriptionClickListener.onClickPackageDelete(presc.getPrescription_id());
            }
        });
        prescriptionViewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrescriptionClickListener.onClickPackageEdit(presc.getPrescription_id());
            }
        });
    }

    @Override
    public PrescriptionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_prescription_card, viewGroup, false);
        return new PrescriptionViewHolder(itemView);
    }

    public static class PrescriptionViewHolder extends RecyclerView.ViewHolder{
        protected TextView tvDrugName;
        protected TextView tvDrugApi;
        protected TextView tvWhat;
        protected TextView tvWhen;
        protected TextView tvUntil;
        protected Button btnEdit;
        protected Button btnDelete;
        protected ImageView imageView;
        protected ImageView roundLetter;

        public PrescriptionViewHolder(View v) {
            super(v);
            tvDrugName =  (TextView) v.findViewById(R.id.presc_drug_name);
            tvDrugApi =  (TextView) v.findViewById(R.id.presc_drug_api);
            tvWhat =  (TextView) v.findViewById(R.id.presc_what_tv);
            tvWhen =  (TextView) v.findViewById(R.id.pres_when_tv);
            tvUntil =  (TextView) v.findViewById(R.id.pres_until_tv);
            btnEdit = (Button) v.findViewById(R.id.button_edit);
            btnDelete = (Button) v.findViewById(R.id.button_delete);
            imageView = (ImageView) v.findViewById(R.id.package_image);
            roundLetter = (ImageView) v.findViewById(R.id.item_package_letter);
        }
    }

    public interface PrescriptionClickListener {
        public void onClickPackageDelete(long prescId);
        public void onClickPackageEdit(long prescId);
    }
}
