package it.dedonatis.emanuele.drugstore.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.data.DrugContract;
import it.dedonatis.emanuele.drugstore.models.DrugPackage;
import it.dedonatis.emanuele.drugstore.utils.DateUtils;

public class PackageRecyclerAdapter extends RecyclerView.Adapter<PackageRecyclerAdapter.PackageViewHolder>{

    private static final String LOG_TAG = PackageRecyclerAdapter.class.getSimpleName();
    private List<DrugPackage> drugPackages;
    private PackageClickListener packageClickListener;
    private Context mContext;

    public PackageRecyclerAdapter(Context context, List<DrugPackage> drugPackages, PackageClickListener packageClickListener) {
        this.mContext = context;
        this.drugPackages = drugPackages;
        this.packageClickListener = packageClickListener;
    }

    @Override
    public int getItemCount() {
        return drugPackages.size();
    }

    @Override
    public void onBindViewHolder(PackageViewHolder packageViewHolder, int i) {
        final DrugPackage pkg = drugPackages.get(i);
        packageViewHolder.tvDescription.setText(pkg.getDescription());
        packageViewHolder.tvExpDate.setText(pkg.getStringExpriartion_date());
        packageViewHolder.tvUnits.setText(pkg.getUnits() + "");
        packageViewHolder.imageView.setImageURI(pkg.getImageUri());

        String letter = "";
        try {
            letter = String.valueOf(pkg.getDescription().charAt(0));
        }catch (StringIndexOutOfBoundsException e) {

        }
        TextDrawable drawable = TextDrawable.builder().buildRound(letter, pkg.getDrugColor());
        packageViewHolder.roundLetter.setImageDrawable(drawable);
        if(pkg.getUnits() < 1)
            packageViewHolder.btnUse.setEnabled(false);
        packageViewHolder.btnUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packageClickListener.onClickPackageUse(pkg.getPackageID(), pkg.getUnits());
            }
        });
    }

    @Override
    public PackageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_package_card, viewGroup, false);
        return new PackageViewHolder(itemView);
    }

    public static class PackageViewHolder extends RecyclerView.ViewHolder{
        protected TextView tvDescription;
        protected TextView tvExpDate;
        protected TextView tvUnits;
        protected Button btnUse;
        protected ImageView imageView;
        protected ImageView roundLetter;

        public PackageViewHolder(View v) {
            super(v);
            tvDescription =  (TextView) v.findViewById(R.id.package_description);
            tvExpDate = (TextView)  v.findViewById(R.id.package_exp_date);
            tvUnits = (TextView)  v.findViewById(R.id.package_units_left);
            btnUse = (Button) v.findViewById(R.id.button_use);
            imageView = (ImageView) v.findViewById(R.id.package_image);
            roundLetter = (ImageView) v.findViewById(R.id.item_package_letter);
        }
    }

    public interface PackageClickListener {
        public void onClickPackageUse(long packageId, int units);
    }
}
