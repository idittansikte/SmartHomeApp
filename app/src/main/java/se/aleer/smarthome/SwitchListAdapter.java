package se.aleer.smarthome;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SwitchListAdapter extends ArrayAdapter<Switch> {

    private Context mContext;
    List<Switch> mSwitches;
    Storage mStorage;
    public SwitchListAdapter(Context context, List<Switch> switches){
        super(context, R.layout.list_single, switches);
        mContext = context;
        mSwitches = switches;
    }

    public class ViewHolder {
        ImageButton switchButton;
        ProgressBar progressBar;
        TextView switchName;
    }

   /* @Override
    public int getCount(){
        return mSwitches.size();
    }*/

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
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_single, null);
            holder = new ViewHolder();
            holder.switchButton = (ImageButton) convertView.findViewById(R.id.single_list_button);
            holder.switchName = (TextView) convertView.findViewById(R.id.text);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.single_list_loading);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Switch curSwitch = (Switch) getItem(position);
        // Set list item name
        holder.switchName.setText(curSwitch.getName());

        final ProgressBar pgb = holder.progressBar;
        final ImageButton btn = holder.switchButton;
        // Set curSwitch's state.
        int curSwitchState = curSwitch.getStatus();
        if(curSwitchState == -1)
        {
            Log.d("SLA", "Setting switchState = -1");
            btn.setImageResource(R.drawable.button_white_128);
            //holder.switchButton.setVisibility(View.INVISIBLE);
            holder.progressBar.setVisibility(View.INVISIBLE);
        }else if(curSwitchState == 1 || curSwitchState == 0) { // On or off
            Log.d("SLA", "Setting switchState = " + curSwitchState);
            holder.switchButton.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.INVISIBLE);
            if (curSwitchState == 1) { // On
                btn.setImageResource(R.drawable.button_green_128);
            } else { // Off
                btn.setImageResource(R.drawable.button_red_128);
            }
        }
        holder.switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageSetting ss = new StorageSetting(mContext);
                String port = ss.getString(StorageSetting.PREFS_SERVER_PORT);
                if (port != null) {
                    TCPAsyncTask tcpClient = new TCPAsyncTask(ss.getString(StorageSetting.PREFS_SERVER_URL), Integer.parseInt(port)) {
                        @Override
                        protected void onPostExecute(String s) {
                            if (s == null || s.isEmpty()) {
                                Toast.makeText(mContext, "No response from server...", Toast.LENGTH_SHORT).show();
                                btn.setImageResource(R.drawable.button_white_128);
                                curSwitch.setStatus(0);
                            } else {
                                Toast.makeText(mContext, s + " " + curSwitch.getId(), Toast.LENGTH_SHORT).show();
                                if (s.equals("OK")) {
                                    if (curSwitch.getStatus() == 1) {
                                        btn.setImageResource(R.drawable.button_red_128);
                                        curSwitch.setStatus(0);
                                    } else if (curSwitch.getStatus() == 0) {
                                        btn.setImageResource(R.drawable.button_green_128);
                                        curSwitch.setStatus(1);
                                    }
                                }

                                pgb.setVisibility(View.INVISIBLE);
                                //btn.setVisibility(View.VISIBLE);
                                btn.setActivated(true);
                            }
                        }
                    };
                    //btn.setVisibility(View.VISIBLE);
                    btn.setImageResource(R.drawable.button_white_128);
                    btn.setActivated(false);
                    pgb.setVisibility(View.VISIBLE);
                    tcpClient.execute("S:" + curSwitch.getId() + ":" + (curSwitch.getStatus() == 1 ? "0" : "1"));
                }
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
        Log.d("SwitchListAdapter", "Search for index START");
        Log.d("SwitchListAdaper", "mSwitches size: " + mSwitches.size());
        int i = mSwitches.indexOf(swtch);
        Log.d("SwitchListAdapter", "Search for index DONE");
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
