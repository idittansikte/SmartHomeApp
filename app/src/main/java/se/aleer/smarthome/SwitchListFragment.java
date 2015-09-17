package se.aleer.smarthome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import android.os.Handler;

import se.aleer.smarthome.drag_sort_listview.DragSortController;
import se.aleer.smarthome.drag_sort_listview.DragSortListView;

public class SwitchListFragment extends Fragment {

    public static final String ARG_ITEM_ID = "favorite_list";

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener()
    {
        @Override
        public void drop(int from, int to)
        {
            if (from != to)
            {
                Switch item = mSwitchListAdapter.getItem(from);
                mSwitchListAdapter.remove(item);
                mSwitchListAdapter.insert(item, to);
                mStorage.saveSwitchList(mActivity, mSwitches);
                Toast.makeText(mActivity, "mSwitchList: " + mSwitchListAdapter.getCount(), Toast.LENGTH_SHORT).show();
            }else{
                SwitchManagerPopup popup = new SwitchManagerPopup(mActivity, yo);
                popup.setSwitch(mSwitchListAdapter.getItem(from), from);
                popup.initiatePopup();
            }
        }
    };
    SwitchListFragment yo = this;
    private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener()
    {
        @Override
        public void remove(int which)
        {
            mSwitchListAdapter.remove(mSwitchListAdapter.getItem(which));
        }
    };

    private List<Integer> codes = new ArrayList<>();


    private String TAG = "SwitchList";
    private Storage mStorage;
    private Activity mActivity;
    List<Switch> mSwitches;
    private DragSortListView mDragSortListView;
    private SwitchListAdapter mSwitchListAdapter;
    final Handler mHandler = new Handler();

    // Update SwitchListAdapter in interval
    final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            // Get and set status for all switches...
            TCPAsyncTask getStatusArduino = new TCPAsyncTask(){
                @Override
                protected void onPostExecute(String s) {
                    Toast.makeText(mActivity, "Updating...", Toast.LENGTH_SHORT).show();
                    updateListAdapter(s);
                    mSwitchListAdapter.notifyDataSetChanged();
                }
            };
            getStatusArduino.execute("G");
            mHandler.postDelayed(this, 10000);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mStorage = new Storage();
        //mHandler.postDelayed(mRunnable, 15000);
        //mRunnable.run();
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
                showAlert(getResources().getString(R.string.empty_switch_list_title),
                        getResources().getString(R.string.empty_switch_list_msg));
            }
            mSwitchListAdapter = new SwitchListAdapter(mActivity, mSwitches);
            mDragSortListView.setAdapter(mSwitchListAdapter);

            mDragSortListView.setDropListener(onDrop);
            mDragSortListView.setRemoveListener(onRemove);

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
        TCPAsyncTask getStatusArduino = new TCPAsyncTask(){
            @Override
            protected void onPostExecute(String s)
            {
                if(!updateListAdapter(s)) { // If no response from server, set all switch status to 0;
                    Log.d("SLF", "No message from server, setting all switch statuses to 0");
                    for (int adapterPos = 0; adapterPos < mSwitchListAdapter.getCount(); ++adapterPos) {
                        Switch sw = mSwitchListAdapter.getItem(adapterPos);
                        mSwitchListAdapter.setItemStatus(adapterPos, 0);
                    }

                }
                // Update switch-list
                mSwitchListAdapter.notifyDataSetChanged();
            }

        };
        getStatusArduino.execute("G");
        return view;
    }

    private void findViewsById(View view) {
        mDragSortListView = (DragSortListView) view.findViewById(R.id.listview);
    }

    public void showAlert(String title, String message) {
        if (mActivity != null && !mActivity.isFinishing()) {
            AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                    .create();
            alertDialog.setTitle(title);
            alertDialog.setMessage(message);
            alertDialog.setCancelable(false);

            // setting OK Button
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            getFragmentManager().popBackStackImmediate();
                        }
                    });
            alertDialog.show();
        }
    }

    public void add(String name, int controller, int status)
    {
        Switch swtch = new Switch(controller, 0, name);
        swtch.setState(status);
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
        swtch.setState(0);
        add(swtch);
    }

    public void updateSwitch(Switch swtch, int position){
        mSwitchListAdapter.remove(swtch);
        mSwitchListAdapter.insert(swtch, position);
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

    public void removeEnable(){
        //controller.setRemoveEnabled(true);
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

    private boolean updateListAdapter(String serverSwitches) {
        // Check if response from server
        if (serverSwitches == null || serverSwitches.equals("")) {
            return false;
        }

        String[] switches = serverSwitches.split(":");
        // Debug print
        for (String tmp : switches) {
            Log.d("####---->>>", tmp);
        }
        // Go through statuses stored at server
        for (int i = 0; i < switches.length; ++i) {
            Log.d("SLF", "Message from server: " + serverSwitches);
            // Something is wrong if this have happen
            if (switches.length < i + 1) {
                Toast.makeText(mActivity, "Weird switch list from server... Contact someone", Toast.LENGTH_SHORT).show();
                return false;
            }
            // Convert to ints
            int controller = Integer.valueOf(switches[i]);
            int status = Integer.valueOf(switches[++i]);
            // For every switch in adapter-list. Check if server switch exists on app
            boolean switchExist = false;
            for (int adapterPos = 0; adapterPos < mSwitchListAdapter.getCount(); ++adapterPos) {
                Switch sw = mSwitchListAdapter.getItem(adapterPos);
                // If switch exists on app, set servers saved status
                if (sw.getId() == controller) {
                    mSwitchListAdapter.setItemStatus(adapterPos, status);
                    switchExist = true;
                }
            } // adapterlist
            if (!switchExist) {
                add("Sync\'ed", controller, status);
            }
        }
        return true;
    }

}
