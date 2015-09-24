package se.aleer.smarthome;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import android.os.Handler;

import se.aleer.smarthome.drag_sort_listview.DragSortController;
import se.aleer.smarthome.drag_sort_listview.DragSortListView;

public class SwitchListFragment extends Fragment implements FragmentManager.OnBackStackChangedListener {

    private String mTitle; // Tab title set by adapter when creating instance
    private int mPage; // FragmentAdapter using this to keep track of tabs/fragments

    public static final String ARG_ITEM_ID = "favorite_list";
    private String TAG = "SwitchListFragment";
    private Storage mStorage;
    private Activity mActivity;
    List<Switch> mSwitches;
    private DragSortListView mDragSortListView;
    private SwitchListAdapter mSwitchListAdapter;
    final Handler mHandler = new Handler();
    OnEditSwitchListener mCallback;

    public static SwitchListFragment newInstance(int page, String title) {
        SwitchListFragment fragment = new SwitchListFragment();
        Bundle args = new Bundle();
        args.putInt("page", page);
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnEditSwitchListener {
        public void onEditSwitch(Switch swtch);
    }

    // Handles events from SwitchManagerFragment
    public void manageManagedSwitch(Switch swtch, boolean remove)
    {
        if(remove){
            deleteSwitch(swtch);
        }
        else{
            if (mSwitchListAdapter.contains(swtch.getId()))
                updateSwitch(swtch, 333);
            else{
                swtch.setId(getUniqueId());
                add(swtch);
            }

        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnEditSwitchListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnEditSwitchListener");
        }

    }

    private DragSortListView.DragSortListener dragSortListener = new DragSortListView.DragSortListener() {
        @Override
        public void drag(int from, int to) {
            // Disable data fetcher is it doesn't interrupt the drag
            mHandler.removeCallbacks(mRunnable);
        }

        @Override
        public void drop(int from, int to) {
            if (from != to)
            {
                Switch item = mSwitchListAdapter.getItem(from);
                mSwitchListAdapter.remove(item);
                mSwitchListAdapter.insert(item, to);
                mStorage.saveSwitchList(mActivity, mSwitches);
                Toast.makeText(mActivity, "mSwitchList: " + mSwitchListAdapter.getCount(), Toast.LENGTH_SHORT).show();
            }else{ // If dropped ad the same position activate switch-edit
                Switch s = mSwitchListAdapter.getItem(from);
                if (s != null)
                    Log.d(TAG, "Switch is not null");
                mCallback.onEditSwitch(mSwitchListAdapter.getItem(from));
                /*SwitchManagerPopup popup = new SwitchManagerPopup(mActivity, yo);
                popup.setSwitch(mSwitchListAdapter.getItem(from), from);
                popup.initiatePopup();*/
            }
            // Enable it again
            mRunnable.run();
        }

        @Override
        public void remove(int which) {
            mSwitchListAdapter.remove(mSwitchListAdapter.getItem(which));
        }
    };

    final Context c = this.getActivity();
    // Update SwitchListAdapter in interval
    final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            // Get and set status for all switches...
            StorageSetting ss = new StorageSetting(getActivity());
            String port = ss.getString(StorageSetting.PREFS_SERVER_PORT);
            if (port != null) {
                TCPAsyncTask getStatusArduino = new TCPAsyncTask(ss.getString(StorageSetting.PREFS_SERVER_URL),
                        Integer.parseInt(port)) {
                    @Override
                    protected void onPostExecute(String s) {
                        //Toast.makeText(mActivity, "Updating...", Toast.LENGTH_SHORT).show();
                        updateListAdapter(s);
                        mSwitchListAdapter.notifyDataSetChanged();
                    }
                };
                getStatusArduino.execute("G");
            }
            mHandler.postDelayed(this, 10000);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set arguments from newInstance
        mPage = getArguments().getInt("page", 0);
        mTitle = getArguments().getString("title");

        mActivity = getActivity();
        mStorage = new Storage();
        setHasOptionsMenu(true);
        getActivity().getFragmentManager().addOnBackStackChangedListener(this);
        // Show back button in menu
        ((RemoteControl)getActivity()).hideUpButton();
    }


    public void onBackStackChanged() {
        // enable Up button only if there are entries on the back stack
        if(getActivity().getFragmentManager().getBackStackEntryCount() < 1) {
            ((RemoteControl)getActivity()).hideUpButton();
        }
    }

