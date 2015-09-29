package se.aleer.smarthome;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.os.Handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;


public class TimerFragment extends Fragment implements TimerListAdapter.customTextTimeListener, TimePickerDialog.OnTimeSetListener {

    private final int REQUEST_CODE = 314;
    private int mPage;
    private String mTitle;
    private List<Timer> mList;
    private TimerListAdapter mTimerListAdapter;
    private int mCurrentTimerPos;
    private Boolean mCurrentTimerWhat;
    private final Handler mHandler = new Handler();

    public static TimerFragment newInstance(int page, String title){
        TimerFragment fragment = new TimerFragment();
        Bundle args = new Bundle();
        args.putInt("page", page);
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StorageTimers storageTimers = new StorageTimers();
        mList = storageTimers.getTimerList(getContext());
        // Check if there is something in memory
        if(mList == null){ // If not, create a new one
            mList = new ArrayList<>();
        }
        // Set "constructor" arguments from newInstance
        mPage = getArguments().getInt("page", 0);
        mTitle = getArguments().getString("title");
        /*mList.add(new Timer("Test 1"));
        mList.add(new Timer("Test 2"));
        mList.add(new Timer("Test 3"));*/

        mTimerListAdapter = new TimerListAdapter(getActivity(), mList);
        mTimerListAdapter.setCustomTextTimeListener(this);

    }

    @Override
    public void onResume(){
        super.onResume();
        mRunnable.run();
    }

