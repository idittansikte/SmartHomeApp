package se.aleer.smarthome;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.content.Context;
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
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import android.os.Handler;

import com.google.gson.Gson;

import se.aleer.smarthome.drag_sort_listview.DragSortController;
import se.aleer.smarthome.drag_sort_listview.DragSortListView;

public class SwitchListFragment extends Fragment implements FragmentManager.OnBackStackChangedListener {

    // Some random code that is sent to switch manager and get back.
    private int REQUEST_CODE = 134;

    public static final String ARG_ITEM_ID = "favorite_list";
    private static String TAG = "SwitchListFragment";
    private StorageSwitches mStorageSwitches;
    private Activity mActivity;
    List<Switch> mSwitches;
    private DragSortListView mDragSortListView;
    private SwitchListAdapter mSwitchListAdapter;
    final Handler mHandler = new Handler();


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

        mActivity = getActivity();
        mStorageSwitches = new StorageSwitches();
        setHasOptionsMenu(true);
        ((RemoteControl) getActivity()).hideUpButton();
    }


    public void onBackStackChanged() {
        // enable Up button only if there are entries on the back stack
        if (getActivity().getFragmentManager().getBackStackEntryCount() < 1) {
            ((RemoteControl) getActivity()).hideUpButton();
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
            case R.id.action_settings:
                Intent intent = new Intent(mActivity, Settings.class);
                startActivity(intent);
                return true;
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

        if (mSwitches == null) {
            mSwitches = new ArrayList<>();
            mStorageSwitches.saveSwitchList(mActivity, mSwitches);
        }
        if (mSwitches.size() == 0) {
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
        /*StorageSetting storageSetting = new StorageSetting(getActivity());
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
                    }*//*

                    }
                    // Update switch-list
                    mSwitchListAdapter.notifyDataSetChanged();
                }

            };
            getStatusArduino.execute("G");
        }*/
        return view;
    }

    public void add(String name, int controller, int status) {
        Switch swtch = new Switch(controller, 0, name);
        swtch.setStatus(status);
        add(swtch);
    }

    public void add(Switch swtch) {
        final Switch sw = swtch; // TODO: Make some queue or something
        // Stop fetching...
        // Send update to server
        StorageSetting ss = new StorageSetting(getActivity());
        String port = ss.getString(StorageSetting.PREFS_SERVER_PORT);
        if (port != null) {
            TCPAsyncTask tcpClient = new TCPAsyncTask(ss.getString(StorageSetting.PREFS_SERVER_URL),
                    Integer.parseInt(port)) {
                @Override
                protected void onPostExecute(String s) {

                    if (s == null) {
                        Toast.makeText(mActivity, R.string.no_response_from_server, Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (s.equals("OK")) {
                        // If all ok we add switch...
                        mSwitchListAdapter.add(sw);
                        Toast.makeText(mActivity, "Switch successfully added to server", Toast.LENGTH_SHORT).show();
                    } else if (s.equals("NOK")) {
                        Toast.makeText(mActivity, R.string.server_cache_full, Toast.LENGTH_LONG).show();
                    }
                }
            };
            // Send remove command;
            tcpClient.execute("A:" + swtch.getId());
        }else {
            Toast.makeText(mActivity, R.string.no_server_configuration, Toast.LENGTH_LONG).show();
        }
    }

    public void add(String name) {
        Switch swtch = new Switch(getUniqueId(), 0, name);
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
        // Stop fetching...
        mSwitchListAdapter.remove(sw);
        // Get server config and send request...
        StorageSetting ss = new StorageSetting(getActivity());
        String port = ss.getString(StorageSetting.PREFS_SERVER_PORT);
        if (port != null) {
            TCPAsyncTask tcpClient = new TCPAsyncTask(ss.getString(StorageSetting.PREFS_SERVER_URL),
                    Integer.parseInt(port)) {
                @Override
                protected void onPostExecute(String s) {

                    if (s == null) {
                        Toast.makeText(mActivity, R.string.no_response_from_server, Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (s.equals("OK")) {
                        Toast.makeText(mActivity, "Switch removed successfully", Toast.LENGTH_LONG).show();
                    } else if (s.equals("NOK")) {
                        Toast.makeText(mActivity, R.string.no_such_switch_at_server, Toast.LENGTH_SHORT).show();
                    }
                }
            };
        // Send remove command;
        tcpClient.execute("R:" + sw.getId());
        }else{
            Toast.makeText(mActivity, R.string.no_server_configuration, Toast.LENGTH_LONG).show();
        }
    }

    private int getUniqueId() {
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
                        swtch.setId(getUniqueId());
                        add(swtch);
                    }
                } else if (resultCode == SwitchManagerFragment.REQUEST_CODE_DELETE){ // Remove
                    deleteSwitch(swtch);
                }
            }
        }
    }

    /*
     * Return list
     * Function for activity update feeder...
     */
    public Map<Integer, String> mapList(){
        Map<Integer,String> m = new HashMap<>();
        for (Switch sw: mSwitches ){
            m.put(sw.getId(), sw.getName());
        }
        return m;
    }
}