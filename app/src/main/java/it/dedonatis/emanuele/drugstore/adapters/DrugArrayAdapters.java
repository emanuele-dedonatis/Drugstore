package it.dedonatis.emanuele.drugstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.models.Drug;

public class DrugArrayAdapters extends ArrayAdapter<Drug>{

    public DrugArrayAdapters(Context context, List<Drug> drugList){
        super(context, 0, drugList);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        // -------------------
        // NAME
        // number of packages
        // -------------------

        Drug drug = getItem(position);
        DrugViewHolder drugViewHolder;

        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(R.layout.drug_row, null);

            drugViewHolder = new DrugViewHolder();
            drugViewHolder.nameTextView = (TextView) convertView.findViewById(R.id.drug_name);
            drugViewHolder.numPackagesTextView = (TextView) convertView.findViewById(R.id.drug_numPackages);

            convertView.setTag(drugViewHolder);

        }
        else {
            drugViewHolder = (DrugViewHolder) convertView.getTag();
        }



        drugViewHolder.nameTextView.setText(drug.getName());
        drugViewHolder.numPackagesTextView.setText(drug.getPackages().size() + " packages");

        return convertView;
    }

    static class DrugViewHolder {
        TextView nameTextView;
        TextView numPackagesTextView;
    }
}
