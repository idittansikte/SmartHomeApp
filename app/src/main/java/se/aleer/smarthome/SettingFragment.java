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
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import se.aleer.smarthome.drag_sort_listview.DragSortController;
import se.aleer.smarthome.drag_sort_listview.DragSortListView;

public class SettingFragment extends Fragment {

    public static final String ARG_ITEM_ID = "setting";

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener()
    {
        @Override
        public void drop(int from, int to)
        {
            if (from != to)
            {
                String item = adapter.getItem(from);
                adapter.remove(item);
                adapter.insert(item, to);
                int code = codes.get(from);
                codes.remove(from);
                codes.add(to, code);
                //Toast.makeText(RemoteControl.this, "You dropped it.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener()
    {
        @Override
        public void remove(int which)
        {
            adapter.remove(adapter.getItem(which));
        }
    };

    private DragSortListView dragListView;
    private ArrayAdapter<String> adapter;
    private String[] web = {};
    private List<Integer> codes = new ArrayList<>();


    private String TAG = "SwitchList";
    private StorageSwitches mStorageSwitches;
    private Activity mActivity;
    List<Switch> mSwitches;
    private DragSortListView mDragSortListView;
    private SwitchListAdapter mSwitchListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mStorageSwitches = new StorageSwitches();
    }

    @Override
    public void onResume() {
        //getActivity().setTitle(R.string.switch_list_title);
        //getActivity().getActionBar().setTitle(R.string.switch_list_title);
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_switch_list, container,
                false);

        mDragSortListView = (DragSortListView) view.findViewById(R.id.listview);

        mSwitches = mStorageSwitches.getSwitchList(mActivity);

        if ( mSwitches == null ){
            showAlert(getResources().getString(R.string.empty_switch_list_title),
                    getResources().getString(R.string.empty_switch_list_msg));
        }
        else{
            if(mSwitches.size() == 0){
                showAlert(getResources().getString(R.string.empty_switch_list_title),
                        getResources().getString(R.string.empty_switch_list_msg));
            }
            mSwitchListAdapter = new SwitchListAdapter(mActivity, mSwitches);
            mDragSortListView.setAdapter(mSwitchListAdapter);

            mDragSortListView.setDropListener(onDrop);
            mDragSortListView.setRemoveListener(onRemove);

            Log.d(TAG, "2");
            DragSortController controller = new DragSortController(dragListView);
            controller.setDragHandleId(R.id.text);
            //controller.setClickRemoveId(R.id.);
            //controller.setRemoveEnabled(true);
            //controller.setSortEnabled(true);
            controller.setDragInitMode(2);

            mDragSortListView.setFloatViewManager(controller);
            mDragSortListView.setOnTouchListener(controller);
            mDragSortListView.setDragEnabled(true);
        }


        //productListView.setOnItemClickListener(this);
        //productListView.setOnItemLongClickListener(this);
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
                            // activity.finish();
                            getFragmentManager().popBackStackImmediate();
                        }
                    });
            alertDialog.show();
        }
    }

    public void add(String name) {
        adapter.add(name);
        adapter.notifyDataSetChanged();
        int rand =  (int) Math.round(Math.random() * 100);
        codes.add(rand);
        //Log.d(TAG, man.getData());

    }

    public void removeEnable(){
        //controller.setRemoveEnabled(true);
    }

    /*
    SwitchList(Activity activity, View onView){
        mActivity = activity;
        init(onView);
    }

    private void init(View onView){
        dragListView = (DragSortListView) onView; // findViewById(R.id.listview);

        ArrayList<String> list = new ArrayList<String>(Arrays.asList(web));

        adapter = new ArrayAdapter<String>(mActivity.getApplication(), R.layout.list_single, R.id.text, list)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                final int p = position;
                LayoutInflater inflater = mActivity.getLayoutInflater();
                View rowView= inflater.inflate(R.layout.list_single, null, true);

                TextView txtTitle = (TextView) rowView.findViewById(R.id.text);
                Button off = (Button) rowView.findViewById(R.id.button_left);
                Button on = (Button) rowView.findViewById(R.id.button_right);

                txtTitle.setText(adapter.getItem(position));
                off.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mActivity, "OFF " + codes.get(p), Toast.LENGTH_SHORT).show();
                    }
                });

                on.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mActivity, "ON "+codes.get(p), Toast.LENGTH_SHORT).show();
                    }
                });
                return rowView;
            }
        };

        dragListView.setAdapter(adapter);

        dragListView.setDropListener(onDrop);
        dragListView.setRemoveListener(onRemove);

        Log.d(TAG, "2");
        DragSortController controller = new DragSortController(dragListView);
        controller.setDragHandleId(R.id.text);
        //controller.setClickRemoveId(R.id.);
        //controller.setRemoveEnabled(true);
        //controller.setSortEnabled(true);
        controller.setDragInitMode(2);

        dragListView.setFloatViewManager(controller);
        dragListView.setOnTouchListener(controller);
        dragListView.setDragEnabled(true);
    }

    public void add(String name) {
        adapter.add(name);
        adapter.notifyDataSetChanged();
        int rand =  (int) Math.round(Math.random() * 100);
        codes.add(rand);
        //Log.d(TAG, man.getData());

    }

    public void removeEnable(){
        //controller.setRemoveEnabled(true);
    }
    */

}
