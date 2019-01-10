package mshkliai.com.ft_hangouts.listView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mshkliai.com.ft_hangouts.R;

public class CustomAdapter extends BaseAdapter {
    private Context         context;
    private List<Model>   rowItems;

    public CustomAdapter(Context context, List<Model> items) {
        this.context = context;
        this.rowItems = items;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView photo;
        TextView name;
        TextView num;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_contacts_item, null);

            holder = new ViewHolder();

            holder.name = convertView.findViewById(R.id.name);
            holder.num = convertView.findViewById(R.id.number);
            holder.photo = convertView.findViewById(R.id.photo);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Model rowItem = (Model) getItem(position);

        holder.name.setText(rowItem.getName());
        holder.num.setText(rowItem.getNumber());

        Bitmap  photo = rowItem.getPhoto();

        if (photo != null) {
            holder.photo.setImageBitmap(rowItem.getPhoto());
        } else {
            holder.photo.setImageResource(R.drawable.ic_photo_black_24dp);
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }
}