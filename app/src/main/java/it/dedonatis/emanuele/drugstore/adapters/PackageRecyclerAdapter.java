package it.dedonatis.emanuele.drugstore.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.data.DrugContract;
import it.dedonatis.emanuele.drugstore.models.DrugPackage;

public class PackageRecyclerAdapter extends RecyclerView.Adapter<PackageRecyclerAdapter.PackageViewHolder> {

    private List<DrugPackage> drugPackages;

    public PackageRecyclerAdapter(List<DrugPackage> drugPackages) {
        this.drugPackages = drugPackages;
    }

    @Override
    public int getItemCount() {
        return drugPackages.size();
    }

    @Override
    public void onBindViewHolder(PackageViewHolder contactViewHolder, int i) {
        DrugPackage pkg = drugPackages.get(i);
        contactViewHolder.tvDescription.setText(pkg.getDescription());
        contactViewHolder.tvExpDate.setText(pkg.getExpiration_date() + "");
        contactViewHolder.tvUnits.setText(pkg.getUnits_left() + "/" + pkg.getUnits());
    }

    @Override
    public PackageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_package_card, viewGroup, false);

        return new PackageViewHolder(itemView);
    }

    public static class PackageViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvDescription;
        protected TextView tvExpDate;
        protected TextView tvUnits;

        public PackageViewHolder(View v) {
            super(v);
            tvDescription =  (TextView) v.findViewById(R.id.package_description);
            tvExpDate = (TextView)  v.findViewById(R.id.package_exp_date);
            tvUnits = (TextView)  v.findViewById(R.id.package_units);
        }
    }
}