    @Override
    public void onPause(){
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
        StorageTimers storageTimers = new StorageTimers();
        storageTimers.saveTimerList(getContext(), mList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        ListView listView = (ListView) view.findViewById(R.id.timer_listview);
        // Set adapter listener
        registerForContextMenu(listView);
        // -----------------
        listView.setAdapter(mTimerListAdapter);
        //listView.setOnItemLongClickListener(mListViewListener);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.FAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showManagerDialog(null);
            }
        });
        return view;

    }

    /*
     * On click listener for the timer-adapter. Is called when someone clicks an item in
     * the timer list.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.timer_listview) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(mList.get(info.position).getTitle());
            String[] menuItems = getResources().getStringArray(R.array.timer_listview_menu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }
    /*
     * Called when the user clicks on some menu item created by onCreateContextMenu
     *
     * The options are:
     *      Edit ->  Open the TimerManager
     *      Remove -> Remove the Timer and clear its switches
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        Timer selectedTimer = mList.get(info.position);
        switch (menuItemIndex){
            case 0: // Edit
                showManagerDialog(selectedTimer);
                break;
            case 1: // Remove
                deleteTimer(selectedTimer);
                break;
            default:
                Toast.makeText(getContext(), "This option is not implemented yet..", Toast.LENGTH_LONG).show();
        }
        return true;
    }

    /*
     * This is called from the adapter when someone clicks a
     * time-filed in timer-list.
     */
    @Override
    public void onEditTimeListener(int position, Boolean value) {
        Toast.makeText(getActivity(), "Button click ",
                Toast.LENGTH_SHORT).show();
        mCurrentTimerPos = position;
        mCurrentTimerWhat = value;
        showTimePickerDialog();
    }

    /*
     * Show TimerManagerDialogFragment
     */
    private void showManagerDialog(Timer timer){
        Log.d("showManagerDialog", "Here");
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag("TimerManager");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }
        TimerManagerDialogFragment managerDialog = new TimerManagerDialogFragment();
        Bundle bundle = new Bundle();
        if (timer != null) {
            Log.d("TTT", timer.getTitle());
            bundle.putString(TimerManagerDialogFragment.NAME_BUNDLE_KEY, timer.getTitle()); // Name of
            bundle.putInt(TimerManagerDialogFragment.ID_BUNDLE_KEY, timer.getId()); // id
            Log.d("TTT", timer.getTitle());
        } else { // If new one
            Log.d("TTT", "New one");
            bundle.putInt(TimerManagerDialogFragment.ID_BUNDLE_KEY, generate_timer_id()); // New generated id
            bundle.putString(TimerManagerDialogFragment.NAME_BUNDLE_KEY, ""); // Name
        }
        managerDialog.setArguments(bundle);
        managerDialog.setTargetFragment(this, REQUEST_CODE);
        managerDialog.show(manager, "TimerManager");
    }

    /*
     * Shows the time picker dialog
     */
    public void showTimePickerDialog(/*View v*/) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        DialogFragment newFragment = new TimePickerDialogFragment(this);
        newFragment.show(ft, "timePicker");
    }

    /*
     *  Called when user have picked a time in TimePickerDialogFragment
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Log.d("GGGG", "TimePickerDialogFragment " + hourOfDay + ":" + minute);
        Timer timer = mList.get(mCurrentTimerPos);
        if (mCurrentTimerWhat == TimerListAdapter.TIME_ON)
            timer.setTimeOn(hourOfDay, minute);
        else
            timer.setTimeOff(hourOfDay, minute);
        saveTimer(timer);
    }

    /*
     * Called when the timer manager have edited or added a timer.
     *
     * *** Handles ONLY the TIMER part. Updating/saving/deleting timer-ids in switches
     *     is handled by the timer manager...
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Make sure fragment codes match up
        if (requestCode == REQUEST_CODE) {
            String name = data.getStringExtra(TimerManagerDialogFragment.NAME_BUNDLE_KEY);
            Integer id = data.getIntExtra(TimerManagerDialogFragment.ID_BUNDLE_KEY, -1);
            Timer tmpTimer = new Timer(name);
            tmpTimer.setId(id);

            // Find if timer exists..
            for(Timer timer : mList) {
                if(timer.equals(tmpTimer)){ // If exists
                    timer.setTitle(name);
                    timer.setId(id);
                    saveTimer(timer);
                    return;
                }
            }
            tmpTimer.setTitle(name);
            tmpTimer.setId(id);
            saveTimer(tmpTimer);
        }
    }

    /*
     * Generates a unique timer id.
     * Used when creating new timers.
     */
    private int generate_timer_id(){
        int id = mList.size()+1; // A good starting point
        TreeSet<Integer> takenIds = new TreeSet<>();
        for (Timer timer : mList) {
            takenIds.add(timer.getId());
        }
        while (true) {
            if (!takenIds.contains(id)) {
                return id;
            }
            ++id;
        }
    }

    // Sync timer list with server list in interval
    final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            // Get and set status for all switches...
            StorageSetting ss = new StorageSetting(getActivity());
            String port = ss.getString(StorageSetting.PREFS_SERVER_PORT);
            if (port != null) {
                TCPAsyncTask getStatusArduino = new TCPAsyncTask(ss.getString(StorageSetting.PREFS_SERVER_URL),
                        Integer.parseInt(port)) {
                    // TODO: Disable this when waiting for remove/add/switch...
                    @Override
                    protected void onPostExecute(String s) {
                        //Toast.makeText(mActivity, "Updating...", Toast.LENGTH_SHORT).show();
                        serverSync(s);
                    }
                };
                getStatusArduino.execute("W");
            }
            mHandler.postDelayed(this, 20000);
        }
    };

    private void deleteTimer(final Timer timer){
        mHandler.removeCallbacks(mRunnable);
        StorageSwitches storageSwitches = new StorageSwitches();
        List<Switch> storedSwitches = storageSwitches.getSwitchList(getContext());
        for(Switch sw : storedSwitches){
            if(sw.getTimerId() == timer.getId()){
                sw.setTimerId(-1);
            }
        }
        storageSwitches.saveSwitchList(getContext(), storedSwitches);
        mList.remove(timer);
        mTimerListAdapter.notifyDataSetChanged();

        // Add timer at server...
        StorageSetting ss = new StorageSetting(getActivity());
        String port = ss.getString(StorageSetting.PREFS_SERVER_PORT);
        if (port != null) {
            TCPAsyncTask tcpClient = new TCPAsyncTask(ss.getString(StorageSetting.PREFS_SERVER_URL),
                    Integer.parseInt(port)) {
                @Override
                protected void onPostExecute(String s) {

                    if (s == null) {
                        Toast.makeText(getContext(), R.string.no_response_from_server, Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (s.equals("OK")) {
                        Toast.makeText(getContext(), "Timer removed successfully", Toast.LENGTH_LONG).show();
                    } else if (s.equals("NOK")) {
                        Toast.makeText(getContext(), R.string.no_such_switch_at_server, Toast.LENGTH_SHORT).show();
                    }
                    // Continue fetching
                    mRunnable.run();
                }
            };
            // Send add command;
            tcpClient.execute("Q:" + timer.getId());
        }else{
            Toast.makeText(getContext(), R.string.no_server_configuration, Toast.LENGTH_LONG).show();
        }
    }

    private void saveTimer(final Timer timer){
        mHandler.removeCallbacks(mRunnable);
        // Add timer at server...
        StorageSetting ss = new StorageSetting(getActivity());
        String port = ss.getString(StorageSetting.PREFS_SERVER_PORT);
        if (port != null) {
            TCPAsyncTask tcpClient = new TCPAsyncTask(ss.getString(StorageSetting.PREFS_SERVER_URL),
                    Integer.parseInt(port)) {
                @Override
                protected void onPostExecute(String s) {

                    if (s == null) {
                        Toast.makeText(getContext(), R.string.no_response_from_server, Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (s.equals("OK")) {
                        if(! mList.contains(timer)) {
                            mList.add(timer);
                        }
                        mTimerListAdapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Timer updated successfully", Toast.LENGTH_LONG).show();
                    } else if (s.equals("NOK")) {
                        Toast.makeText(getContext(), R.string.no_such_switch_at_server, Toast.LENGTH_SHORT).show();
                    }
                    // Continue fetching
                    mRunnable.run();
                }
            };
            // Generate switch id string
            String switch_string = "";
            StorageSwitches storageSwitches = new StorageSwitches();
            List<Switch> sw_list = storageSwitches.getSwitchList(getContext());
            for(Switch sw : sw_list){
                if(sw.getTimerId() == timer.getId())
                    switch_string += sw.getId() + ":";
            }
            // Send add command;
            Log.d("Switches: ", switch_string);
            tcpClient.execute("T:" + timer.getId() + ":" + timer.getTimeOnHour() + ":" + timer.getTimeOnMin()
                                + ":" + timer.getTimeOffHour() + ":" + timer.getTimeOffMin() + ":" + switch_string);
        }else{
            Toast.makeText(getContext(), R.string.no_server_configuration, Toast.LENGTH_LONG).show();
        }
    }

    private void serverSync(String serverMessage){

        if (serverMessage == null){
            Toast.makeText(getContext(), R.string.no_response_from_server, Toast.LENGTH_LONG).show();
            return;
        }
        Log.d("ServerRespTimerList: " , serverMessage );
        if (serverMessage.isEmpty() || serverMessage.equals("-1")){
            return;
        }
        try {
            StorageSwitches storageSwitches = new StorageSwitches();
            List<Switch> switchList = storageSwitches.getSwitchList(getContext());
            Map<Integer, Integer> map = new HashMap<>();
            List<Timer> newList = new ArrayList<>();
            String[] server_switchlist = serverMessage.split("N");
            for (int i = 0; i < server_switchlist.length; ++i) {
                String[] server_switch = server_switchlist[i].split(":");
                if (server_switch.length != 6) {
                    throw new IOException("Arguments from server not matching expectation");
                }
                int swId = Integer.valueOf(server_switch[0]);
                int timerId = Integer.valueOf(server_switch[1]);
                int onHour = Integer.valueOf(server_switch[2]);
                int onMinute = Integer.valueOf(server_switch[3]);
                int offHour = Integer.valueOf(server_switch[4]);
                int offMinute = Integer.valueOf(server_switch[5]);

                if (swId < 10 || swId > 255 || timerId < 0 || timerId > 255 || onHour < 0 || onHour > 24 || onMinute < 0 || onMinute > 60
                        || offHour < 0 || offHour > 24 || offMinute < 0 || offMinute > 60) {
                    Toast.makeText(getContext(), "Corrupted data at server", Toast.LENGTH_LONG).show();
                    throw new IOException("Corrupted data at server");
                }
                map.put(swId, timerId);
                Timer timer = new Timer(timerId, "Synced", onHour, onMinute, offHour, offMinute);
                int index = mList.indexOf(timer);
                if(index == -1){ // If it doesn't exist
                    newList.add(timer);
                }else { // Exist

                    Timer existing = mList.get(index);
                    if (!newList.contains(existing))
                    {
                        existing.setTimeOn(onHour, onMinute);
                        existing.setTimeOff(offHour, offMinute);
                        newList.add(existing);
                    }
                }
            }
            for(Switch sw : switchList){
                if(map.containsKey(sw.getId())){
                    sw.setTimerId(map.get(sw.getId()));
                }else {
                    sw.setTimerId(-1);
                }
            }
            storageSwitches.saveSwitchList(getContext(), switchList);
            mList.clear();
            mList.addAll(newList);
            mTimerListAdapter.notifyDataSetChanged();
        }catch (Exception e){
            Toast.makeText(getContext(), "Unreadable response from server... ", Toast.LENGTH_LONG).show();
            Log.e("updateArrayAdapter", "Could not parse server message. Error: " + e.getMessage());
            return;
        }
    }
}
