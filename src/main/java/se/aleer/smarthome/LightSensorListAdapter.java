package se.aleer.smarthome;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class LightSensorListAdapter extends ArrayAdapter<LightSensor> {
    private Context context;
    lightSensorAdapterInterface mCallback;
    public static Boolean TIME_ON = true;
    public static Boolean TIME_OFF = false;

    public interface lightSensorAdapterInterface {
        void onListItemClick(int position, Boolean what);
    }

    public void setListener(lightSensorAdapterInterface listener) {
        this.mCallback = listener;
    }

    public LightSensorListAdapter(Context context, List<LightSensor> items) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
    }

    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     */
    private class ViewHolder{
        public TextView titleText;
        public TextView tv_on;
        public TextView tv_off;

        public ViewHolder(View view){
            this.titleText = (TextView) view.findViewById(R.id.light_sensor_list_single_title);
            this.tv_on = (TextView) view.findViewById(R.id.light_sensor_list_single_time_on);
            this.tv_off = (TextView) view.findViewById(R.id.light_sensor_list_single_time_off);
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
            mViewToUse = mInflater.inflate(R.layout.light_sensor_list_single, parent, false);

            holder = new ViewHolder(mViewToUse);
            mViewToUse.setTag(holder);
        }else {
            holder = (ViewHolder) mViewToUse.getTag();
        }
        LightSensor item = (LightSensor)getItem(position);
        holder.titleText.setText(item.getName());

        holder.tv_on.setText(item.getValueOn());
        holder.tv_off.setText(item.getValueOff());

        holder.tv_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onListItemClick(position, TIME_ON);
                }
            }
        });
        holder.tv_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onListItemClick(position, TIME_OFF);
                }
            }
        });

        return mViewToUse;
    }
}
