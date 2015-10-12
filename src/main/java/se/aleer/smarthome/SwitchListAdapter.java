package se.aleer.smarthome;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SwitchListAdapter extends ArrayAdapter<Switch> {


    private Context mContext;
    List<Switch> mSwitches;
    StorageSwitches mStorageSwitches;
    OnSwitchStatusChangeListener mCallback;


    public interface OnSwitchStatusChangeListener {
        public void changeStatus(Switch swtch);
    }

    public void setOnSwitchStatusChangeListener(OnSwitchStatusChangeListener listener){
        mCallback = listener;
    }

    public SwitchListAdapter(Context context, List<Switch> switches){
        super(context, R.layout.list_single, switches);
        mContext = context;
        mSwitches = switches;
    }

    public class ViewHolder {
        ImageButton switchButton;
        ProgressBar progressBar;
        TextView switchName;
        ImageView timerIndicator;
        ImageView lightSensorIndicator;
    }

    public void setItemStatus(int position, int status)
    {
        mSwitches.get(position).setStatus(status);
    }

    @Override
    public Switch getItem(int position) {
        return mSwitches.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        // This is for not redo things we've done before
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_single, null);
            // Populate viewHolder with all views
            holder = new ViewHolder();
            holder.switchButton = (ImageButton) convertView.findViewById(R.id.single_list_button);
            holder.switchName = (TextView) convertView.findViewById(R.id.text);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.single_list_loading);
            holder.timerIndicator = (ImageView) convertView.findViewById(R.id.has_timer_indicator);
            holder.lightSensorIndicator = (ImageView) convertView.findViewById(R.id.has_light_sensor_indicator); // Not implemented yet
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Switch curSwitch = (Switch) getItem(position);
         /* ### NAME ### */
        // Set list item name
        holder.switchName.setText(curSwitch.getName());

        /* ### INDICATORS ### */
        if (curSwitch.hasTimer){
            holder.timerIndicator.setVisibility(View.VISIBLE);
        }else{
            holder.timerIndicator.setVisibility(View.INVISIBLE);
        }

        // Not implemented yet...
        holder.lightSensorIndicator.setVisibility(View.INVISIBLE);

        /* ### Button AND progressbar ### */
        final ProgressBar pgb = holder.progressBar;
        final ImageButton btn = holder.switchButton;
        // Set curSwitch's state.
        int curSwitchState = curSwitch.getStatus();
        if(curSwitch.waitingUpdate){ // Set loading and waiting image.
            if(btn.isActivated())
                btn.setClickable(false);
            if(pgb.getVisibility() != View.VISIBLE)
                pgb.setVisibility(View.VISIBLE);
            btn.setImageResource(R.drawable.button_white_128);
        }else {
            btn.setClickable(true);
            holder.progressBar.setVisibility(View.INVISIBLE);
            if (curSwitchState == -1) // If not known status
            {
                btn.setImageResource(R.drawable.button_white_128);

            } else if (curSwitchState == 1 || curSwitchState == 0) { // On or off
                holder.switchButton.setVisibility(View.VISIBLE);

                if (curSwitchState == 1) { // On
                    btn.setImageResource(R.drawable.button_green_128);
                } else { // Off
                    btn.setImageResource(R.drawable.button_red2_128);
                }
            }
        }
        holder.switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrate.vibrate(mContext);
                curSwitch.waitingUpdate = true;
                notifyDataSetChanged();
                mCallback.changeStatus(curSwitch);
            }
        });


        return convertView;
    }

    @Override
    public void add(Switch swtch) {
        mSwitches.add(swtch);
        notifyDataSetChanged();
    }

    @Override
    public void insert(Switch swtch, int position){
        mSwitches.add(position, swtch);
        notifyDataSetChanged();
    }

    @Override
    public void remove(Switch swtch) {
        super.remove(swtch);
        mSwitches.remove(swtch);
        notifyDataSetChanged();
    }

    @Override
    public int getPosition(Switch swtch) {
        //Log.d("SwitchListAdapter", "Search for index START");
        //Log.d("SwitchListAdaper", "mSwitches size: " + mSwitches.size());
        int i = mSwitches.indexOf(swtch);
        //Log.d("SwitchListAdapter", "Search for index DONE");
        return i;
    }

    // Check if list contains object with an id
    public boolean contains(int id)
    {
        for(Switch swtch : mSwitches)
        {
            if(swtch.getId() == id){
                return true;
            }
        }
        return false;
    }
}
