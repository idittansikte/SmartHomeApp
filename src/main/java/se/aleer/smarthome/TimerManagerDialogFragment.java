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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimerManagerDialogFragment extends DialogFragment {
    private static String TAG = "TimerManager";
    DoubleListViewWrapper mDlvw;
    final static public String SWITCHIDS_BUNDLE_KEY = "KEY_SWITCH_IDS";
    final static public String TIMER_BUNDLE_KEY = "KEY_SWITCH_NAME";
    final static public String SWITCHES_BUNDLE_KEY = "KEY_SWITCH_IDS";
    private ViewHolder mViewHolder;
    private Timer mTimer;
    private Map<Integer, String> mSwitches;

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
            try {
                Gson gson = new Gson();
                mTimer = gson.fromJson(bundle.getString(TIMER_BUNDLE_KEY), Timer.class);
                Type type = new TypeToken<Map<Integer, String>>() {}.getType();
                mSwitches = gson.fromJson(bundle.getString(SWITCHES_BUNDLE_KEY), type);
                if(!mSwitches.isEmpty())
                {
                    Log.d(TAG, "mSwitches");
                    for (Map.Entry<Integer, String> d : mSwitches.entrySet()){
                        Log.d(TAG, d.getValue());
                    }
                }
            }catch (Exception e){
                Log.e(TAG, "Failed to parse parameters from bundle" + e.getMessage());
                dismiss();
            }
        }else {
            Toast.makeText(getContext(), "Failed...", Toast.LENGTH_LONG).show();
            dismiss();
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

        mViewHolder.name.setText(mTimer.getName());
        return view;
    }

    private void setupDoubleListView(View view){
        List<Switch> selected = new ArrayList<>();
        List<Switch> unselected = new ArrayList<>();
        // If not a new Timer...
        for(Map.Entry<Integer, String> entry : mSwitches.entrySet()){
            if(mTimer.haveSwitch(entry.getKey()))
                selected.add(new Switch(entry.getKey(), entry.getValue()));
            else
                unselected.add(new Switch(entry.getKey(), entry.getValue()));
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
        mTimer.clearSwitchList();
        for(Switch s : selectedSwitches){
            mTimer.addSwitch(s.getId());
        }
        mTimer.setName(mViewHolder.name.getText().toString());
        // Send edited/added timer back...
        Intent intent = new Intent();
        Gson gson = new Gson();
        Toast.makeText(getContext(), "Timer id:" + mTimer.getId(), Toast.LENGTH_LONG).show();
        intent.putExtra(TIMER_BUNDLE_KEY, gson.toJson(mTimer)); // Timer ID
        getTargetFragment().onActivityResult(getTargetRequestCode(), 0, intent);
        dismiss();
    }
}
