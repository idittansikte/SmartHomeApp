package se.aleer.smarthome;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class TimerFragment extends Fragment implements TimerListAdapter.customTextTimeListener {

    private int mPage;
    private String mTitle;
    private List<Timer> mList;
    private TimerListAdapter mTimerListAdaper;

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
        // Set "constructor" arguments from newInstance
        mPage = getArguments().getInt("page", 0);
        mTitle = getArguments().getString("title");
        mList = new ArrayList<>();
        mList.add(new Timer("Test 1"));
        mList.add(new Timer("Test 2"));
        mList.add(new Timer("Test 3"));

        mTimerListAdaper = new TimerListAdapter(getActivity(), mList);
        mTimerListAdaper.setCustomTextTimeListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        ListView listView = (ListView) view.findViewById(R.id.timer_listview);
        listView.setAdapter(mTimerListAdaper);
        //listView.setOnItemClickListener(mListViewListener);
        //setListAdapter(mTimerListAdaper);
        //setListShown(true);

        return view;

    }

    /*AdapterView.OnItemClickListener mListViewListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
            final Timer item = (Timer) parent.getItemAtPosition(position);
            view.animate().setDuration(2000).alpha(0)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mList.remove(item);
                            mTimerListAdaper.notifyDataSetChanged();
                            view.setAlpha(1);
                        }
                    });
        }
    };*/


    @Override
    public void onEditTimeListener(int position, Boolean value) {
        Toast.makeText(getActivity(), "Button click ",
                Toast.LENGTH_SHORT).show();

    }
}
