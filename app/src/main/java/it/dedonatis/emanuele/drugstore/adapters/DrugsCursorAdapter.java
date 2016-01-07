package it.dedonatis.emanuele.drugstore.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.data.DrugContract;
import it.dedonatis.emanuele.drugstore.fragments.DrugsListFragment;

public class DrugsCursorAdapter extends CursorAdapter {
    private static final String LOG_TAG = DrugsCursorAdapter.class.getSimpleName();

    public DrugsCursorAdapter(Context context) {
        super(context, null, 0);
    }

    private static final String[] PKG_COLUMNS = {
            DrugContract.PackageEntry.TABLE_NAME + "." + DrugContract.PackageEntry._ID,
            DrugContract.PackageEntry.COLUMN_UNITS_LEFT
    };
    public static final int COL_PKG_ID = 0;
    public static final int COL_PKG_UNITS_LEFT = 1;

    ColorGenerator generator = ColorGenerator.MATERIAL;

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_drug_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        long id = cursor.getLong(DrugsListFragment.COL_DRUG_ID);
        String name = cursor.getString(DrugsListFragment.COL_DRUG_NAME);
        String api = cursor.getString(DrugsListFragment.COL_DRUG_API);

        viewHolder.nameTv.setText(name);
        viewHolder.apiTv.setText(api);

        String letter = String.valueOf(name.charAt(0));


        TextDrawable drawable = TextDrawable.builder().buildRound(letter, generator.getColor(name));
        viewHolder.countImg.setImageDrawable(drawable);
    }

    public static class ViewHolder{
            public TextView nameTv;
            public TextView apiTv;
        public ImageView countImg;

            public ViewHolder(View itemView) {
                nameTv = (TextView) itemView.findViewById(R.id.item_drug_name);
                apiTv = (TextView) itemView.findViewById(R.id.item_drug_api);
                countImg = (ImageView) itemView.findViewById(R.id.item_drug_count);
            }
        }
}
