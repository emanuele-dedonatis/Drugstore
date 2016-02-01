package it.dedonatis.emanuele.drugstore.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.data.DataContract;
import it.dedonatis.emanuele.drugstore.fragments.DrugsListFragment;
import it.dedonatis.emanuele.drugstore.utils.ColorUtils;

public class DrugsCursorAdapter extends CursorAdapter {
    private static final String LOG_TAG = DrugsCursorAdapter.class.getSimpleName();

    public DrugsCursorAdapter(Context context) {
        super(context, null, 0);
    }

    private static final String[] PKG_COLUMNS = {
            DataContract.PackageEntry.TABLE_NAME + "." + DataContract.PackageEntry._ID,
            DataContract.PackageEntry.COLUMN_UNITS
    };
    public static final int COL_PKG_ID = 0;
    public static final int COL_PKG_UNITS = 1;


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

        String name = cursor.getString(DrugsListFragment.COL_DRUG_NAME);
        String api = cursor.getString(DrugsListFragment.COL_DRUG_API);

        viewHolder.nameTv.setText(name);
        viewHolder.apiTv.setText(api);

        String letter = "";
        try {
            letter = String.valueOf(name.charAt(0));
        }catch (StringIndexOutOfBoundsException e) {

        }
        TextDrawable drawable = TextDrawable.builder().buildRound(letter, ColorUtils.getDrugColor(name, api));
        viewHolder.roundLetter.setImageDrawable(drawable);

    }

    public static class ViewHolder{
            public TextView nameTv;
            public TextView apiTv;
            public ImageView roundLetter;

            public ViewHolder(View itemView) {
                nameTv = (TextView) itemView.findViewById(R.id.item_drug_name);
                apiTv = (TextView) itemView.findViewById(R.id.item_drug_api);
                roundLetter = (ImageView) itemView.findViewById(R.id.item_drug_letter);
            }
        }
}
