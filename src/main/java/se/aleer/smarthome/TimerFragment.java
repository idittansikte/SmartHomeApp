package se.aleer.smarthome;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
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

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;


public class TimerFragment extends Fragment implements TimerListAdapter.customTextTimeListener, TimePickerDialog.OnTimeSetListener {

    private static String TAG = "TimerFragment";
    private final int REQUEST_CODE_MANGER = 314;
    private final int REQUEST_CODE_PICKER = 124;
    private int mPage;
    private String mTitle;
    private List<Timer> mList;
    private TimerListAdapter mTimerListAdapter;
    private int mCurrentTimerPos;
    private Boolean mCurrentTimerWhat;
    private Map<Integer, String> mSwitches;
    private OnGetSwitchListListener mCallback;

    public interface OnGetSwitchListListener {
        public Map<Integer,String> onGetSwitchList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnGetSwitchListListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnEditSwitchListener");
        }

    }

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
        mSwitches = new HashMap<>();
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

    /*
     * Called from MainActivity when SwitchListFragment have modified
     * id's or names in its switch list.
     */
    public void onSwitchListUpdate(Map<Integer, String> sw_list){
        mSwitches = sw_list;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
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
     *
     * Result: show time picker dialog
     */
    @Override
    public void onEditTimeListener(int position, Boolean value) {
        Toast.makeText(getActivity(), "Button click ", Toast.LENGTH_SHORT).show();

        mCurrentTimerPos = position;
        mCurrentTimerWhat = value;

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        DialogFragment newFragment = new TimePickerDialogFragment();
        newFragment.setTargetFragment(this, REQUEST_CODE_PICKER);
        newFragment.show(ft, "timePicker");
    }

    /*
     * Show TimerManagerDialogFragment
     */
    private void showManagerDialog(Timer timer){

        mSwitches = mCallback.onGetSwitchList();

        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag("TimerManager");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }
        // Fix lists/ Take away occupied switches
        Map<Integer, String> availableSw = new HashMap<>();
        TreeSet<Integer> occupiedSw = new TreeSet<>();
        for(Timer t : mList){
            for(Integer sid : t.getSwitchList())
                occupiedSw.add(sid);
        }

        for(Map.Entry<Integer, String> allsw : mSwitches.entrySet()){
            if(!occupiedSw.contains(allsw.getKey()) || (timer != null && timer.haveSwitch(allsw.getKey()))){
                availableSw.put(allsw.getKey(), allsw.getValue());
            }
        }
        // end fix
        TimerManagerDialogFragment managerDialog = new TimerManagerDialogFragment();
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        if (timer != null) { // If edit exiting timer..
            String gsonTimer = gson.toJson(timer);
            String gsonSwitchList = gson.toJson(availableSw);
            bundle.putString(TimerManagerDialogFragment.TIMER_BUNDLE_KEY, gsonTimer); // Name of
            bundle.putString(TimerManagerDialogFragment.SWITCHES_BUNDLE_KEY, gsonSwitchList); // id
            //Log.d("TTT", timer.getTitle());
        } else { // If new timer is to be added
            Timer nt = new Timer(generate_timer_id());
            Toast.makeText(getContext(), "Timer id:" + nt.getId(), Toast.LENGTH_LONG).show();
            bundle.putString(TimerManagerDialogFragment.TIMER_BUNDLE_KEY, gson.toJson(nt)); // New generated id
            bundle.putString(TimerManagerDialogFragment.SWITCHES_BUNDLE_KEY, gson.toJson(availableSw)); // Name
        }
        managerDialog.setArguments(bundle);
        managerDialog.setTargetFragment(this, REQUEST_CODE_MANGER);
        managerDialog.show(manager, "TimerManager");
    }

    /*
     * Shows the time picker dialog
     */
    public void showTimePickerDialog(/*View v*/) {

    }

    /*
     *  Called when user have picked a time in TimePickerDialogFragment
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Log.d(TAG, "TimePickerDialogFragment " + hourOfDay + ":" + minute);
        Timer timer = mList.get(mCurrentTimerPos);
        if (mCurrentTimerWhat == TimerListAdapter.TIME_ON)
            timer.setTimeOn(hourOfDay, minute);
        else
            timer.setTimeOff(hourOfDay, minute);

        for(Integer i : timer.getSwitchList()){
            Log.d(TAG, "Timer have switch: " + i );
        }
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
        switch (requestCode){

            case REQUEST_CODE_MANGER:
                Gson gson = new Gson();
                Timer timer = gson.fromJson(data.getStringExtra(TimerManagerDialogFragment.TIMER_BUNDLE_KEY), Timer.class);

                if(mList.contains(timer))
                    deleteTimer(timer);
                saveTimer(timer);
                break;
            case REQUEST_CODE_PICKER:

                break;
            default:
                // Do nothing
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


    private void deleteTimer(final Timer timer){
        mList.remove(timer);
        mTimerListAdapter.notifyDataSetChanged();
        saveToMemory();
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
                }
            };
            // Send add command;
            tcpClient.execute("Q:" + timer.getId());
        }else{
            Toast.makeText(getContext(), R.string.no_server_configuration, Toast.LENGTH_LONG).show();
        }
    }

    private void saveTimer(final Timer timer){
        if( mList.contains(timer)) {
           // TODO: HERE's WHERE I LEFT
        }else // If completely new timer
        {
            mList.add(timer);
        }
        saveToMemory();
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
                        Toast.makeText(getContext(), "Timer updated successfully", Toast.LENGTH_LONG).show();
                    } else if (s.equals("NOK")) {
                        Toast.makeText(getContext(), R.string.no_such_switch_at_server, Toast.LENGTH_SHORT).show();
                    }
                    // Continue fetching
                }
            };
            Log.d(TAG, "Adding timer to server:\n"+timer.toString());
            tcpClient.execute("T:" + timer.toString());
        }else{
            Toast.makeText(getContext(), R.string.no_server_configuration, Toast.LENGTH_LONG).show();
        }
    }

    /*
     * Called from MainActivity when syncing with the server.
     *
     * Input is all timers at server and that's the most recent ones
     */
    public void serverSync(List<Timer> serverTimers){
        if(serverTimers.isEmpty())
            return;
        List<Timer> newList = new ArrayList<>();
        try {
            for(Timer timer : serverTimers){
                int index = mList.indexOf(timer);
                if(index == -1){ // If it doesn't exist in our list
                    newList.add(timer); // Add it
                }else { // IF it exists in our list
                    // Replace it with servers timer
                    timer.setTitle(mList.get(index).getTitle());
                    newList.add(timer);
                }
            }
            // Replace server syncList with current old one...
            mList.clear();
            mList.addAll(newList);
            mTimerListAdapter.notifyDataSetChanged();
        }catch (Exception e){
            Toast.makeText(getContext(), "Unable  ", Toast.LENGTH_LONG).show();
            Log.e("updateArrayAdapter", "Could not parse server message. Error: " + e.getMessage());
        }
    }

    private void saveToMemory(){
        StorageTimers storageTimers = new StorageTimers();
        storageTimers.saveTimerList(getContext(), mList);
    }
}
