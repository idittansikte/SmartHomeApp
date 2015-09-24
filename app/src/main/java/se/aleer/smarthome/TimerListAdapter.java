package se.aleer.smarthome;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextClock;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;

public class TimerListAdapter extends ArrayAdapter<Timer> {
    private Context context;
    customTextTimeListener mCustomTextTimeListener;

    public interface customTextTimeListener {
        void onEditTimeListener(int position,Boolean on);
    }

    public void setCustomTextTimeListener(customTextTimeListener listener) {
        this.mCustomTextTimeListener = listener;
    }

    public TimerListAdapter(Context context, List<Timer> items) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
    }

    /**
     * Holder for the list items.
     */
    private class ViewHolder{
        public TextView titleText;
        public TextClock clockOn;
        public TextClock clockOff;

        public ViewHolder(View view){
            this.titleText = (TextView) view.findViewById(R.id.timer_list_single_title);
            this.clockOn = (TextClock) view.findViewById(R.id.timer_list_single_time_on);
            this.clockOff = (TextClock) view.findViewById(R.id.timer_list_single_time_off);
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
            mViewToUse = mInflater.inflate(R.layout.timer_list_single, parent, false);

            holder = new ViewHolder(mViewToUse);
            mViewToUse.setTag(holder);
        }else {
            holder = (ViewHolder) mViewToUse.getTag();
        }
        Timer item = (Timer)getItem(position);
        holder.titleText.setText(item.getTitle());
        holder.clockOn.setText(item.getTimeOn());
        holder.clockOn.setText(item.getTimeOff());

        holder.clockOn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mCustomTextTimeListener != null) {
                    mCustomTextTimeListener.onEditTimeListener(position, true);
                }
                return true;
            }
        });
        holder.clockOff.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mCustomTextTimeListener != null) {
                    mCustomTextTimeListener.onEditTimeListener(position, true);
                }
                return true;
            }
        });

        return mViewToUse;
    }
}
