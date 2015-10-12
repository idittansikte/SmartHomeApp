package se.aleer.smarthome;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeSet;
import android.os.Handler;

import com.google.gson.Gson;

import se.aleer.smarthome.drag_sort_listview.DragSortController;
import se.aleer.smarthome.drag_sort_listview.DragSortListView;

public class SwitchListFragment extends Fragment implements FragmentManager.OnBackStackChangedListener, SwitchListAdapter.OnSwitchStatusChangeListener {

    // Some random code that is sent to switch manager and get back.
    private int REQUEST_CODE = 134;
    public static final String ARG_ITEM_ID = "favorite_list";
    public static String TAG = "SwitchListFragment";
    private StorageSwitches mStorageSwitches;
    private Activity mActivity;
    List<Switch> mSwitches;
    private DragSortListView mDragSortListView;
    private SwitchListAdapter mSwitchListAdapter;
    final Handler mHandler = new Handler();
    private SwitchFragmentListener mCallback;
    /** FIFO queue to know what request is sent back */
    public static final int REQUEST_REMOVE = 0;
    public static final int REQUEST_STATUS = 1;
    public static final int REQUEST_SAVE = 2;
    private FIFORequestQueue mSwitchRequestQueue;


    public interface SwitchFragmentListener {
        public Map<Integer,String> onGetSwitchList();
        public void saveSwitch(String swtch);
        public void removeSwitch(String swtch);
        public void changeSwitchStatus(String swtch);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (SwitchFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement SwitchFragmentListener");
        }

    }

    public static SwitchListFragment newInstance(int page, String title) {
        SwitchListFragment fragment = new SwitchListFragment();
        Bundle args = new Bundle();
        args.putInt("page", page);
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }




