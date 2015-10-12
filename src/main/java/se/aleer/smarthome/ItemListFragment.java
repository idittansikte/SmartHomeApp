package se.aleer.smarthome;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by alex on 2015-10-12.
 */
public abstract class ItemListFragment extends Fragment {

    protected List<ItemList> mList;
    protected ArrayAdapter mArrayAdapter;

    private ItemListFragmentListener mCallback;

    public interface ItemListFragmentListener {
        public Map<Integer,String> onGetSwitchList();
        public void saveItemList(String timer);
        public void removeItemList(String timer);
        public void onItemListChange(TreeSet<Integer/*Switch ID*/> switchTree);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (ItemListFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ItemListFragmentListener");
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    protected void setListAdapter(ArrayAdapter adapter){
        mArrayAdapter = adapter;
    }

    private void deleteTimer(final Timer timer){
        mList.remove(timer);
        mTimerListAdapter.notifyDataSetChanged();
        saveToMemory();

        // Send add command;
        mCallback.removeTimer(Integer.toString(timer.getId()));
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
        mCallback.saveTimer(timer.toString());
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
