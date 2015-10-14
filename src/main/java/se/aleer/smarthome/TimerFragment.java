package se.aleer.smarthome;

import android.content.Context;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;


public class TimerFragment extends Fragment implements TimerListAdapter.customTextTimeListener, TimePickerDialog.OnTimeSetListener {

    public static final String TAG = "TimerFragment";
    private final int REQUEST_CODE_MANGER = 314;
    private final int REQUEST_CODE_PICKER = 124;
    private List<Timer> mList;
    private TimerListAdapter mTimerListAdapter;
    private int mCurrentTimerPos;
    private Boolean mCurrentTimerWhat;
    private Map<Integer, String> mSwitches;
    private TimerFragmentListener mCallback;
    private RequestInterface mRequestCallback;

    public interface TimerFragmentListener {
        public Map<Integer,String> onGetSwitchList();
        public void onTimerListChange(TreeSet<Integer/*Switch ID*/> switchTree);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (TimerFragmentListener) context;
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
        setHasOptionsMenu(true);
        mSwitches = new HashMap<>();
        StorageTimers storageTimers = new StorageTimers();
        mList = storageTimers.getTimerList(getContext());
        // Check if there is something in memory
        if(mList == null){ // If not, create a new one
            mList = new ArrayList<>();
        }
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
    public void onPause() {
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

        return view;

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_fragment_timer, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_add_timer:
                showManagerDialog(null);
                //mCallback.onEditSwitch(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    /**
     * On click listener for the timer-adapter. Is called when someone clicks an item in
     * the timer list.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.timer_listview) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(mList.get(info.position).getName());
            String[] menuItems = getResources().getStringArray(R.array.timer_listview_menu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
        // Vibrate if vibration is on
        Vibrate.vibrate(getContext());
    }
    /**
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
                // Vibrate if vibration is on
                Vibrate.vibrate(getContext());
                break;
            case 1: // Remove
                deleteTimer(selectedTimer);
                // Vibrate if vibration is on
                Vibrate.vibrate(getContext());
                break;
            default:
                Toast.makeText(getContext(), "This option is not implemented yet..", Toast.LENGTH_LONG).show();
        }
        return true;
    }

    /**
     * This is called from the adapter when someone clicks a
     * time-filed in timer-list.
     *
     * Result: show time picker dialog
     */
    @Override
    public void onEditTimeListener(int position, Boolean value) {
        mCurrentTimerPos = position;
        mCurrentTimerWhat = value;

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        DialogFragment newFragment = new TimePickerDialogFragment();
        newFragment.setTargetFragment(this, REQUEST_CODE_PICKER);
        newFragment.show(ft, "timePicker");

        // Vibrate if vibration is on
        Vibrate.vibrate(getContext());
    }

    /**
     * This is a helper function that compiles a tree of switch-id that is
     * used by some timer.
     *
     * @return A list of all switches occupied with some Timer
     */
    private TreeSet<Integer> getOccupiedSwitches(){
        TreeSet<Integer> occupiedSw = new TreeSet<>();
        for(Timer t : mList){
            for(Integer sid : t.getSwitchList())
                occupiedSw.add(sid);
        }
        return occupiedSw;
    }

    /**
     * Show TimerManagerDialogFragment
     *
     * @param timer The timer that is about to get changed or added.
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
        TreeSet<Integer> occupiedSw = getOccupiedSwitches();

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
            Timer nt = new Timer(Item.generate_unique_id(mList));
            Toast.makeText(getContext(), "Timer id:" + nt.getId(), Toast.LENGTH_LONG).show();
            bundle.putString(TimerManagerDialogFragment.TIMER_BUNDLE_KEY, gson.toJson(nt)); // New generated id
            bundle.putString(TimerManagerDialogFragment.SWITCHES_BUNDLE_KEY, gson.toJson(availableSw)); // Name
        }
        managerDialog.setArguments(bundle);
        managerDialog.setTargetFragment(this, REQUEST_CODE_MANGER);
        managerDialog.show(manager, "TimerManager");
    }

    /**
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

    /**
     * Called when the TimeManagerDialogFragment have edited or added a timer.
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
                // Notify SwitchListFragment that the list have changed and send all new occupied switch-ids..
                mCallback.onTimerListChange(getOccupiedSwitches());
                break;
            case REQUEST_CODE_PICKER:

                break;
            default:
                // Do nothing
        }

    }


    private void deleteTimer(final Timer timer){
        mList.remove(timer);
        mTimerListAdapter.notifyDataSetChanged();
        saveToMemory();

        // Send add command;
        mRequestCallback.sendRequest(TAG, ServerRequestMaker.makeRemoveTimerRequest(timer));
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
        mRequestCallback.sendRequest(TAG, ServerRequestMaker.makeSaveTimerRequest(timer));
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
                    timer.setName(mList.get(index).getName());
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
