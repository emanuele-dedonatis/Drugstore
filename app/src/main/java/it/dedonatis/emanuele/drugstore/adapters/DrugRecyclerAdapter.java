package it.dedonatis.emanuele.drugstore.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.models.Drug;
import it.dedonatis.emanuele.drugstore.utils.ImageUtils;

public class DrugRecyclerAdapter extends RecyclerView.Adapter<DrugRecyclerAdapter.DrugViewHolder>{

    private static final String LOG_TAG = DrugRecyclerAdapter.class.getSimpleName();
    private List<Drug> mDrugs;
    private DrugClickListener mDrugClickListener;
    private Context mContext;

    public DrugRecyclerAdapter(Context context, List<Drug> drugs, DrugClickListener drugClickListener) {
        this.mContext = context;
        this.mDrugs = drugs;
        this.mDrugClickListener = drugClickListener;
    }

    @Override
    public int getItemCount() {
        return mDrugs.size();
    }

    @Override
    public void onBindViewHolder(DrugViewHolder drugViewHolder, int i) {
        final Drug drug = mDrugs.get(i);
        drugViewHolder.tvName.setText(drug.getName());
        drugViewHolder.tvApi.setText(drug.getApi());
        drugViewHolder.imgRoundLetter.setImageDrawable(ImageUtils.generateRoundLetter(drug.getName(), drug.getColor()));

        drugViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrugClickListener.onDrugClick(drug.getId(),
                        drug.getName(),
                        drug.getApi(),
                        drug.getColor());
            }
        });

        drugViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mDrugClickListener.onDrugLongClick(drug.getId(),
                        drug.getName(),
                        drug.getApi());
                return true;
            }
        });
    }

    @Override
    public DrugViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_drug_list, viewGroup, false);
        return new DrugViewHolder(itemView);
    }

    /**
     * DRUG VIEW HOLDER
     */
    public static class DrugViewHolder extends RecyclerView.ViewHolder{
        protected TextView tvName;
        protected TextView tvApi;
        protected ImageView imgRoundLetter;

        public DrugViewHolder(View v) {
            super(v);
            tvName =  (TextView) v.findViewById(R.id.item_drug_name);
            tvApi = (TextView)  v.findViewById(R.id.item_drug_api);
            imgRoundLetter = (ImageView) v.findViewById(R.id.item_drug_letter);
        }
    }

    public interface DrugClickListener {
        public void onDrugClick(long drugId, String name, String api, int color);
        public void onDrugLongClick(long drugId, String name, String api);
    }
}
