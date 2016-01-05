package it.dedonatis.emanuele.drugstore.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.fragments.DrugsListFragment;

public class DrugsCursorAdapter extends CursorAdapter {
    private static final String LOG_TAG = DrugsCursorAdapter.class.getSimpleName();

    public DrugsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

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
    }

    public static class ViewHolder{
            public TextView nameTv;
            public TextView apiTv;

            public ViewHolder(View itemView) {
                nameTv = (TextView) itemView.findViewById(R.id.item_drug_name);
                apiTv = (TextView) itemView.findViewById(R.id.item_drug_api);
            }
        }
}
