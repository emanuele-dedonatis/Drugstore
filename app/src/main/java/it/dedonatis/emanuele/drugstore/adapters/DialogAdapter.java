package it.dedonatis.emanuele.drugstore.adapters;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import it.dedonatis.emanuele.drugstore.R;

public class DialogAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    public static final int SHARE_POSITION = 0;
    public static final int EDIT_POSITION = 1;
    public static final int DELETE_POSITION = 2;

    public DialogAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;

        if (view == null) {
                view = layoutInflater.inflate(R.layout.item_dialog_bottom, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.item_dialog_text);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.item_dialog_icon);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Context context = parent.getContext();
        switch (position) {
            case SHARE_POSITION:
                viewHolder.textView.setText(context.getString(R.string.share));
                viewHolder.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_menu_share));
                break;
            case EDIT_POSITION:
                viewHolder.textView.setText(context.getString(R.string.edit));
                viewHolder.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_create_black_24dp));
                break;
            case DELETE_POSITION:
                viewHolder.textView.setText(context.getString(R.string.delete));
                viewHolder.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_delete_black_24dp));
                break;
            default:
                viewHolder.textView.setText("");
                break;
        }

        return view;
    }

    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
