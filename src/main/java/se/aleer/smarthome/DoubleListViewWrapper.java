package se.aleer.smarthome;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by alex on 2015-09-25.
 */
public class DoubleListViewWrapper {


    ViewHolder mViewHolder;

    private SwitchnameListAdapter mAdapterSelected;
    private SwitchnameListAdapter mAdapterUnselected;
    private List<Switch> mListSelected;
    private List<Switch> mListUnselected;
    private Context mContext;

    DoubleListViewWrapper(Context context, View view, List<Switch> listLeft, List<Switch> listRight){
        mContext = context;
        mViewHolder = new ViewHolder(view);
        mListSelected = listRight;
        mListUnselected = listLeft;
        mAdapterSelected = new SwitchnameListAdapter(mContext, mListSelected);
        mAdapterUnselected = new SwitchnameListAdapter(mContext , mListUnselected);

        // Set adapters for listViews
        mViewHolder.unselectedListView.setAdapter(mAdapterUnselected);
        mViewHolder.selectedListView.setAdapter(mAdapterSelected);
        // Set listeners for listView
        mViewHolder.unselectedListView.setOnItemClickListener(getUnselectedListViewListener());
        mViewHolder.selectedListView.setOnItemClickListener(getSelectedListViewListener());
    }

    public class ViewHolder {
        View rootView;
        ListView unselectedListView;
        ListView selectedListView;
        LinearLayout listViewTitles;
        TextView rightListViewTitle;
        TextView leftListViewTitle;
        public ViewHolder(View view){
            this.rootView = view;
            this.unselectedListView = (ListView) view.findViewById(R.id.leftListView);
            this.selectedListView = (ListView) view.findViewById(R.id.rightListView);
            this.listViewTitles = (LinearLayout) view.findViewById(R.id.listViewTitles);
            this.rightListViewTitle = (TextView) view.findViewById(R.id.rightListViewTitle);
            this.leftListViewTitle = (TextView) view.findViewById(R.id.leftListViewTitle);
        }
    }

    public void enableTitles(Boolean enable){
        if (enable){
            mViewHolder.listViewTitles.setVisibility(View.VISIBLE);
        }else{
            mViewHolder.listViewTitles.setVisibility(View.GONE);
        }
    }

    public void setTitles(String left, String right){
        mViewHolder.leftListViewTitle.setText(left);
        mViewHolder.rightListViewTitle.setText(right);
    }

    private AdapterView.OnItemClickListener getUnselectedListViewListener () {
        return new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                view.setClickable(false);
                final Switch item = (Switch) parent.getItemAtPosition(position);
                mListUnselected.remove(item);
                mListSelected.add(item);
                mAdapterSelected.notifyDataSetChanged();
                mAdapterUnselected.notifyDataSetChanged();
            }
        };
    }

    private AdapterView.OnItemClickListener getSelectedListViewListener () {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                view.setClickable(false);
                final Switch item = (Switch) parent.getItemAtPosition(position);
                mListSelected.remove(item);
                mListUnselected.add(item);
                mAdapterSelected.notifyDataSetChanged();
                mAdapterUnselected.notifyDataSetChanged();
            }
        };
    }

    public List<Switch> getSelectedList(){
        return mListSelected;
    }
}
