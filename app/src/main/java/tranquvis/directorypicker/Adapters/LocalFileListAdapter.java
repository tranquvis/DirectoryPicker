package tranquvis.directorypicker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tranquvis.directorypicker.R;

public class LocalFileListAdapter extends ArrayAdapter<File>
{
    public List<File> data;

    private int layoutResourceId;
    private Context context;

    public LocalFileListAdapter(Context context, int layoutResourceId, ArrayList<File> data) {
        super(context, layoutResourceId, data);
        this.data = data;
        this.layoutResourceId = layoutResourceId;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceId, parent, false);

        }

        File f = data.get(position);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.name);
        titleTextView.setText(f.getName());

        ImageView iconView = (ImageView) convertView.findViewById(R.id.imageView_icon);

        int iconRes = -1;
        if(f.isDirectory())
        {
            iconRes = R.drawable.ic_folder_grey_700_24dp;
        }

        if(iconRes != -1)
            iconView.setImageResource(iconRes);

        return convertView;
    }
}
