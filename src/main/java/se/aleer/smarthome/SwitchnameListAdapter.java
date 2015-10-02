package se.aleer.smarthome;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.List;

public class SwitchnameListAdapter extends ArrayAdapter<Switch> {
    private Context context;

    public SwitchnameListAdapter(Context context, List<Switch> items) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
    }

    /**
     * Holder for the list items.
     */
    private class ViewHolder{
        public TextView name;

        public ViewHolder(View view){
            this.name = (TextView) view.findViewById(R.id.textView_name);
        }
    }

    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View mViewToUse = convertView;
        // This block exists to inflate the settings list item conditionally based on whether
        // we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(mViewToUse == null) {
            mViewToUse = mInflater.inflate(R.layout.switchname_list_single, parent, false);

            holder = new ViewHolder(mViewToUse);
            mViewToUse.setTag(holder);
        }else {
            holder = (ViewHolder) mViewToUse.getTag();
        }
        Switch item = (Switch)getItem(position);
        holder.name.setText(item.getName());

        return mViewToUse;
    }
}