    @Override
    public void onResume() {
        //getActivity().setTitle(R.string.switch_list_title);
        //getActivity().getActionBar().setTitle(R.string.switch_list_title);
        mRunnable.run();
        super.onResume();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void onPause(){
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void onStop(){
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
            case R.id.action_settings:
                Intent intent = new Intent(mActivity, Settings.class);
                startActivity(intent);
                return true;
            case R.id.action_add_switch:
                mCallback.onEditSwitch(null);
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

        mSwitches = mStorage.getSwitchList(mActivity);

        if ( mSwitches == null ){
            mSwitches = new ArrayList<Switch>();
            mStorage.saveSwitchList(mActivity, mSwitches);
        }
            if(mSwitches.size() == 0){
                new Alerter(mActivity).showAlert(getResources().getString(R.string.empty_switch_list_title),
                        getResources().getString(R.string.empty_switch_list_msg));
            }
            mSwitchListAdapter = new SwitchListAdapter(mActivity, mSwitches);
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

        // Get and set status for all switches...
        StorageSetting storageSetting = new StorageSetting(getActivity());
        String port = storageSetting.getString(StorageSetting.PREFS_SERVER_PORT);
        if (port != null) {
            TCPAsyncTask getStatusArduino = new TCPAsyncTask(storageSetting.getString(StorageSetting.PREFS_SERVER_URL), Integer.parseInt(port)) {
                @Override
                protected void onPostExecute(String s) {
                    if (!updateListAdapter(s)) { // If no response from server, set all switch status to 0;
                    /*Log.d("SLF", "No message from server, setting all switch statuses to 0");
                    for (int adapterPos = 0; adapterPos < mSwitchListAdapter.getCount(); ++adapterPos) {
                        Switch sw = mSwitchListAdapter.getItem(adapterPos);
                        mSwitchListAdapter.setItemStatus(adapterPos, 0);
                    }*/

                    }
                    // Update switch-list
                    mSwitchListAdapter.notifyDataSetChanged();
                }

            };
            getStatusArduino.execute("G");
        }
        return view;
    }

    public void add(String name, int controller, int status)
    {
        Switch swtch = new Switch(controller, 0, name);
        swtch.setStatus(status);
        add(swtch);
    }

    public void add(Switch swtch){
        mSwitchListAdapter.add(swtch);
        mStorage.addSwitch(mActivity, swtch);

        TCPAsyncTask tcpClient = new TCPAsyncTask() {
            @Override
            protected void onPostExecute(String s) {

                if ( s == null ){
                    Toast.makeText(mActivity, "Could not add switch to server, null error", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(s.equals("OK")){
                    Toast.makeText(mActivity, "Switch successfully added to server", Toast.LENGTH_SHORT).show();
                }
                else if(s.equals("NOK")){
                    Toast.makeText(mActivity, "Add error at server", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(mActivity, "Server error", Toast.LENGTH_SHORT).show();
                }
            }
        };
        // Send remove command;
        tcpClient.execute("A:" + swtch.getId());
    }
    public void add(String name) {
        Switch swtch = new Switch(getUniqueId(), 0, name);
        swtch.setStatus(0);
        add(swtch);
    }

    public void updateSwitch(Switch swtch, int position){
        int pos = mSwitchListAdapter.getPosition(swtch);
        mSwitchListAdapter.remove(swtch);
        mSwitchListAdapter.insert(swtch, pos);
        mStorage.saveSwitchList(mActivity, mSwitches);
    }

    public void deleteSwitch(Switch swtch){
        mSwitchListAdapter.remove(swtch);
        mStorage.saveSwitchList(mActivity, mSwitches);
        TCPAsyncTask tcpClient = new TCPAsyncTask() {
            @Override
            protected void onPostExecute(String s) {

                if ( s == null ){
                    Toast.makeText(mActivity, "Could not remove switch at server, null error", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(s.equals("OK")){
                    Toast.makeText(mActivity, "Switch removed successfully", Toast.LENGTH_SHORT).show();
                }
                else if(s.equals("NOK")){
                    Toast.makeText(mActivity, "Remove error at server", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(mActivity, "Unknown error", Toast.LENGTH_SHORT).show();
                }
            }
        };
        // Send remove command;
        tcpClient.execute("R:" + swtch.getId());
    }

    private int getUniqueId(){
        int id = 10;
        TreeSet<Integer> takenIds = new TreeSet<>();
        for (Switch i : mSwitches){
            takenIds.add(i.getId());
        }
        while(true){
            if ( ! takenIds.contains(id) ){
                return id;
            }
            ++id;
        }
    }

    private boolean updateListAdapter(String serverResponse) {
        // Check if response from server
        if (serverResponse == null || serverResponse.equals("")) {
            disableSwitches();
            return false;
        }

        // Validate and parse serverResponse into a map
        Map<Integer, Integer> serverSwitches = new HashMap<Integer, Integer>();
        String[] switches = serverResponse.split(":");
        for (int i = 0; i < switches.length; ++i){
            if (switches.length < i + 1) {
                Toast.makeText(mActivity, "Weird switch list from server... Contact someone", Toast.LENGTH_SHORT).show();
                return false;
            }
            int id = Integer.valueOf(switches[i]);
            int status = Integer.valueOf(switches[++i]);
            if(id < 10 || id > 255){
                Toast.makeText(mActivity, "Invalid switch-id at server", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(status > 1 || status < 0){
                Toast.makeText(mActivity, "Invalid switch-status at server", Toast.LENGTH_SHORT).show();
                return false;
            }
            serverSwitches.put(id, status);
        }

        // Go through statuses stored at server
        for (Map.Entry<Integer, Integer> entry : serverSwitches.entrySet()) {
            // Convert to ints
            int id = entry.getKey();
            int status = entry.getValue();
            // For every switch in adapter-list. Check if server switch exists on app
            boolean switchExist = false;
            for (int adapterPos = 0; adapterPos < mSwitchListAdapter.getCount(); ++adapterPos) {
                Switch sw = mSwitchListAdapter.getItem(adapterPos);
                // If switch exists on app, set servers saved status
                if (sw.getId() == id) {
                    mSwitchListAdapter.setItemStatus(adapterPos, status);
                    switchExist = true;
                }
            } // adapterlist

            if (!switchExist) {
                add("Sync\'ed", id, status);
            }
        }

        // Check if we have to remove an object
        for (int adapterPos = 0; adapterPos < mSwitchListAdapter.getCount(); ++adapterPos) {
            Switch sw = mSwitchListAdapter.getItem(adapterPos);

            if (! serverSwitches.containsKey(sw.getId())){

                this.deleteSwitch(sw);
            }
        }
        return true;
    }

    private void disableSwitches()
    {
        for (int adapterPos = 0; adapterPos < mSwitchListAdapter.getCount(); ++adapterPos) {
            mSwitchListAdapter.getItem(adapterPos).setStatus(-1);
        }
    }

}
