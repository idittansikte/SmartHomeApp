package se.aleer.smarthome;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class TimerManagerDialogFragment extends DialogFragment {

    DoubleListViewWrapper mDlvw;
    final static public String SWITCHIDS_BUNDLE_KEY = "KEY_SWITCH_IDS";
    final static public String NAME_BUNDLE_KEY = "KEY_SWITCH_NAME";
    final static public String ID_BUNDLE_KEY = "KEY_SWITCH_IDS";
    private ViewHolder mViewHolder;
    private int mId;
    private String mName;

    public class ViewHolder {
        Button button_cancel;
        Button button_save;
        EditText name;
        public ViewHolder(View view){
            this.button_cancel = (Button) view.findViewById(R.id.button_cancel);
            this.button_save = (Button) view.findViewById(R.id.button_save);
            this.name = (EditText) view.findViewById(R.id.et_name);
        }
    }

    // Empty constructor needed from dialog fragment
    public TimerManagerDialogFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            mName = bundle.getString(NAME_BUNDLE_KEY);
            mId = bundle.getInt(ID_BUNDLE_KEY, -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_timer_manager_dialog, container);

        setupDoubleListView(view);

        mViewHolder = new ViewHolder(view);
        mViewHolder.button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mViewHolder.button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult();
            }
        });

        mViewHolder.name.setText(mName);
        return view;
    }

    private void setupDoubleListView(View view){
        // Get switch list from memory...
        StorageSwitches storageSwitches = new StorageSwitches();
        List<Switch> switchesMem = storageSwitches.getSwitchList(getContext());
        List<Switch> selected = new ArrayList<>();
        // If not a new Timer...
        List<Switch> unselected = new ArrayList<>();
        // Separate switches into two lists
        for (Switch sw : switchesMem) {
            if (sw.getTimerId() == mId){ // If this timer got this switch, add it to selected
                selected.add(sw);
            }else if (sw.getTimerId() == -1){ // If not already taken by another timer...
                unselected.add(sw);
            }
        }
        mDlvw = new DoubleListViewWrapper(getContext(), view, unselected, selected);
        mDlvw.enableTitles(true);
        mDlvw.setTitles("Available", "Selected");
        getDialog().setTitle("Timer Manager");
    }

    /*
     * When the user clicks "save" this function is called from the button OnClickListener.
     *
     * Functions saves/removes the timer-ids into switches and returns all values that belongs to
     * the timer class back to TimerFragment.
     */
    private void sendResult() {
        List<Switch> selectedSwitches = mDlvw.getSelectedList();
        if (selectedSwitches.isEmpty()){
            //TODO: Show warning popup! (You have to select at least one!)
            return;
        }
        // Save and remove timerID in switches...
        StorageSwitches storageSwitches = new StorageSwitches();
        List<Switch> switchesMem = storageSwitches.getSwitchList(getContext());
        for (Switch sw : switchesMem) {
            if(selectedSwitches.contains(sw)){ // If switch is in selected list
                sw.setTimerId(mId); // Set ownership to this timer
            }else if (sw.getTimerId() == mId){ // If switch is not in the selected list but timer has ownership
                sw.setTimerId(-1); // Remove it so other timers can select it...
            }
        }
        // Save switch changes
        storageSwitches.saveSwitchList(getContext(), switchesMem);
        // Send edited/added timer back...
        Intent intent = new Intent();
        intent.putExtra(ID_BUNDLE_KEY, mId); // Timer ID
        intent.putExtra(NAME_BUNDLE_KEY, mViewHolder.name.getText().toString()); // Name of Timer...
        getTargetFragment().onActivityResult(getTargetRequestCode(), 0, intent);
        dismiss();
    }
}
