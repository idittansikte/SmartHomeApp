package se.aleer.smarthome;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class TimerListAdapter extends ArrayAdapter<Timer> {
    private Context context;
    customTextTimeListener mCustomTextTimeListener;
    public static Boolean TIME_ON = true;
    public static Boolean TIME_OFF = false;

    public interface customTextTimeListener {
        void onEditTimeListener(int position, Boolean what);
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
        public TextView clockOn;
        public TextView clockOff;

        public ViewHolder(View view){
            this.titleText = (TextView) view.findViewById(R.id.timer_list_single_title);
            this.clockOn = (TextView) view.findViewById(R.id.timer_list_single_time_on);
            this.clockOff = (TextView) view.findViewById(R.id.timer_list_single_time_off);
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
        final ViewHolder holder;
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
        holder.titleText.setText(item.getName());
        String onHour = "";
        String onMinute = "";
        String offHour = "";
        String offMinute = "";
        if(item.getTimeOnHour() < 10)
            onHour += "0";
        onHour += item.getTimeOnHour();
        if(item.getTimeOnMin() < 10)
            onMinute += "0";
        onMinute += item.getTimeOnMin();
        if(item.getTimeOffHour() < 10)
            offHour += "0";
        offHour += item.getTimeOffHour();
        if(item.getTimeOffMin() < 10)
            offMinute += "0";
        offMinute += item.getTimeOffMin();

        holder.clockOn.setText(onHour + ":" + onMinute);
        holder.clockOff.setText(offHour+ ":" + offMinute);

        holder.clockOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomTextTimeListener != null) {
                    mCustomTextTimeListener.onEditTimeListener(position, TIME_ON);
                }
            }
        });
        holder.clockOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomTextTimeListener != null) {
                    mCustomTextTimeListener.onEditTimeListener(position, TIME_OFF);
                }
            }
        });

        return mViewToUse;
    }
}