    private DragSortListView.DragSortListener dragSortListener = new DragSortListView.DragSortListener() {
        @Override
        public void drag(int from, int to) {
            // Disable data fetcher is it doesn't interrupt the drag
        }

        @Override
        public void drop(int from, int to) {
            if (from != to) {
                Switch item = mSwitchListAdapter.getItem(from);
                mSwitchListAdapter.remove(item);
                mSwitchListAdapter.insert(item, to);
                mStorageSwitches.saveSwitchList(mActivity, mSwitches);
                Toast.makeText(mActivity, "mSwitchList: " + mSwitchListAdapter.getCount(), Toast.LENGTH_SHORT).show();
            } else { // If dropped ad the same position activate switch-edit
                Switch s = mSwitchListAdapter.getItem(from);
                if (s != null)
                    Log.d(TAG, "Switch is not null");
                showManagerDialog(mSwitchListAdapter.getItem(from));
            }
            // Enable it again
        }

        @Override
        public void remove(int which) {
            mSwitchListAdapter.remove(mSwitchListAdapter.getItem(which));
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSwitchRequestQueue = new FIFORequestQueue();
        mActivity = getActivity();
        mStorageSwitches = new StorageSwitches();
        setHasOptionsMenu(true);
    }


    public void onBackStackChanged() {
        // enable Up button only if there are entries on the back stack
        if (getActivity().getFragmentManager().getBackStackEntryCount() < 1) {
            ;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Save switch list, we may not come back...
        mStorageSwitches.saveSwitchList(mActivity, mSwitches);
    }

    @Override
    public void onStop() {
        super.onStop();
        //mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_fragment_switch_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_add_switch:
                showManagerDialog(null);
                //mCallback.onEditSwitch(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_switch_list, container,
                false);

        mDragSortListView = (DragSortListView) view.findViewById(R.id.listview);

        mSwitches = mStorageSwitches.getSwitchList(mActivity);
        // Set timers on all whom got a timer connected
        initWhoHasTimer();

        if (mSwitches == null) {
            mSwitches = new ArrayList<>();
            mStorageSwitches.saveSwitchList(mActivity, mSwitches);
        }
        if (mSwitches.size() == 0) {
            new Alerter(mActivity).showAlert(getResources().getString(R.string.empty_switch_list_title),
                    getResources().getString(R.string.empty_switch_list_msg));
        }
        mSwitchListAdapter = new SwitchListAdapter(mActivity, mSwitches);
        // Set button listener
        mSwitchListAdapter.setOnSwitchStatusChangeListener(this);

        mDragSortListView.setAdapter(mSwitchListAdapter);

        //mDragSortListView.setDropListener(onDrop);
        //mDragSortListView.setRemoveListener(onRemove);
        mDragSortListView.setDragSortListener(dragSortListener);

        DragSortController controller = new DragSortController(mDragSortListView);
        controller.setDragHandleId(R.id.single_list_head);

        //controller.setClickRemoveId(R.id.);
        controller.setRemoveEnabled(false);
        //controller.setSortEnabled(true);
        controller.setDragInitMode(2);

        mDragSortListView.setFloatViewManager(controller);
        mDragSortListView.setOnTouchListener(controller);
        mDragSortListView.setDragEnabled(true);

        return view;
    }

    public void add(String name, int controller, int status) {
        Switch swtch = new Switch(controller, 0, name);
        swtch.setStatus(status);
        add(swtch);
    }

    /*
    TODO: Implement this the the new setup
    if (s.equals("OK")) {
                        // If all ok we add switch...
                        mSwitchListAdapter.add(sw);
                        Toast.makeText(mActivity, "Switch successfully added to server", Toast.LENGTH_SHORT).show();
                    } else if (s.equals("NOK")) {
                        Toast.makeText(mActivity, R.string.server_cache_full, Toast.LENGTH_LONG).show();
                    }

     */
    public void add(Switch swtch) {
        mSwitchListAdapter.add(swtch);
        // Put request on queue so we can handle the response later
        mSwitchRequestQueue.put(swtch.getId(), REQUEST_SAVE);
        // Send add command to server;
        mCallback.saveSwitch(Integer.toString(swtch.getId()));
    }

    public void add(String name) {
        Switch swtch = new Switch(Item.generate_unique_id(mSwitches), 0, name);
        swtch.setStatus(0);
        add(swtch);
    }

    public void updateSwitch(Switch swtch) {
        int pos = mSwitchListAdapter.getPosition(swtch);
        mSwitchListAdapter.remove(swtch);
        mSwitchListAdapter.insert(swtch, pos);
        mStorageSwitches.saveSwitchList(mActivity, mSwitches);
    }

    public void deleteSwitch(final Switch sw) {
        // Put request on queue so we can handle the response later
        mSwitchRequestQueue.put(sw.getId(), REQUEST_REMOVE);
        // Send remove command to server;
        mCallback.removeSwitch(Integer.toString(sw.getId()));
    }

 /*   private int getUniqueId() {
        int id = 10;
        TreeSet<Integer> takenIds = new TreeSet<>();
        for (Switch i : mSwitches) {
            takenIds.add(i.getId());
        }
        while (true) {
            if (!takenIds.contains(id)) {
                return id;
            }
            ++id;
        }
    }
*/
    public boolean updateListAdapter(Map<Integer,Integer> serverSwitches) {
        // Check if response from server

        // Go through statuses stored at server
        for (Map.Entry<Integer, Integer> entry : serverSwitches.entrySet()) {
            // Convert to ints
            int id = entry.getKey();
            int status = entry.getValue();
            // For every switch in adapter-list. Check if server switch exists on app
            boolean switchExist = false;
            for (int adapterPos = 0; adapterPos < mSwitchListAdapter.getCount(); ++adapterPos) {
                Switch sw = mSwitchListAdapter.getItem(adapterPos);
                //Log.d(TAG, sw.toString());
                // If switch exists on app, set servers saved status
                if (sw.getId() == id) {
                    mSwitchListAdapter.setItemStatus(adapterPos, status);
                    switchExist = true;
                }
                if (!serverSwitches.containsKey(sw.getId())) {

                    this.deleteSwitch(sw);
                }
            } // adapterlist

            if (!switchExist) {
                add("Sync\'ed", id, status);
            }
        }
        mSwitchListAdapter.notifyDataSetChanged();
        return true;
    }

    private void disableSwitches() {
        for (int adapterPos = 0; adapterPos < mSwitchListAdapter.getCount(); ++adapterPos) {
            mSwitchListAdapter.getItem(adapterPos).setStatus(-1);
        }
    }

    private void showManagerDialog(Switch sw) {
        Log.d("showManagerDialog", "Here");
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag("SwitchManager");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        } else {
            SwitchManagerFragment managerDialog = new SwitchManagerFragment();
            if (sw != null) {
                Bundle bundle = new Bundle();
                Gson gson = new Gson();
                String gsonString = gson.toJson(sw);
                Log.d("DEASD", gsonString);
                bundle.putString("Switch", gsonString);
                managerDialog.setArguments(bundle);
            }
            managerDialog.setTargetFragment(this, REQUEST_CODE);
            managerDialog.show(manager, "SwitchManager");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure fragment codes match up
        if (requestCode == REQUEST_CODE) {
            String gsonString = data.getStringExtra(SwitchManagerFragment.SWITCH_BUNDLE_KEY);
            if(gsonString != null){
                Gson gson = new Gson();
                Switch swtch = gson.fromJson(gsonString, Switch.class);
                if (resultCode == SwitchManagerFragment.REQUEST_CODE_ADD){ // Or edit
                    if (mSwitchListAdapter.contains(swtch.getId()))
                        updateSwitch(swtch);
                    else {
                        swtch.setId(Item.generate_unique_id(mSwitches));
                        add(swtch);
                    }
                } else if (resultCode == SwitchManagerFragment.REQUEST_CODE_DELETE){ // Remove
                    deleteSwitch(swtch);
                }
            }
        }
    }

    /**
     * Turns the SwitchList to a map with:
     * Key: switch ID
     * Value: switch NAME
     *
     * Function for activity update feeder...
     */
    public Map<Integer, String> mapList(){
        Map<Integer,String> m = new HashMap<>();
        for (Switch sw: mSwitches ){
            m.put(sw.getId(), sw.getName());
        }
        return m;
    }

    /**
     * Called from adapter when user clicks the status button on a switch list item.
     *
     * @param swtch the switch that are about to get turned on/off.
     */
    public void changeStatus(Switch swtch){
        mSwitchRequestQueue.put(swtch.getId(), REQUEST_STATUS);
        String status = swtch.getId() + ":" + (swtch.getStatus() == 1 ? "0" : "1");
        mCallback.changeSwitchStatus(status);
    }

    /**
     * Called from Main Activity when request is done.
     * @param ok Tells if the request went bad or good.
     */
    public void onRequestFinished(boolean ok){
        if (mSwitchRequestQueue.isEmpty() ){
            Log.e(TAG, "FIFO queue item is lost...");
            return;
        }
        FIFORequestQueue.requestItem queueItem = mSwitchRequestQueue.pop();
        Switch sw = getSwitchById(queueItem.id);
        if ( sw == null ) {
            Log.e(TAG, "|| onRequestFinished || Switch not found");
        }
        switch (queueItem.type){
            case REQUEST_STATUS:
                onRequestStatusResponse(sw, ok);
                break;
            case REQUEST_SAVE:
                onRequestSaveResponse(sw, ok);
                break;
            case REQUEST_REMOVE:
                onRequestRemoveResponse(sw, ok);
                break;
        }
    }

    /**
     * Handle status request response.
     *
     * @param sw The switch it's regarding.
     * @param ok If request went bad or good.
     */
    private void onRequestStatusResponse(Switch sw, boolean ok){
        sw.waitingUpdate = false;
        if (ok) {
            if(sw.getStatus() == 0){
                sw.setStatus(1);
            }else {
                sw.setStatus(0);
            }
        }else{ // Server did not change status...

        }
        mSwitchListAdapter.notifyDataSetChanged();
    }

    /**
     * Handle remove request response.
     *
     * @param sw The switch it's regarding.
     * @param ok If request went bad or good.
     */
    private void onRequestRemoveResponse(Switch sw, boolean ok){
        if(ok){
            mSwitchListAdapter.remove(sw);
        }else
            Toast.makeText(mActivity, "Server did not respond, could not remove that bitch! Uhhm I mean switch!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Handle switch-save server-request response.
     *
     * @param sw The switch it's regarding.
     * @param ok If request went bad or good.
     */
    private void onRequestSaveResponse(Switch sw, boolean ok){
        if(!ok){
            mSwitchListAdapter.remove(sw);
            Toast.makeText(mActivity, "Server did not respond, could not save that bitch! Uhhm I mean switch!", Toast.LENGTH_SHORT).show();
        }

    }

    private Switch getSwitchById(int swId){
        for (Switch sw: mSwitches ){
            if (sw.getId() == swId)
                return sw;
        }
        return null;
    }

    /**
     * Called all the way back from TimerFragment on timer change.
     *
     * @param switchTree A tree of switch-ids whom got a timer connected to it.
     */
    public  void onTimerListChange(TreeSet<Integer/*Switch ID*/> switchTree) {
        for (Switch sw : mSwitches ){
            if(switchTree.contains(sw.getId())){
                sw.hasTimer=true;
            }else
                sw.hasTimer=false;
        }
        mSwitchListAdapter.notifyDataSetChanged();
    }

    /**
     * Initialize Switches "hasTimer" variable with what is stored in
     * StorageTimer.
     *
     * Is called when fragment is created. Futured updates is called from
     * TimerFragment with onTimerListChange...
     *
     */
    private void initWhoHasTimer(){
        StorageTimers storageTimers = new StorageTimers();
        List<Timer> timers = storageTimers.getTimerList(getContext());
        for(Timer timer : timers){
            for(Integer swId : timer.getSwitchList()){
                Switch sw = getSwitchById(swId);
                if(sw != null){
                    sw.hasTimer = true;
                }
            }

        }
    }
}