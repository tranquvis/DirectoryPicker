package tranquvis.directorypicker.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import tranquvis.directorypicker.R;

public class ElementListAdapter extends ArrayAdapter<File>
{
    private static final int LAYOUT_RES = R.layout.listview_item_element;

    public ElementListAdapter(Context context, ArrayList<File> data) {
        super(context, LAYOUT_RES, data);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(LAYOUT_RES, parent, false);
        }

        File f = getItem(position);
        if(f == null)
            return convertView;

        TextView titleTextView = (TextView) convertView.findViewById(R.id.name);
        titleTextView.setText(f.getName());

        ImageView iconView = (ImageView) convertView.findViewById(R.id.imageView_icon);
        if(f.isDirectory())
            iconView.setImageResource(R.drawable.ic_folder_grey_700_24dp);

        return convertView;
    }
}
