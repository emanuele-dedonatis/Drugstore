package it.dedonatis.emanuele.drugstore.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import it.dedonatis.emanuele.drugstore.R;

public class DialogImageAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private Uri mImageUri;

    public DialogImageAdapter(Context context, Uri imageUri) {
        layoutInflater = LayoutInflater.from(context);
        mImageUri = imageUri;
    }

    @Override
    public int getCount() {
        return 1;
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
        Context context = parent.getContext();

        if (view == null) {
            view = layoutInflater.inflate(R.layout.dialog_image, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.dialog_image_view);
            if (mImageUri != null)
                viewHolder.imageView.setImageURI(Uri.parse(mImageUri.toString()));
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }


        return view;
    }

    static class ViewHolder {
        ImageView imageView;
    }
}
